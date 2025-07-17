package kr.co.won.bank.event.listener

import kr.co.won.bank.domain.event.AccountCreateEvent
import kr.co.won.bank.domain.event.TransactionCreateEvent
import kr.co.won.bank.domain.repository.AccountReadViewRepository
import kr.co.won.bank.domain.repository.AccountRepository
import kr.co.won.bank.domain.repository.TransactionReadViewRepository
import kr.co.won.bank.domain.repository.TransactionRepository
import org.slf4j.LoggerFactory.*
import org.springframework.context.event.EventListener
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.retry.annotation.Backoff


@Component
class EventReader(
    private val accountRepository: AccountRepository,
    private val accountReadViewRepository: AccountReadViewRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionReadViewRepository: TransactionReadViewRepository
    // TODO matrics, txAdvice
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

    }

    @EventListener
    @Async("taskExecutor")
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000)
    ) // 에러가 발생 시에 재 시도 3번까지 진행 후 최대 1000초 대기 후 다음 시도
    fun handleTransactionCreated(event: TransactionCreateEvent) {

    }

}
