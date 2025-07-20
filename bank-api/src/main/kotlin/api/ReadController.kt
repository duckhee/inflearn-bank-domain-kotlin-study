package kr.co.won.bank.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.won.bank.common.ApiResponse
import kr.co.won.bank.domain.dto.AccountView
import kr.co.won.bank.domain.dto.TransactionView
import kr.co.won.bank.service.AccountReadService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@RestController
@RequestMapping(path = ["/api/v1/read"])
@Tag(name = "Read API", description = "read operation")
class ReadController(
    private val accountReadService: AccountReadService,
) {

    private val logger = LoggerFactory.getLogger(ReadController::class.java)


    @Operation(
        summary = "accountNumber api",
        description = "accountNumber api",
        responses = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = AccountView::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "Account Not Found",
            )
        ]
    )
    @GetMapping("/{accountNumber}")
    fun getAccount(
        @Parameter(description = "Account number", required = true)
        @PathVariable accountNumber: String
    ) : ResponseEntity<ApiResponse<AccountView>> {
        logger.info("Getting account $accountNumber")
        return accountReadService.getAccount(accountNumber)
    }

    @Operation(
        summary = "Get transaction history",
        description = "Retrieves the transaction history for a specific account",
        responses = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "Transaction history retrieved successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = TransactionView::class))]
            ),
            SwaggerApiResponse(
                responseCode = "404",
                description = "Account not found"
            )
        ]
    )
    @GetMapping("/{accountNumber}/transactions")
    fun getTransactionHistory(
        @Parameter(description = "Account number to retrieve transactions for", required = true)
        @PathVariable accountNumber: String,
        @Parameter(description = "Maximum number of transactions to return")
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<ApiResponse<List<TransactionView>>> {
        logger.info("Getting transaction history for account: $accountNumber")
        return accountReadService.transactionHistory(accountNumber, limit)
    }

    @Operation(
        summary = "Get all accounts",
        description = "Retrieves a list of all accounts in the system"
    )
    @GetMapping("/all")
    fun getAllAccounts(): ResponseEntity<ApiResponse<List<AccountView>>> {
        logger.info("Getting all accounts")
        return accountReadService.allAccount()
    }
}