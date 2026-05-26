# Tasks: MVP Night Events Booking and AI Recommendations

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | 1200-1800 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1 → PR 2 → PR 3 → PR 4 |
| Delivery strategy | ask-on-risk |
| Chain strategy | feature-branch-chain |

Decision needed before apply: No
Chained PRs recommended: Yes
Chain strategy: feature-branch-chain
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Base técnica + auth JWT | PR 1 | base: rama tracker `feature/mvp-nightout-ai` |
| 2 | CRUD de venues/events | PR 2 | base: rama del PR 1 |
| 3 | Booking/cancelación | PR 3 | base: rama del PR 2 |
| 4 | Recomendaciones + docs | PR 4 | base: rama del PR 3 |

## Phase 1: Fundación y TDD base

- [ ] 1.1 RED: crear `src/test/java/com/ironhack/nightoutai/auth/AuthControllerIntegrationTest.java` cubriendo registro/login válidos, duplicado y 401/403 por rol.
- [ ] 1.2 Modificar `pom.xml` y `src/main/resources/application.properties` para Security, Validation, JWT, perfiles H2/MySQL, Spring AI y bootstrap del primer `ADMIN`.
- [ ] 1.3 Crear `src/main/java/com/ironhack/nightoutai/shared/web/RestExceptionHandler.java`, enums compartidos y DTOs mínimos para respuestas consistentes.
- [ ] 1.4 Implementar `auth/` y `users/` (`SecurityConfig`, `JwtService`, `JwtAuthenticationFilter`, `AuthController`, `User`, `UserRepository`, servicio auth) hasta pasar los tests 1.1.

## Phase 2: Catálogo y administración

- [ ] 2.1 RED: crear tests `src/test/java/com/ironhack/nightoutai/venues/VenueControllerIntegrationTest.java` y `events/EventControllerIntegrationTest.java` para escenarios ADMIN válidos e inválidos del spec.
- [ ] 2.2 Crear `venues/` (`Venue`, repository, service, controller, DTOs`) validando capacidad y retiro incompatible con eventos futuros activos.
- [ ] 2.3 Crear `events/` (`Event`, `EventStatus`, repository con consultas de catálogo, service, controller, DTOs`) validando sala existente, fecha futura y capacidad snapshot.
- [ ] 2.4 REFACTOR: consolidar seguridad por rol y catálogo público/protegido para que los tests de auth + catálogo queden verdes.

## Phase 3: Booking transaccional y recomendaciones

- [ ] 3.1 RED: crear `src/test/java/com/ironhack/nightoutai/tickets/TicketServiceTest.java` y `tickets/TicketControllerIntegrationTest.java` para compra válida, saldo insuficiente, sin cupo y cancelación única.
- [ ] 3.2 Implementar `tickets/` (`Ticket`, `GeneralTicket`, `VipTicket`, `TicketStatus`, repositories, `TicketService`, `TicketController`) con `@Transactional` y lock pesimista en `EventRepository.findByIdForUpdate`.
- [ ] 3.3 RED: crear `src/test/java/com/ironhack/nightoutai/recommendations/RecommendationServiceTest.java` y `RecommendationControllerIntegrationTest.java` para auth, candidatos reales y fallback por falla del proveedor.
- [ ] 3.4 Implementar `recommendations/` (`RecommendationService`, controller, DTOs, adapter Spring AI`) filtrando primero catálogo real y explicando `aiGenerated`.

## Phase 4: Persistencia, documentación y verificación final

- [ ] 4.1 Crear `src/test/java/com/ironhack/nightoutai/shared/persistence/RepositoryPersistenceTest.java` para email único, `SINGLE_TABLE` y consultas con lock sobre H2.
- [ ] 4.2 Crear `src/test/java/com/ironhack/nightoutai/e2e/NightOutFlowE2ETest.java` cubriendo register → login → buy → recommend → cancel según specs.
- [ ] 4.3 Crear `README.md` y `docs/uml/nightout-ai-mvp.puml` con setup MySQL/H2, endpoints, roles, proveedor AI y decisiones del MVP.
- [ ] 4.4 Ejecutar `./mvnw test` y `./mvnw verify`, corregir desvíos y dejar `tasks.md` listo para marcar avances en `sdd-apply`.
