package im.bigs.pg.external.pg.service

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object TestPgValueEncryption {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 16 // 128비트니까
    private const val GCM_IV_LENGTH = 12 // 96비트니

    private val BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding()
    private val BASE64_URL_DECODER = Base64.getUrlDecoder()
    fun encrypt(
        plainText: String,
        secretKeyBase64: String,
        ivBase64: String
    ): String {

        val keyBytes = MessageDigest.getInstance("SHA-256").digest(secretKeyBase64.toByteArray(StandardCharsets.UTF_8))
        val ivBytes = BASE64_URL_DECODER.decode(ivBase64)

        if (ivBytes.size != GCM_IV_LENGTH) {
            throw IllegalArgumentException("디코드 크기 이상함")
        }

        val secretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance(ALGORITHM)
        val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, ivBytes)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec)

        val cipherTextWithTag = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        return BASE64_URL_ENCODER.encodeToString(cipherTextWithTag)
    }
}
