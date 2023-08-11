package br.com.gomining.schoolsimulator.common.swagger;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SwaggerConfig {

//    http://localhost:8080/api/swagger-ui/index.html#/

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("School Simulator API")
                .packagesToScan("br.com.gomining.schoolsimulator.controller")
                .pathsToMatch("/**")
                .build();
    }

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(metaData())
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }
//    private ApiInfo metaData() {
//        return new ApiInfoBuilder()
//                .title("School Simulator API")
//                .description("School Simulator API é uma aplicação web desenvolvida" +
//                        " em Java com Spring Boot e MongoDB. Essa API simula um sistema escolar, " +
//                        "permitindo gerenciar registros de estudantes, atividades e notas. " +
//                        "Ela inclui endpoints para criar," +
//                        " ler, atualizar e excluir dados de estudantes e atividades.")
//                .version("1.0.0")
//                .contact(new Contact("Camila Ramão Barpp",
//                        "https://www.github.com/camilabarpp",
//                        "milabarpp5@gmail.com"))
//                .license("Apache License Version 2.0")
//                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
//                .build();
//    }
}
