# Bitácora de asistencia de IA

Alumno: Marcelo Kropff (Y43819)
Ejercicio: POO-06, revisión, paquetes, constructores y sobrecarga (Counter-Strike 2)

## Asistente y modelo

| Dato | Valor |
|---|---|
| Marca del asistente o agente | Claude Code (Anthropic), sesión web en claude.ai/code |
| Modelo exacto del LLM | _(completar con el modelo que muestra la herramienta)_ |

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

## Verificación propia (marcar al hacerla)

- [ ] Corrí `./mvnw spring-boot:run` y probé los curl del README.
- [ ] Corrí `./mvnw test`.
- [ ] Leí el diff de cada commit antes de pasarlo a `main`.
