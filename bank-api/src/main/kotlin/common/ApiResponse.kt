package kr.co.won.bank.common

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.ResponseEntity


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: Error? = null
) {

    companion object {
        fun <T> success(data: T, msg: String = "Success"): ResponseEntity<ApiResponse<T>> {
            return ResponseEntity.ok(ApiResponse<T>(true, msg, data))
        }

        fun <T> error(
            msg: String,
            errorCode: String? = null,
            detail: Any? = null,
            path: String? = null
        ): ResponseEntity<ApiResponse<T>> {
            return ResponseEntity.badRequest().body(
                ApiResponse<T>(
                    false,
                    msg,
                    data = null,
                    error = Error(errorCode, detail, path)
                )
            )
        }

        fun <T> exceptionError(
            msg: String,
            errorCode: String? = null,
            detail: Any? = null,
            path: String? = null
        ): ApiResponse<T> {
            return ApiResponse(
                false,
                msg,
                data = null,
                error = Error(errorCode, detail, path)
            )
        }
    }


}

data class Error(
    val code: String? = null,
    val detail: Any? = null,
    val path: String? = null
) {

}