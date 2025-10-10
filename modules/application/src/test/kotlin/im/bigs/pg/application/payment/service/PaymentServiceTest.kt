package im.bigs.pg.application.payment.service

class 결제서비스Test {
//    private val partnerRepo = mockk<PartnerOutPort>()
//    private val feeRepo = mockk<FeePolicyOutPort>()
//    private val paymentRepo = mockk<PaymentOutPort>()
//    private val pgClient = object : PgClientOutPort {
//        override fun supports(partnerId: Long) = true
//        override fun approve(request: PgApproveRequest) =
//            PgApproveResult("APPROVAL-123", LocalDateTime.of(2024, 1, 1, 0, 0), PaymentStatus.APPROVED)
//    }
//    private lateinit var paymentService: PaymentService
//    private lateinit var queryService: QueryPaymentsService
//
//    @BeforeEach
//    fun setUp() {
//        paymentService = PaymentService(partnerRepo, feeRepo, paymentRepo, listOf(pgClient))
//        queryService = QueryPaymentsService(paymentRepo)
//    }
//
//    @Test
//    @DisplayName("결제 시 수수료 정책을 적용하고 저장해야 한다")
//    fun `결제 시 수수료 정책을 적용하고 저장해야 한다`() {
//        val service = PaymentService(partnerRepo, feeRepo, paymentRepo, listOf(pgClient))
//        every { partnerRepo.findById(1L) } returns Partner(1L, "TEST", "Test", true)
//        every { feeRepo.findEffectivePolicy(1L, any()) } returns FeePolicy(
//            id = 10L, partnerId = 1L, effectiveFrom = Instant.parse("2020-01-01T00:00:00Z"),
//            percentage = BigDecimal("0.0300"), fixedFee = BigDecimal("100")
//        )
//        val savedSlot = slot<Payment>()
//        every { paymentRepo.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 99L) }
//
//        val cmd = PaymentCommand(partnerId = 1L, amount = BigDecimal("10000"), cardLast4 = "4242")
//        val res = service.pay(cmd)
//
//        assertEquals(99L, res.id)
//        assertEquals(BigDecimal("400"), res.feeAmount)
//        assertEquals(BigDecimal("9600"), res.netAmount)
//        assertEquals(PaymentStatus.APPROVED, res.status)
//    }
//
//    @Test
//    fun `paymentAndSummary test입니다`() {
//        val inputQuery = PaymentQuery(
//            partnerId = 1L,
//            status = PaymentStatus.APPROVED,
//            from = LocalDateTime.of(2025, 10, 9, 0, 0),
//            to = LocalDateTime.of(2025, 10, 31, 23, 59),
//            limit = 10,
//            cursorCreatedAt = LocalDateTime.of(2025, 10, 11, 12, 0),
//            cursorId = 100L
//        )
//
//        val mockSummary = PaymentSummaryProjection(
//            count = 50L,
//            totalAmount = BigDecimal("50000.00"),
//            totalNetAmount = BigDecimal("48000.00")
//        )
//        val mockItems = listOf<Payment>()
//        val expectedResult = PaymentAndSummaryResult(
//            items = mockItems,
//            summary = mockSummary,
//            nextCursor = "new-cursor-101",
//            hasNext = true
//        )
//
//        every { paymentRepo.paymentAndSummary(any()) } returns expectedResult
//
//        val actualResult = queryService.paymentAndSummary(inputQuery)
//
//        verify(exactly = 1) { paymentRepo.paymentAndSummary(any()) }
//
//        assertEquals(expectedResult.summary.count, actualResult.summary.count)
//        assertEquals(expectedResult.summary.totalAmount, actualResult.summary.totalAmount)
//        assertEquals(expectedResult.nextCursor, actualResult.nextCursor)
//        assertEquals(expectedResult.hasNext, actualResult.hasNext)
//    }
}
