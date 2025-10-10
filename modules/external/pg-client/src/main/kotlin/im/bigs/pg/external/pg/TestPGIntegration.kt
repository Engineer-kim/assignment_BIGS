package im.bigs.pg.external.pg

import com.fasterxml.jackson.databind.ObjectMapper
import im.bigs.pg.application.payment.port.out.TestPgApprovalCommand
import im.bigs.pg.application.pg.port.out.PgApproveResult
import im.bigs.pg.application.pg.port.out.TestPgClientOutPort
import im.bigs.pg.domain.payment.PaymentStatus
import im.bigs.pg.external.pg.config.TestPgProperties
import im.bigs.pg.external.pg.dto.TestPGResponse
import im.bigs.pg.external.pg.dto.TestPgEncryptionRequest
import im.bigs.pg.external.pg.dto.TestPgErrorResponse
import im.bigs.pg.external.pg.dto.TestPgPlainTextRequest
import im.bigs.pg.external.pg.service.TestPgValueEncryption
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime


@Component
class TestPGIntegration(
    private val restTemplate: RestTemplate,
    private val pgProperties: TestPgProperties,
    private val objectMapper: ObjectMapper,
) : TestPgClientOutPort {

    override fun supports(partnerId: Long): Boolean = partnerId % 2L == 1L

    override fun approve(command: TestPgApprovalCommand): PgApproveResult {

        val plaintextRequest = TestPgPlainTextRequest(
            cardNumber = command.cardNumber,
            birthDate = command.birthDate,
            expiry = command.expiry,
            password = command.password,
            amount = command.amount
        )

        val plaintextJson = objectMapper.writeValueAsString(plaintextRequest)

        println("[DEBUG] PG 요청 평문 JSON: $plaintextJson")

        // TODO: 실제 암호화 로직 필요 => 했음
        // 현재는 그냥 빈값으로 둠  => 했음

        val ecryptPlainText = TestPgValueEncryption.encrypt(
            plainText = plaintextJson,
            secretKeyBase64 = pgProperties.apiKey,
            ivBase64 = pgProperties.ivValue
        )

        val requestBody = TestPgEncryptionRequest(enc = ecryptPlainText)

        val jsonString = objectMapper.writeValueAsString(requestBody)
        println("암호화한뒤 로직: $jsonString")
        
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("API-KEY", pgProperties.apiKey)
        }
        println("[DEBUG] PG API-KEY는? ${pgProperties.apiKey}")


        val url = pgProperties.baseUrl + pgProperties.endPoint

        try {
            val responseEntity = restTemplate.postForEntity(
                url,
                HttpEntity(requestBody, headers),
                TestPGResponse::class.java
            )
            println("[DEBUG] responseEntity::::::::::::::::::::::: ${responseEntity}")
            if (responseEntity.statusCode.is2xxSuccessful && responseEntity.body != null) {
                val body = responseEntity.body!!
                val approvedDateTime = LocalDateTime.parse(body.approvedAt)

                val status = when (body.status) {
                    "APPROVED" -> PaymentStatus.APPROVED
                    "CANCELLED" -> PaymentStatus.CANCELED
                    else -> throw IllegalStateException("알수없는 PG 상태값: ${body.status}")
                }

                return PgApproveResult(
                    approvalCode = body.approvalCode,
                    approvedAt = approvedDateTime,
                    status = status,
                )
            }
        } catch (e: HttpClientErrorException.BadRequest) {
            println("PG 400 응답 본문: ${e.responseBodyAsString}")
            throw RuntimeException("큰일남 오류남", e)
        } catch (e: HttpClientErrorException.UnprocessableEntity) {
            // 422 등등
            val errorResponse = objectMapper.readValue(e.responseBodyAsString, TestPgErrorResponse::class.java)
            throw IllegalStateException("PG 쪽 422 오류: ${errorResponse.message} (${errorResponse.errorCode})")
        } catch (e: Exception) {
            // 500 등등
            println("e:::::::::::::::::::::::::::::::::::::: $e")
            throw RuntimeException("큰일남 오류남222", e)
        }

        throw IllegalStateException("뭔가 잘못됨 진짜 큰일남")
    }
}
