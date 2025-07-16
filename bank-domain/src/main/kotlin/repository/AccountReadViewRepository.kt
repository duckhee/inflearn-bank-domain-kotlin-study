package kr.co.won.bank.domain.repository

import kr.co.won.bank.domain.entity.AccountReadView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*


@Repository
interface AccountReadViewRepository : JpaRepository<AccountReadView, Long> {

    fun findByAccountNumber(accountNumber: String): Optional<AccountReadView>

    @Query("SELECT a FROM AccountReadView a ORDER BY a.balance DESC")
    fun findAllOrdersByBalanceDesc(): List<AccountReadView>

    @Query("SELECT a FROM AccountReadView a WHERE a.balance >= :minBalance")
    fun findByBalanceGreaterThanEqual(@Param("minBalance") balance: BigDecimal): List<AccountReadView>


    @Query("SELECT a FROM AccountReadView  a WHERE a.accountHolderName LIKE %:name%")
    fun findByAccountHolderNameContains(@Param("name") name: String): List<AccountReadView>
}