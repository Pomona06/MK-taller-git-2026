# Respuestas reales del escenario de POO-09

Capturadas el 2026-11-23 16:35 con `insomnia/capturar-respuestas.sh`.

- A1 (Marcelo): http://localhost:8082, commit `5de6e65`
- A2 (Matías): http://localhost:8080
- A3 (Joaquín): http://localhost:8083

| # | Pedido | HTTP |
|---|---|---|
| 01 | `GET http://localhost:8082/v3/api-docs` | 200 |
| 02 | `GET http://localhost:8080/v3/api-docs` | 200 |
| 03 | `GET http://localhost:8083/v3/api-docs` | 200 |
| 04 | `POST http://localhost:8082/api/armas` | 201 |
| 05 | `POST http://localhost:8082/api/jugadores` | 201 |
| 06 | `POST http://localhost:8082/api/jugadores` | 201 |
| 07 | `POST http://localhost:8082/api/escenarios/disparos-rapidos` | 201 |
| 08 | `GET http://localhost:8082/api/escenarios/disparos-rapidos` | 200 |
| 09 | `GET http://localhost:8082/api/armas/1` | 200 |
| 10 | `GET http://localhost:8082/api/jugadores/1` | 200 |
| 11 | `POST http://localhost:8082/api/escenarios/disparos-rapidos` | 409 |
| 12 | `POST http://localhost:8082/api/escenarios/disparos-rapidos` | 409 |
| 13 | `GET http://localhost:8082/api/escenarios/disparos-rapidos` | 200 |
| 14 | `GET http://localhost:8082/api/armas/1` | 200 |
