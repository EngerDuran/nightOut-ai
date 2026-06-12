# Guía de Estudio — NightOut AI

## Arquitectura General

```
Controller (API REST) → Service (Lógica de negocio) → Repository (BD)
                                ↓
                          Spring AI (RRPP Virtual)
                                ↓
                          Tools (GuestList + Email)
```

---

## 1. Seguridad — JWT

### `CustomAuthenticationFilter.java`
- **POST /api/login** recibe `{username, password}`
- Si auth OK → genera JWT con `access_token` (firma HMAC256 con "secret")
- El token expira en 10 minutos
- **Demo**: Login devuelve `{ "access_token": "eyJ..." }`
- Postman captura ese token y lo reusa en todas las requests

### `SecurityConfig.java`
- Reglas: `POST /api/events` solo ADMIN, `POST /api/tickets` USER+ADMIN, `/api/login` público
- Lo demás requiere token válido

---

## 2. CRUD de Venues, Events, Tickets, Bookings

### Patrón: Controller → Service → Repository

| Endpoint | Método | Controlador | Qué hace |
|----------|--------|-------------|----------|
| `/api/venues` | GET | VenueController | Lista todas las salas |
| `/api/venues` | POST | VenueController | Crea sala (ADMIN) |
| `/api/events` | GET | EventController | Lista eventos |
| `/api/events` | POST | EventController | Crea evento (ADMIN) |
| `/api/tickets` | GET | TicketController | Lista tickets |
| `/api/tickets` | POST | TicketController | Crea ticket |
| `/api/bookings` | GET | BookingController | Lista reservas |
| `/api/bookings` | POST | BookingController | Compra entrada |

### Atención en la demo:
- `POST /api/venues` → body `{name, location, capacity}`
- `POST /api/events` → body `{name, date, venueId, genre, dressCode}`
- `POST /api/tickets` → body `{eventId, price}`

---

## 3. Herencia SINGLE_TABLE

### `Ticket.java` → `GeneralTicket.java`

```
Ticket (base)
  ├── id, price, event
  └── ticket_type (discriminator)
       └── GeneralTicket: standingArea, gateNumber
```

TODOS los tickets viven en la misma tabla `ticket`. La columna `ticket_type` diferencia el tipo.

**Para la demo:** "Tengo una tabla con dos tipos de entradas gracias a SINGLE_TABLE, una estrategia de herencia en JPA."

---

## 4. RRPP Virtual — Spring AI

### `EventChatService.java`
- Usa `ChatClient` de Spring AI para hablar con OpenAI
- El sistema tiene memoria conversacional (`MessageChatMemoryAdvisor`)
- Tools disponibles:
  - `getEventsByGenre(String genre)` → busca eventos
  - `addToGuestList(names, emails, eventName, freeEntry)` → apunta en lista

### `GuestListTools.java` ⭐ (MOMENTO CLAVE DE LA DEMO)
- **Tool de IA** que Spring AI inyecta automáticamente al modelo
- Cuando el usuario dice "Apúntame...", la IA decide llamar a esta función
- **Con emails**: envía confirmación individual a CADA invitado
- **Sin emails**: envía resumen al usuario autenticado

### Flujo de la demo:
```
Usuario → Chat → Spring AI → OpenAI → decide llamar a addToGuestList()
  → GuestListTools.addToGuestList()
    → Guarda en BD (tabla guest_list)
    → Busca usuario por email
    → Envía email con todos los detalles de la fiesta
    → Guarda registro en tabla notification
```

---

## 5. Sistema de Emails

### `EmailService.java`
- JavaMailSender + Gmail SMTP
- `sendEmail(to, subject, body, user, type)` → con usuario registrado
- `sendEmail(to, subject, body, type)` → sin usuario (invitados no registrados)
- Cada envío se guarda en `Notification` (historial)

### `EmailTestController.java`
| Endpoint | Qué hace |
|----------|----------|
| `GET /test-recommend` | Envía recomendación al usuario autenticado AHORA |
| `GET /my-history` | Muestra mis correos recibidos |
| `GET /all` | Todos los correos (ADMIN) |
| `POST /trigger-weekly` | Simula el envío semanal a todos |

---

## 6. Flujo Completo de la Demo (Postman)

```
1. Login ADMIN → token
2. POST /api/venues → "Sala Ironhack Madrid"
3. POST /api/events → "IronParty" en venueId=9
4. POST /api/tickets → Ticket GENERAL (eventId=11)
5. POST /api/tickets → Ticket VIP (eventId=11)
6. Login USER (Carlos) → cambia token
7. GET /event-chat/chat/1?message="Quiero salir de fiesta..."
8. GET /event-chat/chat/1?message="Apuntame a mi (email) y a Lucia (email)..."
9. GET /api/notifications/my-history → ver correos
```

---

## 7. Modelo de Datos

```
User ──── Booking ──── Ticket ──── Event ──── Venue
  │                       │
  │                  GeneralTicket (SINGLE_TABLE)
  │
  └── Notification (historial de correos)

GuestList (lista de invitados, sin relación con User)
  ├── guestName, guestEmail
  └── event, status, freeEntry
```

---

## 8. Lo que NO está en el proyecto (para preguntas del profesor)

- ❌ Tests unitarios (se pueden añadir después)
- ❌ Docker / Docker Compose
- ❌ Refresh tokens (solo access token con 10min)
- ❌ No hay frontend (solo API REST + Postman)
- ❌ No hay CI/CD

---

## 9. Posibles preguntas del profesor

**¿Por qué SINGLE_TABLE y no JOINED o TABLE_PER_CLASS?**
→ Más simple, una sola tabla, mejor rendimiento en consultas. La desventaja es que las columnas de subtipos pueden ser NULL.

**¿Cómo se asegura que dos personas no compren el mismo ticket?**
→ `PESSIMISTIC_WRITE` lock en BookingService. Cuando alguien compra, se bloquea la fila del ticket hasta que termina la transacción.

**¿Cómo funciona el RRPP?**
→ Spring AI conecta con OpenAI, le da contexto del sistema (RRPP de discoteca) y tools que puede llamar (buscar eventos, añadir a lista). El modelo decide CUÁNDO llamarlas.

**¿El email es real?**
→ Sí, Gmail SMTP con contraseña de aplicación. Se envía en tiempo real.
