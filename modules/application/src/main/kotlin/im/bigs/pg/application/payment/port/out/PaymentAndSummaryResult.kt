package im.bigs.pg.application.payment.port.out

import im.bigs.pg.domain.payment.Payment

data class PaymentAndSummaryResult(
    val items: List<Payment>,
    val summary: PaymentSummaryProjection,
    val nextCursor: String? = null,
    val hasNext: Boolean
)
