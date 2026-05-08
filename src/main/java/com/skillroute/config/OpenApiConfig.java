package com.skillroute.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI skillRouteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SkillRoute REST API")
                        .version("1.0")
                        .description("Документация сервиса для синхронизации навыков и обмена сообщениями"));
    }
}