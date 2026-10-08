# Bitácora de asistencia de IA

Alumno: Marcelo Kropff (Y43819)
Ejercicio: POO-06, revisión, paquetes, constructores y sobrecarga (Counter-Strike 2)

## Asistente y modelo

Usé el mismo asistente y el mismo modelo en todos los chats: taller de Git, ejercicio POO-06 y revisión final.

| Dato | Valor |
|---|---|
| Marca del asistente o agente | Claude Code (Anthropic), sesión web en claude.ai/code |
| Modelo exacto del LLM | _claude-opus-5-5 alto |

## Resumen de prompts

### Taller de Git

- Le pasé la consigna del taller y elegí la opción B de Counter Strike 2. Le describí de memoria el diagrama que tenía de la clase (Arma, armas de fuego, granadas, rifles y pistolas) y le pedí que me diga qué tenía que corregir para cumplir la consigna. Después le pedí agregar disparo único o ráfaga a la pistola.
- Le pedí que me explique paso a paso qué tenía que hacer, desde crear el repo hasta la entrega, porque no sabía por dónde empezar.
- Le pasé capturas de GitHub y de start.spring.io para que me diga qué marcar al crear el repo y el proyecto.
- Al correr mvnw me tiró error porque no encontraba la carpeta .mvn. Le pregunté qué pasaba y era que al descomprimir con el explorador no se copiaron los archivos ocultos.
- Le pedí cómo mover todo el contenido de una carpeta a otra por terminal. Me equivoqué escribiendo el nombre de la carpeta, puse shopt y mv en la misma línea, y terminé dejando pom.xml, mvnw y .mvn adentro de src. Le pasé el ls para que me ayude a ordenarlo.
- Al hacer el primer commit Git no tenía configurado mi nombre y mail. Después el push falló porque GitHub ya no acepta contraseña. Le pedí ayuda para crear un token y como seguía fallando probamos el token con curl y lo pusimos en la URL del remoto.
- Le pedí corregir el paquete, que había quedado con com duplicado por cómo completé el formulario de Spring Initializr.
- Le pedí que me genere las clases del modelo y los dos controllers. Las copié, compilé y probé los endpoints con curl. La primera vez falló porque había cerrado la terminal donde corría el servidor.
- Le pedí el README y la bitácora para Classroom. Después le pedí que los reescriba para que suenen como los escribiría yo, sin colores ni adornos, y los corregí a mano.
- No veía el diagrama Mermaid en VS Code. Le pregunté y era que miraba el editor y no la vista previa.
- Una notificación de VS Code abrió un agente de Copilot que quería subir Java a la versión 25 y yo le daba permiso a todo sin saber qué hacía. Le pedí ayuda para volver atrás. El agente había creado su propia rama y cambiado el pom.xml, así que volví a main, borré la rama, descarté los cambios y borré la carpeta que había dejado.
- Después me di cuenta de que el modelo tenía que partir del código que dio el profesor en el Zulip (Vendible, Armas, ArmasDeFuego) y no del diagrama que yo había escrito de memoria. Le pasé el zip y le pedí rehacer el modelo a partir de esas clases. Al compilar faltaba Avatar.java porque no lo había copiado.
- Le pasé el repo de la guía y le pedí que me diga qué me faltaba comparado con lo que pedía. Hice el git log y el checkout al primer commit. git log se quedó trabado en el paginador y no sabía salir. El checkout no me dejó porque tenía cambios sin guardar. Después configuré la clave SSH. La parte de colaboración con un compañero la dejé porque no tenía con quién hacerla.

### Ejercicio POO-06

- Le pasé el nuevo enunciado, la rúbrica y el template de paquetes, y le pedí que revise todo el repo y me diga qué cumplía y qué faltaba.
- Me dijo que los paquetes no seguían el template, que no había constructores sobrecargados ni sobrecarga de un mensaje, que un precio negativo terminaba en error 500 sin mensaje y que el test no encontraba la clase Application. También me avisó que el token de GitHub estaba escrito en la descripción del proyecto, así que lo borré.
- Le pedí que me arme los cambios: mover las clases a py.edu.uc.lp3 con domain y rest.controller, agregar a cada arma un constructor simple y uno completo, agregar disparar(distancia), validar los valores para que ningún objeto quede en un estado imposible y agregar un endpoint con el JSON de cada clase hija.
- Le pedí que me diga paso a paso dónde ir y qué comandos correr para subir todo en commits separados.
- Le pedí actualizar el README con la parte de sobrecarga y sobreescritura, esta bitácora y el documento de especificaciones para Classroom.

### Revisión final con Claude Code

- Le pedí revisar el repo contra el enunciado y la rúbrica y que me diga qué faltaba.
- Le pedí que implemente lo que faltaba en commits separados, y lo pasé a main con un pull request.
- Le pedí que me guíe en los pasos de la entrega, que arme el documento de especificaciones y que verifique que el repo cumpla todos los criterios.
- Le hice consultas sobre el commit de la solución y sobre qué tenía que incluir esta bitácora.

