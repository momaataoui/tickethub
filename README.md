# 🎟️ TicketHub — Billetterie d'événements

Application de billetterie (concerts, conférences, matchs) : **API REST Spring Boot** + **front-end Angular**.
Les **organisateurs** créent des événements avec un nombre de places limité, les **utilisateurs** achètent des billets.

> **Règle clé :** ne jamais vendre plus de places que la capacité, **même si plusieurs personnes achètent en même temps**.

---

## 🧱 Stack technique

| Domaine | Technologie |
|---|---|
| Back-end | Java 21, Spring Boot 4.1 (Web MVC, Data JPA, Validation, Security) |
| Front-end | Angular (dernière version stable : composants standalone, signals), TypeScript |
| Base de données | PostgreSQL 16 (Docker) |
| Migrations | Flyway |
| Tests back | JUnit 5, Mockito, MockMvc, Testcontainers |
| Tests front | Tests unitaires Angular (`ng test`), `HttpTestingController` |
| Documentation API | springdoc-openapi (Swagger UI) |
| Build | Maven (wrapper `mvnw`), Angular CLI / npm |


---

