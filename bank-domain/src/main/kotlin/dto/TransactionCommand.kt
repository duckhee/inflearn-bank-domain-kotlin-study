package kr.co.won.bank.domain.dto

import kr.co.won.bank.domain.entity.TransactionType
import java.math.BigDecimal

data class TransactionCommand(
    val accountNumber: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val description: String,
) {

}



