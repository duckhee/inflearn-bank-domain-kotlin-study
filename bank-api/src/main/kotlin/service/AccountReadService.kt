package kr.co.won.bank.service

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import kr.co.won.bank.common.ApiResponse
import kr.co.won.bank.core.common.CircuitBreakerUtils.execute
import kr.co.won.bank.core.common.TxAdvice
import kr.co.won.bank.domain.dto.AccountView
import kr.co.won.bank.domain.dto.TransactionView
import kr.co.won.bank.domain.repository.AccountReadViewRepository
import kr.co.won.bank.domain.repository.TransactionReadViewRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AccountReadService(
    private val txAdvice: TxAdvice,
    private val accountViewRepository: AccountReadViewRepository,
    private val transactionViewRepository: TransactionReadViewRepository,
    private val circuitBreaker: CircuitBreakerRegistry
) {
    private val logger = LoggerFactory.getLogger(AccountReadService::class.java)
    private val breaker = circuitBreaker.circuitBreaker("accountRead")

    fun getAccount(accountNumber: String): ResponseEntity<ApiResponse<AccountView>> {
        return breaker.execute(
            operation = {
                txAdvice.readOnly {
                    val response = accountViewRepository.findByAccountNumber(accountNumber)

                    return@readOnly if (response.isEmpty) {
                        ApiResponse.error<AccountView>("Account not found")
                    } else {
                        ApiResponse.success<AccountView>(AccountView.fromReadView(response.get()))
                    }
                }!!
            },
            fallback = { exception ->
                logger.warn("Get Account Failed", exception)
                ApiResponse.error<AccountView>(
                    msg = "Get Account Failed",
                )
            }
        )
    }

    fun transactionHistory(accountNumber: String, limit: Int?): ResponseEntity<ApiResponse<List<TransactionView>>> {
        return breaker.execute(
            operation = {
                txAdvice.readOnly {
                    val accountReadViewEntity = accountViewRepository.findByAccountNumber(accountNumber)
                    if (accountReadViewEntity.isEmpty) {
                        return@readOnly ApiResponse.error("Account Not Found")
                    }

                    val transactionEntity = if (limit != null) {
                        transactionViewRepository.findByAccountNumberOrderByCreatedAtDesc(accountNumber).take(limit)
                    } else {
                        transactionViewRepository.findByAccountNumberOrderByCreatedAtDesc(accountNumber)
                    }

                    return@readOnly ApiResponse.success(transactionEntity.map { TransactionView.fromReadView(it) })
                }!!
            },
            fallback = { exception ->
                logger.warn("Get Transaction History Failed", exception)
                ApiResponse.error<List<TransactionView>>(
                    msg = "Get Transaction History Failed",
                )
            }
        )
    }

    fun allAccount(): ResponseEntity<ApiResponse<List<AccountView>>> {
        return breaker.execute(
            operation = {
                txAdvice.readOnly {
                    val response = accountViewRepository.findAll().map { AccountView.fromReadView(it) }
                    return@readOnly ApiResponse.success(response)
                }!!
            },
            fallback = { exception ->
                logger.warn("Get All Account Failed", exception)
                ApiResponse.error<List<AccountView>>(
                    msg = "Get All Account Failed",
                )
            }
        )
    }

}
