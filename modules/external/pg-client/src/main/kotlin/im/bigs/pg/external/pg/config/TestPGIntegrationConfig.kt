package im.bigs.pg.external.pg.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties(TestPgProperties::class)
class TestPGIntegrationConfig {
    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}
