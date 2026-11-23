# Especificaciones — Ejercicio POO-08 aplicado a Counter-Strike 2 (versión corregida)

| | |
|---|---|
| **Alumno** | Marcelo Kropff (Y43819) |
| **Asignatura** | Lenguaje de Programación 3 (CYT646) |
| **Ejercicio** | `ejercicio-poo-08-crud-h2-insomnia-2026-10-28` |
| **Dominio** | Counter-Strike 2: armas del inventario, jugadores y chalecos |
| **Usuario de GitHub** | `Pomona06` |
| **Repositorio** | https://github.com/Pomona06/MK-taller-git-2026 |
| **Enlace al commit de la corrección** | https://github.com/Pomona06/MK-taller-git-2026/commit/4198ec615bf7dd930a3b04c835b04f962877418d |
| **Colección de Insomnia** | [`insomnia/coleccion-armas-cs2.json`](../insomnia/coleccion-armas-cs2.json) |

---

## 1. Objetivo

Publicar un servicio HTTP con Spring Boot (Maven, Java 21, Spring Web, Spring Data JPA y H2) sobre el
modelado de **armas de Counter-Strike 2**, que guarda su estado en una base **H2**. El servicio ofrece:

1. persistencia en H2 de la familia `Armas` (padre abstracto y las hijas `Rifle`, `Pistola`, `Sniper` y
   `Granada`) y de las otras dos clases que necesita la simulación, `Jugador` y `Chaleco`;
2. un **catálogo** con las armas reales de CS2: solo se pueden crear armas que existen en el juego, con sus
   valores;
3. un **CRUD REST completo** sobre la familia `Armas` (`/api/armas`);
4. **dos reglas de negocio** del juego, la de **munición** y la de **vida**, que alimentan una
   **simulación de daño** persistida (`/api/simulaciones/dano`);
5. una **jerarquía de excepciones** (`JuegoException` y cinco hijas) con un *handler* HTTP por tipo;
6. la interfaz de dominio **`Audible`** (POO-07), que usa el endpoint `/api/sonidos`;
7. una **colección de Insomnia** versionada con el CRUD, la simulación y un pedido por cada excepción.

Un compañero clona, arranca con `./mvnw spring-boot:run`, importa la colección y recorre: crear → simular
→ ver el estado persistido → provocar cada error.

---

## 2. Las dos reglas

1. **Regla de munición:** un arma de fuego solo dispara si el cargador tiene las balas de ese disparo (una,
   o tres en ráfaga) y solo recarga si le queda un cargador de reserva, y una granada se lanza una sola
   vez; si no, el servicio responde 409 (`MunicionInsuficienteException` o `GranadaUsadaException`) y no
   cambia nada.
2. **Regla de vida:** el daño de un disparo baja con la distancia, el chaleco desvía una parte según la
   relación de blindaje del arma, la vida queda siempre entre 0 y 100, y un jugador eliminado no recibe más
   daño (409, `JugadorEliminadoException`).

Participan tres clases, las tres guardadas: **el arma** (hija de `Armas`), **`Jugador`** y **`Chaleco`**.
Un disparo cambia a la vez la munición del arma (o deja la granada lanzada), la durabilidad del chaleco y la
vida del jugador. Las reglas viven en la capa service (`SimulacionServiceImpl` y `ReglasDeArmas`); el
dominio guarda el estado y hace los cálculos de cada tipo de arma (`danoA()`, `balasPorDisparo()`).

---

## 3. URL

Base: `http://localhost:8080`

| Método y ruta | Qué hace | Éxito | Errores |
|---|---|---|---|
| `GET /` | Servicio vivo y lista de endpoints. | 200 | |
| `GET /api/catalogo` | Las 11 armas de CS2 que se pueden crear. | 200 | |
| `POST /api/armas` | Crea un arma: `{"modelo", "equipo"}`; sin `id` y sin estadísticas. | 201 + `id` + `Location` | 400 |
| `GET /api/armas` | Lista lo guardado. Filtros: `?equipo=CT` o `?precioMaximo=3000`. | 200 | 400 |
| `GET /api/armas/{id}` | Un arma. | 200 | 404 |
| `PATCH /api/armas/{id}` | Cambia `apodo` y/o `modoRafaga`; lo omitido queda igual. | 200 | 400, 404 |
| `DELETE /api/armas/{id}` | Borra el arma. | 204 | 404 |
| `POST /api/armas/{id}/disparos?distancia=&veces=` | Munición: dispara y guarda. | 200 | 400, 404, 409 |
| `POST /api/armas/{id}/recargas` | Munición: usa un cargador de la reserva. | 200 | 400, 404, 409 |
| `POST /api/jugadores` | Crea un jugador con 100 de vida: `{"nombre", "equipo", "chaleco"}`. | 201 + `id` | 400 |
| `GET /api/jugadores` | Lista los jugadores (`?vivos=true` para los que siguen vivos). | 200 | |
| `GET /api/jugadores/{id}` | Un jugador con su vida y blindaje. | 200 | 404 |
| `POST /api/simulaciones/dano` | `{"armaId", "jugadorId", "distancia"}`: el arma le dispara al jugador y se guardan los dos. | 200 | 400, 404, 409 |
| `GET /api/sonidos?sitio=&segundos=` | Granadas sin lanzar y una C4, tratadas como `Audible`. | 200 | 400 |
| `GET /h2-console` | Consola de H2 (JDBC URL `jdbc:h2:file:./data/armas`, usuario `sa`, sin contraseña). | | |

Errores: `{"error": "<mensaje>", "excepcion": "<tipo>"}`.

| Excepción (hija de `JuegoException`) | HTTP | Cuándo |
|---|---|---|
| `ValorInvalidoException` | 400 | Un dato que no puede existir en el juego: arma inventada, arma de otro equipo, ráfaga en un arma que no la tiene, Name Tag en una granada. |
| `RecursoNoEncontradoException` | 404 | No hay un arma o un jugador con ese id. |
| `MunicionInsuficienteException` | 409 | Regla de munición: sin balas o sin reserva. |
| `GranadaUsadaException` | 409 | Regla de munición: la granada ya se lanzó. |
| `JugadorEliminadoException` | 409 | Regla de vida: el jugador ya tiene vida 0. |

---

## 4. Invariantes

Los sostiene la capa service, frente al JSON y frente a JPA:

- **Armas:** solo modelos del catálogo de CS2. El precio, el daño, el cargador y la reserva salen del
  catálogo (archivos `weapons.vdata` e `items_game.txt` del juego); el JSON no los puede elegir.
- **Equipo:** CT o TT, y solo uno que pueda comprar ese modelo (el AK-47 es de los terroristas; el AWP, de
  los dos).
- **Munición:** `carga` y `cargadoresRestantes` son `private`, sin setter, y nunca quedan negativos: el
  servicio verifica la regla antes de mandar `disparar()` o `recargar()`.
- **Ráfaga:** solo la Glock-18 y la FAMAS la tienen; con ráfaga, un disparo gasta tres balas.
- **Granada:** se lanza una sola vez; `lanzada` se guarda en la base. No lleva Name Tag y no se recarga.
- **Jugador:** nombre no vacío; equipo CT o TT; empieza con 100 de vida; la vida solo baja con `recibirImpacto()`, nunca por
  debajo de 0; con vida 0 no recibe más daño.
- **Chaleco:** un Kevlar nuevo tiene 100 de blindaje; solo baja con `gastar()`.
- **C4:** sitio A o B; de 0 a 40 segundos.
- **JSON:** nunca se asigna directo a una entidad. Llega a un DTO (`ArmaRequest`, `JugadorRequest`) y el
  servicio llama al constructor.
- **JPA:** carga los campos por reflexión, sin setters públicos. El constructor sin argumentos de cada arma
  y del chaleco es el constructor simple de POO-06, que deja un objeto completo; el de `Jugador` es
  `protected` y solo lo usa JPA.
- **Transacciones:** cada servicio es `@Transactional`. Si una regla falla, no se guarda nada a medias: un
  `POST` ilegal no deja fila y un 409 no gasta la bala.

---

## 5. Cómo probarlo

### 5.1 Arrancar

```bash
git clone https://github.com/Pomona06/MK-taller-git-2026.git
cd MK-taller-git-2026
./mvnw spring-boot:run
```

Requiere Java 21. Para empezar con la base vacía, borrar la carpeta `data/` con el servicio frenado.

### 5.2 Insomnia

1. **Import** → **File** → `insomnia/coleccion-armas-cs2.json`.
2. Elegir el entorno **Base Environment** (`base_url`, `armaId`, `sniperId`, `granadaId`, `famasId`,
   `jugadorId`, `botId`). Con una base nueva, los ids ya coinciden; si no, copiar el `id` de cada `POST` a
   la variable que indica.
3. Correr las carpetas 0 a 4 en orden.

### 5.3 Recorrido esperado (base nueva)

| # | Pedido | Resultado esperado |
|---|---|---|
| 1 | `GET /` y `GET /api/catalogo` | 200, `"estado":"vivo"` y las 11 armas. |
| 2 | `POST /api/armas` `{"modelo":"AK-47"}` | 201, `id` 1, equipo TT, precio 2700, 30 balas y 3 cargadores. |
| 3 | `GET /api/armas` y `GET /api/armas/1` | 200, el AK-47 aparece y coincide. |
| 4 | `PATCH /api/armas/1` `{"apodo":"La Fiel"}` y `GET /api/armas/1` | 200, el apodo quedó guardado. |
| 5 | `POST /api/jugadores` `{"nombre":"Enemigo","equipo":"CT","chaleco":true}` | 201, equipo CT, vida 100 y blindaje 100. |
| 6 | `POST /api/simulaciones/dano` `{"armaId":1,"jugadorId":1,"distancia":10}` | 200: 35 de daño; 27 a la vida y 3 al chaleco. Vida 73, blindaje 97, carga 29. |
| 7 | `GET /api/jugadores/1` y `GET /api/armas/1` | Vida 73, blindaje 97, carga 29: quedó guardado, también después de reiniciar. |
| 8 | AWP (CT) contra el Bot sin chaleco a 10 m | 200: 114 de daño, vida 0, `"eliminado": true`. |
| 9 | HE (TT) contra el Enemigo a 3 m | 200: vida 14, blindaje 78; la HE queda `"lanzada": true`. |
| 10 | FAMAS con `{"modoRafaga":true}` y un disparo | 200: gasta 3 balas, quedan 22 de 25. |
| 11 | `GET /api/sonidos?sitio=B&segundos=7` (con una flashbang creada) | 200: la flashbang y la C4 responden `sonido()` por la interfaz `Audible`; la HE lanzada no aparece. |
| 12 | `POST /api/armas` con `{"modelo":"Hacha"}`, `{"modelo":"AK-47","equipo":"CT"}` o `{"modelo":"AWP"}` | **400** `ValorInvalidoException`; la lista no creció. |
| 13 | `PATCH` con `{"modoRafaga":true}` al AK-47, o con `{"apodo":"Regalo"}` a la HE | **400** `ValorInvalidoException`. |
| 14 | `GET /api/armas/9999`, `DELETE /api/armas/9999`, `GET /api/jugadores/9999` | **404** `RecursoNoEncontradoException`. |
| 15 | AWP sin balas ni reserva: disparar y recargar | **409** `MunicionInsuficienteException` dos veces. |
| 16 | Simular otra vez contra el Bot | **409** `JugadorEliminadoException`; el AK-47 no gastó la bala. |
| 17 | Volver a usar la HE (disparar o simular) | **409** `GranadaUsadaException`; el Enemigo sigue con 14 de vida. |
| 18 | `DELETE /api/armas/1` y `GET /api/armas/1` | 204 y después 404. |
| 19 | Consola H2: `SELECT * FROM ARMA; SELECT * FROM JUGADOR; SELECT * FROM CHALECO;` | Se ven el modelo, la carga, la granada lanzada, la vida y la durabilidad. |

### 5.4 Tests automáticos

```bash
./mvnw test
```

34 tests, `BUILD SUCCESS`. `ArmaCrudControllerTest` recorre el catálogo, el CRUD, los `POST` y `PATCH`
ilegales sin cambios en la base, la ráfaga, la regla de munición con el AWP, la granada de un solo uso, la
simulación con el estado persistido y el 409 del jugador eliminado. `SimulacionServiceImplTest` prueba el
reparto del daño entre la vida y el chaleco. Usan una base H2 en memoria aparte
(`src/test/resources/application.properties`).

### 5.5 Preguntas de anclaje

- **¿Puede otra capa dejar el objeto en un estado imposible?** Los controllers no pueden: no hay setters
  públicos y solo reciben DTO. Los mensajes del dominio no validan, así que la garantía es que solo los
  servicios los llaman, y siempre después de verificar las reglas.
- **Si cambiamos H2 por otra base, ¿qué se toca?** `pom.xml` (driver) y `application.properties` (URL,
  usuario, contraseña). El dominio, los repositorios, los servicios y los controllers quedan quietos.
- **¿Quién lanza la excepción?** La capa service. El controller y el dominio no lanzan ninguna.

---

## 6. Enlace al commit

https://github.com/Pomona06/MK-taller-git-2026/commit/4198ec615bf7dd930a3b04c835b04f962877418d
