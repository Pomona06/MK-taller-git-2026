# Especificaciones — Ejercicio POO-08 aplicado a Counter-Strike 2

| | |
|---|---|
| **Alumno** | Marcelo Kropff (Y43819) |
| **Asignatura** | Lenguaje de Programación 3 (CYT646) |
| **Ejercicio** | `ejercicio-poo-08-crud-h2-insomnia-2026-10-28` |
| **Dominio** | Counter-Strike 2: armas del inventario, jugadores y chalecos |
| **Usuario de GitHub** | `Pomona06` |
| **Repositorio** | https://github.com/Pomona06/MK-taller-git-2026 |
| **Enlace al commit de la solución** | Al final de este documento y en la entrega de Classroom |
| **Colección de Insomnia** | [`insomnia/coleccion-armas-cs2.json`](../insomnia/coleccion-armas-cs2.json) |

---

## 1. Objetivo

Publicar un servicio HTTP con Spring Boot (Maven, Java 21, Spring Web, Spring Data JPA y H2) sobre el
modelado de **armas de Counter-Strike 2**, que guarda su estado en una base **H2**. El servicio ofrece:

1. persistencia en H2 de la familia `Armas` (padre abstracto y las hijas `Rifle`, `Pistola`, `Sniper` y
   `Granada`) y de las otras dos clases que necesita la simulación, `Jugador` y `Chaleco`;
2. un **CRUD REST completo** sobre la familia `Armas` (`/api/armas`);
3. **dos reglas de negocio** del juego, la de **munición** y la de **vida**, que alimentan una
   **simulación de daño** persistida (`/api/simulaciones/dano`);
4. una **jerarquía de excepciones** (`JuegoException` y cuatro hijas) con un *handler* HTTP por tipo;
5. la interfaz de dominio **`Audible`** (POO-07), que usa el endpoint `/api/sonidos`;
6. una **colección de Insomnia** versionada con el CRUD, la simulación y un pedido por cada excepción.

Un compañero clona, arranca con `./mvnw spring-boot:run`, importa la colección y recorre: crear → simular
→ ver el estado persistido → provocar cada error.

---

## 2. Las dos reglas

1. **Regla de munición:** un arma de fuego solo dispara si el cargador tiene las balas que pide ese disparo
   (1, o 3 en ráfaga) y solo recarga si le queda un cargador de repuesto; si no, lanza
   `MunicionInsuficienteException` (409) y la munición guardada no cambia.
2. **Regla de vida:** el daño pasa primero por el chaleco, que absorbe la mitad mientras le quede
   durabilidad, y la vida del jugador queda siempre entre 0 y 100; un jugador eliminado no recibe más daño
   y, si se le dispara igual, `Jugador` lanza `JugadorEliminadoException` (409) y el arma no gasta la bala.

Participan tres clases, las tres guardadas: **el arma** (hija de `Armas`), **`Jugador`** y **`Chaleco`**.
Un disparo cambia a la vez la carga del arma, la durabilidad del chaleco y la vida del jugador. Las reglas
son métodos del dominio (`disparar()`, `danoA()`, `absorber()`, `recibirDano()`); el controller solo
dispara la simulación.

---

## 3. URLs

Base: `http://localhost:8080`

| Método y ruta | Qué hace | Éxito | Errores |
|---|---|---|---|
| `GET /` | Servicio vivo y cantidad de armas guardadas. | 200 | |
| `POST /api/armas` | Crea un arma. JSON con `tipo` (`rifle`, `pistola`, `sniper`, `granada`) y los campos que quieras; sin `id`. | 201 + `id` + `Location` | 400 |
| `GET /api/armas` | Lista lo guardado. Filtros: `?equipo=CT` o `?precioMaximo=3000`. | 200 | |
| `GET /api/armas/{id}` | Un arma. | 200 | 404 |
| `PATCH /api/armas/{id}` | Cambia `nombre` y/o `precio`; lo omitido queda igual. | 200 | 400, 404 |
| `DELETE /api/armas/{id}` | Borra el arma. | 204 | 404 |
| `POST /api/armas/{id}/disparos?distancia=` | Munición: dispara y guarda la carga. | 200 | 404, 409 |
| `POST /api/armas/{id}/recargas` | Munición: usa un cargador de repuesto. | 200 | 400, 404, 409 |
| `POST /api/jugadores` | Crea un jugador: `{"nombre","vida","blindaje"}`. | 201 + `id` | 400 |
| `GET /api/jugadores` | Lista los jugadores (`?vivos=true` para los que siguen vivos). | 200 | |
| `GET /api/jugadores/{id}` | Un jugador con su vida y blindaje. | 200 | 404 |
| `POST /api/simulaciones/dano` | `{"armaId","jugadorId","distancia"}`: el arma le dispara al jugador y se guardan los dos. | 200 | 400, 404, 409 |
| `GET /api/sonidos?sitio=&segundos=` | Granadas guardadas y una C4, tratadas como `Audible`. | 200 | 400 |
| `GET /h2-console` | Consola de H2 (JDBC URL `jdbc:h2:file:./data/armas`, usuario `sa`, sin contraseña). | | |

Errores: `{"error": "<mensaje>", "excepcion": "<tipo>"}`.

| Excepción (hija de `JuegoException`) | HTTP | Cuándo |
|---|---|---|
| `ValorInvalidoException` | 400 | Un constructor o mensaje recibió un valor ilegal. |
| `RecursoNoEncontradoException` | 404 | No hay un arma o un jugador con ese id. |
| `MunicionInsuficienteException` | 409 | Regla de munición. |
| `JugadorEliminadoException` | 409 | Regla de vida. |

---

## 4. Invariantes

Se sostienen en el dominio, frente al JSON y frente a JPA:

- **Armas:** nombre no vacío; equipo `CT` o `TT`; precio, peso y daño no negativos; capacidad del cargador
  mayor a 0; cargadores no negativos; precisión entre 0 y 1; distancia no negativa.
- **Munición:** `carga` y `cargadoresRestantes` son `private`, sin setter. Solo cambian con `disparar()` y
  `recargar()`, y nunca quedan negativos.
- **Jugador:** nombre no vacío; vida entre 0 y 100; solo cambia con `recibirDano()`; con vida 0 no recibe
  más daño.
- **Chaleco:** durabilidad entre 0 y 100; solo baja con `absorber()`.
- **JSON:** nunca se asigna directo a una entidad. Llega a un DTO (`ArmaRequest`, `JugadorRequest`) y el
  servicio llama al constructor, que valida. `PATCH` pasa por `renombrar()` y `cambiarPrecio()`.
- **JPA:** carga los campos por reflexión, sin setters públicos. El constructor sin argumentos de cada arma y
  del chaleco es el constructor simple de POO-06, que deja un objeto legal; el de `Jugador` es `protected` y
  solo lo usa JPA.
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

1. **Import** → **From File** → `insomnia/coleccion-armas-cs2.json`.
2. Elegir el entorno **Base Environment** (`base_url`, `armaId`, `sniperId`, `jugadorId`, `botId`). Con una
   base nueva los ids ya coinciden; si no, copiar el `id` de cada `POST` a la variable que indica.
3. Correr las carpetas 0 a 4 en orden.

### 5.3 Recorrido esperado (base nueva)

| # | Pedido | Resultado esperado |
|---|---|---|
| 1 | `GET /` | 200, `"estado":"vivo"`. |
| 2 | `POST /api/armas` `{"tipo":"rifle","nombre":"AK-47","modoRafaga":true}` | 201, `id` 1, `"tipo":"rifle"`, carga 30. |
| 3 | `GET /api/armas` | 200, el AK-47 aparece. |
| 4 | `GET /api/armas/1` | 200, coincide. |
| 5 | `PATCH /api/armas/1` `{"precio":2500}` y `GET /api/armas/1` | 200, precio 2500 y sigue así. |
| 6 | `POST /api/jugadores` `{"nombre":"Enemigo","vida":100,"blindaje":100}` | 201, `id` 1. |
| 7 | `POST /api/simulaciones/dano` `{"armaId":1,"jugadorId":1,"distancia":10}` | 200: 3 balas, 29 de daño, el chaleco absorbe 14; vida 85, blindaje 86, carga 27. |
| 8 | `GET /api/jugadores/1` y `GET /api/armas/1` | Vida 85, blindaje 86, carga 27: quedó guardado (también después de reiniciar). |
| 9 | `POST /api/jugadores` `{"nombre":"Bot","vida":10}` y simular contra él | 200, `"eliminado": true`, vida 0. |
| 10 | `GET /api/sonidos?sitio=B&segundos=7` (con una flashbang creada) | 200, la flashbang y la C4 responden `sonido()` por la interfaz `Audible`. |
| 11 | `POST /api/armas` `{"tipo":"rifle","dano":-5}` y `GET /api/armas` | **400** `ValorInvalidoException`; la lista no creció. |
| 12 | `POST /api/jugadores` `{"nombre":"Enemigo","vida":150}` | **400** `ValorInvalidoException`. |
| 13 | `GET /api/armas/9999` · `DELETE /api/armas/9999` · `GET /api/jugadores/9999` | **404** `RecursoNoEncontradoException`. |
| 14 | AWP con 1 bala y 0 cargadores: disparar dos veces y recargar | 200, después **409** `MunicionInsuficienteException` dos veces. |
| 15 | Simular otra vez contra el Bot | **409** `JugadorEliminadoException`; el Bot sigue en 0 y el AK-47 no gastó la bala. |
| 16 | `DELETE /api/armas/1` y `GET /api/armas/1` | 204 y después 404. |
| 17 | Consola H2: `SELECT * FROM ARMA; SELECT * FROM JUGADOR; SELECT * FROM CHALECO;` | Se ven las filas con la carga, la vida y la durabilidad que dejó la simulación. |

### 5.4 Tests automáticos

```bash
./mvnw test
```

31 tests, `BUILD SUCCESS`. `ArmaCrudControllerTest` recorre el CRUD, el `POST` ilegal sin fila sucia, la
simulación con el estado persistido, el 409 del jugador eliminado que no gasta munición y los errores de
jugador. Usan una base H2 en memoria aparte (`src/test/resources/application.properties`).

### 5.5 Preguntas de anclaje

- **¿Puede otra capa dejar el objeto en un estado imposible?** No: ni el controller ni JPA tienen setters
  públicos de munición o vida; todo pasa por constructores y mensajes que validan.
- **Si cambiamos H2 por otra base, ¿qué se toca?** `pom.xml` (driver) y `application.properties` (URL,
  usuario, contraseña). El dominio, los repositorios, los servicios y los controllers quedan quietos.
- **¿Quién lanza la excepción?** El objeto del juego (`ArmasDeFuego`, `Jugador`, los constructores). El
  servicio solo lanza la de recurso inexistente. El controller no lanza ninguna.

---

## 6. Enlace al commit

El enlace exacto va en la entrega de Classroom y al principio del [README](../README.md).
