package kr.co.won.bank.event.listener

import kr.co.won.bank.core.common.TxAdvice
import kr.co.won.bank.domain.entity.AccountReadView
import kr.co.won.bank.domain.entity.TransactionReadView
import kr.co.won.bank.domain.entity.TransactionType
import kr.co.won.bank.domain.event.AccountCreateEvent
import kr.co.won.bank.domain.event.TransactionCreateEvent
import kr.co.won.bank.domain.repository.AccountReadViewRepository
import kr.co.won.bank.domain.repository.AccountRepository
import kr.co.won.bank.domain.repository.TransactionReadViewRepository
import kr.co.won.bank.domain.repository.TransactionRepository
import kr.co.won.bank.monitoring.metrics.BankMetrics
import org.slf4j.LoggerFactory.*
import org.springframework.context.event.EventListener
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.retry.annotation.Backoff
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime


@Component
class EventReader(
    private val accountRepository: AccountRepository,
    private val accountReadViewRepository: AccountReadViewRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionReadViewRepository: TransactionReadViewRepository,
    private val eventMetrics: BankMetrics,
    private val txAdvice: TxAdvice,
) {

    private val logger = getLogger(EventReader::class.java)

    @EventListener
    @Async("taskExecutor")
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000)
    ) // 에러가 발생 시에 재 시도 3번까지 진행 후 최대 1000초 대기 후 다음 시도
    fun handleAccountCreated(event: AccountCreateEvent) {
        val startTime = Instant.now()
        val eventType = "AccountCreatedEvent"

        logger.info("Event received: $eventType")

        try {
            txAdvice.runNew {
                val account = accountRepository.findById(event.accountId).orElseThrow {
                    IllegalStateException("Account does not exist - ${event.accountId}")
                }

                val accountReadView = AccountReadView(
                    accountNumber = account.accountNumber,
                    accountHolderName = account.accountHolderName,
                    balance = account.balance,
                    createdAt = account.createdAt,
                    lastUpdatedAt = LocalDateTime.now(),
                    transactionCount = 0,
                    totalDeposit = BigDecimal.ZERO,
                    totalWithdrawals = BigDecimal.ZERO,
                )

                accountReadViewRepository.save(accountReadView)

                logger.info("Account ${account.id} created")

                val duration = Duration.between(startTime, Instant.now())

                eventMetrics.recordEventProcessingTime(duration, eventType)
                eventMetrics.incrementEventProcessed(eventType)
                logger.info("Event processed: $eventType, duration: $duration")
            }
        } catch (e: Exception) {
            logger.error("Event processing failed: $eventType", e)
            eventMetrics.incrementEventFailed(eventType)
            throw e
        }
    }

    @EventListener
    @Async("taskExecutor")
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000)
    ) // 에러가 발생 시에 재 시도 3번까지 진행 후 최대 1000초 대기 후 다음 시도
    fun handleTransactionCreated(event: TransactionCreateEvent) {
        val startTime = Instant.now()
        val eventType = "TransactionCreatedEvent"

        logger.info("Event received: $eventType")

        try {
            txAdvice.runNew {
                val transaction = transactionRepository.findById(event.transactionId).orElseThrow {
                    IllegalStateException("Transaction does not exist - ${event.transactionId}")
                }

                val account = accountRepository.findById(event.accountId).orElseThrow {
                    IllegalStateException("Account does not exist - ${event.accountId}")
                }

                val transactionReadView = TransactionReadView(
                    id = transaction.id,
                    accountId = event.accountId,
                    accountNumber = account.accountNumber,
                    amount = transaction.amount,
                    type = transaction.type,
                    description = transaction.description,
                    createdAt = transaction.createdAt,
                    balanceAfter = account.balance,
                )

                transactionReadViewRepository.save(transactionReadView)

                logger.info("Transaction ${transaction.id} created")

                val accountReadView = accountReadViewRepository.findById(account.id!!).orElseThrow {
                    IllegalStateException("AccountReadView does not exist - ${account.id}")
                }

                val updateAccountReadView: AccountReadView = accountReadView.copy(
                    balance = account.balance,
                    lastUpdatedAt = LocalDateTime.now(),
                    transactionCount = accountReadView.transactionCount + 1,
//                    totalDeposit = if (transaction.type.name == TransactionType.DEPOSIT.name) accountReadView.totalDeposit + transaction.amount else accountReadView.totalDeposit,
//                    totalWithdrawals = if (transaction.type.name == TransactionType.WITHDRAWAL.name) accountReadView.totalWithdrawals + transaction.amount else accountReadView.totalWithdrawals,
                    totalDeposit = when {
                        (transaction.type.name == TransactionType.DEPOSIT.name) -> accountReadView.totalDeposit + transaction.amount
                        (transaction.type.name == TransactionType.TRANSFER.name) -> accountReadView.totalDeposit + transaction.amount
                        else -> accountReadView.totalDeposit
                    },
                    totalWithdrawals = when {
                        (transaction.type.name == TransactionType.WITHDRAWAL.name) -> accountReadView.totalWithdrawals + transaction.amount
                        (transaction.type.name == TransactionType.TRANSFER.name) -> accountReadView.totalWithdrawals + transaction.amount
                        else -> accountReadView.totalWithdrawals
                    }
                )

                accountReadViewRepository.save(updateAccountReadView)

                val duration = Duration.between(startTime, Instant.now())

                eventMetrics.recordEventProcessingTime(duration, eventType)
                eventMetrics.incrementEventProcessed(eventType)
                logger.info("Event processed: $eventType, duration: $duration")
            }
        } catch (e: Exception) {
            logger.error("Event processing failed: $eventType", e)
            throw e
        }
    }

}
