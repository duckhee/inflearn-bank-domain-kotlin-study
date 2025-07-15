package kr.co.won.bank.exception

import kr.co.won.bank.common.ApiResponse
import kr.co.won.bank.common.ApiResponse.Companion.exceptionError
import kr.co.won.bank.core.exception.AccountNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest


@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFoundHandler(
        exception: AccountNotFoundException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Account not found ", exception)
        val response: ApiResponse<Nothing> = exceptionError<Nothing>(
            msg = exception.message ?: "Account not found",
            errorCode = "Account not found",
            path = getPath(request)
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }


    private fun getPath(request: WebRequest): String? {
        return request.getDescription(false).removePrefix("uri=")
            .takeIf { it.isNotBlank() }
    }
}