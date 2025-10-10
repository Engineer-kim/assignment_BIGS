package im.bigs.pg.external.pg

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import im.bigs.pg.application.payment.port.out.TestPgApprovalCommand
import im.bigs.pg.domain.payment.PaymentStatus
import im.bigs.pg.external.pg.config.TestPgProperties
import im.bigs.pg.external.pg.dto.TestPGResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class `PG 연동 테스트`{
    private lateinit var restTemplate: RestTemplate
    private lateinit var mockServer: MockRestServiceServer
    private lateinit var objectMapper: ObjectMapper
    private lateinit var pgProperties: TestPgProperties
    private lateinit var integration: TestPGIntegration


    private val TEST_BASE_URL = "http://testpg.com"
    private val TEST_ENDPOINT = "/approve"
    private val TEST_API_KEY = "test-api-key-uuid"
    private val TEST_IV_VALUE = "AAAAAAAAAAAAAAAA"

    // 테스트용 민감 정보 커맨드
    private val command = TestPgApprovalCommand(
        amount = BigDecimal("10000.00"),
        cardNumber = "4444555566667777",
        birthDate = "19900101",
        expiry = "2512",
        password = "1234"
    )

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        restTemplate = RestTemplate()
        mockServer = MockRestServiceServer.createServer(restTemplate)

        pgProperties = TestPgProperties(
            baseUrl = TEST_BASE_URL,
            endPoint = TEST_ENDPOINT,
            apiKey = TEST_API_KEY,
            ivValue = TEST_IV_VALUE,
        )
        integration = TestPGIntegration(restTemplate, pgProperties, objectMapper)
    }

    @Test
    fun `approve 성공 테스트`() {
        val approvedAtTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))
        val approvalCode = "TEST123456"

        val mockResponse = TestPGResponse(
            approvalCode = approvalCode,
            approvedAt = approvedAtTime,
            maskedCardLast4 = "7777",
            amount = command.amount.toInt(),
            status = "APPROVED" // ⬅️ 응답 상태
        )
        mockServer.expect(requestTo(pgProperties.baseUrl + pgProperties.endPoint))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("API-KEY", pgProperties.apiKey))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(mockResponse)))
        val result = integration.approve(command)

        mockServer.verify()
        assertEquals(approvalCode, result.approvalCode)
        assertEquals(PaymentStatus.APPROVED, result.status)
    }


    @Test
    fun `approve 422 한도 초과`() {
        val errorCode = "INSUFFICIENT_LIMIT"
        val errorMessage = "한도가 초과되었습니다."
        val errorBody = """{
            "code": 1002,
            "errorCode": "$errorCode", 
            "message": "$errorMessage",
            "referenceId": "b48c79bd-e1b3-416a-a583-efe90d1ee438"
        }"""

        mockServer.expect(requestTo(pgProperties.baseUrl + pgProperties.endPoint))
            .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorBody))

        val exception = assertThrows<IllegalStateException> {
            integration.approve(command)
        }

        mockServer.verify()

        assertTrue(exception.message!!.contains(errorMessage))
        assertTrue(exception.message!!.contains(errorCode))
    }

    @Test
    fun `approve 401 API-KEY 헤더 없음`() {
        mockServer.expect(requestTo(pgProperties.baseUrl + pgProperties.endPoint))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED))

        val runtimeException = assertThrows<RuntimeException> {
            integration.approve(command)
        }

        mockServer.verify()

        val cause = runtimeException.cause
        assertNotNull(cause)
        assertTrue(cause is HttpClientErrorException, "원인 예외는 HttpClientErrorException 계열이어야 합니다.")
        assertTrue(runtimeException.message!!.contains("큰일남 오류남"))
    }

    @Test
    fun `approve 401 API-KEY 포맷 오류`() {
        val expectedApiKey = pgProperties.apiKey

        //어떤식으로든 API-KEY가 잘못된 상황을 시뮬레이션
        mockServer.expect(requestTo(pgProperties.baseUrl + pgProperties.endPoint))
            .andExpect(header("API-KEY", expectedApiKey))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED))

        val runtimeException = assertThrows<RuntimeException> {
            integration.approve(command)
        }

        mockServer.verify()
        val cause = runtimeException.cause
        assertNotNull(cause)
        assertTrue(cause is HttpClientErrorException, "아무튼 오류남(401 API-KEY 포맷 오류)")
        assertTrue(runtimeException.message!!.contains("큰일남 오류남"))
    }

    @Test
    fun `approve 401 미등록 API-KEY`() {
        val expectedApiKey = pgProperties.apiKey

        mockServer.expect(requestTo(pgProperties.baseUrl + pgProperties.endPoint))
            .andExpect(header("API-KEY", expectedApiKey))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED))

        val runtimeException = assertThrows<RuntimeException> {
            integration.approve(command)
        }

        mockServer.verify()

        assertTrue(runtimeException.message!!.contains("큰일남 오류남"))
        val cause = runtimeException.cause
        assertNotNull(cause)
        assertTrue(cause is HttpClientErrorException, "큰일남 오류남")
    }
}