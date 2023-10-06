# Проект
Используются Java 17 и Gradle 8.3

Для использования Swagger UI и получения документации, после запуска программы перейдите по ссылке:
http://localhost:8080/swagger-ui/index.html

### Внешние зависимости: 

1) Spring Boot (version 3.14)
2) Swagger (springdoc-openapi-starter-webmvc-ui, version 2.2.0)
3) Lombok (version 1.18.28)
4) Resilience4J (version 2.1.0)

# Сборка и запуск

### Сборка

1. Открываем в консоли корень проекта.
2. Выполняем: 
    ``gradlew build``

### Запуск
1. Выполняем: ``java -jar build/libs/fintech-1.2-boot.jar``
