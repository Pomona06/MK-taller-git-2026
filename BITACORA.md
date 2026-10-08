# Bitácora de asistencia de IA

Alumno: Marcelo Kropff (Y43819)
Ejercicio: POO-06, revisión, paquetes, constructores y sobrecarga (Counter-Strike 2)

## Asistente y modelo

| Dato | Valor |
|---|---|
| Marca del asistente o agente | Claude Code (Anthropic), sesión web en claude.ai/code |
| Modelo exacto del LLM | _claude-opus-5-5 alto |

<!-- Si en el taller anterior (commits del 25/09) usaste otra IA, agregá una fila más con su marca y modelo. -->

## Resumen de prompts

1. **Revisión contra la rúbrica.** Le pasé el zip del repositorio, mi bitácora del taller de Git en PDF, el
   enunciado POO-06, la rúbrica de ocho criterios y el template de paquetes. Le pedí que revisara todo en
   detalle, me dijera si cumplía cada indicador y me armara una lista paso a paso con lo que faltaba.
   - El asistente compiló y levantó el servicio y probó los endpoints. Encontró que:
     - el test fallaba;
     - los paquetes no seguían el template;
     - había un solo constructor por clase y ningún mensaje sobrecargado;
     - `setPrecio()` era público;
     - un valor inválido devolvía 500 en lugar de 400;
     - faltaban esta bitácora y el apartado del README.
2. **Implementación.** Le pedí que implementara los pasos 1 a 8 de esa lista, con un commit por paso:
   1. mover las clases a `py.edu.uc.lp3.domain` y `py.edu.uc.lp3.rest.controller`, y arreglar el test;
   2. validar las reglas en los constructores, cerrar `Vendible` y responder 400 con el mensaje de la clase;
   3. agregar constructores sobrecargados encadenados con `this(...)` y el endpoint `/armas/crear-simple`;
   4. sobrecargar `disparar(double distancia)`;
   5. agregar `/armas/comparar` para ver `disparar()` de cada clase hija en un solo JSON;
   6. agregar tests del dominio y de los controllers;
   7. actualizar el README con la licencia, el Mermaid y el apartado de sobrecarga y sobreescritura;
   8. crear esta bitácora.

   Desde la interfaz de Claude Code creé el pull request #1 con esos commits.
3. **Qué hacer después.** Le dije que no entendía qué me tocaba hacer y le pedí los pasos uno por uno,
   indicando dónde se hace cada uno (GitHub, Classroom o la terminal). Me dio el orden:
   1. revisar el PR;
   2. completar el modelo en la bitácora;
   3. mergear con «Create a merge commit»;
   4. copiar el enlace al commit;
   5. ponerlo en el README;
   6. entregar en Classroom;
   7. pasar mi usuario al chat del curso.
4. **Archivo de especificaciones.** Le pedí que hiciera el archivo para Classroom. Armó
   `docs/ESPECIFICACIONES.md` con:
   - objetivo;
   - consignas aplicadas a las armas de CS2;
   - cómo probarlo: arranque, diez casos con la respuesta esperada (comprobados con el servicio levantado),
     tests y pregunta de anclaje.
5. **Ajuste de la bitácora.** Le aclaré que en la línea del modelo dejo `alto` porque trabajé con el nivel
   alto de esfuerzo, y le pedí los pasos que faltaban sin esa corrección.
6. **Después del merge.** Le avisé que el PR estaba mergeado. Revisó `main`, me confirmó que el commit de la
   solución es el del merge y me devolvió el archivo de especificaciones con ese enlace ya puesto.
7. **Verificación final.** Le pedí que verificara si el repositorio cumplía todos los indicadores. Clonó
   `main` desde cero y comprobó lo siguiente:
   - compila;
   - pasan los 11 tests;
   - arranca con `./mvnw spring-boot:run`;
   - los endpoints responden, también los casos que deben dar 400;
   - no hay archivos de compilación en Git.

   Después lo comparó criterio por criterio con la rúbrica. Lo que quedaba pendiente era de mi lado:
   entregar en Classroom y escribir en el chat.
8. **Dudas sobre la entrega.** Le pregunté:
   - qué era el commit `dd82cd8`: es el merge del PR, la foto de la solución completa;
   - si hacía falta una lista de commits: no la pide la rúbrica, porque el historial se ve en GitHub.
9. **Esta bitácora.** Le pedí que el resumen incluyera todos los prompts de la sesión, no solo los de la
   implementación, y la actualizó.

## Verificación propia (marcar al hacerla)

- [ ] Corrí `./mvnw spring-boot:run` y probé los curl del README.
- [ ] Corrí `./mvnw test`.
- [ ] Leí el diff de cada commit antes de pasarlo a `main`.
