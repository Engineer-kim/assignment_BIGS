package im.bigs.pg.domain.calculation

import im.bigs.pg.domain.partner.FeePolicy
import org.junit.jupiter.api.DisplayName
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class 수수료계산기Test {

    private val TEST_TIME = Instant.parse("2025-10-09T04:40:00Z")
    @Test
    @DisplayName("퍼센트 수수료만 적용 시 반올림 및 정산금이 정확해야 한다")
    fun `퍼센트 수수료만 적용 시 반올림 및 정산금이 정확해야 한다`() {
        val amount = BigDecimal("10000")
//        val rate = BigDecimal("0.0235")
        val policy = FeePolicy(
            partnerId = 1L,
            effectiveFrom = TEST_TIME,
            // 기존 테스트코드와 동일하게
            percentage = BigDecimal("0.0235"),
            fixedFee = null
        )
        val (fee, net) = FeeCalculator.calculateFee(amount, policy = policy)
        assertEquals(BigDecimal("235"), fee)
        assertEquals(BigDecimal("9765"), net)
    }

    @Test
    @DisplayName("퍼센트+정액 수수료가 함께 적용되어야 한다")
    fun `퍼센트와 정액 수수료가 함께 적용되어야 한다`() {
        val amount = BigDecimal("10000")
//        val rate = BigDecimal("0.0300")
//        val fixed = BigDecimal("100")
        val policy = FeePolicy(
            partnerId = 1L,
            effectiveFrom = TEST_TIME,
            // 기존 테스트코드와 동일하게
            percentage = BigDecimal("0.0300"),
            fixedFee = BigDecimal("100")
        )
        val (fee, net) = FeeCalculator.calculateFee(amount, policy = policy)
        assertEquals(BigDecimal("400"), fee)
        assertEquals(BigDecimal("9600"), net)
    }
}
