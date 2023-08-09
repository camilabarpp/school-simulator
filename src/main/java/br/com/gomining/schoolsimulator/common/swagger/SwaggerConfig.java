package br.com.gomining.schoolsimulator.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@EnableSwagger2
@Configuration
public class SwaggerConfig {

//    http://localhost:8080/swagger-ui/index.html#/
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metaData())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("School Simulator API")
                .description("School Simulator API é uma aplicação web desenvolvida" +
                        " em Java com Spring Boot e MongoDB. Essa API simula um sistema escolar, " +
                        "permitindo gerenciar registros de estudantes, atividades e notas. " +
                        "Ela inclui endpoints para criar," +
                        " ler, atualizar e excluir dados de estudantes e atividades.")
                .version("1.0.0")
                .contact(new Contact("Camila Ramão Barpp",
                        "https://www.github.com/camilabarpp",
                        "milabarpp5@gmail.com"))
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
                .build();
    }
}
