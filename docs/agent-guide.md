# Agent Guide

Este documento define cómo debe trabajar un agente dentro de NightOut AI. La prioridad NO es producir código rápido; la prioridad es respetar alcance, orden y verificabilidad.

## Contrato mínimo de trabajo

Antes de escribir código, el agente DEBE leer en este orden:

1. issue asignada
2. `README.md`
3. `CONTRIBUTING.md`
4. `openspec/changes/mvp-night-events-booking-and-ai-recommendations/tasks.md`
5. `design.md`
6. spec del dominio que va a tocar

Si no puede resumir la tarea en 3-5 líneas, todavía NO entendió el trabajo.

## Reglas operativas

- Trabajar una sola issue a la vez.
- No ampliar alcance sin aprobación explícita.
- Mantener tests y docs en la misma unidad de trabajo.
- Seguir la estrategia `feature-branch-chain`.
- Respetar package-by-feature y las decisiones del `design.md`.

## Qué debe explicar antes de implementar

- objetivo de la issue
- archivos que probablemente cambiarán
- tests que va a escribir o actualizar
- riesgos principales
- qué queda fuera de alcance

## Estrategia de PR obligatoria

Usar **feature-branch-chain**.

### Traducción práctica

- PR 1 base: rama tracker del MVP
- PR 2 base: rama del PR 1
- PR 3 base: rama del PR 2
- PR 4 base: rama del PR 3

El agente debe confirmar siempre la base correcta antes de abrir o actualizar un PR.

## Checklist antes de declarar “done”

- [ ] el cambio resuelve exactamente la issue
- [ ] el comportamiento coincide con el spec
- [ ] la implementación respeta el diseño
- [ ] los tests relevantes están en verde
- [ ] la documentación visible está actualizada
- [ ] el PR explica verificación y alcance

## Casos donde el agente debe frenar

- falta una decisión funcional no resuelta por OpenSpec
- la tarea requiere tocar otra issue no planificada
- el diff ya no parece reviewable
- la base del PR no coincide con la cadena definida

## Qué recordar sobre IA en este proyecto

- la IA debe recomendar sobre eventos REALES del catálogo
- el backend filtra candidatos antes del proveedor
- debe existir fallback determinístico si el proveedor falla
- el agente no debe convertir el MVP en chatbot abierto
