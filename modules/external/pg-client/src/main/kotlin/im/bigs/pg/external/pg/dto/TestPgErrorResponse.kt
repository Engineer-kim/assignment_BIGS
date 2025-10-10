package im.bigs.pg.external.pg.dto

data class TestPgErrorResponse(
    val code: Int,
    val errorCode: String,
    val message: String,
    val referenceId: String
)
