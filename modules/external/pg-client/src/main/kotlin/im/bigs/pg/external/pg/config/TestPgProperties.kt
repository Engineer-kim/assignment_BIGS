package im.bigs.pg.external.pg.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "pg.test-pg")
open class TestPgProperties(
    val baseUrl: String,
    val apiKey: String,
    val ivValue: String,
    val endPoint: String
)