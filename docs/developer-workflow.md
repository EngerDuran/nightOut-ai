# Developer Workflow

Esta guía existe para que un developer pueda entrar al repo, entender el contexto rápido y trabajar sin romper el plan del MVP.

## Quick path

1. Leé `README.md` y `CONTRIBUTING.md`.
2. Abrí la tracking issue `#5`.
3. Leé el artifact OpenSpec de la issue actual.
4. Creá tu rama usando la estrategia `feature-branch-chain`.
5. Ejecutá tests antes y después de cambiar código.

## Qué construir primero

| Orden | Issue | Resultado esperado |
|---|---|---|
| 1 | #1 | base técnica, auth JWT y modelo de usuario |
| 2 | #2 | CRUD de salas y eventos |
| 3 | #3 | booking/cancelación con transacciones |
| 4 | #4 | recomendaciones con Spring AI + docs finales |

## Mapa de documentación útil

| Documento | Cuándo usarlo |
|---|---|
| `proposal.md` | para recordar alcance y límites |
| `specs/` | para validar comportamiento esperado |
| `design.md` | para decidir estructura técnica |
| `tasks.md` | para ejecutar en el orden correcto |

## Flujo recomendado por issue

1. **Entender**: resumí con tus palabras qué pide la issue.
2. **Leer**: revisá spec + design + task lines relevantes.
3. **Probar en rojo**: escribí o ejecutá los tests que deben fallar primero.
4. **Implementar**: resolvé solo el alcance de la issue.
5. **Verificar**: `./mvnw test` y, cuando toque, `./mvnw verify`.
6. **Documentar**: actualizá docs si cambió algo visible.

## Setup mínimo esperado

- Java 25
- Maven Wrapper
- MySQL para el entorno objetivo
- H2 para soporte local/test
- credenciales del proveedor de Spring AI

## Riesgos a vigilar

- mezclar varias issues en el mismo PR
- saltarse auth/base y empezar por features dependientes
- dejar que el proveedor IA invente resultados no presentes en catálogo
- romper consistencia de saldo o cupo por lógica fuera de servicio transaccional

## Qué NO hacer

- no apuntes todos los PRs a `main`
- no metas pagos reales, QR o chat abierto en el MVP
- no cambies el modelo de dominio sin alinear OpenSpec e issues

## Siguiente lectura

Si vas a trabajar con ayuda de IA, seguí con `docs/agent-guide.md`.
