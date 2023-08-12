![Java Version](https://img.shields.io/badge/Java-11-orange)
![Spring Boot Version](https://img.shields.io/badge/Spring_Boot-2.6.1-green)
![MongoDB Version](https://img.shields.io/badge/MongoDB-brightgreen)
![Maven Version](https://img.shields.io/badge/Maven-red)
![Swagger Version](https://img.shields.io/badge/Swagger-3-blue)
![JUnit](https://img.shields.io/badge/JUnit-5-blue)
![Mockito](https://img.shields.io/badge/Mockito-blue)
# School Simulator

O School Simulator é um projeto que visa simular operações escolares,
desde o gerenciamento de
atividades até o registro de alunos. Esta aplicação foi
desenvolvida usando a tecnologia Spring Boot 2.6.1, oferecendo uma estrutura
robusta para construir uma aplicação Java de forma eficiente.

## Funcionalidades Principais

- Gerenciamento de estudantes, atividades e notas.
- Autenticação e autorização seguras usando **Spring Security**.
- Geração de tokens **JWT** para autenticação de usuários.
- Integração com bancos de dados **MongoDB**.
- Documentação de API usando o **Swagger UI**.

## Tecnologias Utilizadas

- **Spring Boot**: Framework Java para desenvolvimento de aplicativos robustos.
- **Spring Security**: Para autenticação e autorização seguras.
- **MongoDB**: Banco de dados NoSQL para armazenamento de dados flexíveis.
- **Mongo Embedded**: Para testes de integração.
- **Mockito e JUnit**: Para testes unitários.
- **Swagger UI**: Para documentação interativa da API.

## Pré-requisitos

- Java JDK 11.
- Maven para construção e gerenciamento de dependências.
- MongoDB instalado e configurado.

## Configuração do Ambiente

- Clone este repositório.
- Execute o aplicativo usando o IntelliJ IDEA ou sua IDE preferida.

## Instalação

1. Clone o repositório:
```bash
git clone https://github.com/camilabarpp/school-simulator.git
```
2. Acesse o diretório: `school-simulator`
3. Após abrir o projeto na sua IDE, execute os seguintes comandos no terminal:
4. Construa e empacote o projeto: `mvn clean install`
5. Execute o aplicativo: `mvn spring-boot:run`

## Como Usar

1. [Acesse a API no navegador:](http://localhost:8080/api/v1/swagger-ui/index.html#/)
2. Autentique-se usando a rota `/auth/login` ou
3. Se registre usando a rota: `/auth/register`.
4. Sugestão de usuário para **Registro**:
```bash
{
    "username": "admin@mail.com",
    "password": "Admin@123",
    "role": "ADMIN"
}
```
3. Após logando, copie o token de acesso e vá na barra Auth do Postman e selecione Bearer Token e cole o token.
3. Explore as rotas disponíveis para gerenciamento de estudantes, atividades e notas.

## Testes

1. Execute os testes automatizados para garantir a integridade do código:
```bash
mvn test
```
2. Execute os testes de cobertura com o jacoco:
```bash
 mvn clean test jacoco:report
```



