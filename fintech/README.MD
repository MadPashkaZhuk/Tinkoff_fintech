# Проект
Используются Java 17 и Gradle 8.3

Для использования Swagger UI и получения документации, после запуска программы перейдите по ссылке:
http://localhost:8080/swagger-ui/index.html

Для запуска тестов требуется Docker Engine для TestContainers.

Предварительно заменитe ${API_KEY} на ваш ключ в application.properties либо используйте его как системную переменную, в таком случае ничего менять не надо.

Аналогично требуется поступить с переменными H2_USERNAME, H2_PASSWORD для подключения к базе данных и ADMIN_USERNAME, ADMIN_PASSWORD для задания параметров администратора.
### Внешние зависимости: 

1) Spring Boot
2) Swagger
3) Lombok
4) Resilience4J
5) Liquibase
6) Spring JDBC
7) Hibernate
8) Spring Data JPA
9) JUnit
10) Mockito
11) TestContainers
12) Spring Security
13) Docker
14) Kafka
15) Logback & SLF4J

# Сборка и запуск

### Сборка

1. Открываем в консоли корень проекта.
2. Выполняем: 
    ``gradlew build``

### Запуск
1. Выполняем: ``java -jar build/libs/fintech-1.4-boot.jar``
