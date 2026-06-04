# NightOut AI

--Backend REST API para descubrir eventos nocturnos
--Se usará para comprar y cancelar entradas según aforo y saldo
--recibir recomendaciones reales apoyadas por Spring AI.

## Descripción del proyecto

NightOut AI es el proyecto final individual de Ironhack para Java Backend Developer. El MVP está diseñado para demostrar negocio real y cumplimiento académico al mismo tiempo:

- autenticación Bearer con JWT
- CRUD REST para usuarios, salas y eventos
- compra y cancelación de entradas
- control de aforo y saldo demo
- herencia JPA `Ticket -> GeneralTicket / VipTicket`
- recomendación real con Spring AI

## Diagrama de clases

- Ruta objetivo del UML: `docs/uml/nightout-ai-mvp.puml`
- Estado: pendiente de implementación documental en la issue #4

## Configuración

### Requisitos

- Java 25
- Maven Wrapper (`./mvnw`)
- MySQL para el entorno objetivo
- Spring AI

### Arranque rápido

1. Revisá `CONTRIBUTING.md` y `docs/developer-workflow.md`.
2. Leé los artifacts del cambio en `openspec/changes/mvp-night-events-booking-and-ai-recommendations/`.
3. Configurá variables/propiedades de base de datos, JWT y Spring AI.
4. Ejecutá tests con `./mvnw test`.
5. Implementá la siguiente issue en orden, respetando la estrategia de PR `feature-branch-chain`.

## Tecnologías utilizadas

| Área | Stack |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.0.6 |
| API | Spring Web MVC |
| Seguridad | Spring Security + JWT |
| Persistencia | Spring Data JPA + MySQL |
| IA | Spring AI 2.0.0-M6 |
| Build | Maven Wrapper |
| Planificación | OpenSpec + GitHub Issues + GitHub Project |



## Estructura del Proyecto
El proyecto está organizado siguiendo el patrón de arquitectura **Model-View-Controller (MVC) por capas**
Esto facilita la separación de responsabilidades que veremos a continuación:

* `controller/` — Controladores REST que gestionan las peticiones HTTP y definen los endpoints.
* `service/` — Capa de lógica de negocio donde reside el procesamiento de los datos.
* `repository/` — Interfaces de acceso a datos utilizando Spring Data JPA.
* `model/` — Definición de las entidades JPA y sus relaciones (incluye herencia de tickets).
* `dto/` — Objetos de transferencia de datos para el intercambio de información.
* `security/` — Configuración de Spring Security, filtros JWT y lógica de autenticación.
* `enums/` — Enumeraciones para estados y tipos de datos.
* `shared/` — Utilidades y lógica común compartida en la aplicación.

## Plan de ejecución

### Project e issues

- GitHub Project: `NightOut AI MVP Roadmap`
- Milestone: `MVP NightOut AI`
- Tracking issue: #5

### Orden de trabajo

1. Issue #1 — Base técnica, configuración y autenticación JWT
2. Issue #2 — CRUD de salas y eventos
3. Issue #3 — Booking y cancelación transaccional de entradas
4. Issue #4 — Recomendaciones con Spring AI, README y UML

### Estrategia de PR

La estrategia elegida es **feature-branch-chain**:

- existe una rama tracker del MVP
- cada PR hijo apunta a la rama del PR inmediatamente anterior
- solo la rama tracker termina integrándose a `main`

Leé `CONTRIBUTING.md` para el flujo exacto.

## Documentación para developers y agentes

- `CONTRIBUTING.md` — reglas de contribución y flujo de ramas/PR
- `docs/developer-workflow.md` — setup y forma recomendada de trabajar issue por issue
- `docs/agent-guide.md` — contrato de trabajo para agentes
- `openspec/changes/mvp-night-events-booking-and-ai-recommendations/` — propuesta, specs, diseño y tasks del MVP
- `HELP.md` — referencias generadas por Spring Boot

## Enlaces adicionales

- Repositorio: https://github.com/EngerDuran/nightOut-ai
- GitHub Project: https://github.com/users/EngerDuran/projects/1
- Roadmap issue: https://github.com/EngerDuran/nightOut-ai/issues/5

## Trabajo futuro

- recarga de saldo y pagos reales
- QR real
- cupones/promociones
- dashboards administrativos
- recomendaciones más avanzadas o conversacionales
- Implementar MCP
- implementar RedTests
- mejoras de concurrencia más allá del MVP

## Recursos

- `docs/propuesta_proyecto_nightout_ai.pdf`
- `HELP.md`
- artifacts OpenSpec del cambio `mvp-night-events-booking-and-ai-recommendations`

## Miembros del equipo

- Estudiante responsable: Enger Durán
- Tipo de proyecto: individual
