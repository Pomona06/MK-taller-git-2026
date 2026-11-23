# Especificaciones — Ejercicio POO-09 (APIs cruzadas) aplicado a Counter-Strike 2

| | |
|---|---|
| **Alumno** | Marcelo Kropff (Y43819) |
| **Asignatura** | Lenguaje de Programación 3 (CYT646) |
| **Ejercicio** | `ejercicio-poo-09-apis-cruzadas-2026-11-16` |
| **Dominio** | Counter-Strike 2 |
| **Repositorio** | https://github.com/Pomona06/MK-taller-git-2026 |
| **Enlace al commit de la solución** | https://github.com/Pomona06/MK-taller-git-2026/commit/b0c71a11d66c24c30f0b963d58c509974f70d80e |
| **Colección de Insomnia** | [`insomnia/coleccion-armas-cs2.json`](../insomnia/coleccion-armas-cs2.json), carpetas 5 y 6 |

---

## 1. El grupo

Trío de Counter-Strike 2. En mi entrega:

| Rol | Integrante | Repositorio | URL base | OpenAPI | Regla que consulto |
|---|---|---|---|---|---|
| **A1** (yo) | Marcelo Kropff (`Pomona06`) | [MK-taller-git-2026](https://github.com/Pomona06/MK-taller-git-2026) | `http://localhost:8082` | `/v3/api-docs` · `/swagger-ui.html` | — |
| **A2** | Matías Jara (`Vanzfranhait`) | [mj-taller-git-2026](https://github.com/Vanzfranhait/mj-taller-git-2026) (commit `c2ac475`) | `http://localhost:8080` | `/v3/api-docs` | Disparo rápido de francotirador: `POST /api/reglas/disparo-rapido` |
| **A3** | Joaquín Benegas (`joaquinBenegas0`) | [jb-taller-git-2026](https://github.com/joaquinBenegas0/jb-taller-git-2026) (commit `ecba4ed`) | `http://localhost:8083` | `/v3/api-docs` · `/swagger-ui.html` | Daño con blindaje: `POST /api/reglas/danio` |

Las reglas que consulto, en una frase cada una:

- **A2 (Matías):** un francotirador con estabilidad mayor a 0,7 puede hacer un disparo rápido que gasta 2 balas;
  si no está estable o tiene menos de 2 balas, la regla lo rechaza (400).
- **A3 (Joaquín):** solo se le dispara a un enemigo vivo (otro equipo, vida mayor a 0); el blindaje absorbe hasta
  la mitad del daño y el resto baja la vida, que nunca queda por debajo de 0 (si no, 422).

Mis dos reglas, que consultan ellos, son las de POO-08 (munición y vida). Las publico sin estado en
`POST /api/reglas/disparo`, `/api/reglas/recarga` y `/api/reglas/impacto`.

## 2. El escenario, en una frase

Un jugador hace un **disparo rápido** (dos balas de una vez) con un francotirador de su equipo contra un enemigo, y la jugada
solo se guarda en mi H2 si la regla de disparo rápido de A2 dice que el arma está estable y tiene dos balas, y
la regla de daño de A3 dice que el objetivo es un enemigo vivo y calcula cómo queda.

`POST /api/escenarios/disparos-rapidos` con `{"armaId", "tiradorId", "objetivoId", "distancia", "conMira"}`:

0. Antes de consultar a nadie se controla lo que no puede pasar en el juego: el arma es un francotirador del
   equipo del tirador, la bala llega a esa distancia (208 m el AWP), el tirador está vivo y no es el objetivo.
   Si no, 400 (o 409 si el tirador está eliminado) y no se consulta a A2 ni a A3.
1. Primero se consulta a **A2**: con mira la estabilidad es 1.0 y sin mira, 0.5; la munición es la del cargador.
2. Después a **A3**: el daño de las dos balas a esa distancia, contra el objetivo con su vida, blindaje y equipo.
3. Si los dos aceptan: el arma gasta dos balas, el objetivo queda con la vida y el blindaje que calculó A3 y se
   guarda una `Jugada`. Responde **201**; `GET /api/escenarios/disparos-rapidos` la muestra.
4. Si alguno rechaza: **409** con `RechazoDeA2Exception` o `RechazoDeA3Exception`, `rechazadoPor` y lo que
   respondió el compañero. La base queda igual. Si un compañero no responde: **503**
   (`CompaneroNoDisponibleException`).

Las URL de A2 y A3 están en `application.properties` y se cambian con `A2_URL`, `A2_RUTA`, `A3_URL` y
`A3_RUTA`.

## 3. Cómo probarlo

### 3.1 Arrancar los tres servicios

En una sola máquina, primero A2 y A3, después A1:

```bash
cd mj-taller-git-2026 && ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8080
```
```bash
cd jb-taller-git-2026 && ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8083
```
```bash
cd MK-taller-git-2026 && ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

### 3.2 Insomnia

Importar `insomnia/coleccion-armas-cs2.json`, elegir el subentorno **«Grupo POO-09 (este servicio en
8082)»**, correr las carpetas 0 a 4 y después la **6**:

| # | Pedido | Resultado esperado |
|---|---|---|
| 1 | `GET /v3/api-docs` de A1, A2 y A3 | 200: los tres OpenAPI |
| 2 | Crear el AWP (CT), el objetivo Halcón (TT, con chaleco), Aliado (CT) y el tirador Sombra (CT) | 201 cada uno |
| 3 | Escenario feliz: AWP con mira contra Halcón a 40 m | **201**: A2 y A3 aceptan; la jugada queda guardada |
| 4 | `GET` de las jugadas, del AWP y de Halcón | Una jugada; el AWP con 3 balas; Halcón con la vida y el blindaje que calculó A3 |
| 5 | Escenario sin mira | **409** `RechazoDeA2Exception`: estabilidad 0,5. No se consulta a A3 |
| 6 | Escenario contra Aliado (mismo equipo que el AWP) | **409** `RechazoDeA3Exception`: fuego amigo |
| 6b | Halcón (TT) con el AWP de los CT; el AWP a 300 m | **400** las dos: se rechazan antes de consultar a A2 y A3 |
| 7 | `GET` de las jugadas y del AWP | Sigue una sola jugada y el AWP con 3 balas: los rechazos no cambiaron nada |

Con los tres prendidos, `./insomnia/capturar-respuestas.sh` guarda las respuestas reales en
`insomnia/respuestas/`.

**Prueba real con los tres servicios** (23 de noviembre de 2026, 16:35; Matías en `c2ac475`, Joaquín en
`ecba4ed` y este servicio en `5de6e65`, cuando el tirador todavía era solo un nombre): los 14 pedidos de
`capturar-respuestas.sh` dieron lo esperado y las
respuestas están en [`insomnia/respuestas/`](../insomnia/respuestas/) ([resumen](../insomnia/respuestas/resumen.md)):

| Pedido | HTTP | Qué respondió |
|---|---|---|
| Escenario feliz: AWP con mira contra Halcón a 40 m | 201 | A2: `{"permitido":true,"municionDespues":3,"estabilidadDespues":0.5}`. A3: 222 de daño, el blindaje absorbe 100 y Halcón queda con vida 0 (eliminado). La jugada quedó guardada y el AWP con 3 balas. |
| Rechazo de A2: sin mira | 409 | `RechazoDeA2Exception`; Matías respondió 400 «El francotirador no está estabilizado». |
| Rechazo de A3: contra Aliado | 409 | `RechazoDeA3Exception`; Joaquín respondió 422 «Fuego amigo: Sombra y Aliado son del mismo equipo (ANTITERRORISTAS)». |
| Después de los rechazos | 200 | Sigue una sola jugada y el AWP con 3 balas: los rechazos no cambiaron nada. |


### 3.3 Tests

```bash
./mvnw test
```

53 tests. `EscenarioControllerTest` y `ClientesDeCompanerosTest` levantan servidores HTTP falsos que responden
como A2 y A3: camino feliz, rechazo de A2 (y que A3 no se consulta), rechazo de A3, compañero caído, datos
inválidos y lo que no puede pasar en el juego (tirador eliminado o del otro equipo, disparo a sí mismo, bala
fuera de alcance), verificando en cada rechazo que la base no cambió.

### 3.4 Pregunta de anclaje

*Si un compañero cambia el nombre de una ruta, ¿alcanza con actualizar lo que dice el OpenAPI?* Sí: se cambia
`A2_RUTA` o `A3_RUTA` (o la propiedad) sin tocar código. Si cambia un campo, se toca solo `DisparoRapidoA2` o
`DanioA3`. No hay código copiado de sus proyectos.

### 3.5 Revisión final: ¿esto puede pasar en el juego?

Antes de entregar repasé cada dato que acepta la API con esa pregunta y cerré cuatro casos: una cantidad de
disparos que desbordaba la cuenta de balas (ahora 409), balas que llegaban a cualquier distancia (ahora el
alcance real de cada arma, 400), un tirador que era solo un nombre (ahora un jugador guardado, vivo y del
equipo del arma) y equipos de más de cinco jugadores (409 `EquipoCompletoException`). El detalle está en el
README, sección «Revisión final».

## 4. Corrección de POO-08

Todas las observaciones están cerradas: la tabla con cada una, cómo se cerró y su commit está en el README,
sección «Situación de POO-08».

## 5. Enlace al commit

https://github.com/Pomona06/MK-taller-git-2026/commit/b0c71a11d66c24c30f0b963d58c509974f70d80e
