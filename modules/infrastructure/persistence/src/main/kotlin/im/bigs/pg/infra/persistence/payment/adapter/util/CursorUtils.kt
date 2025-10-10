package im.bigs.pg.infra.persistence.payment.adapter.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

object CursorUtils {
    private const val CURSOR_DELIMITER = "_"
    private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

    fun encode(createdAt: LocalDateTime, id: Long): String {
        val timestamp = createdAt.format(DATE_FORMATTER)
        val plainCursor = "$timestamp$CURSOR_DELIMITER$id"
        return Base64.getEncoder().encodeToString(plainCursor.toByteArray())
    }

    fun decode(cursor: String?): Pair<LocalDateTime?, Long?> {
        if (cursor.isNullOrBlank()) {
            return Pair(null, null)
        }

        return try {
            val decodedBytes = Base64.getDecoder().decode(cursor)
            val plainCursor = String(decodedBytes)
            val parts = plainCursor.split(CURSOR_DELIMITER)

            if (parts.size != 2) throw IllegalArgumentException("Invalid cursor format")

            val createdAt = LocalDateTime.parse(parts[0], DATE_FORMATTER)
            val id = parts[1].toLong()
            Pair(createdAt, id)
        } catch (e: Exception) {
            System.out.println("무언가 잘못됨 원인은 --> ${e.message}")
            Pair(null, null)
        }
    }
}
