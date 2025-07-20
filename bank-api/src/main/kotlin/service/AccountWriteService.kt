package kr.co.won.bank.service

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import kr.co.won.bank.common.ApiResponse
import kr.co.won.bank.core.common.CircuitBreakerUtils.execute
import kr.co.won.bank.core.common.TxAdvice
import kr.co.won.bank.core.lock.DistributedLockService
import kr.co.won.bank.domain.dto.AccountView
import kr.co.won.bank.domain.entity.Account
import kr.co.won.bank.domain.entity.Transaction
import kr.co.won.bank.domain.entity.TransactionType
import kr.co.won.bank.domain.event.AccountCreateEvent
import kr.co.won.bank.domain.event.TransactionCreateEvent
import kr.co.won.bank.domain.repository.AccountRepository
import kr.co.won.bank.domain.repository.TransactionRepository
import kr.co.won.bank.event.publisher.EventPublisher
import kr.co.won.bank.monitoring.metrics.BankMetrics
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class AccountWriteService(
    private val txAdvice: TxAdvice,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val circuitBreaker: CircuitBreakerRegistry,
    private val lockService: DistributedLockService,
    private val eventPublisher: EventPublisher,
    private val bankMetrics: BankMetrics,
) {

    private val logger = LoggerFactory.getLogger(AccountWriteService::class.java)
    private val breaker = circuitBreaker.circuitBreaker("accountWrite")

    private fun randomAccountNumber(): String {
        return System.currentTimeMillis().toString()
    }

    fun createAccount(name: String, balance: BigDecimal): ResponseEntity<ApiResponse<AccountView>> {
        return breaker.execute(
            operation = {
                val account = txAdvice.run {
                    val accountNumber = randomAccountNumber()
                    val account = Account(
                        accountNumber = accountNumber,
                        balance = balance,
                        accountHolderName = name
                    )
                    accountRepository.save(account)
                }!!

                bankMetrics.incrementAccountCreated()
                bankMetrics.updateAccountCount(accountRepository.count())

                eventPublisher.publishAsync(
                    AccountCreateEvent(
                        accountId = account.id!!,
                        accountNumber = account.accountNumber,
                        accountHolderName = account.accountHolderName,
                        initializedBalance = account.balance,
                        occurredOn = account.createdAt
                    )
                )

                return@execute ApiResponse.success<AccountView>(
                    AccountView.from(account),
                    msg = "Create Account Success"
                )

            },
            fallback = { exception ->
                logger.warn("Create Account Failed", exception)
                ApiResponse.error<AccountView>(
                    msg = "Create Account Failed",
                )
            }
        )
    }

    fun transfer(fromAccount: String, toAccount: String, amount: BigDecimal): ResponseEntity<ApiResponse<String>> {
        return breaker.execute(
            operation = {
                lockService.executeWithTransactionLock(fromAccount, toAccount) {
                    transferInternal(fromAccount, toAccount, amount)
                }
            },
            fallback = { exception ->
                logger.warn("transfer Failed", exception)
                ApiResponse.error<String>(
                    msg = "transfer Failed",
                )
            }
        )!!
    }

    private fun transferInternal(
        fromAccount: String,
        toAccount: String,
        amount: BigDecimal
    ): ResponseEntity<ApiResponse<String>> {
        val transactionResult = txAdvice.run {
            var fromAcct = accountRepository.findByAccountNumber(fromAccount)

            if (fromAcct == null) {
                return@run null to "From Account not found"
            }

            if (fromAcct.balance < amount) {
                return@run null to "From Account Balance limit"
            }

            var toAcct = accountRepository.findByAccountNumber(toAccount)

            if (toAcct == null) {
                return@run null to "To Account not found"
            }

            fromAcct.balance = fromAcct.balance.subtract(amount)
            toAcct.balance = toAcct.balance.add(amount)

            val savedFromAccount = accountRepository.save(fromAcct)
            val savedToAccount = accountRepository.save(toAcct)

            val fromTransaction = Transaction(
                account = fromAcct,
                amount = amount,
                type = TransactionType.TRANSFER,
                description = "Transfer From"
            )

            val fromSavedTransaction = transactionRepository.save(fromTransaction)

            val toTransaction = Transaction(
                account = toAcct,
                amount = amount,
                type = TransactionType.TRANSFER,
                description = "Transfer To"
            )

            val savedToTransaction = transactionRepository.save(toTransaction)

            bankMetrics.incrementTransaction("TRANSFER")
            bankMetrics.incrementTransaction("TRANSFER")

            return@run Pair(
                listOf(
                    Pair(fromSavedTransaction, savedFromAccount),
                    Pair(savedToTransaction, savedToAccount)
                ),
                null
            )
        }!!

        if (transactionResult.first == null) {
            return ApiResponse.error(transactionResult.second!!)
        }

        transactionResult.first!!.forEach { (savedTransaction, savedAccount) ->
            eventPublisher.publishAsync(
                TransactionCreateEvent(
                    transactionId = savedTransaction.id!!,
                    accountId = savedAccount.id!!,
                    type = TransactionType.TRANSFER,
                    description = "Transaction Created",
                    amount = amount,
                    balanceAfter = savedAccount.balance,
                )
            )
        }

        return ApiResponse.success<String>(
            data = "Transfer Completed",
            msg = "Transfer Completed"
        )
    }
}