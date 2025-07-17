package kr.co.won.bank.event.publisher

import com.sun.org.slf4j.internal.Logger
import com.sun.org.slf4j.internal.LoggerFactory
import kr.co.won.bank.domain.event.DomainEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component


interface EventPublisher {

    fun publish(event: DomainEvent)

    fun publishAsync(event: DomainEvent)

    fun publishAll(events: List<DomainEvent>)

    fun publishAllAsync(events: List<DomainEvent>)
}


@Component
class EventPublisherImpl(
    private val eventPublisher: ApplicationEventPublisher, // spring에서 제공하는 Event Publisher -> Application 내부에서 이벤트 발행 및 처리하기 위한 부분 외부의 시스템과 처리하기 위해서는 Redis나 RabbitMQ, Kafka를 이용해서 처리해야 한다.
) : EventPublisher {

    private val logger: Logger = LoggerFactory.getLogger(EventPublisherImpl::class.java)

    override fun publish(event: DomainEvent) {
        eventPublisher.publishEvent(event)
    }

    @Async("taskExecutor")
    override fun publishAsync(event: DomainEvent) {
        eventPublisher.publishEvent(event)
    }

    override fun publishAll(events: List<DomainEvent>) {
        events.forEach { event -> eventPublisher.publishEvent(event) }
    }

    @Async("taskExecutor")
    override fun publishAllAsync(events: List<DomainEvent>) {
        events.forEach { event -> eventPublisher.publishEvent(event) }
    }

}