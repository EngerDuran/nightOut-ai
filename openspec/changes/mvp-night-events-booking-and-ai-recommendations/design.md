# Design: MVP Night Events Booking and AI Recommendations

## Technical Approach

Implementar un monolito Spring Boot por feature sobre la base hoy bootstrap-only (`NightOutAiApplication`), con REST + DTOs + servicios transaccionales + JPA/MySQL + seguridad JWT stateless. El diseño aterriza los cuatro specs: auth/roles, gestión de salas-eventos, compra/cancelación y recomendaciones IA, manteniendo H2 para tests/local y Spring AI solo para rankear eventos reales ya filtrados por backend.

## Architecture Decisions

| Decisión | Choice | Alternatives considered | Rationale |
|---|---|---|---|
| Estructura | Paquetes por feature: `auth`, `users`, `venues`, `events`, `tickets`, `recommendations`, `shared` | package-by-layer | Aísla el MVP por capacidad y coincide con la propuesta; evita mezclar features cuando el proyecto crezca. |
| JWT | Spring Security stateless + `JwtAuthenticationFilter` + `BCryptPasswordEncoder` + claims mínimas (`sub`, `role`) | sesiones, Basic Auth | Bearer es requisito; stateless simplifica API REST y testing. |
| Herencia tickets | `@Inheritance(SINGLE_TABLE)` en `Ticket` con discriminador `GENERAL`/`VIP` | `JOINED`, dos tablas sin padre | Cumple padre/hijo con menos joins y menor complejidad para el MVP. |
| Consistencia compra/cancelación | `@Transactional` en `TicketService` con lock pesimista del `Event` al reservar/cancelar | optimistic locking con retries | Prioriza evitar sobreventa con lógica simple y trazable en MySQL. |
| IA | Backend arma candidatos reales y Spring AI solo los ordena/explica; fallback determinístico si falla proveedor | dejar que el LLM invente respuesta libre, reglas sin IA | Cumple requisito de IA real SIN violar catálogo real ni depender totalmente del proveedor. |

## Data Flow

```text
Bearer JWT -> SecurityFilterChain -> Controller -> Service -> Repository -> MySQL
                                             |
                                             -> RecommendationService -> ChatClient -> AI provider
                                                           |
                                                           -> valida IDs contra catálogo real
```

Compra: `TicketController` -> `TicketService.purchase()` -> lock `Event` -> valida evento futuro, cupo, saldo, tipo -> crea `Ticket` -> debita `User.balance` -> guarda todo en una transacción.

Cancelación: mismo servicio -> valida propietario/ADMIN y estado -> marca `CANCELLED` -> acredita saldo -> libera cupo una sola vez.

## File Changes

| File | Action | Description |
|---|---|---|
| `pom.xml` | Modify | Agregar starters/deps de Security, Validation y JWT. |
| `src/main/resources/application.properties` | Modify | Configurar perfiles H2/MySQL, JWT y Spring AI. |
| `src/main/java/com/ironhack/nightoutai/auth/SecurityConfig.java` | Create | Filtro JWT, rutas públicas y reglas por rol. |
| `src/main/java/com/ironhack/nightoutai/auth/JwtService.java` | Create | Emisión/validación del token Bearer. |
| `src/main/java/com/ironhack/nightoutai/auth/AuthController.java` | Create | `/api/auth/register` y `/api/auth/login`. |
| `src/main/java/com/ironhack/nightoutai/users/User.java` | Create | Usuario con email único, password hash, rol, saldo y perfil simple. |
| `src/main/java/com/ironhack/nightoutai/venues/Venue.java` | Create | Sala administrable con capacidad y estado activo. |
| `src/main/java/com/ironhack/nightoutai/events/Event.java` | Create | Evento con `venue`, fecha, capacidad snapshot, precios y estado. |
| `src/main/java/com/ironhack/nightoutai/tickets/Ticket.java` | Create | Padre JPA con estado, importe y relación a usuario/evento. |
| `src/main/java/com/ironhack/nightoutai/tickets/GeneralTicket.java` | Create | Hija para entrada general. |
| `src/main/java/com/ironhack/nightoutai/tickets/VipTicket.java` | Create | Hija para entrada VIP. |
| `src/main/java/com/ironhack/nightoutai/tickets/TicketService.java` | Create | Compra/cancelación transaccional. |
| `src/main/java/com/ironhack/nightoutai/recommendations/RecommendationService.java` | Create | Orquesta candidatos, prompt y fallback. |
| `src/main/java/com/ironhack/nightoutai/shared/web/RestExceptionHandler.java` | Create | Errores 400/401/403/409 consistentes. |
| `src/test/java/com/ironhack/nightoutai/tickets/TicketServiceTest.java` | Create | Reglas de saldo, aforo y cancelación. |
| `src/test/java/com/ironhack/nightoutai/auth/AuthControllerIntegrationTest.java` | Create | Registro/login y protección por roles. |
| `README.md` | Create | Setup, endpoints, roles y decisiones MVP. |
| `docs/uml/nightout-ai-mvp.puml` | Create | UML previo exigido por la entrega. |

## Interfaces / Contracts

```java
enum Role { USER, ADMIN }
enum TicketStatus { ACTIVE, CANCELLED }
enum EventStatus { DRAFT, PUBLISHED, CANCELLED }

record PurchaseTicketRequest(Long eventId, TicketType type) {}
record RecommendationResponse(List<RecommendationItem> items, boolean aiGenerated) {}
```

Repositorios clave: `UserRepository.findByEmail`, `EventRepository.findByIdForUpdate`, `TicketRepository.findByIdAndUserId`.

## Testing Strategy

| Layer | What to Test | Approach |
|---|---|---|
| Unit | saldo, aforo, cancelación única, fallback IA | JUnit 5 + mocks de repositorios/AI client |
| Integration | auth JWT, 401/403, CRUD ADMIN, compra/cancelación | `@SpringBootTest` + `MockMvc` + H2 |
| Persistence | email único, herencia `SINGLE_TABLE`, lock de evento | `@DataJpaTest` sobre repositorios |
| E2E | flujo feliz register->login->buy->recommend->cancel | test HTTP con contexto completo, sin browser |

## Migration / Rollout

Sin migración de datos previa: es el primer esquema funcional. Para el MVP se usará auto-creación JPA en local/test y configuración MySQL documentada en README; el primer `ADMIN` se bootstrappea por propiedades al iniciar si no existe.

## Open Questions

- [ ] Confirmar el proveedor/credenciales Spring AI antes de implementar el adapter real; no bloquea entidades ni endpoints.
