# User Auth and Roles Specification

## Purpose

Definir autenticación Bearer y autorización por roles para registro, login y acceso seguro al MVP.

## Requirements

### Requirement: Registro e inicio de sesión

El sistema MUST permitir registrar usuarios con identidad única e iniciar sesión con credenciales válidas, devolviendo una identidad autenticada reutilizable para llamadas posteriores.

#### Scenario: Registro y login válidos

- GIVEN un visitante con email no registrado y datos obligatorios válidos
- WHEN solicita registro y luego login con sus credenciales
- THEN el sistema crea la cuenta y devuelve autenticación Bearer válida
- AND la identidad autenticada queda asociada al rol `USER`

#### Scenario: Registro duplicado o credenciales inválidas

- GIVEN un email ya registrado o una combinación de credenciales incorrecta
- WHEN el visitante intenta registrarse o iniciar sesión
- THEN el sistema MUST rechazar la operación con error de autenticación o conflicto

### Requirement: Autorización por roles

El sistema MUST aplicar autorización por roles, reservando operaciones administrativas para `ADMIN` y negando acceso a peticiones sin token válido o sin permisos suficientes.

#### Scenario: Acceso administrativo autorizado

- GIVEN un usuario autenticado con rol `ADMIN`
- WHEN invoca un recurso administrativo protegido
- THEN el sistema permite la operación solicitada

#### Scenario: Acceso prohibido o no autenticado

- GIVEN una petición sin token válido o con rol `USER` sobre un recurso `ADMIN`
- WHEN intenta acceder al recurso protegido
- THEN el sistema MUST responder con error de autenticación o autorización
