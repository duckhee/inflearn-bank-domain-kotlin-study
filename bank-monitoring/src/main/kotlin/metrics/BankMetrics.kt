package kr.co.won.bank.monitoring.metrics

import io.micrometer.core.instrument.*

import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong


@Component
class BankMetrics(
    private val meterRegistry: MeterRegistry, // micro-meter의 주용한 Interface를 제공해준다.
) {
    private val accountGauge = AtomicLong(0) // multi-instance의 경우 해당 값을 데이터 베이스 또는 다른 부분으로 처리해야 한다.

    init {
        meterRegistry.gauge("bank.account.total", accountGauge) { it.get().toDouble() }
    }

    fun incrementAccountCreated() {
        Counter.builder("bank.account.created").description("Number of accounts created").register(meterRegistry)
            .increment()
    }

    fun updateAccountCount(count: Long) {
        accountGauge.set(count)
    }

    fun incrementTransactionCreated(type: String) {
        Counter.builder("bank.transaction.created")
            .description("Number of transactions created")
            .tag("type", type) // metrics에 대한 정보가 많아지기 때문에 tag에 대한 정보를 무분별하게 사용하지 않는 게 좋다.
            .register(meterRegistry)
    }

    fun recordTransactionAmount(amount: BigDecimal, type: String) {
        // 분포 요약이라는 매트릭에 기록을 하기 위해서 사용된다. (최소 값이나 최대 값 평균 같은 것이 DistributionSummary에 포함이 된다.)
        DistributionSummary.builder("bank.transaction.amount")
            .description("Amount of transactions")
            .tag("type", type)
            .register(meterRegistry)
            .record(amount.toDouble())
    }

    fun incrementEventFailed(eventType: String) {
        Counter.builder("bank.event.failed")
            .description("Number of events failed")
            .tag("type", eventType)
            .register(meterRegistry)
            .increment()
    }

    fun recordEventProcessingTime(duration: Duration, eventType: String) {
        Timer.builder("bank.event.processing.time")
            .description("Time of event processing")
            .tag("type", eventType)
            .register(meterRegistry)
            .record(duration)
    }

    fun incrementLockAcquisitionSuccess(lockKey: String) {
        Counter.builder("bank.lock.acquisition.success")
            .description("Number of lock acquisition success")
            .tag("lock_key", lockKey)
            .register(meterRegistry)
            .increment()
    }

    fun recordApiResponseTime(duration: Duration, endPoint: String, method: String) {
        Timer.builder("bank.api.response.time")
            .description("Time of API response")
            .tag("end_point", endPoint)
            .tag("method", method)
            .register(meterRegistry)
            .record(duration)
    }

}