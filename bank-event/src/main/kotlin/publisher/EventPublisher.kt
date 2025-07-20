package kr.co.won.bank.event.publisher

import kr.co.won.bank.domain.event.DomainEvent
import kr.co.won.bank.monitoring.metrics.BankMetrics
import org.slf4j.LoggerFactory
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
    private val metrics: BankMetrics,
) : EventPublisher {

    private val logger = LoggerFactory.getLogger(EventPublisherImpl::class.java)

    override fun publish(event: DomainEvent) {
        logger.info("Event published: $event")
        try {
            eventPublisher.publishEvent(event)

            metrics.incrementEventPublished(event::class.simpleName!! ?: "UnkownEvent")

        } catch (e: Exception) {
            logger.error("Event publish error: $event", e)
        }
    }

    @Async("taskExecutor")
    override fun publishAsync(event: DomainEvent) {
        logger.info("async Event published: $event")
        try {
            eventPublisher.publishEvent(event)

            metrics.incrementEventPublished(event::class.simpleName!! ?: "UnkownEvent")
        } catch (e: Exception) {
            logger.error("async Event publish error: $event", e)
        }
    }

    override fun publishAll(events: List<DomainEvent>) {
        logger.info("Events published: $events")
        events.forEach { event ->
            {
                try {
                    eventPublisher.publishEvent(event)

                    metrics.incrementEventPublished(event::class.simpleName!! ?: "UnkownEvent")
                } catch (e: Exception) {
                    logger.error("Events publish error: $events", e)
                }
            }
        }
    }

    @Async("taskExecutor")
    override fun publishAllAsync(events: List<DomainEvent>) {
        logger.info("async Events published: $events")
        events.forEach { event ->
            {
                try {
                    eventPublisher.publishEvent(event)

                    metrics.incrementEventPublished(event::class.simpleName!! ?: "UnkownEvent")
                } catch (e: Exception) {
                    logger.error("async Events publish error: $events", e)
                }
            }
        }
    }

}