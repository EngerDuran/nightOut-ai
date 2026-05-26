# NightOut AI

Backend REST API para descubrir eventos nocturnos, comprar o cancelar entradas según aforo y saldo, y recibir recomendaciones reales apoyadas por Spring AI.

## Estado actual

- Estado del repo: bootstrap Spring Boot + planificación SDD completa
- Estado del MVP: definido, documentado y dividido en issues
- Próxima ejecución recomendada: **Issue #1 — Base técnica, configuración y autenticación JWT**

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
- credenciales válidas del proveedor configurado para Spring AI

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
| Testing | JUnit 5 + Spring Boot Test |
| IA | Spring AI 2.0.0-M6 |
| Build | Maven Wrapper |
| Planificación | OpenSpec + GitHub Issues + GitHub Project |

## Estructura de controladores y rutas

La estructura objetivo del MVP es **package-by-feature**:

- `auth/` — registro, login y JWT
- `users/` — usuario, saldo y perfil mínimo
- `venues/` — gestión de salas
- `events/` — catálogo y administración de eventos
- `tickets/` — compra/cancelación y herencia JPA
- `recommendations/` — recomendación con Spring AI
- `shared/` — errores, enums y contratos comunes

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
- mejoras de concurrencia más allá del MVP

## Recursos

- `docs/propuesta_proyecto_nightout_ai.pdf`
- `HELP.md`
- artifacts OpenSpec del cambio `mvp-night-events-booking-and-ai-recommendations`

## Miembros del equipo

- Estudiante responsable: Enger Duran
- Tipo de proyecto: individual
