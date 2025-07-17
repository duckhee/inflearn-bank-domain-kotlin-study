package kr.co.won.bank.event.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor


@EnableAsync
@Configuration
class AsyncConfiguration {

    @Bean(name = ["taskExecutor"])
    fun taskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2 // 평송에 유지하는 쓰레드 개수 설정
        executor.maxPoolSize = 5 // 최대로 동작할 쓰레드의 수
        executor.queueCapacity = 100 // 작업에 대한 큐 사이즈
        executor.setThreadNamePrefix("bank-event-")
        executor.initialize()
        return executor
    }
}

