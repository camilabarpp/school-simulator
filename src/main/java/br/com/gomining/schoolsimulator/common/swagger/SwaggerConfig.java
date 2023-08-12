package br.com.gomining.schoolsimulator.common.swagger;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SwaggerConfig {

//    http://localhost:8080/api/v1/swagger-ui/index.html#/
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("School Simulator API")
                .packagesToScan("br.com.gomining.schoolsimulator.controller")
                .pathsToMatch("/**")
                .build();
    }
}
