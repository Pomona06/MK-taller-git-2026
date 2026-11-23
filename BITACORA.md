# Bitácora de asistencia de IA

Alumno: Marcelo Kropff (Y43819)
Ejercicios: POO-06 (revisión, paquetes, constructores y sobrecarga), interfaces y POO-08 (CRUD REST, H2 e Insomnia, con su corrección) y POO-09 (APIs cruzadas), sobre Counter-Strike 2

## Asistente y modelo

Usé el mismo asistente y el mismo modelo en todos los chats: taller de Git, ejercicio POO-06, revisión final, ejercicio de interfaces, POO-08, su corrección y POO-09.

| Dato | Valor |
|---|---|
| Marca del asistente o agente | Claude Code (Anthropic), sesión web en claude.ai/code |
| Modelo exacto del LLM | `claude-opus-5-5` (Claude Opus 5.5, nivel de esfuerzo «high») |

## Resumen de prompts

### Taller de Git

- Le pasé la consigna del taller y elegí la opción B de Counter-Strike 2. Le describí de memoria el diagrama que tenía de la clase (Arma, armas de fuego, granadas, rifles y pistolas) y le pedí que me dijera qué tenía que corregir para cumplir la consigna. Después le pedí agregar disparo único o ráfaga a la pistola.
- Le pedí que me explicara paso a paso qué tenía que hacer, desde crear el repo hasta la entrega, porque no sabía por dónde empezar.
- Le pasé capturas de GitHub y de start.spring.io para que me dijera qué marcar al crear el repo y el proyecto.
- Al correr mvnw me tiró error porque no encontraba la carpeta .mvn. Le pregunté qué pasaba y era que al descomprimir con el explorador no se copiaron los archivos ocultos.
- Le pregunté cómo mover todo el contenido de una carpeta a otra por terminal. Me equivoqué escribiendo el nombre de la carpeta, puse shopt y mv en la misma línea, y terminé dejando pom.xml, mvnw y .mvn adentro de src. Le pasé el ls para que me ayudara a ordenarlo.
- Al hacer el primer commit, Git no tenía configurado mi nombre y mail. Después el push falló porque GitHub ya no acepta contraseña. Le pedí ayuda para crear un token y, como seguía fallando, probamos el token con curl y lo pusimos en la URL del remoto.
- Le pedí corregir el paquete, que había quedado con com duplicado por cómo completé el formulario de Spring Initializr.
- Le pedí que me generara las clases del modelo y los dos controllers. Las copié, compilé y probé los endpoints con curl. La primera vez falló porque había cerrado la terminal donde corría el servidor.
- Le pedí el README y la bitácora para Classroom. Después le pedí que los reescribiera para que sonaran como los escribiría yo, sin colores ni adornos, y los corregí a mano.
- No veía el diagrama Mermaid en VS Code. Le pregunté y era que miraba el editor y no la vista previa.
- Una notificación de VS Code abrió un agente de Copilot que quería subir Java a la versión 25 y yo le daba permiso a todo sin saber qué hacía. Le pedí ayuda para volver atrás. El agente había creado su propia rama y cambiado el pom.xml, así que volví a main, borré la rama, descarté los cambios y borré la carpeta que había dejado.
- Después me di cuenta de que el modelo tenía que partir del código que dio el profesor en el Zulip (Vendible, Armas, ArmasDeFuego) y no del diagrama que yo había escrito de memoria. Le pasé el zip y le pedí rehacer el modelo a partir de esas clases. Al compilar faltaba Avatar.java porque no lo había copiado.
- Le pasé el repo de la guía y le pedí que me dijera qué me faltaba comparado con lo que pedía. Hice el git log y el checkout al primer commit. El git log se quedó trabado en el paginador y no sabía salir. El checkout no me dejó porque tenía cambios sin guardar. Después configuré la clave SSH. La parte de colaboración con un compañero la dejé porque no tenía con quién hacerla.

### Ejercicio POO-06

- Le pasé el nuevo enunciado, la rúbrica y el template de paquetes, y le pedí que revisara todo el repo y me dijera qué cumplía y qué faltaba.
- Me dijo que los paquetes no seguían el template, que no había constructores sobrecargados ni sobrecarga de un mensaje, que un precio negativo terminaba en error 500 sin mensaje y que el test no encontraba la clase Application. También me avisó que el token de GitHub estaba escrito en la descripción del proyecto, así que lo borré.
- Le pedí que me armara los cambios: mover las clases a py.edu.uc.lp3 con domain y rest.controller, agregar a cada arma un constructor simple y uno completo, agregar disparar(distancia), validar los valores para que ningún objeto quede en un estado imposible y agregar un endpoint con el JSON de cada clase hija.
- Le pedí que me dijera paso a paso dónde ir y qué comandos correr para subir todo en commits separados.
- Le pedí actualizar el README con la parte de sobrecarga y sobreescritura, esta bitácora y el documento de especificaciones para Classroom.

### Revisión final con Claude Code

- Le pedí que revisara el repo contra el enunciado y la rúbrica y que me dijera qué faltaba.
- Le pedí que implementara lo que faltaba en commits separados, y lo pasé a main con un pull request.
- Le pedí que me guiara en los pasos de la entrega, que armara el documento de especificaciones y que verificara que el repo cumpliera todos los criterios.
- Le hice consultas sobre el commit de la solución y sobre qué tenía que incluir esta bitácora.

### Ejercicio de interfaces

- Le pasé la consigna de la clase y el ejemplo de la pizarra, y le pedí ayuda para resolverla con mi modelo de CS2.
- Elegimos como capacidad compartida el sonido: la implementan la Granada y una clase nueva, la bomba C4, que no es un arma. La C4 reemplaza el alcance por defecto porque se escucha en todo el mapa.
- Le pedí que agregara el servicio REST, los tests, la sección del README y esta parte de la bitácora en commits separados.

### Ejercicio POO-08 (CRUD REST, H2 e Insomnia)

- Le pasé lo que se escribió en el Zulip de la clase y capturas de Spring Initializr y de la consola H2, porque no había entendido qué se pedía. Encontró el enunciado POO-08 en el repo de la cátedra y lo resolvió sobre mis armas.
- Le pedí que guardara la familia de armas en H2 con un CRUD en tres capas (controller, servicio y repositorio), con una simulación de daño y de munición en la que participaran tres clases, una jerarquía de excepciones con un handler por tipo, la colección de Insomnia con el caso feliz y cada error, y el README con las dos reglas.
- Lo hizo en commits separados y comprobó que todo funcionara: tests, la colección completa contra el servicio, los datos después de reiniciar y la tabla en H2.

### POO-08: revisión final y persistencia del jugador

- Le pasé otra vez el enunciado de POO-08, la rúbrica y el template, y le pedí que verificara todo lo que
  pedían, qué ya estaba hecho y que me dijera paso a paso qué tenía que entregar y si hacían falta capturas.
- Me marcó que la simulación de daño creaba el jugador y el chaleco en memoria, así que la regla de vida no
  quedaba guardada y un GET posterior solo mostraba la munición. Le pedí que guardara Jugador y Chaleco
  en H2, con un endpoint /api/jugadores, y que la simulación recibiera armaId y jugadorId.
- También le pedí completar lo que faltaba en el README: la familia de excepciones en el diagrama, por
  qué las excepciones son no comprobadas, las respuestas de «Dos reglas» y «Excepciones», y arreglar el
  enlace al commit, que estaba mal escrito.
- Le pedí el documento de especificaciones de POO-08 para Classroom, porque el que estaba era el de POO-06.
- Corrió los tests, recorrió la colección de Insomnia contra el servicio, reinició para ver que los datos
  seguían y miró las tres tablas en la consola H2. Lo subió en commits separados.

### Corrección de POO-08

- Le pasé el PDF de la corrección del profesor (17 de 24) y le reclamé que me había dicho que estaba todo
  bien. Revisó la corrección criterio por criterio y reconoció qué errores eran suyos: la ortografía del
  README, una frase vieja que todavía hablaba de `IllegalArgumentException` y la granada que se podía usar
  infinitas veces. También me señaló que el criterio de poner las validaciones en los servicios no coincidía
  con lo que decía el enunciado.
- Le pasé la nota que el profesor escribió para toda la materia sobre el uso de la IA.
- Le pedí que me dijera qué correcciones había que hacer. Corrió un corrector ortográfico, leyó a mano el
  README, la bitácora y los comentarios, y revisó el código preguntándose si cada dato podía pasar en el
  juego. Encontró más errores de los que había marcado el profesor y datos imposibles en CS2, como el AK-47
  en ráfaga.
- Elegí la opción de un catálogo con las armas reales de CS2. Como las wikis estaban bloqueadas en su red,
  sacó los valores de los archivos del juego que publica SteamDatabase en GitHub.
- Hizo los cambios en commits con prefijos semánticos: el catálogo; las validaciones, las reglas y las
  excepciones en los servicios; la granada de un solo uso; los tests; la colección de Insomnia y la
  documentación. Recorrió la colección completa contra el servicio y revisó la ortografía antes de darme el
  resultado.

### POO-09 (APIs cruzadas): preparación antes de armar el grupo

- Le pasé la consigna de POO-09 con los enlaces al enunciado, a los grupos y al ejemplo UNO, y le pregunté
  qué hacía falta, si necesitaba compañeros para empezar y si era el último trabajo. Leyó los tres
  documentos: sin grupo no se puede entregar, la corrección de POO-08 vale 3 de los 15 puntos y en el
  repositorio de la cátedra POO-09 es el último publicado, pero ningún documento dice que sea el final.
- Le pregunté si se recuperaban los puntos de POO-08. Me mostró que el enunciado dice que POO-08 no se
  vuelve a calificar con la rúbrica de 24, y que la corrección vale esos 3 puntos dentro de POO-09.
- Le pedí que hiciera lo que no depende de los compañeros: la sección del README con cada observación de
  POO-08 y cómo se cerró, el OpenAPI publicado y lo que tengo que pasarles a mis compañeros.
- Agregó endpoints para consultar mis dos reglas sin tocar la base, así un compañero me consulta sin
  modificar mis datos. Publicó el OpenAPI con springdoc (eligió la versión 3.2.0 porque la 3.2.1, de un día
  antes, era para otra versión de Spring Boot), lo validó y revisó que cada operación muestre solo sus
  errores. Escribió la ficha docs/PARA-COMPANEROS.md y probó que el servicio arranca en otro puerto.

### POO-09: el escenario con el grupo

- Le pasé los datos que me mandaron Joaquín y Matías. Revisó sus repositorios: el `main` de Joaquín todavía era
  su POO-08 y el de Matías no tenía springdoc, así que les pedí que subieran POO-09. Quiso arrancar sus
  servicios para leer el OpenAPI, pero el control de permisos de la sesión no lo dejó, y el enunciado pide
  usar solo su OpenAPI y sus reglas escritas; quedé en pedirles el JSON de su OpenAPI.
- Cuando subieron POO-09, leyó sus README: los puertos acordados (Marcelo 8082, Joaquín 8083, Matías 8084), el
  contrato de daño de Joaquín y la regla de disparo rápido de Matías. Acepté el escenario que propuso:
  disparo rápido con francotirador, con Matías como A2 y Joaquín como A3.
- Lo hizo en commits separados: equipo (CT o TT) en los jugadores, que hace falta para el fuego amigo; los
  clientes HTTP con las URL configurables; el escenario, que consulta a los dos antes de tocar la base y guarda
  la jugada en una transacción aparte; la colección de Insomnia con un script para guardar las respuestas
  reales; y la documentación.
- Lo probó con servidores falsos que responden como cada compañero. Así encontró que el cliente mandaba el
  JSON por partes y lo cambió para que vaya con su largo, y que los nombres con acento de
  `application.properties` salían mal porque Spring lee ese archivo en ISO-8859-1.
