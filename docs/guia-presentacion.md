# 🚀 Guía Rápida para la Presentación — NightOut AI

## 1. Orden de la demo (10 min)

### Minuto 0-2: Login + Crear evento (ADMIN)

```http
### 1. Login como admin
POST http://localhost:8080/api/login
Content-Type: application/json

{
  "username": "alex.garrido@nightout.ai",
  "password": "Kapital2026!"
}
```
→ Copiar el `access_token`

```http
### 2. Crear evento (ADMIN)
POST http://localhost:8080/api/events
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "name": "Fiesta de prueba",
  "date": "2026-08-15T23:00:00",
  "status": "CONFIRMED",
  "venueId": 1,
  "genre": "Reggaetón",
  "dressCode": "Fiesta"
}
```

### Minuto 2-4: Login usuario + Comprar entrada

```http
### 3. Login como usuario
POST http://localhost:8080/api/login
Content-Type: application/json

{
  "username": "carlos.perez@gmail.com",
  "password": "Carlos2026!"
}
```

```http
### 4. Ver eventos disponibles
GET http://localhost:8080/api/events
Authorization: Bearer {{token}}
```

```http
### 5. Comprar entrada
POST http://localhost:8080/api/tickets
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "eventId": 1,
  "price": 35.0
}
```

```http
### 6. Hacer reserva
POST http://localhost:8080/api/bookings
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "ticketId": 1,
  "quantity": 2,
  "paymentMethod": "tarjeta"
}
```

### Minuto 4-6: RRPP Virtual (Spring AI)

```http
### 7. Recomendación IA
GET http://localhost:8080/event-chat/recommend?message="Quiero ir a bailar reggaetón por Madrid, tengo 50€ para la noche"
Authorization: Bearer {{token}}
```

```http
### 8. Lista de invitados (el momento WOW)
GET http://localhost:8080/event-chat/recommend?message="Hola soy Carlos, apúntame a mí y a mi amigo Luis a la lista gratis para el Reggaetón Night en Teatro Kapital"
Authorization: Bearer {{token}}
```

### Minuto 6-8: Sistema de correos

```http
### 9. Probar envío de correo
GET http://localhost:8080/api/notifications/test-recommend
Authorization: Bearer {{token}}
```

```http
### 10. Ver historial de correos
GET http://localhost:8080/api/notifications/my-history
Authorization: Bearer {{token}}
```

### Minuto 8-10: Cierre

- Mostrar `GET /api/events` (herencia JPA: GeneralTicket/VipTicket)
- Mostrar `GET /api/bookings` (control de aforo)
- Decir: "El código está en mi GitHub, con README completo"
- Agradecer

---

## 2. Lo que tenés que saber decir (con confianza)

### Arquitectura
- "Usé Spring Boot con arquitectura MVC por capas: Controller → Service → Repository"
- "Las entidades JPA se mapean a MySQL con `ddl-auto: update`"
- "Uso DTOs para no exponer las entidades directamente en las requests"

### Spring Security
- "Implementé autenticación Bearer con JWT manualmente, con dos filtros: uno para login y otro para validar el token en cada request"
- "El token se genera con HMAC256 y expira a los 10 minutos"
- "Los roles ROLE_USER y ROLE_ADMIN controlan el acceso a endpoints"

### Herencia JPA
- "Ticket es la clase padre, GeneralTicket y VipTicket son hijas con SINGLE_TABLE"
- "Elegí SINGLE_TABLE porque es más rápido en consultas (una sola tabla) y la jerarquía es simple"

### Concurrencia
- "Para evitar sobreventa de entradas, uso `PESSIMISTIC_WRITE` en EventRepository"
- "Cuando un usuario compra, se bloquea el evento hasta que termina la transacción"
- "Si dos usuarios compran la última entrada al mismo tiempo, uno espera y el otro la consigue"

### Spring AI
- "Spring AI permite que la IA llame a funciones de Java directamente con `@Tool`"
- "El RRPP Virtual tiene un system prompt con personalidad y reglas"
- "Cuando el usuario pide lista de invitados, la IA extrae los nombres y llama a addToGuestList()"

### GuestList (el diferenciador)
- "La IA entiende lenguaje natural: 'apúntame a mí y a mi amigo' y lo registra en MySQL"
- "Esto demuestra cómo la IA no solo conversa, sino que ejecuta acciones reales en la base de datos"

### Sistema de correos
- "Cada lunes a las 10 AM, el sistema analiza el historial de compras de cada usuario"
- "Busca eventos futuros en las salas donde ya compraron y les mails recomendaciones"
- "Usa `@Scheduled` y `JavaMailSender` con Gmail SMTP"

---

## 3. Conceptos clave que pueden preguntar

| Pregunta | Respuesta |
|----------|-----------|
| ¿Por qué SINGLE_TABLE? | Porque es más rápida y la jerarquía es simple (2 subclases) |
| ¿Pessimistic vs Optimistic? | Pesimista bloquea el registro, optimista lanza excepción si cambió. Usé pesimista porque prefiero que esperen a que fallen |
| ¿Cómo funciona Spring AI? | Spring AI envía el mensaje a OpenAI + la definición de las tools. OpenAI decide cuándo llamarlas |
| ¿Por qué DTOs? | Para no exponer la entidad completa. Controlo qué datos entran y salen |
| ¿Cómo se configura el email? | Con `spring.mail.*` en application.yaml. Uso Gmail SMTP con contraseña de aplicación |
| ¿Qué pasa si no hay SMTP? | La app no falla. Capturo la excepción y guardo el intento como FAILED en DB |

---

## 4. Si tenés tiempo, mostrá estas URLs en el navegador

```
https://github.com/EngerDuran/nightOut-ai
→ El README con badges, estructura, rutas, todo

https://github.com/users/EngerDuran/projects/1
→ El tablero con las issues
```

---

## 5. Frase de cierre (dejá una buena impresión)

> *"Este proyecto me llevó a investigar temas que no vimos en clase, como concurrencia con bloqueo pesimista o integración de inteligencia artificial. Fue un desafío grande pero aprendí muchísimo, y ya tengo planeadas las siguientes mejoras: tests, Docker, y despliegue. Gracias por su tiempo."*
