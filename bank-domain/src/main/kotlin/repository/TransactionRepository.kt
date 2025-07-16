package kr.co.won.bank.domain.repository

import kr.co.won.bank.domain.entity.Account
import kr.co.won.bank.domain.entity.Transaction
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findByAccountOrderByCreatedAtDesc(account: Account): List<Transaction>
    fun findTopByAccountOrderByCreatedAtDesc(account: Account, pageable: Pageable): List<Transaction>
}