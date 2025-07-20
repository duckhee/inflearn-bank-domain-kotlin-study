package kr.co.won.bank.domain.dto

import kr.co.won.bank.domain.entity.Account
import kr.co.won.bank.domain.entity.AccountReadView
import kr.co.won.bank.domain.entity.Transaction
import kr.co.won.bank.domain.entity.TransactionReadView
import kr.co.won.bank.domain.entity.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

// 읽기 모델 -> 읽을 때 값을 해당 형태로 만들어서 반환한다.
data class AccountView(
    val id: Long,
    val accountNumber: String,
    val balance: BigDecimal,
    val accountHolderName: String,
    val createdAt: LocalDateTime,
) {

    companion object {
        fun from(account: Account) = AccountView(
            id = account.id!!,
            accountNumber = account.accountNumber,
            balance = account.balance,
            accountHolderName = account.accountHolderName,
            createdAt = account.createdAt
        )

        fun fromReadView(account: AccountReadView) = AccountView(
            id = account.id!!,
            accountNumber = account.accountNumber,
            balance = account.balance,
            accountHolderName = account.accountHolderName,
            createdAt = account.createdAt
        )
    }
}

data class TransactionView(
    val id: Long,
    val accountId: Long,
    val accountNumber: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val description: String,
    val createdAt: LocalDateTime,
    val balanceAfter: BigDecimal,
) {
    companion object {
        fun from(transaction: Transaction) = TransactionView(
            id = transaction.id!!,
            accountId = transaction.account.id!!,
            accountNumber = transaction.account.accountNumber,
            amount = transaction.amount,
            type = transaction.type,
            description = transaction.description,
            createdAt = transaction.createdAt,
            balanceAfter = transaction.account.balance
        )

        fun fromReadView(transaction: TransactionReadView) = TransactionView(
            id = transaction.id!!,
            accountId = transaction.accountId!!,
            accountNumber = transaction.accountNumber,
            amount = transaction.amount,
            type = transaction.type,
            description = transaction.description,
            createdAt = transaction.createdAt,
            balanceAfter = transaction.balanceAfter
        )
    }

}

data class AccountBalanceView(
    val accountNumber: String,
    val balance: BigDecimal,
    val accountHolderName: String,
    val lastUpdatedAt: LocalDateTime,
) {
    companion object {
        fun from(account: Account) = AccountBalanceView(
            accountNumber = account.accountNumber,
            balance = account.balance,
            accountHolderName = account.accountHolderName,
            lastUpdatedAt = account.createdAt
        )
    }
}