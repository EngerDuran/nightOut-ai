# Ticket Booking and Cancellation Specification

## Purpose

Definir la compra y cancelación de entradas con tipos heredados, validaciones de saldo y control de aforo.

## Requirements

### Requirement: Emisión de entradas

El sistema MUST emitir entradas `EntradaGeneral` o `EntradaVIP` solo para usuarios autenticados cuando el evento esté disponible, exista aforo y el saldo interno del comprador cubra el importe.

#### Scenario: Compra válida de entrada

- GIVEN un `USER` autenticado con saldo suficiente y un evento disponible con cupo libre
- WHEN solicita comprar una entrada de tipo permitido
- THEN el sistema emite la entrada, descuenta el saldo y reduce el aforo disponible

#### Scenario: Compra rechazada por saldo o aforo

- GIVEN un evento sin cupo, no disponible o un comprador con saldo insuficiente
- WHEN el usuario intenta comprar una entrada
- THEN el sistema MUST rechazar la compra sin emitir entrada ni alterar saldo o aforo

### Requirement: Cancelación de entradas

El sistema MUST permitir cancelar una entrada activa al propietario o a `ADMIN` mientras el evento no haya comenzado, restaurando saldo y disponibilidad exactamente una vez.

#### Scenario: Cancelación válida

- GIVEN una entrada activa de un evento futuro y un actor autorizado
- WHEN solicita la cancelación
- THEN el sistema marca la entrada como cancelada, libera el cupo y restituye el saldo correspondiente

#### Scenario: Cancelación inválida o repetida

- GIVEN una entrada ya cancelada, ajena o de un evento iniciado/finalizado
- WHEN se intenta cancelar
- THEN el sistema MUST rechazar la operación sin aplicar devoluciones duplicadas
