# MK-taller-git-2026 — Modelado de armas de CS2 con Spring Boot (Marcelo Kropff Y43819)

Trabajo práctico POO-08 (CRUD REST, H2, reglas de juego y excepciones) de Lenguaje de Programación 3,
construido sobre los ejercicios POO-06 e interfaces. Servicio HTTP propio en Spring Boot sobre el modelado de Counter-Strike 2 (opción B), con herencia,
sobreescritura, sobrecarga, ocultamiento de información y paquetes según el template de la cátedra.
Después se sumó el ejercicio de **interfaces**: una capacidad compartida por dos familias distintas
(ver [Interfaz Audible](#interfaz-audible-capacidad-compartida)). En **POO-08** las armas se guardan en
una base **H2** con un CRUD REST en tres capas, y una simulación de daño entre el arma, el jugador y su
chaleco, también guardados (ver [POO-08](#poo-08-crud-rest-h2-e-insomnia)).

- Enunciados: [POO-06](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-06-revision-paquetes-constructores-2026-09-30.md) · [POO-08](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-08-crud-h2-insomnia-2026-10-28.md)
- Bitácora de IA: [BITACORA.md](BITACORA.md)
- Especificaciones aplicadas a CS2: [POO-08](docs/ESPECIFICACIONES-POO-08.md) · [POO-06](docs/ESPECIFICACIONES.md)
- Colección de Insomnia: [insomnia/coleccion-armas-cs2.json](insomnia/coleccion-armas-cs2.json)
- Enlace al commit de la solución POO-08: https://github.com/Pomona06/MK-taller-git-2026/commit/e1f5309ec85711a4ed4cf5b854950a821bed5858
- Enlace al commit de la solución POO-06: https://github.com/Pomona06/MK-taller-git-2026/commit/e7a1c2019b316689e0d4b630e5aee0edf2a06bf2

## Licencia

Apache License 2.0. El texto completo está en [LICENSE](LICENSE).

## Cómo correr

Requiere Java 21.

```bash
./mvnw spring-boot:run
```

Queda escuchando en http://localhost:8080 (`GET /` confirma que está vivo). La primera vez Maven
descarga las dependencias y tarda un poco. Los tests se corren con `./mvnw test`.

## POO-08: CRUD REST, H2 e Insomnia

### Las dos reglas

1. **Regla de munición:** un arma de fuego solo dispara si el cargador tiene las balas que pide ese disparo y solo recarga si le queda un cargador de repuesto; si no, el servicio responde 409 y la munición guardada no cambia.
2. **Regla de vida:** el daño de un disparo pasa primero por el chaleco, que absorbe la mitad mientras le quede durabilidad, y la vida del jugador queda siempre entre 0 y 100; un jugador eliminado no puede recibir más daño y, si se le dispara igual, el servicio responde 409 y el arma no gasta la bala.

Las tres clases que participan, y que quedan guardadas en H2, son **el arma** (una hija de `Armas`:
`Rifle`, `Pistola`, `Sniper` o `Granada`), **`Jugador`** y **`Chaleco`**. Un solo disparo cambia el estado
de las tres a la vez: la carga del arma, la durabilidad del chaleco y la vida del jugador.

### Propiedades de H2

Están en `src/main/resources/application.properties`:

| Propiedad | Qué hace |
|---|---|
| `spring.datasource.url=jdbc:h2:file:./data/armas` | Guarda la base en el archivo `./data/armas.mv.db`, así lo creado sigue ahí después de reiniciar el servicio. |
| `spring.datasource.username=sa` | Usuario de la base (el que se pone en la consola H2). |
| `spring.datasource.password=` | La contraseña está vacía porque la base es local y solo de desarrollo. |
| `spring.jpa.hibernate.ddl-auto=update` | Hibernate crea las tablas `arma`, `jugador` y `chaleco` si no existen y las ajusta a las entidades sin borrar los datos. |
| `spring.jpa.open-in-view=false` | La conexión a la base se cierra al terminar el servicio y no queda abierta mientras se arma el JSON. |
| `spring.h2.console.enabled=true` | Habilita la consola web de H2 en `/h2-console` (solo para desarrollo). |

La carpeta `data/` está en `.gitignore`: la base es de cada uno y no se sube al repo. Los tests usan una
base en memoria aparte (`src/test/resources/application.properties`).

### Ver la tabla en la consola H2

1. Arrancá el servicio con `./mvnw spring-boot:run`.
2. Abrí http://localhost:8080/h2-console
3. En **JDBC URL** poné `jdbc:h2:file:./data/armas`, usuario `sa` y contraseña vacía, y tocá **Connect**.
4. Corré `SELECT * FROM ARMA;`. Todas las armas están en esa tabla y la columna `TIPO` dice si la fila es
   `rifle`, `pistola`, `sniper` o `granada`.
5. Corré `SELECT * FROM JUGADOR;` y `SELECT * FROM CHALECO;`. Después de simular se ve la vida y la
   durabilidad que quedaron; `JUGADOR.CHALECO_ID` apunta al chaleco de cada jugador (vacío si no tiene).

Mientras el servicio corre, la base está bloqueada por él: la consola tiene que abrirse desde
`/h2-console` del mismo servicio, no desde otro programa.

### Endpoints de la API

| Método y ruta | Qué hace | Éxito | Errores |
|---|---|---|---|
| `POST /api/armas` | Crea un arma. El JSON lleva `tipo` y los campos que quieras; lo que falte toma el valor del constructor simple de ese tipo. No lleva `id`. | 201 + `id` + `Location` | 400 |
| `GET /api/armas` | Lista lo guardado. Filtros opcionales: `?equipo=CT` o `?precioMaximo=3000`. | 200 | |
| `GET /api/armas/{id}` | Un arma. | 200 | 404 |
| `PATCH /api/armas/{id}` | Cambia `nombre` y/o `precio`. | 200 | 400, 404 |
| `DELETE /api/armas/{id}` | Borra el arma. | 204 | 404 |
| `POST /api/armas/{id}/disparos?distancia=` | Simulación de munición: dispara y guarda la carga. | 200 | 404, 409 |
| `POST /api/armas/{id}/recargas` | Simulación de munición: usa un cargador de repuesto. | 200 | 400 (granada), 404, 409 |
| `POST /api/jugadores` | Crea un jugador: `{"nombre", "vida", "blindaje"}`. Vida omitida = 100; blindaje 0 u omitido = sin chaleco. | 201 + `id` | 400 |
| `GET /api/jugadores` | Lista los jugadores guardados con su vida y blindaje. Filtro opcional `?vivos=true`. | 200 | |
| `GET /api/jugadores/{id}` | Un jugador. | 200 | 404 |
| `POST /api/simulaciones/dano` | Simulación de daño: `{"armaId", "jugadorId", "distancia"}`. El arma guardada le dispara al jugador guardado y se guardan los dos. | 200 | 400, 404, 409 |
| `GET /api/sonidos?sitio=&segundos=` | Las granadas guardadas y una C4, tratadas como `Audible`. | 200 | 400 |

Ejemplo de creación y de simulación:

```bash
curl -X POST localhost:8080/api/armas -H "Content-Type: application/json" \
     -d '{"tipo":"rifle","nombre":"AK-47","modoRafaga":true}'
curl -X POST localhost:8080/api/jugadores -H "Content-Type: application/json" \
     -d '{"nombre":"Enemigo","vida":100,"blindaje":100}'
curl -X POST localhost:8080/api/simulaciones/dano -H "Content-Type: application/json" \
     -d '{"armaId":1,"jugadorId":1,"distancia":10}'
curl localhost:8080/api/jugadores/1      # vida 85 y blindaje 86: quedó guardado
curl localhost:8080/api/armas/1          # carga 27/30: las 3 balas de la ráfaga quedaron gastadas
```

Respuesta de la simulación (probada con una base nueva):

```json
{"armaId":1,"arma":"AK-47","tipoArma":"Rifle",
 "disparo":"AK-47 disparó a 10.0m (3 balas, precisión 0.75) causando 29 de daño. Quedan 27/30 balas.",
 "cargaDespues":27,"danoDelArma":29,"jugadorId":1,"jugador":"Enemigo","danoRecibido":15,
 "vidaAntes":100,"vidaDespues":85,"blindajeDespues":86,"eliminado":false}
```

### Simulación de daño: tres clases del modelo

`POST /api/simulaciones/dano` hace participar a tres clases, las tres guardadas en H2:

1. **El arma guardada** (cualquier hija de `Armas`): `disparar(distancia)` gasta munición y `danoA(distancia)`
   dice cuánto daño hace a esa distancia. Cada rama lo calcula a su modo: un arma de fuego pierde la mitad
   fuera de su alcance y una granada solo daña dentro de su radio.
2. **`Chaleco`** (Kevlar, es un `Vendible` pero no un arma; tabla `chaleco`): `absorber()` se queda con la
   mitad del daño mientras le quede durabilidad, y se gasta en lo que absorbió.
3. **`Jugador`** (tabla `jugador`): `recibirDano()` pasa el daño por el chaleco y sostiene la regla de vida.

El servicio busca el arma y el jugador por id, les manda los mensajes del dominio y guarda los dos (el
chaleco viaja en cascada con el jugador). Todo corre en una transacción: si una regla falla, no se guarda
ni la bala gastada ni la vida perdida.

### Jerarquía de excepciones y un handler por tipo

```
RuntimeException
└── JuegoException (abstracta)
    ├── ValorInvalidoException         400  un constructor o mensaje recibió un valor ilegal
    ├── RecursoNoEncontradoException   404  no hay un arma con ese id
    ├── MunicionInsuficienteException  409  regla de munición
    └── JugadorEliminadoException      409  regla de vida
```

`ManejadorErrores` es un `@RestControllerAdvice` con un `@ExceptionHandler` por cada tipo, cada uno con su
`@ResponseStatus`, y responde `{"error": "<mensaje>", "excepcion": "<tipo>"}`. Elegí 409 (*Conflict*) para
las dos reglas porque el pedido está bien formado, pero el estado del arma o del jugador no permite hacerlo;
400 queda para datos que son ilegales en sí mismos. Las dos hermanas de 409 se distinguen por el campo
`excepcion` del JSON (`MunicionInsuficienteException` o `JugadorEliminadoException`) y por el mensaje.

**¿Comprobada o no comprobada?** `JuegoException` extiende `RuntimeException`, así que toda la familia es
**no comprobada**: el compilador no obliga a declararla con `throws` ni a atraparla. La elegí así porque
quien la lanza es el dominio (un constructor, `disparar()`, `recibirDano()`) y quien la resuelve es el
handler HTTP, varias capas más arriba; con excepciones comprobadas cada servicio y controller del medio
tendría que repetir `throws` sin hacer nada con ella. Además, Spring deshace la transacción `@Transactional`
por defecto ante una no comprobada, y eso es lo que garantiza que un 409 no deje la bala gastada en la base.
Una **comprobada** (`extends Exception`) sirve cuando quien llama puede y debe recuperarse ahí mismo, por
ejemplo leer un archivo que puede no existir.

### Arquitectura en tres capas

```mermaid
classDiagram
    direction LR
    class ArmaCrudController {
        +crear(ArmaRequest) 201
        +listar(equipo, precioMaximo)
        +buscar(id)
        +actualizar(id, ArmaPatchRequest)
        +borrar(id) 204
        +disparar(id, distancia)
        +recargar(id)
    }
    class JugadorController {
        +crear(JugadorRequest) 201
        +listar(vivos)
        +buscar(id)
    }
    class SimulacionController {
        +simularDano(SimulacionDanoRequest)
    }
    class SonidoController {
        +sonidosGuardados(sitio, segundos)
    }
    class ManejadorErrores {
        <<RestControllerAdvice>>
        +valorInvalido() 400
        +noEncontrado() 404
        +municionInsuficiente() 409
        +jugadorEliminado() 409
    }
    class ArmaService {
        <<interface>>
    }
    class JugadorService {
        <<interface>>
    }
    class SimulacionService {
        <<interface>>
        +simularDano(SimulacionDanoRequest)
    }
    class ArmaRepository {
        <<interface>>
        +findByEquipo(String) List~Armas~
        +findByPrecioLessThanEqual(Long) List~Armas~
    }
    class GranadaRepository {
        <<interface>>
    }
    class JugadorRepository {
        <<interface>>
        +findByVidaGreaterThan(int) List~Jugador~
    }
    class JpaRepository~T, ID~ {
        <<interface>>
    }
    class Armas {
        <<Entity abstract>>
        +disparar(double)* String
        +danoA(double)* int
    }
    class ArmasDeFuego {
        <<Entity abstract>>
    }
    class Rifle {
        <<Entity>>
    }
    class Pistola {
        <<Entity>>
    }
    class Sniper {
        <<Entity>>
    }
    class Granada {
        <<Entity>>
    }
    class Jugador {
        <<Entity>>
        +recibirDano(int) int
    }
    class Chaleco {
        <<Entity>>
        +absorber(int) int
    }
    class Audible {
        <<interface>>
        +sonido() String
    }
    class BombaC4
    class RuntimeException
    class JuegoException {
        <<abstract>>
    }
    class ValorInvalidoException
    class RecursoNoEncontradoException
    class MunicionInsuficienteException
    class JugadorEliminadoException

    ArmaCrudController ..> ArmaService : usa
    JugadorController ..> JugadorService : usa
    SimulacionController ..> SimulacionService : usa
    SonidoController ..> ArmaService : usa
    SonidoController ..> Audible : usa
    ArmaService ..> ArmaRepository : usa
    ArmaService ..> GranadaRepository : usa
    JugadorService ..> JugadorRepository : usa
    SimulacionService ..> ArmaRepository : usa
    SimulacionService ..> JugadorRepository : usa
    JpaRepository <|-- ArmaRepository
    JpaRepository <|-- GranadaRepository
    JpaRepository <|-- JugadorRepository
    ArmaRepository ..> Armas : guarda
    GranadaRepository ..> Granada : guarda
    JugadorRepository ..> Jugador : guarda
    Armas <|-- ArmasDeFuego
    Armas <|-- Granada
    ArmasDeFuego <|-- Rifle
    ArmasDeFuego <|-- Pistola
    ArmasDeFuego <|-- Sniper
    Jugador o-- Chaleco
    Audible <|.. Granada
    Audible <|.. BombaC4
    RuntimeException <|-- JuegoException
    JuegoException <|-- ValorInvalidoException
    JuegoException <|-- RecursoNoEncontradoException
    JuegoException <|-- MunicionInsuficienteException
    JuegoException <|-- JugadorEliminadoException
    ManejadorErrores ..> JuegoException : atiende cada hija
    Armas ..> MunicionInsuficienteException : lanza
    Jugador ..> JugadorEliminadoException : lanza
```

Cada interfaz `XxxService` la implementa una clase `XxxServiceImpl` en `service/impl` (no se dibujan para
que el diagrama se lea; es la misma flecha `<|..`).

Cómo leer las flechas:

- **Herencia** (`<|--`, línea llena): «es un». `Rifle` es un `ArmasDeFuego`, que es un `Armas`;
  `ArmaRepository` extiende `JpaRepository`.
- **Implementación de interfaz** (`<|..`, línea punteada con triángulo): la clase cumple el protocolo.
  `Granada` y `BombaC4` implementan `Audible`, y cada `XxxServiceImpl` implementa su `XxxService`.
- **Uso del repositorio** (`..>` con «usa» o «guarda», línea punteada con flecha): una capa le habla a la
  otra a través de una interfaz. El controller usa el servicio y el servicio usa el repositorio; ninguno
  conoce la clase concreta que Spring arma por detrás.
- **Jerarquía de excepciones** (`<|--` desde `RuntimeException`): `JuegoException` es la base y sus cuatro
  hijas nombran la regla que falló. Las flechas «lanza» muestran que la excepción nace en el dominio, y
  «atiende cada hija» que `ManejadorErrores` tiene un handler por tipo.
- **Composición** (`o--`): el `Jugador` tiene un `Chaleco` (o ninguno).

El diagrama completo de la familia de armas está en [El modelo](#el-modelo).

### Colección de Insomnia

El archivo es `insomnia/coleccion-armas-cs2.json`.

1. Arrancá el servicio con `./mvnw spring-boot:run`.
2. En Insomnia: **Import** → **From File** → elegí `insomnia/coleccion-armas-cs2.json`.
3. Arriba a la izquierda, en el selector de entornos, elegí **Base Environment**. Trae `base_url`
   (`http://localhost:8080`), `armaId`, `sniperId`, `jugadorId` y `botId`. Para cambiarlos: **Manage
   Environments** (o el ícono del engranaje del entorno).
4. Corré las carpetas en orden, de arriba abajo:
   - **0. Servicio vivo:** `GET /`.
   - **1. CRUD (caso feliz):** el primer `POST` crea el AK-47. **Copiá el `id` de la respuesta a `armaId`**
     en el entorno y seguí con `GET` lista, `GET` por id, `PATCH` y el filtro por equipo.
   - **2. Simulaciones e interfaz:** se crea el Enemigo con chaleco (**copiá su `id` a `jugadorId`**) y se
     simula: el AK-47 le dispara. Los dos `GET` siguientes muestran lo que quedó guardado: el Enemigo con
     85 de vida y 86 de blindaje, y el AK-47 con 27/30 balas. Después se crea el Bot con 10 de vida
     (**copiá su `id` a `botId`**) y una segunda simulación lo elimina. El `POST` del AWP crea un
     francotirador con una sola bala (**copiá su `id` a `sniperId`**); se dispara esa bala, se crea una
     flashbang y se piden los sonidos por la interfaz.
   - **3. Errores:** un pedido por cada hija de la jerarquía. Tres 400 (`ValorInvalidoException`), cuatro
     404 (`RecursoNoEncontradoException`), dos 409 de munición (`MunicionInsuficienteException`: el AWP ya
     no tiene balas ni cargadores) y un 409 contra el Bot ya eliminado (`JugadorEliminadoException`). Después
     de los `POST` ilegales, `GET listar armas` y `GET jugadores` muestran que no se guardó nada, y el último
     `GET` muestra que el 409 no cambió al Bot.
   - **4. Borrar:** `DELETE` del AK-47 (204) y el `GET` posterior (404).

Si la base es nueva, los ids son 1 (AK-47), 2 (AWP) y 3 (flashbang) en armas, y 1 (Enemigo) y 2 (Bot) en
jugadores, que son los valores que ya trae el entorno. Para empezar de cero, frená el servicio y borrá la
carpeta `data/`.

### Lo que hay que pensar

**Recurso y verbo.** El sustantivo de la API es `armas`: la URL nombra el recurso (`/api/armas/7`) y el
verbo HTTP dice qué hacer (`GET` leer, `POST` crear, `PATCH` cambiar, `DELETE` borrar). Con
`/api/crearRifle` habría que inventar una URL por cada acción y por cada tipo, y el verbo dejaría de
significar algo. Las simulaciones crean un resultado nuevo (un disparo, una recarga), por eso son `POST`
sobre subrecursos: `/api/armas/7/disparos`.

**Identidad.** La clave es `id` (`Long`) y la genera H2 (`GenerationType.IDENTITY`), en `arma`, `jugador` y
`chaleco`. El JSON de creación no lo lleva: `ArmaRequest` y `JugadorRequest` ni siquiera tienen ese campo. Se devuelve en la respuesta del `POST` y en el
encabezado `Location`.

**Herencia y tablas.** Elegí `SINGLE_TABLE`: toda la familia en la tabla `arma`, con la columna `tipo` como
discriminador. Las cuatro hijas comparten casi todo (nombre, precio, equipo, daño) y casi siempre se listan
juntas en el inventario, así que una sola tabla evita joins. El costo es que las columnas de una hija
quedan en `null` en las filas de otra: una granada no tiene `carga`. En el JSON, `@JsonTypeInfo` agrega la
propiedad `"tipo": "rifle"` (o `granada`, etc.) con el mismo nombre que el discriminador. Para crear,
`ArmaRequest` también lleva `tipo` y el servicio elige el constructor de esa hija.

**Interfaz y repositorio.** `ArmaRepository` es una interfaz y Spring Data genera la clase. El servicio
depende de esa interfaz, no de una clase concreta de acceso a datos, y el controller depende de la
interfaz `ArmaService`. Es lo mismo que con `Audible`: quien pide un mensaje no necesita saber el apellido
de la clase que lo responde. Por eso cambiar la base no toca ni el servicio ni el controller.

**Protocolo del dominio.** `GET /api/sonidos` usa la interfaz `Audible`. En la misma lista aparecen las
granadas guardadas en H2 (familia `Armas`) y una C4 (otra familia, no persistida), y el endpoint solo les
pide `sonido()` y `alcanceSonido()`.

**Ocultamiento.** Las reglas las validan los constructores y los mensajes del dominio (`renombrar`,
`cambiarPrecio`, `disparar`, `recargar`, `recibirDano`). Los campos son privados y no tienen setters
públicos. JPA los carga por reflexión y el JSON nunca se asigna directo a la entidad: llega a un
`ArmaRequest` y el servicio llama al constructor. El constructor sin argumentos que pide JPA es el
constructor simple de POO-06 (`new Rifle()` es un AK-47 legal, `new Chaleco()` es un Kevlar nuevo), así que
tampoco deja un objeto a medias. En `Jugador` no hay un jugador «por defecto» que tenga sentido (necesita un
nombre), así que el constructor vacío es **`protected`** y solo lo usa JPA para rearmar la fila; desde el
controller o el servicio no se puede llamar. La vida no tiene setter: solo baja con `recibirDano()`.
En Insomnia, el pedido «400 POST con daño negativo» seguido de «GET listar armas» muestra que no quedó
una fila sucia.

**PUT y PATCH.** Elegí `PATCH` y solo para `nombre` y `precio`, que son lo que cambia en la tienda (un
cambio de balance o un skin con otro nombre). Un campo omitido queda como estaba. Lo demás no se edita a
mano: la munición cambia disparando y recargando, y el tipo o el daño definen otra arma.

**DELETE.** Es un borrado físico: la fila desaparece y la respuesta es 204. El dominio no tiene un mensaje
de «descartar», así que no hay un estado intermedio que modelar.

**Dos reglas.** El hecho del juego es **un disparo que pega**: en un mismo instante el arma pierde balas
(regla de munición), el chaleco pierde durabilidad y el jugador pierde vida (regla de vida). El recurso de
la simulación es `POST /api/simulaciones/dano`, y para munición sola `POST /api/armas/{id}/disparos` y
`/recargas`. Son `POST` porque cada simulación es un hecho nuevo, no se repite igual. Un `GET
/api/jugadores/{id}` y un `GET /api/armas/{id}` posteriores muestran la vida, el blindaje y la carga ya
guardados, y siguen ahí después de reiniciar el servicio.

**Excepciones.** La base es `JuegoException` (abstracta: nunca se lanza «un error del juego» sin decir
cuál). `ValorInvalidoException` es un dato imposible (daño negativo, vida 150); `RecursoNoEncontradoException`
es un id que no está en la base; `MunicionInsuficienteException` y `JugadorEliminadoException` son las dos
reglas del juego, cada una con el nombre de lo que falló. Un recurso inexistente y una regla incumplida son
tipos distintos y hasta tienen distinto código (404 frente a 409): en el primero el pedido apunta a algo que
no existe; en el segundo el objeto existe pero su estado no permite la acción. El JSON de error lleva
`error` (el mensaje que armó la clase que falló) y `excepcion` (el tipo), que es lo que distingue a las
hermanas que comparten 409. **¿Quién lanza la excepción?** El objeto del juego: `Armas`/`ArmasDeFuego`
lanzan la de munición, `Jugador` la de vida y los constructores la de valor inválido. El servicio solo lanza
la de recurso inexistente, porque es el que le pregunta al repositorio. El controller no lanza ninguna.

**Si mañana cambiamos H2 por otra base**, se tocan `pom.xml` (el driver, y la consola H2 se va) y
`application.properties` (URL, usuario y contraseña). Quedan quietos el dominio, los repositorios, los servicios y los controllers,
porque hablan con interfaces y con anotaciones JPA estándar.

## Endpoints en memoria (POO-06 y POO-07)

| Método y ruta | Qué hace |
|---|---|
| `GET /` | Confirma que el servicio está vivo y lista los endpoints. |
| `GET /armas/crear?tipo=...` | Construye el arma con el **constructor completo**. Todos los atributos se pueden pisar por query string (`nombre`, `precio`, `equipo`, `dano`, `modoRafaga`, `aturde`, etc.); si no mandás, usa unos defaults razonables. |
| `GET /armas/crear-simple?tipo=...` | Construye el arma con los **constructores sobrecargados**: sin argumentos, `(nombre)` o `(nombre, equipo / modoRafaga / aturde)` según lo que venga en la URL. El JSON dice qué firma se usó. |
| `GET /armas/comparar` | Arma una `List<Armas>` con un Rifle, una Pistola, un Sniper y una Granada y le pide a cada una `disparar()`. |
| `GET /sonidos?sitio=A&segundos=40` | Arma una `List<Audible>` con una HE, una flashbang y una C4, y le pide a cada una `sonido()` y `alcanceSonido()` a través de la interfaz. `sitio` (A o B) y `segundos` (0 a 40) son opcionales y configuran la C4. |

`tipo` puede ser `rifle`, `pistola`, `sniper` o `granada`. En los tres endpoints de armas, el parámetro
opcional `distancia` hace que se use la sobrecarga `disparar(double)` en lugar de `disparar()`.

Las respuestas de `crear` y `crear-simple` devuelven un JSON con: el tipo concreto, el constructor usado,
cómo se ve en la tienda, qué pasa si la comprás, qué mensaje de disparo se usó, qué pasa si disparás y la
animación de inspección.

Si un valor rompe una regla del dominio, la clase lo rechaza en el constructor y el servicio responde
**400** con el mensaje de la clase: `{"error": "...", "excepcion": "ValorInvalidoException"}`.

### Ejemplos probados

```bash
curl "http://localhost:8080/"
curl "http://localhost:8080/armas/crear?tipo=rifle&nombre=AK-47&modoRafaga=true"
curl "http://localhost:8080/armas/crear?tipo=sniper&nombre=AWP&dano=115"
curl "http://localhost:8080/armas/crear-simple?tipo=sniper"
curl "http://localhost:8080/armas/crear-simple?tipo=granada&nombre=Flashbang&aturde=true"
curl "http://localhost:8080/armas/crear-simple?tipo=pistola&nombre=USP-S&equipo=CT&distancia=25"
curl "http://localhost:8080/armas/comparar"
curl "http://localhost:8080/armas/comparar?distancia=25"
curl "http://localhost:8080/sonidos?sitio=B&segundos=7"
curl "http://localhost:8080/sonidos?sitio=C"             # 400: sitio inválido
curl "http://localhost:8080/armas/crear?tipo=rifle&dano=-5"      # 400: daño negativo
curl "http://localhost:8080/armas/crear?tipo=rifle&equipo=XYZ"   # 400: equipo inválido
```

Respuesta de `GET /armas/comparar` (el mismo mensaje, cuatro implementaciones distintas):

```json
[
  {"tipoConcreto":"Rifle","nombre":"AK-47","disparo":"AK-47 disparó (1 balas, precisión 0.75) causando 36 de daño. Quedan 29/30 balas."},
  {"tipoConcreto":"Pistola","nombre":"Glock-18","disparo":"Glock-18 disparó (1 balas, precisión 0.6) causando 30 de daño. Quedan 19/20 balas."},
  {"tipoConcreto":"Sniper","nombre":"AWP","disparo":"AWP disparó (1 balas, precisión 0.95) causando 46 de daño. Quedan 4/5 balas."},
  {"tipoConcreto":"Granada","nombre":"HE","disparo":"Lanzada a 20.0m. HE explotó (radio 5.0m, causa 57 de daño)"}
]
```

## Paquetes

La organización sigue el template [lp3-template-tp](https://github.com/alefq/lp3-template-tp/tree/main/src/main/java/py/edu/uc/lp3):

```
src/main/java/py/edu/uc/lp3/
├── Application.java              solo arranca Spring Boot
├── domain/                       reglas del juego
│   ├── Vendible.java             @MappedSuperclass
│   ├── Armas.java                @Entity abstracta (tabla arma, SINGLE_TABLE)
│   ├── ArmasDeFuego.java         @Entity abstracta
│   ├── Rifle.java · Pistola.java · Sniper.java · Granada.java   @Entity
│   ├── Audible.java              interfaz: sonido() y default alcanceSonido()
│   ├── BombaC4.java              otra familia: implementa Audible
│   ├── Jugador.java              @Entity (tabla jugador): regla de vida
│   └── Chaleco.java              @Entity (tabla chaleco): Kevlar, absorbe daño
├── exceptions/                   JuegoException y sus cuatro hijas
├── interfaces/                   Cotizable, VideoJuegoPosicionable, Avatar, Posicion
├── repository/                   interfaces de Spring Data
│   ├── ArmaRepository.java
│   ├── GranadaRepository.java
│   └── JugadorRepository.java
├── service/                      capa de negocio (interfaces)
│   ├── ArmaService.java · JugadorService.java · SimulacionService.java
│   ├── dto/                      ArmaRequest, ArmaPatchRequest, JugadorRequest, SimulacionDanoRequest, respuestas
│   └── impl/                     ArmaServiceImpl, JugadorServiceImpl, SimulacionServiceImpl
└── rest/controller/              entrada HTTP
    ├── IndexController.java      GET /
    ├── ArmaCrudController.java   /api/armas (CRUD y munición)
    ├── JugadorController.java    /api/jugadores
    ├── SimulacionController.java /api/simulaciones/dano
    ├── ArmaController.java       /armas/crear, /armas/crear-simple, /armas/comparar
    ├── SonidoController.java     /sonidos y /api/sonidos
    └── ManejadorErrores.java     un @ExceptionHandler por tipo de excepción
```

El dominio no conoce nada de HTTP ni de la base, los repositorios son solo interfaces y los controllers no
tienen reglas del juego: traducen HTTP, le hablan al servicio y devuelven el error de cada excepción.

## El modelo

Acá cambió bastante respecto a la primera versión subida. Me di cuenta que el esqueleto real que veníamos usando del Zulip no era el que había armado yo, sino uno que ya venía con parte del código puesto. Rifle, Pistola, Sniper y Granada no estaban, eso le agregué yo.

```mermaid
classDiagram
    namespace interfaces {
        class Cotizable {
            <<interface>>
            +getPrecio(String) Long
            +getPrecioUSD(String) Double
        }
        class VideoJuegoPosicionable {
            <<interface>>
            +getUbicacion() Posicion
            +getAvatar() Avatar
        }
    }
    namespace domain {
        class Vendible {
            -Long precio
            -String descripcion
            +getPrecio() Long
            +getPrecio(String) Long
            #setPrecio(Long) void
            +getDescripcion() String
            #setDescripcion(String) void
        }
        class Armas {
            <<abstract>>
            -Long id
            -String nombre
            -String equipo
            -double peso
            -int dano
            #Armas(nombre, precio, equipo, peso, dano)
            +comprar(double) String final
            +disparar()* String
            +disparar(double distanciaMetros)* String
            +inspeccionar()* String
            +recargar()* String
            +danoA(double distanciaMetros)* int
            +renombrar(String) void
            +cambiarPrecio(long) void
            +mostrarEnTienda() String
            #validarDistancia(double)$ void
        }
        class ArmasDeFuego {
            <<abstract>>
            -int carga
            -int cargadoresRestantes
            -int capacidadCargador
            -double precision
            -double retroceso
            -long tiempoRecargaMs
            -String animacion
            #ArmasDeFuego(nombre, precio, equipo, peso, dano, capacidadCargador, cargadoresRestantes, precision, retroceso, tiempoRecargaMs, animacion)
            +disparar() String final
            +disparar(double distanciaMetros) String final
            +danoA(double distanciaMetros) int final
            +recargar() String final
            +inspeccionar() String
            -efectuarDisparo(int, String) String
            #alcanceEfectivo() double
            #balasPorDisparo()* int
            #calcularDano()* int
        }
        class Granada {
            -double radioExplosion
            -double distanciaLanzamiento
            -boolean aturde
            -double visibilidadReducida
            -long cooldownMs
            -long ultimoLanzamiento
            -boolean explotada
            +Granada()
            +Granada(String nombre)
            +Granada(String nombre, boolean aturde)
            +Granada(nombre, precio, equipo, peso, dano, radioExplosion, distanciaLanzamiento, aturde, visibilidadReducida, cooldownMs)
            +disparar() String
            +disparar(double distanciaMetros) String
            +inspeccionar() String
            +danoA(double distanciaMetros) int
            +recargar() String
            -lanzar(double) String
            -explotar() String
            +sonido() String
        }
        class Jugador {
            +int VIDA_MAXIMA$
            -String nombre
            -int vida
            -Chaleco chaleco
            +Jugador(String nombre)
            +Jugador(String nombre, int vida, Chaleco chaleco)
            +recibirDano(int) int
            +estaEliminado() boolean
        }
        class Chaleco {
            -int durabilidad
            +Chaleco()
            +Chaleco(int durabilidad)
            +absorber(int) int
        }
        class Audible {
            <<interface>>
            +sonido() String
            +alcanceSonido() String
        }
        class BombaC4 {
            +int TIEMPO_TOTAL$
            -int DESACTIVAR_SIN_KIT$
            -String sitio
            -int segundosRestantes
            +BombaC4(String sitio)
            +BombaC4(String sitio, int segundosRestantes)
            +sonido() String
            +alcanceSonido() String
            +getSitio() String
            +getSegundosRestantes() int
        }
        class Rifle {
            -boolean modoRafaga
            +Rifle()
            +Rifle(String nombre)
            +Rifle(String nombre, boolean modoRafaga)
            +Rifle(nombre, precio, equipo, peso, dano, capacidadCargador, cargadoresRestantes, precision, retroceso, tiempoRecargaMs, modoRafaga)
            +automatico() boolean
            #balasPorDisparo() int
            #calcularDano() int
        }
        class Pistola {
            -boolean esArmaInicial
            -boolean modoRafaga
            +Pistola()
            +Pistola(String nombre)
            +Pistola(String nombre, String equipo)
            +Pistola(nombre, precio, equipo, peso, dano, capacidadCargador, cargadoresRestantes, precision, retroceso, tiempoRecargaMs, esArmaInicial, modoRafaga)
            +esInicial() boolean
            +disparoUnicoORafaga() boolean
            #alcanceEfectivo() double
            #balasPorDisparo() int
            #calcularDano() int
        }
        class Sniper {
            -boolean conMira
            +Sniper()
            +Sniper(String nombre)
            +Sniper(String nombre, String equipo)
            +Sniper(nombre, precio, equipo, peso, dano, capacidadCargador, cargadoresRestantes, precision, retroceso, tiempoRecargaMs)
            +apuntarConMira() void
            +bajarMira() void
            #alcanceEfectivo() double
            #balasPorDisparo() int
            #calcularDano() int
        }
    }
    Cotizable <|.. Vendible
    VideoJuegoPosicionable <|.. Vendible
    Vendible <|-- Armas
    Armas <|-- ArmasDeFuego
    Armas <|-- Granada
    ArmasDeFuego <|-- Rifle
    ArmasDeFuego <|-- Pistola
    ArmasDeFuego <|-- Sniper
    Audible <|.. Granada
    Audible <|.. BombaC4
    Vendible <|-- Chaleco
    Jugador o-- Chaleco
```

Convención del diagrama: `-` privado, `#` protegido, `+` público, `*` abstracto, `$` estático.

## Interfaz Audible (capacidad compartida)

**La capacidad:** sonar en la partida. En CS2 el sonido es información: una HE o una flash se oyen
cerca, y el bip de la C4 se oye desde todo el mapa y se acelera cuando queda poco tiempo.

**Dos familias distintas:**
- `Granada` pertenece a la familia `Vendible → Armas`: se compra y se lanza.
- `BombaC4` no es un arma ni se vende, es el objetivo de la ronda: es otra familia.

No tiene sentido una clase padre común solo para «sonar». Por eso la capacidad es una `interface` en
`domain`, y cada clase la implementa sin cambiar su herencia: `Granada extends Armas implements Audible`.

```java
public interface Audible {
    String sonido();                       // cada clase lo resuelve a su modo
    default String alcanceSonido() {       // comportamiento por defecto
        return "se escucha solo cerca";
    }
}
```

| Clase | `sonido()` | `alcanceSonido()` |
|---|---|---|
| `Granada` | HE: «¡BOOM! grave que retumba». Flashbang: «¡pum! seco y después un pitido agudo en los oídos». | Usa el **default**: «se escucha solo cerca» |
| `BombaC4` | El bip se acelera: «bip... bip...» con más de 10 s, «bip-bip-bip-bip» cuando sin kit ya no da el tiempo, «¡BOOM!» en 0 s. | **Reemplaza el default**: «se escucha en todo el mapa» |

**Servicio REST:** `SonidoController` arma una `List<Audible>` y le pide `sonido()` y `alcanceSonido()`
a cada elemento a través del tipo de la interfaz, sin preguntar a qué familia pertenece:

```bash
curl "http://localhost:8080/sonidos?sitio=B&segundos=7"
```

```json
[
  {"tipoConcreto":"Granada","sonido":"HE: ¡BOOM! grave que retumba","alcance":"se escucha solo cerca"},
  {"tipoConcreto":"Granada","sonido":"Flashbang: ¡pum! seco y después un pitido agudo en los oídos","alcance":"se escucha solo cerca"},
  {"tipoConcreto":"BombaC4","sonido":"bip-bip-bip-bip: quedan 7 s en el sitio B, sin kit ya no llega a desactivarse","alcance":"se escucha en todo el mapa"}
]
```

La C4 también cuida su estado: el constructor rechaza un sitio distinto de A o B y segundos fuera de 0 a
40, y el servicio responde 400 con el mensaje de la clase.

## Sobrecarga y sobreescritura

### Sobreescritura (misma firma en la clase hija, implementación propia)

| Método | Dónde se declara | Quién lo sobreescribe y cómo |
|---|---|---|
| `disparar()` | **abstracto** en `Armas` | `ArmasDeFuego`: gasta balas del cargador y calcula daño. `Granada`: se lanza y explota (daño o aturdimiento), con cooldown. |
| `disparar(double)` | **abstracto** en `Armas` | `ArmasDeFuego`: fuera del alcance efectivo el daño baja a la mitad. `Granada`: si la distancia supera el máximo, no llega. |
| `inspeccionar()` | **abstracto** en `Armas` | `ArmasDeFuego` muestra su animación y retroceso; `Granada` avisa si ya fue usada. |
| `balasPorDisparo()` | **abstracto** en `ArmasDeFuego` | `Rifle` y `Pistola`: 3 en ráfaga, 1 si no. `Sniper`: siempre 1. |
| `calcularDano()` | **abstracto** en `ArmasDeFuego` | `Rifle`: 80 % en ráfaga. `Pistola`: 70 % en ráfaga. `Sniper`: 40 % sin mira. |
| `alcanceEfectivo()` | concreto en `ArmasDeFuego` (30 m) | `Pistola`: 15 m. `Sniper`: 200 m. `Rifle` usa el del padre. |

El método abstracto principal es `disparar()`: es lo que **toda** arma tiene que saber hacer y `Armas` no
puede resolverlo, porque un arma de fuego y una granada responden distinto. Quien usa el modelo
(`ArmaController`) habla siempre con `Armas`; `GET /armas/comparar` lo muestra con una `List<Armas>`.

### Sobrecarga (misma clase, mismo nombre, otra lista de argumentos)

| Qué | Dónde | Firmas |
|---|---|---|
| Mensaje del dominio `disparar` | `Armas` (y sus implementaciones en `ArmasDeFuego` y `Granada`) | `disparar()` y `disparar(double distanciaMetros)` |
| Constructores | `Rifle` | `Rifle()`, `Rifle(String)`, `Rifle(String, boolean)`, completo |
| Constructores | `Pistola` | `Pistola()`, `Pistola(String)`, `Pistola(String, String)`, completo |
| Constructores | `Sniper` | `Sniper()`, `Sniper(String)`, `Sniper(String, String)`, completo |
| Constructores | `Granada` | `Granada()`, `Granada(String)`, `Granada(String, boolean)`, completo |
| Constructores | `Jugador` | `Jugador(String)` (vida 100, sin chaleco) y `Jugador(String, int, Chaleco)`; más el `protected Jugador()` de JPA |
| Constructores | `Chaleco` | `Chaleco()` (nuevo, durabilidad 100) y `Chaleco(int)` |
| `getPrecio` | `Vendible` | `getPrecio()` y `getPrecio(String)` (esta viene de `Cotizable`) |

Los constructores simples encadenan con `this(...)` hasta el constructor completo, que es el **único** que
llama a `super(...)`. Así las validaciones de `Armas` y `ArmasDeFuego` corren para cualquier firma y ninguna
deja el objeto en un estado ilegal. Por ejemplo, `new Sniper()` es un AWP de 4750 $ con 5 balas por
cargador, y `new Granada("Flashbang", true)` es una flash que aturde en vez de hacer daño.

### Cómo se distingue una de la otra

- **Sobrecarga**: misma clase, mismo nombre, **distintos parámetros**. La elige el **compilador** mirando
  los argumentos: `arma.disparar()` contra `arma.disparar(25.0)`.
- **Sobreescritura**: clase hija, **misma firma** que el padre (`@Override`). La elige la **JVM al
  ejecutar** mirando el tipo real del objeto: `arma.disparar()` hace cosas distintas si `arma` es un
  `Rifle` o una `Granada`, aunque la variable sea de tipo `Armas`.

## Por qué es así

Vendible ya venía con precio y descripcion resueltos, no hacía falta duplicar eso en Armas. Lo que sí le cambié en esta revisión fue la visibilidad: los campos estaban sin modificador y `setPrecio()` era público, así que cualquier clase (por ejemplo el controller) podía hacer `arma.setPrecio(-500L)`. Ahora los campos son private, los setters protected y `setPrecio()` rechaza nulos y negativos.

Armas estaba vacía. La completé con lo común a cualquier arma: nombre, equipo, peso, daño. Y con lo que pedía la consigna, que el inventario pueda tratar cualquier arma igual (pedirle que dispare, que se muestre en la tienda, que se compre) sin fijarse de qué tipo es. Por eso disparar() está declarado como abstracto directamente en Armas, no en ArmasDeFuego - así una Granada también responde a disparar() aunque por dentro lo que hace es lanzarse y explotar (delega en un lanzar() privado).

ArmasDeFuego traía un solo campo, carga, y estaba puesto como protected. Lo cambié a private, porque protected igual deja que una subclase lo modifique directo sin pasar por recargar(), y eso rompe lo de que la munición no se pise desde afuera. Lo que sí costó pensar fue cómo evitar que Rifle, Pistola y Sniper terminen repitiendo el mismo disparar() tres veces con pequeños cambios. Terminé poniendo disparar() y recargar() como final en ArmasDeFuego, se escriben una sola vez ahí y cada subclase solo sobreescribe dos métodos pequeños: balasPorDisparo() (cuántas balas gasta un tiro) y calcularDano() (cómo calcula el daño). Rifle y Pistola en modo ráfaga gastan 3 balas por vez, Sniper siempre gasta 1 pero calcula el daño distinto según si tenés la mira puesta o no. Las dos versiones de disparar (con y sin distancia) gastan munición por el mismo método privado, efectuarDisparo().

Un cambio que hice durante la realizacion, al principio solo Rifle tenía lo de ráfaga/automático. Pero analizando mejor debido a innumerables horas de experiencia en ese juegazo recordé que las pistolas CZ también puede disparar en ráfaga, así que le agregué el mismo campo modoRafaga a Pistola y reutilicé el mismo método gancho en vez de inventar uno nuevo, si no, iba a terminar duplicando lógica entre las dos.

Todo lo que tiene que ver con munición (carga, cargadoresRestantes) y con el cooldown de la granada (ultimoLanzamiento, cooldownMs) lo dejé private. La idea es que nadie de afuera pueda, por ejemplo, resetear el cooldown para hacer explotar una granada antes de tiempo, ni cargar balas de la nada sin pasar por recargar().

Las reglas se validan en los constructores: nombre no vacío, equipo `CT` o `TT`, precio, peso y daño no negativos, capacidad del cargador mayor a 0, precisión entre 0 y 1, visibilidad reducida entre 0 y 100, etc. Si un valor no cumple, la clase lanza `IllegalArgumentException` y el objeto no llega a existir.

Una cosa que agregué y que no estaba en el esqueleto original, el atributo nombre en Armas. Lo necesitaba porque si no, comprar(), inspeccionar() y el JSON que devuelve el controller no tienen forma de decir de qué arma están hablando.

mostrarEnTienda() deje como método normal no abstracto en Armas, porque con nombre, precio, equipo y peso alcanza para mostrarla en la tienda - no hacía falta que cada subclase lo reescriba.

Granada tampoco existía, la agregué extendiendo Armas directamente y no ArmasDeFuego, porque no dispara balas ni se recarga.

### ¿Qué pasa si el controller asigna a mano la vida o la munición?

No puede: `carga`, `cargadoresRestantes` y el cooldown son private y no tienen setter, y `setPrecio()` es protected. La munición solo cambia con `disparar()` y `recargar()`. Si el controller manda un valor inválido al constructor, la clase lo rechaza y `ManejadorErrores` responde 400.

## Controller

ArmaController recibe tipo por query param y ahí sí tiene que usar un
switch para saber qué constructor llamar ya eso no se puede evitar porque construir
el objeto correcto requiere saber el tipo. Pero una vez que la instancia
ya existe, todo el resto del método la trata como Armas, llama
.mostrarEnTienda(), .comprar(), .disparar(), .inspeccionar() sin
ningún if ni instanceof de por medio.

## Repositorio

https://github.com/Pomona06/MK-taller-git-2026
