# Especificaciones — Ejercicio POO-06 aplicado a Counter-Strike 2

| | |
|---|---|
| **Alumno** | Marcelo Kropff (Y43819) |
| **Asignatura** | Lenguaje de Programación 3 (CYT646) |
| **Ejercicio** | `ejercicio-poo-06-revision-paquetes-constructores-2026-09-30` |
| **Dominio** | Counter-Strike 2: armas del inventario del jugador |
| **Usuario de GitHub** | `Pomona06` |
| **Repositorio** | https://github.com/Pomona06/MK-taller-git-2026 |
| **Enlace al commit de la solución** | Ver «Enlace al commit de la solución» al principio del README |

---

## 1. Objetivo

Publicar un servicio HTTP con Spring Boot (Maven, Java 21, Spring Web) sobre el modelado de **armas de
Counter-Strike 2**. El modelo tiene una clase base abstracta `Armas` y sus clases hijas: armas de fuego
(`Rifle`, `Pistola`, `Sniper`) y `Granada`. Sobre ese modelo se aplican:

- herencia;
- **sobreescritura** del método abstracto `disparar()`;
- **sobrecarga** de constructores y del mensaje `disparar`;
- ocultamiento de la información (munición y cooldown privados);
- paquetes según el template de la cátedra.

Un compañero tiene que poder clonar el repositorio, arrancar el servicio con `./mvnw spring-boot:run` y
usarlo desde el navegador, sin abrir un `main()` en el entorno de desarrollo.

---

## 2. Consignas aplicadas al dominio

### 2.1 Paquetes según el template `py.edu.uc.lp3`

| Paquete | Contenido |
|---|---|
| `py.edu.uc.lp3` | `Application`: solo arranca Spring Boot |
| `py.edu.uc.lp3.domain` | `Vendible`, `Armas`, `ArmasDeFuego`, `Rifle`, `Pistola`, `Sniper`, `Granada` (reglas del juego) |
| `py.edu.uc.lp3.interfaces` | `Cotizable`, `VideoJuegoPosicionable`, `Avatar`, `Posicion` |
| `py.edu.uc.lp3.rest.controller` | `IndexController`, `ArmaController`, `ManejadorErrores` (entrada HTTP) |

El dominio no conoce nada de HTTP y los controllers no contienen reglas del juego.

### 2.2 Jerarquía

```
Vendible
└── Armas  (abstracta)
    ├── ArmasDeFuego  (abstracta)
    │   ├── Rifle
    │   ├── Pistola
    │   └── Sniper
    └── Granada
```

### 2.3 Método abstracto y sobreescritura

- `Armas` declara `public abstract String disparar()`. Toda arma tiene que saber disparar, pero el padre no
  puede resolverlo, porque cada tipo responde distinto.
  - **`ArmasDeFuego`** lo implementa gastando balas del cargador y calculando el daño.
  - **`Granada`** lo implementa lanzándose y explotando: hace daño o aturde, y respeta un cooldown.
- `ArmasDeFuego` declara dos métodos abstractos más, `balasPorDisparo()` y `calcularDano()`.
  `Rifle`, `Pistola` y `Sniper` los sobreescriben, cada uno a su modo:
  - Rifle en ráfaga: 3 balas y 80 % del daño.
  - Sniper sin mira: 40 % del daño.
- El controller habla con todas como `Armas`. El texto del JSON sale del método sobreescrito.

### 2.4 Constructores simples y sobrecargados

Cada clase hija tiene cuatro constructores. Todos encadenan con `this(...)` hasta el completo, que es el
único que llama a `super(...)`:

| Clase | Constructores |
|---|---|
| `Rifle` | `Rifle()` (AK-47) · `Rifle(String nombre)` · `Rifle(String nombre, boolean modoRafaga)` · completo |
| `Pistola` | `Pistola()` (Glock-18) · `Pistola(String nombre)` · `Pistola(String nombre, String equipo)` · completo |
| `Sniper` | `Sniper()` (AWP) · `Sniper(String nombre)` · `Sniper(String nombre, String equipo)` · completo |
| `Granada` | `Granada()` (HE) · `Granada(String nombre)` · `Granada(String nombre, boolean aturde)` · completo |

Como todos pasan por las validaciones de `Armas` y `ArmasDeFuego`, ninguna firma deja el objeto en un
estado ilegal.

### 2.5 Sobrecarga de un mensaje del dominio

`Armas` declara `disparar()` y `disparar(double distanciaMetros)`: la misma acción con otra lista de
argumentos.

- **Arma de fuego**: fuera de su alcance efectivo el daño baja a la mitad. El alcance es de 30 m para el
  rifle, 15 m para la pistola y 200 m para el sniper.
- **Granada**: si la distancia supera su alcance máximo, no llega.

### 2.6 Ocultamiento e invariantes

- `carga`, `cargadoresRestantes` y el cooldown de la granada son `private` y no tienen setter. La munición
  solo cambia con `disparar()` y `recargar()`.
- `Vendible` tiene `precio` privado y `setPrecio()` protegido, que rechaza valores nulos o negativos.
- Los constructores rechazan, con `IllegalArgumentException`:
  - nombre vacío;
  - equipo distinto de `CT` o `TT`;
  - precio, peso o daño negativos;
  - cargador de capacidad 0;
  - precisión fuera de 0 a 1;
  - visibilidad reducida fuera de 0 a 100;
  - distancia negativa.
- `ManejadorErrores` traduce ese rechazo a **HTTP 400** con el mensaje de la clase.

### 2.7 Servicios REST

| Endpoint | Qué hace |
|---|---|
| `GET /` | `IndexController`: confirma que el servicio está vivo y lista los endpoints. |
| `GET /armas/crear?tipo=...` | Construye con el constructor completo usando los parámetros de la URL. |
| `GET /armas/crear-simple?tipo=...` | Construye con los constructores sobrecargados, según qué parámetros lleguen. |
| `GET /armas/comparar` | Le pide `disparar()` a un Rifle, una Pistola, un Sniper y una Granada, tratados como `List<Armas>`. |

`tipo` puede ser `rifle`, `pistola`, `sniper` o `granada`. El parámetro opcional `distancia` usa la
sobrecarga `disparar(double)`.

### 2.8 Documentación

- `README.md`: licencia Apache 2.0, diagrama Mermaid alineado con `src/` y apartado de sobrecarga y
  sobreescritura.
- `BITACORA.md`: asistente de IA, modelo y resumen de prompts.

---

## 3. Cómo probarlo

### 3.1 Clonar y arrancar

```bash
git clone https://github.com/Pomona06/MK-taller-git-2026.git
cd MK-taller-git-2026
./mvnw spring-boot:run
```

Requiere Java 21. El servicio queda en http://localhost:8080. Las URL de abajo se pueden abrir en el
navegador, en Insomnia o con `curl`.

### 3.2 Casos de prueba

| # | Pedido | Resultado esperado |
|---|---|---|
| 1 | `GET /` | 200 con `"estado":"vivo"` y la lista de endpoints. |
| 2 | `GET /armas/crear?tipo=rifle&nombre=AK-47&modoRafaga=true` | 200, `tipoConcreto: Rifle`. Dispara 3 balas con daño reducido a 24 y quedan 27/30 balas. |
| 3 | `GET /armas/crear-simple?tipo=sniper` | 200, `constructor: Sniper()`. Es un AWP de 4750 $ que, sin mira, causa 46 de daño. |
| 4 | `GET /armas/crear-simple?tipo=granada&nombre=Flashbang&aturde=true` | 200, `constructor: Granada(String, boolean)`. Aturde y reduce la visibilidad un 100 % en vez de hacer daño. |
| 5 | `GET /armas/crear-simple?tipo=pistola&nombre=USP-S&equipo=CT&distancia=25` | 200, `mensajeDisparo: disparar(double)`. Queda fuera del alcance de 15 m, así que el daño baja a 15. |
| 6 | `GET /armas/comparar` | 200 con cuatro elementos: el mismo `disparar()` con cuatro respuestas distintas (Rifle, Pistola, Sniper y Granada). |
| 7 | `GET /armas/comparar?distancia=25` | 200. La pistola pierde daño y la granada «no llega a 25.0m». |
| 8 | `GET /armas/crear?tipo=rifle&dano=-5` | **400** con `{"error":"precio, peso y daño no pueden ser negativos"}`. |
| 9 | `GET /armas/crear?tipo=rifle&equipo=XYZ` | **400** con `{"error":"equipo debe ser CT o TT (recibido: XYZ)"}`. |
| 10 | `GET /armas/crear?tipo=hacha` | **400** con `{"error":"tipo debe ser rifle, pistola, sniper o granada (recibido: hacha)"}`. |

Ejemplo de respuesta del caso 6:

```json
[
  {"tipoConcreto":"Rifle","nombre":"AK-47","disparo":"AK-47 disparó (1 balas, precisión 0.75) causando 36 de daño. Quedan 29/30 balas."},
  {"tipoConcreto":"Pistola","nombre":"Glock-18","disparo":"Glock-18 disparó (1 balas, precisión 0.6) causando 30 de daño. Quedan 19/20 balas."},
  {"tipoConcreto":"Sniper","nombre":"AWP","disparo":"AWP disparó (1 balas, precisión 0.95) causando 46 de daño. Quedan 4/5 balas."},
  {"tipoConcreto":"Granada","nombre":"HE","disparo":"Lanzada a 20.0m. HE explotó (radio 5.0m, causa 57 de daño)"}
]
```

### 3.3 Tests automáticos

```bash
./mvnw test
```

Corre 11 tests y tiene que terminar en `BUILD SUCCESS`:

- **`ArmasTest`**: constructores legales e ilegales, sobreescritura de `disparar()`, sobrecarga
  `disparar(double)` y munición que solo cambia disparando o recargando.
- **`ArmaControllerTest`**: los cuatro endpoints y las respuestas 400.

### 3.4 Pregunta de anclaje

> ¿Qué ocurre si el controller asigna a mano la vida o la munición?

No puede: `carga` y `cargadoresRestantes` son `private` y no tienen setter. Si el controller manda un
valor inválido al constructor, la clase lo rechaza y el servicio responde 400.

---

## 4. Enlace al commit

El enlace exacto al commit de la solución está al principio del [README](../README.md) y en la entrega de Classroom.
