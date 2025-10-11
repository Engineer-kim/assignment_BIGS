package im.bigs.pg.application.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringDocConfig {
    @Bean
    fun bigsAssigmentAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("BIGS 과제")
                    .description(
                        """
                        ## 과제
                         결제 생성 및 조회(페이지네이션 포함) 기능
                        - POST /api/v1/payments: 결제 승인 요청 및 정보 저장
                        - GET /api/v1/payments: 결제 내역 조회 및 커서 기반 페이지네이션 
                        
                        모든 요청에는 HTTP 헤더에 유효한 형식의 API-KEY가 필요함.
                        자세한 내용은 README의 11번 문서 참고.
                        """.trimIndent()
                    )
            )
            .addSecurityItem(
                SecurityRequirement().addList("API-KEY")
            )
            .components(
                Components().addSecuritySchemes(
                    "API-KEY",
                    SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .`in`(SecurityScheme.In.HEADER)
                        .name("API-KEY")
                )
            )
    }
}