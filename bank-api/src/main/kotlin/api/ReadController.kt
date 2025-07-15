package kr.co.won.bank.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.log

@RestController
@RequestMapping(path = ["/api/v1/read"])
@Tag(name = "Read API", description = "read operation")
class ReadController {

    private val logger = LoggerFactory.getLogger(ReadController::class.java)

    @Operation(
        summary = "account number api", description = "account number api", responses = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "successful operation",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @GetMapping("/{accountNumber}")
    fun getAccount(
        @Parameter(
            description = "Account number",
            required = true
        ) // Swagger에 대한 설정
        @PathVariable(name = "accountNumber") accountNumber: String
    ) {
        logger.info(accountNumber)
    }
}