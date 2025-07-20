package kr.co.won.bank.domain.repository

import kr.co.won.bank.domain.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface AccountRepository : JpaRepository<Account, Long> {

    fun findByAccountNumber(accountNumber: String): Account?

}

