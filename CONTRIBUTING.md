# Contributing to NightOut AI

Este repositorio se va a ejecutar **issue por issue**, con alcance controlado y PRs encadenados. Si vas a contribuir como developer o como agente, NO arranques código sin leer primero este documento.

## Quick path

1. Abrí la tracking issue `#5` y elegí la siguiente issue pendiente en orden.
2. Leé `README.md`, `docs/developer-workflow.md`, `docs/agent-guide.md` y el artifact OpenSpec correspondiente.
3. Trabajá en una rama siguiendo la estrategia **feature-branch-chain**.
4. Verificá tests y documentación antes de abrir PR.

## Fuente de verdad

| Artefacto | Para qué sirve |
|---|---|
| `README.md` | visión general del proyecto |
| `openspec/.../proposal.md` | intención y alcance |
| `openspec/.../specs/` | comportamiento esperado |
| `openspec/.../design.md` | diseño técnico |
| `openspec/.../tasks.md` | orden real de ejecución |
| Issues #1-#5 | unidad de trabajo operativa |

## Estrategia de ramas y PR

La estrategia oficial del repo es **feature-branch-chain**.

### Regla principal

- existe una rama tracker del MVP, por ejemplo: `feature/mvp-nightout-ai`
- cada issue vive en una rama hija específica
- cada PR apunta a la rama del PR inmediatamente anterior, NO directamente a `main`
- solo la rama tracker se integra a `main` al final de la cadena

### Ejemplo recomendado

| Issue | Rama sugerida | Base del PR |
|---|---|---|
| #1 | `feature/mvp-nightout-ai-auth` | `feature/mvp-nightout-ai` |
| #2 | `feature/mvp-nightout-ai-catalog` | `feature/mvp-nightout-ai-auth` |
| #3 | `feature/mvp-nightout-ai-booking` | `feature/mvp-nightout-ai-catalog` |
| #4 | `feature/mvp-nightout-ai-ai-docs` | `feature/mvp-nightout-ai-booking` |

Si un PR muestra cambios del PR anterior en el diff, la base está mal apuntada o falta rebase.

## Cómo empezar una issue

Antes de tocar código, confirmá:

- [ ] qué issue estás resolviendo
- [ ] qué artifact OpenSpec la respalda
- [ ] qué tests deben escribirse primero
- [ ] cuál es la rama base correcta
- [ ] qué documentación debe cambiar junto con el código

## Definición de terminado

Una issue NO está terminada si solo “funciona en local”. También debe cumplir esto:

- [ ] tests relevantes en verde
- [ ] alcance limitado a la issue
- [ ] documentación actualizada si cambió comportamiento
- [ ] endpoints/validaciones alineados con los specs
- [ ] PR con descripción clara y pasos de verificación

## Convenciones de implementación

- Preferir package-by-feature.
- Mantener reglas de negocio en services, no en controllers.
- Escribir tests RED primero cuando la tarea lo pida.
- No mezclar varias issues en un mismo PR.
- No expandir scope “porque ya estamos ahí”.

## Commits y PRs

- Hacer commits pequeños y con intención clara.
- Abrir PRs reviewables, uno por work unit/issue.
- Referenciar la issue correspondiente en el PR.
- Incluir qué se verificó y qué queda fuera de alcance.

## Para agentes

Si trabajás con un agente:

- obligalo a leer la issue, `tasks.md`, `design.md` y el spec relevante antes de escribir código
- pedile que explique el objetivo de la tarea antes de implementarla
- exigí actualización de tests y docs en la misma unidad de trabajo

Leé `docs/agent-guide.md` para el contrato completo.
