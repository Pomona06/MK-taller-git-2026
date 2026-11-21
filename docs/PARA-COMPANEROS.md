# POO-09 — Mi API para el grupo (Marcelo Kropff, CS2)

Esta ficha es para quien consulte mi servicio como su A2 o su A3. Tiene todo lo necesario para usar mis
reglas sin abrir mi código.

| | |
|---|---|
| **Quién** | Marcelo Kropff · GitHub `Pomona06` |
| **Juego** | Counter-Strike 2 |
| **Repositorio** | https://github.com/Pomona06/MK-taller-git-2026 |
| **OpenAPI (JSON)** | `http://<host>:<puerto>/v3/api-docs` |
| **OpenAPI (para probar en el navegador)** | `http://<host>:<puerto>/swagger-ui.html` |
| **Mis reglas en JSON** | `GET http://<host>:<puerto>/api/reglas` |

## Cómo arrancar mi servicio en tu máquina

Requiere Java 21.

```bash
git clone https://github.com/Pomona06/MK-taller-git-2026.git
```
```bash
cd MK-taller-git-2026
```

Por defecto escucha en el puerto 8080. Si tu servicio ya usa ese puerto, arrancá el mío en otro, por ejemplo
el 8082:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

Está listo cuando la terminal muestra `Started Application`. Para comprobarlo, abrí
`http://localhost:8082/swagger-ui.html`.

## Mis dos reglas

1. **Regla de munición:** un arma de fuego solo dispara si el cargador tiene las balas de ese disparo (una,
   o tres en ráfaga) y solo recarga si le queda un cargador de reserva, y una granada se lanza una sola vez.
2. **Regla de vida:** el daño baja con la distancia, el chaleco desvía una parte según la relación de
   blindaje del arma, la vida queda entre 0 y 100 y un jugador eliminado no recibe más daño.

Las armas son las de CS2, con sus valores reales: AK-47, Galil AR, M4A4, FAMAS, Glock-18, USP-S, Desert
Eagle, AWP, SSG 08, HE y Flashbang. La lista completa, con precio, daño y cargador, está en
`GET /api/catalogo`.

## Cómo consultarlas desde tu escenario

Estos tres endpoints reciben el estado en el JSON y **no leen ni cambian mi base**. Así tu escenario me puede
consultar sin depender de lo que yo tenga guardado, y si tu otro compañero rechaza la jugada, mi base no
quedó modificada.

| Endpoint | Pregunta | Acepta | Rechaza |
|---|---|---|---|
| `POST /api/reglas/disparo` | ¿Puede disparar esta arma? | 200 | 409 `MunicionInsuficienteException` o `GranadaUsadaException` |
| `POST /api/reglas/recarga` | ¿Puede recargar? | 200 | 409 `MunicionInsuficienteException` |
| `POST /api/reglas/impacto` | ¿Qué le hace este disparo a un jugador? | 200 | 409 `JugadorEliminadoException` |

Un dato que no puede existir en el juego (un arma inventada, una carga mayor que el cargador, una vida de
150) responde **400** con `ValorInvalidoException`. Todos los errores tienen este cuerpo:

```json
{"error": "AK-47 no puede disparar: el cargador tiene 0 balas y hacen falta 1", "excepcion": "MunicionInsuficienteException"}
```

### Regla de munición: disparo

Campos: `modelo` (obligatorio), `carga` (balas en el cargador; omitida = lleno), `modoRafaga` (solo Glock-18
y FAMAS), `veces` (omitido = 1) y, para una granada, `lanzada`.

```bash
curl -X POST localhost:8082/api/reglas/disparo -H "Content-Type: application/json" \
     -d '{"modelo":"AK-47","carga":1}'
```
```json
{"permitido":true,"modelo":"AK-47","balasNecesarias":1,"cargaDespues":0,"detalle":"AK-47 puede disparar 1 vez"}
```

Rechazos: `{"modelo":"AK-47","carga":0}` (sin balas) o `{"modelo":"HE","lanzada":true}` (granada ya usada)
responden 409.

### Regla de munición: recarga

Campos: `modelo`, `carga` y `cargadoresRestantes`.

```bash
curl -X POST localhost:8082/api/reglas/recarga -H "Content-Type: application/json" \
     -d '{"modelo":"AWP","carga":0,"cargadoresRestantes":1}'
```
```json
{"permitido":true,"modelo":"AWP","cargaDespues":5,"cargadoresDespues":0,"detalle":"AWP puede recargar"}
```

Rechazo: `{"modelo":"AWP","carga":0,"cargadoresRestantes":0}` responde 409.

### Regla de vida: impacto

Campos: `modelo`, `distancia` en metros (omitida = 10), `vida` (omitida = 100) y `blindaje` (omitido = 0,
sin chaleco).

```bash
curl -X POST localhost:8082/api/reglas/impacto -H "Content-Type: application/json" \
     -d '{"modelo":"AK-47","distancia":10,"vida":100,"blindaje":100}'
```
```json
{"permitido":true,"modelo":"AK-47","danoSinBlindaje":35,"vidaPerdida":27,"blindajePerdido":3,"vidaDespues":73,"blindajeDespues":97,"eliminado":false}
```

Rechazo: `{"modelo":"AK-47","vida":0}` (jugador ya eliminado) responde 409.

## Qué te pido a cambio

Para consultarte como mi A2 o mi A3 necesito:

1. La URL de tu repositorio y el puerto en el que arranca tu servicio.
2. La URL de tu OpenAPI.
3. Tus dos reglas, en una frase cada una.
4. Un pedido que tu regla acepte y uno que rechace, con el código y el cuerpo de error que devolvés.

Si tu API solo valida las reglas cambiando tu base (por ejemplo, disparando de verdad), te sugiero agregar un
endpoint de consulta que no guarde nada, como los míos: así un rechazo del otro compañero no te deja la
base modificada.

El enunciado no permite copiar el código del otro: solo se usa su OpenAPI. Si al mío le falta algo para
entender una regla, avisame y lo completo.
