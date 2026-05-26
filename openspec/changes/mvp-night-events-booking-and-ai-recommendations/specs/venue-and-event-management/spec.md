# Venue and Event Management Specification

## Purpose

Definir la administración de salas y eventos con control de capacidad, vigencia y disponibilidad del catálogo.

## Requirements

### Requirement: Gestión de salas

El sistema MUST permitir que `ADMIN` cree, consulte, actualice y retire salas, manteniendo capacidad válida y evitando estados incompatibles con eventos futuros activos.

#### Scenario: Alta o edición válida de sala

- GIVEN un `ADMIN` autenticado con datos válidos de la sala
- WHEN crea o actualiza una sala
- THEN el sistema guarda la sala con su capacidad disponible para planificación

#### Scenario: Capacidad inválida o sala comprometida

- GIVEN una capacidad no válida o una sala asociada a eventos futuros activos
- WHEN `ADMIN` intenta guardar cambios incompatibles o retirarla
- THEN el sistema MUST rechazar la operación con error de validación o conflicto

### Requirement: Gestión de eventos y disponibilidad

El sistema MUST permitir que `ADMIN` administre eventos vinculados a una sala existente y SHALL exponer al catálogo solo eventos vigentes cuya disponibilidad no exceda la capacidad de la sala.

#### Scenario: Creación válida de evento disponible

- GIVEN un `ADMIN`, una sala existente y datos válidos de un evento futuro
- WHEN crea el evento dentro de la capacidad permitida
- THEN el sistema publica el evento como disponible en el catálogo

#### Scenario: Evento inválido o no disponible

- GIVEN un evento con sala inexistente, fecha no vigente o disponibilidad mayor a la capacidad
- WHEN `ADMIN` intenta crearlo o actualizarlo
- THEN el sistema MUST rechazar la operación y no publicarlo en el catálogo
