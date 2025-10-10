package im.bigs.pg.external.pg.dto

import java.math.BigDecimal

data class TestPGResponse(
    val approvalCode: String,
    val approvedAt: String,
    val maskedCardLast4: String,
    val amount: BigDecimal,
    val status: String
)
