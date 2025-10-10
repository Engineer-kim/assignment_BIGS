package im.bigs.pg.external.pg.dto

import java.math.BigDecimal

data class TestPgPlainTextRequest(
    val cardNumber: String,
    val birthDate: String,
    val expiry: String,
    val password: String,
    val amount: BigDecimal
)
