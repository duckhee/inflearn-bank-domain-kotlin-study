package kr.co.won.bank.domain.dto

import java.math.BigDecimal


data class TransactionResult(
    val transactionId: Long,
    val accountNumber: String,
    val newBalance: BigDecimal
) {

}

