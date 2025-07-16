package kr.co.won.bank.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime


@Entity
@Table(name = "tbl_transaction_read_view")
class TransactionReadView(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @Column(nullable = false) val accountId: Long = 0,
    @Column(nullable = false) val accountNumber: String = "",
    @Column(nullable = false) val accountHolderName: String = "",
    @Column(nullable = false) @Enumerated(EnumType.STRING) val type: TransactionType = TransactionType.DEPOSIT,
    @Column(nullable = false, precision = 19, scale = 2) val amount: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false) val description: String = "",
    @Column(nullable = false) val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false, precision = 19, scale = 2) val balanceAfter: BigDecimal = BigDecimal.ZERO,
)