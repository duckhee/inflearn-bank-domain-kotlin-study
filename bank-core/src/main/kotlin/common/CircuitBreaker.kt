package kr.co.won.bank.core.common

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CircuitBreakerConfiguration {

    @Bean
    fun circuitBreaker(): CircuitBreakerRegistry {
        val config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50f) // 실패율에 대한 임계값 설정
            .waitDurationInOpenState(java.time.Duration.ofSeconds(30)) // 오류 상홍에서 대기할 시간
            .permittedNumberOfCallsInHalfOpenState(3) // Half-Open 상태에서 재시도할 횟수 설정
            .slidingWindowSize(5) // 실패율을 계산하기 위해서 사용할 최근 갯수 설정
            .minimumNumberOfCalls(3) // 3번 호출 한 다음에 실행하는 형태
            .build()

        return CircuitBreakerRegistry.of(config)
    }
}

object CircuitBreakerUtils {

    fun <T> CircuitBreaker.execute(
        operation: () -> T,
        fallback: (Exception) -> T,
    ): T {
        return try {
            val supplier = CircuitBreaker.decorateSupplier(this) { operation() }
            supplier.get()
        } catch (e: Exception) {
            fallback(e)
        }
    }
}