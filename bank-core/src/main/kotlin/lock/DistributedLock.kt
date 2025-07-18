package kr.co.won.bank.core.lock

import kr.co.won.bank.core.exception.LockAcquireFailedException
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit


@ConfigurationProperties(prefix = "bank.lock")
data class LockProperties(
    val timeout: Long = 5000,
    val releaseTime: Long = 10000,
    val retryInterval: Long = 100,
    val maxRetryAttempts: Long = 50,
) {}


@Service
@EnableConfigurationProperties(LockProperties::class)
class DistributedLockService(
    private val lockProperties: LockProperties,
    private val redissonClient: RedissonClient,
) {
    private val logger = getLogger(DistributedLockService::class.java)

    private fun <T> executeWithLock(lockKey: String, action: () -> T): T {
        val lock = redissonClient.getLock(lockKey)
        return try {
            val acquired = lock.tryLock(
                lockProperties.timeout,
                TimeUnit.SECONDS
            )
            // lock 획득 실패 시
            if (!acquired) {
                logger.info("Failed to acquire lock for key: $lockKey")
                throw LockAcquireFailedException("Acquiring lock for key $lockKey failed")
            }
            try {
                action()
            } finally {
                if (lock.isHeldByCurrentThread) {
                    lock.unlock()
                }
            }
        } catch (e: Exception) {
            logger.error("Exception occurred while executing with lock", e)
            throw e
        }
    }

    fun <T> executeWithAccountLock(accountNumber: String, action: () -> T): T {
        val key = "account:lock:$accountNumber"
        return executeWithLock(key, action)
    }

    fun <T> executeWithTransactionLock(from: String, to: String, action: () -> T): T {
        val sortedAccount = listOf(from, to).sorted()
        val lockKey = "transaction:lock:${sortedAccount[0]}:${sortedAccount[1]}"
        return executeWithLock(lockKey, action)
    }
}