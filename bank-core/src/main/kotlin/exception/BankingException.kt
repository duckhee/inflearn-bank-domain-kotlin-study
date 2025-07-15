package kr.co.won.bank.core.exception


abstract class BankingException(
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class AccountNotFoundException(
    accountNumber: String
) : BankingException("Account $accountNumber not found")