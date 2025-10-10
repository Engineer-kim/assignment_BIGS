package im.bigs.pg.external.pg

import com.fasterxml.jackson.databind.ObjectMapper
import im.bigs.pg.application.pg.port.out.PgApproveResult
import im.bigs.pg.domain.payment.PaymentStatus
import im.bigs.pg.external.pg.config.TestPgProperties
import im.bigs.pg.external.pg.dto.TestPGResponse
import im.bigs.pg.external.pg.dto.TestPgEncryptionRequest
import im.bigs.pg.external.pg.dto.TestPgErrorResponse
import im.bigs.pg.external.pg.dto.TestPgPlainTextRequest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class TestPGIntegration(
    private val restTemplate: RestTemplate,
    private val pgProperties: TestPgProperties,
    private val objectMapper: ObjectMapper
) {
    fun approve(command: TestPgApprovalCommand): PgApproveResult {
        val plaintextRequest = TestPgPlainTextRequest(
            cardNumber = command.cardNumber,
            birthDate = command.birthDate,
            expiry = command.expiry,
            password = command.password,
            amount = command.amount
        )
        
        val plaintextJson = objectMapper.writeValueAsString(plaintextRequest)

        //TODO: 실제 암호화 로직 필요
        //현재는 그냥 빈값으로 둠
        val requestBody = TestPgEncryptionRequest(encryptionValue = "")
        
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("API-KEY", pgProperties.apiKey)
        }

        val url = pgProperties.baseUrl + pgProperties.endPoint

        try {
            val responseEntity = restTemplate.postForEntity(
                url,
                HttpEntity(requestBody, headers),
                TestPGResponse::class.java
            )
            
            if (responseEntity.statusCode.is2xxSuccessful && responseEntity.body != null) {
                val body = responseEntity.body!!
                val approvedInstant = Instant.parse(body.approvedAt)
                val approvedDateTime = LocalDateTime.ofInstant(approvedInstant, ZoneOffset.UTC)

                return PgApproveResult(
                    approvalCode = body.approvalCode,
                    approvedAt = approvedDateTime,
                    status = PaymentStatus.APPROVED
                )
            }
        } catch (e: HttpClientErrorException.UnprocessableEntity) {
            //422 등등
            val errorResponse = objectMapper.readValue(e.responseBodyAsString, TestPgErrorResponse::class.java)
            throw IllegalStateException("PG 쪽 422 오류: ${errorResponse.message} (${errorResponse.errorCode})")
        } catch (e: Exception) {
            //500 등등
            throw RuntimeException("큰일남 오류남", e)
        }

        throw IllegalStateException("뭔가 잘못됨")
    }

}