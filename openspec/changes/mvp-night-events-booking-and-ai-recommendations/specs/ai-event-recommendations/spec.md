# AI Event Recommendations Specification

## Purpose

Definir recomendaciones acotadas de eventos nocturnos usando contexto del usuario y catálogo vigente sin salir del alcance del MVP.

## Requirements

### Requirement: Recomendaciones basadas en catálogo real

El sistema MUST generar recomendaciones usando el perfil simple del usuario, su historial básico y el catálogo vigente, y SHALL incluir únicamente eventos reales disponibles para reserva.

#### Scenario: Recomendación válida con candidatos

- GIVEN un `USER` autenticado con perfil disponible y eventos vigentes en catálogo
- WHEN solicita recomendaciones
- THEN el sistema devuelve sugerencias ordenadas con una justificación entendible
- AND cada sugerencia corresponde a un evento real disponible

#### Scenario: Sin candidatos compatibles

- GIVEN un usuario autenticado sin coincidencias válidas en el catálogo vigente
- WHEN solicita recomendaciones
- THEN el sistema MUST responder sin inventar eventos y comunicar ausencia de coincidencias o una alternativa segura

### Requirement: Robustez y acceso del servicio de IA

El sistema MUST proteger el endpoint de recomendaciones con autenticación y SHOULD degradar de forma segura ante fallas del proveedor devolviendo una respuesta útil basada en eventos reales o un error controlado.

#### Scenario: Solicitud sin autenticación

- GIVEN una petición sin token Bearer válido
- WHEN intenta consumir recomendaciones
- THEN el sistema MUST rechazar el acceso

#### Scenario: Falla del proveedor de IA

- GIVEN un usuario autenticado y una falla del proveedor de IA
- WHEN solicita recomendaciones
- THEN el sistema SHOULD devolver una respuesta de contingencia basada en catálogo real o un error controlado trazable
