package kr.co.won.bank.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime


@Entity
@Table(name = "tbl_account_read_view")
data class AccountReadView(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @Column(nullable = false) val accountNumber: String,
    @Column(nullable = false) val accountHolderName: String,
    // 19개의 숫자와 소수점 2번쨰 자리 까지 저장한다.
    @Column(nullable = false, precision = 19, scale = 2) val balance: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false) val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false) val lastUpdatedAt: LocalDateTime = LocalDateTime.now(),
    // 읽기 최적화를 위한 추가 필드
    @Column(nullable = false) val transactionCount: Int = 0,
    @Column(precision = 19, scale = 2) val totalDeposit: BigDecimal = BigDecimal.ZERO,
    @Column(precision = 19, scale = 2) val totalWithdrawals: BigDecimal = BigDecimal.ZERO,
) {

}

