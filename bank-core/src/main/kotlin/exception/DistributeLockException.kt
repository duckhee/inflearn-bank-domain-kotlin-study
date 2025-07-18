package kr.co.won.bank.core.exception

class LockAcquireFailedException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {

}