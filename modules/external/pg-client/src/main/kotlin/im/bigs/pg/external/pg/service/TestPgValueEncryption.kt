package im.bigs.pg.external.pg.service

import java.nio.charset.StandardCharsets
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import javax.crypto.Cipher

object TestPgValueEncryption {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 16 //128비트니까
    private const val GCM_IV_LENGTH = 12 //96비트니

    private val BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding()
    private val BASE64_URL_DECODER = Base64.getUrlDecoder()
    fun encrypt(
        plainText: String,
        secretKeyBase64: String,
        ivBase64: String
    ): String {
        
        //바이트데이터로 변환해야함
        val keyBytes = BASE64_URL_DECODER.decode(secretKeyBase64)
        val ivBytes = BASE64_URL_DECODER.decode(ivBase64)

        if (ivBytes.size != GCM_IV_LENGTH) {
            throw IllegalArgumentException("IV length must be $GCM_IV_LENGTH bytes (96 bits)")
        }

        val secretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance(ALGORITHM)
        val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, ivBytes) // Tag length는 비트 단위 (128)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec)

        val cipherTextWithTag = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        val finalResult = ivBytes + cipherTextWithTag
        return BASE64_URL_ENCODER.encodeToString(finalResult)
    }
}