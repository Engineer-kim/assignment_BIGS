package im.bigs.pg.external.pg

import java.math.BigDecimal

data class TestPgApprovalCommand(
    val amount: BigDecimal,
    //연동을 위한 필드 추가
    val cardNumber: String,
    val birthDate: String,
    val expiry: String,
    val password: String,
)
