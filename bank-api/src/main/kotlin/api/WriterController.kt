package kr.co.won.bank.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.won.bank.common.ApiResponse
import kr.co.won.bank.domain.dto.AccountView
import kr.co.won.bank.service.AccountWriteService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

import java.math.BigDecimal


data class CreateAccountRequest (
    val name : String,
    val initialBalance : BigDecimal,
)


@RestController
@RequestMapping(path = ["/api/v1/write"])
@Tag(name = "Write API", description = "write operation")
class WriterController(
    private val accountWriteService: AccountWriteService,
) {

    private val logger = LoggerFactory.getLogger(WriterController::class.java)

    @Operation(
        summary = "Create new account",
        description = "Creates a new bank account with the specified holder name and initial balance"
    )
    @PostMapping
    fun createAccount(
        @RequestBody request: CreateAccountRequest
    ): ResponseEntity<ApiResponse<AccountView>> {
        logger.info("Creating account for: ${request.name} with initial balance: ${request.initialBalance}")
        return accountWriteService.createAccount(request.name, request.initialBalance)
    }

    @Operation(
        summary = "Transfer money",
        description = "Transfers the specified amount from one account to another",
        responses = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "Transfer completed successfully"
            ),
            SwaggerApiResponse(
                responseCode = "404",
                description = "Account not found"
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "Invalid amount or insufficient funds"
            )
        ]
    )
    @PostMapping("/transfer")
    fun transfer(
        @Parameter(description = "Source account number", required = true)
        @RequestParam fromAccountNumber: String,
        @Parameter(description = "Destination account number", required = true)
        @RequestParam toAccountNumber: String,
        @Parameter(description = "Amount to transfer", required = true)
        @RequestParam amount: BigDecimal
    ): ResponseEntity<ApiResponse<String>> {
        logger.info("Transferring $amount from $fromAccountNumber to $toAccountNumber")
        return accountWriteService.transfer(fromAccountNumber, toAccountNumber, amount)
    }

}