package im.bigs.pg.application.payment.port.out

import com.fasterxml.jackson.annotation.JsonFormat
import im.bigs.pg.domain.payment.PaymentStatus
import java.time.LocalDateTime

data class PaymentAndSummaryParam(
    val partnerId: Long? = null,
    val status: PaymentStatus? = null,
    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val from: LocalDateTime? = null,
    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val to: LocalDateTime? = null,
    val cursor: String? = null,
    val limit: Int,
)
