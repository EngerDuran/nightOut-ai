# Proposal: MVP Night Events Booking and AI Recommendations

## Intent

Entregar un MVP evaluable para Ironhack que cubra CRUD REST, seguridad Bearer, persistencia MySQL, herencia JPA y una recomendación REAL con Spring AI para descubrir eventos nocturnos y gestionar entradas según aforo y saldo interno.

## Scope

### In Scope
- CRUD de usuarios, salas, eventos y entradas con roles `USER`/`ADMIN`.
- Compra y cancelación de entradas validando aforo, saldo y tipo `EntradaGeneral`/`EntradaVIP`.
- Recomendación acotada con Spring AI basada en perfil simple del usuario y catálogo disponible; README/UML mínimos del MVP.

### Out of Scope
- Pagos reales, recarga de saldo, QR real, cupones y dashboards complejos.
- Chat abierto, memoria conversacional y tiempo real serio.

## Capabilities

### New Capabilities
- `user-auth-and-roles`: registro/login, Bearer auth y autorización `USER`/`ADMIN`.
- `venue-and-event-management`: CRUD de salas y eventos con capacidad y disponibilidad.
- `ticket-booking-and-cancellation`: emisión/cancelación de entradas con herencia JPA, saldo y aforo.
- `ai-event-recommendations`: recomendación generativa acotada a eventos disponibles.

### Modified Capabilities
- None.

## Approach

Spring Boot monolítico con capas controller/service/repository y paquetes por feature. MySQL como persistencia objetivo, H2 solo para soporte local. Reglas transaccionales en compra/cancelación para evitar sobreventa. Spring Security con JWT/Bearer. Spring AI expuesto como endpoint de recomendación no conversacional usando contexto del usuario, historial básico y eventos vigentes.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `src/main/java/com/ironhack/nightoutai/auth/` | New | Seguridad, JWT y control por roles |
| `src/main/java/com/ironhack/nightoutai/users/` | New | Usuario, saldo y perfil básico |
| `src/main/java/com/ironhack/nightoutai/events/` | New | Sala, evento, aforo y catálogo |
| `src/main/java/com/ironhack/nightoutai/tickets/` | New | Herencia `Entrada`/`EntradaGeneral`/`EntradaVIP` |
| `src/main/java/com/ironhack/nightoutai/recommendations/` | New | Integración Spring AI |
| `README.md`, `docs/uml/` | Modified/New | Documentación obligatoria del proyecto |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Sobreventa o saldo inconsistente | Med | Transacciones, validaciones y tests de servicio |
| IA poco útil o costosa | Med | Prompt acotado, fallback determinístico y endpoint único |
| Desvío del alcance académico | Low | Mantener MVP sin pagos/chat/real-time |

## Rollback Plan

Desactivar el endpoint de IA y volver a respuestas determinísticas; revertir módulos por feature en la branch del cambio; conservar esquema base de usuarios/eventos sin compra si falla la lógica transaccional.

## Dependencies

- Clave/configuración de proveedor compatible con Spring AI.
- MySQL accesible y modelo UML aprobado antes de implementar.

## Success Criteria

- [ ] Un `USER` autenticado puede descubrir eventos y comprar/cancelar entradas sin violar aforo ni saldo.
- [ ] Un `ADMIN` puede administrar usuarios, salas y eventos vía REST.
- [ ] Existen al menos 3 modelos, herencia JPA padre/hijo y persistencia MySQL funcional.
- [ ] La recomendación con Spring AI responde con eventos reales del catálogo vigente.
