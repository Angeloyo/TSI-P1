# Técnicas de los Sistemas Inteligentes
## Grado en Informática

### Curso 2024-25. Práctica 1
**Técnicas de Búsqueda Heurística**  
Pablo Mesejo Santiago y David Criado Ramón

## Entorno GVGAI

### General Video Game AI (GVGAI)

* Website: https://github.com/GAIGResearch/GVGAI
* GVGAI: una competición para agentes de IA para
  * Jugar a videojuegos
  * Generación de contenido (mapas de niveles y reglas de juego)
* Los agentes desarrollados se evalúan sobre videojuegos no vistos previamente
* +160 Videojuegos
* Los juegos se describen en el lenguaje VGDL (Video Game Description Language)
  * Schaul, T., 2013, August. A video game description language for model-based or interactive learning. In 2013 IEEE Conference on Computational Intelligence in Games (CIG) (pp. 1-8). IEEE.

### Tiempo Real
* Tiempo límite reacción: 40 ms
* Si devuelves acción en [40,50] ms, se aplica NIL
* Si devuelves acción en > 50 ms, pierdes juego. Descalificado

## Entorno GVGAI - VIDEOJUEGOS

### Descripciones de los videojuegos y vídeos de ejemplo
**GVGAI CHANNEL**  
https://www.youtube.com/channel/UCMFCfXipQT55IK6R504naUQ

### Tipos de juegos
* Un jugador / varios
* Total / parcialmente observables
* Puzzles vs Carreras
* Acción / Aventuras
* Recolectar, disparar, navegar
* Cooperativos / Competitivos

## Entorno GVGAI - TRACKS

Varios Tracks: está concebido para probar distintas técnicas de IA en videojuegos tanto para jugar a videojuegos como para generar contenido.

### Jugar
* Single Player Track
* Multiple Player Track
* Single Player Learning Track

### Generar contenido
* Level Generation Track
* Rule Generation Track
* GameDesign Track

## Entorno GVGAI - INSTALACIÓN

### Instalación del GVG Framework

* Descargad el zip directamente desde
  * https://github.com/GAIGResearch/GVGAI/archive/master.zip
  * https://pradogrado2425.ugr.es/mod/resource/view.php?id=340333

* Enlace de posible utilidad o interés:
  * https://gaigresearch.github.io/gvgaibook/

### Instalación del GVG Framework con ECLIPSE
(hay que tener instalado Java y Eclipse)

* Descomprimir en un <directorio>
* Abrir Eclipse.
* File → New → Java Project
* Untick 'default location' y seleccionar <directorio> en Location → Finish
* Desmarcad la casilla 'Create module-info.java file' (en caso de que esté marcada).

## Entorno GVGAI - EJECUCIÓN

### Ejecución del GVG Framework
* En Package Explorer, navegar hasta
  * Src.tracks.singlePlayer → Test.java
  * Ejecutar Test.java

### Ejecución del GVG Framework

* Por defecto el entorno de juego carga el juego "0" (gameIdx) y en modo jugador humano (ArcadeMachine.playOneGame).
* La estructura del código y documentación básica está en https://github.com/EssexUniversityMCTS/gvgai/wiki/Code-Structure

## Entorno GVGAI - EJECUCIÓN. MODIFICACIÓN

### Modificación del GVG Framework
src.tracks.singlePlayer.Test.java

Consultad este fichero.  
Hay numerosos juegos disponibles para SinglePlayer.  
Cambiad este valor y ejecutad para ver otros juegos.

## Entorno GVGAI - VIDEO GAME DESCRIPTION LANGUAGE (VGDL)

### ¿Cómo podemos saber cómo se definen, por ejemplo, los mapas del juego BoulderDash (id 11)?
1. Abrir all_games_sp.csv
2. Abrir la ruta relativa a boulderdash.txt
3. Ver en boulderdash.txt cómo se definen los objetos del mapa
4. Ver el mapa del nivel 0 de BoulderDash y cómo se visualiza en el juego en la práctica

## Entorno GVGAI - TÉCNICAS EN SINGLE PLAYER PLANNING

### Single Player Planning
GVGAI proporciona agentes básicos para jugar (llamados "controllers") que pueden intercambiarse fácilmente.

* Técnicas:
  * Aleatorio
  * No hacer nada
  * Avanzar una etapa (greedy)
  * Algoritmo genético
  * MCTS (Monte Carlo Tree Search)

## Entorno GVGAI - EXPERIMENTAR

### Experimentar con un agente deliberativo (single player planning agent)
1. Encontrar la clase src.tracks.singlePlayer.Test.java
2. Asignar gameIdx y levelIdx con distintos valores
3. Seleccionar modo de ejecución:
   1. Jugar como humano:
   ```java
   // 1. This starts a game, in a level, played by a human.
   ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);
   ```
   2. Jugar como IA (con un "controller"): Usar (o crear) una variable String conteniendo el path de la clase de un agente (pre)diseñado.
   ```java
   String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
   ...
   // 2. This plays a game in a level by the controller.
   ArcadeMachine.runOneGame(game, level1, visuals, sampleMCTSController, recordActionsFile, seed, 0);
   ```
4. Run Test.java

## Ejemplo Código - CAMEL RACE

### Ejemplo de agente deliberativo para Camel Race
Escogemos el juego de Carrera de Camellos (int gameIdx = 15) y el primer nivel de juego (int levelIdx = 0).

Queremos que el agente escoja el portal de salida más cercano, y luego, de modo voraz, escoja la acción que le acerque más al portal. Es decir, queremos escoger en todo momento, y de modo inmediato, la acción más conveniente para alcanzar el objetivo de llegar al destino y ganar la carrera.

### Código de ejemplo
src/tracks/singlePlayer/evaluacion/src_APELLIDO1_APELLIDO2_NOMBRE/myAgent_Camel.java

```java
package tracks.singlePlayer.evaluacion.src_APELLIDO1_APELLIDO2_NOMBRE;

public class myAgent_Camel extends AbstractPlayer{
    // Aquí van las variables de estado del agente
    // Constructor del agente
    public myAgent_Camel(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // ...
    }
    // Método act
    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // Se llama en cada tick y es el encargado de utilizar el estado interno
        // del agente y la información de los sensores (stateObs) para decidir la
        // siguiente acción (ACTIONS)
        // …
    }
}
```

### Código de ejemplo: myAgent_Camel.java
Acceso a dimensiones y posiciones:

* `getWorldDimension().height`: 144
* `getObservationGrid()[0].length`: 9
* `stateObs.getWorldDimension().width`: 768 
* `stateObs.getObservationGrid().length`: 48

Para una alternativa de cara a obtener este factor de escala, revísese `stateObs.getBlockSize();`

Posición de los portales en coordenadas píxel:
Del primer tipo de portal (podría haber varios) cogemos el primer elemento (el más próximo)

### Acceso a recursos y objetos
Del mismo modo que podemos acceder a los portales, podemos acceder a todo tipo de recursos/objetos:

* Para obtener la posición de los enemigos: `stateObs.getImmovablePositions()`
* Para obtener la posición de los muros y otros obstáculos: `stateObs.getImmovablePositions()`
* Para obtener la posición de las gemas en Boulderdash o las capas en LabyrinthDual: `stateObs.getResourcesPositions()`

Estas llamadas suelen ser costosas. Intentad minimizarlas.

Nota: Revisad el tutorial de GVG-AI que adjuntamos con la práctica. Contiene esta información y mucha más.

### Estrategia para Camel Race
En esencia:
1) Calculamos la posición del avatar en coordenadas grid.
2) Vemos a dónde se movería con todas y cada una de las 4 acciones posibles.
3) Calculamos la distancia de las nuevas posiciones al portal.
4) Escogemos la acción asociada con la distancia más corta. Es decir, escogemos la acción que nos acerca de modo inmediato al objetivo.

## Práctica 1 - LABYRINTH DUAL

La Práctica 1 consiste en desarrollar cuatro controladores basados en técnicas deliberativas de búsqueda dentro del entorno GVGAI que guíen a un avatar a resolver un juego en distintos niveles. El juego escogido es el Labyrinth Dual disponible con el índice 59 en los tipos de juego "singleplayer" que se pueden encontrar en el fichero "all_games_sp.csv".

4 acciones:
* Izquierda
* Derecha
* Arriba
* Abajo

4 tipos de casilla:
* Avatar
* Objetivo
* Trampas
* Muros "inamovibles"
* Muros que se pueden traspasar si se tiene la capa roja
* Muros que se pueden traspasar si se tiene la capa azul

### Objetivo de la práctica
El objetivo de la práctica es que los estudiantes refuercen conocimientos y se familiaricen con técnicas deliberativas de búsqueda.

Para ello, el avatar deberá resolver varios mapas de progresiva dificultad con los siguientes algoritmos:
1. Búsqueda no informada (offline) con Dijkstra
2. Búsqueda heurística (offline) con A*
3. Búsqueda heurística (online) en tiempo real con RTA*
4. Búsqueda heurística (online) en tiempo real con LRTA*

Todos estos algoritmos se repasarán en las próximas sesiones de prácticas.

### Mapas
Los mapas son proporcionados por los profesores de prácticas, aunque se recomienda encarecidamente que los estudiantes creen sus propios mapas y verifiquen el comportamiento de sus algoritmos en ellos.

Seguramente la evaluación de la práctica se realizará con otros mapas (aunque de dificultad más o menos similar).

## Temporización sugerida

* La temporización sugerida para esta práctica:
  * Semana 10 de marzo: Presentación Práctica 1
    * Instalación de GVGAI, revisar materiales, ejecutar códigos de prueba (Camel Race)
  * Semana 17 de marzo: búsqueda offline
    * Implementación de Dijkstra y A*
  * Semana 24 de marzo: Búsqueda online
    * Implementación de RTA* y LRTA*
  * Semana 31 de marzo: Seguimiento/Dudas
    * Competición (para quien quiera participar en ella) + Preparación de la memoria

* Entrega P1: 13 de abril de 2025 hasta las 23:59

## Consideraciones: Código Java

* Para el desarrollo de la práctica, cada estudiante creará el siguiente Java package:
  ```
  src/tracks/singlePlayer/evaluacion/src_APELLIDO1_APELLIDO2_NOMBRE
  ```

* Dentro de este paquete se crearán, al menos, las siguientes clases Java:
  * AgenteDijkstra
  * AgenteAStar
  * AgenteRTAStar
  * AgenteLRTAStar
  * AgenteCompeticion

* Se pueden incluir otras clases auxiliares que sean utilizadas por las anteriores.
* Los nombres de clase también son case-sensitive, por lo que se deben respetar las mayúsculas/minúsculas

**Es REQUISITO IMPRESCINDIBLE respetar esta nomenclatura!!!**

## Resumen workflow general

### Ejemplo con el agente que implementa el algoritmo de Dijkstra

1) Test.java
```java
String DijkstraController = "tracks.singlePlayer.evaluacion.src_APELLIDO1_APELLIDO2_NOMBRE.AgenteDijkstra";
…
ArcadeMachine.runOneGame(game, level1, visuals, DijkstraController, recordActionsFile, seed, 0);
```

2) En el primer tick, GVGAI llama al constructor de AgenteDijkstra (en donde se inicializa la posición de los recursos y casillas prohibidas, etc.) y se llama al método act().

3) En esta primera llamada al act(), como la ruta todavía no existe, se calcula a partir del nodo inicial, el final y las casillas prohibidas (utilizando el algoritmo correspondiente; en este caso, Dijkstra), y se ejecuta la primera acción:
```java
return myaction;
```

4) A partir de esta primera llamada, todas las llamadas restantes a act() se limitan a ejecutar/devolver las acciones restantes en la ruta calculada.

Nota: una posible forma de enfocarlo es que la ruta almacene nodos, y en el act() la ruta sea decodificada en acciones (Types.ACTIONS.ACTION_DOWN, p.ej.) dependiendo de la posición actual del avatar y el siguiente nodo/casilla en la ruta.

## Consideraciones: Definición del juego

* Se debe modificar el número máximo de timesteps, tanto en la definición del juego como en el entorno de GVGAI.
  * Los timesteps (o ticks) representan el tiempo máximo permitido para jugar. Si se alcanza ese valor, y no se ha conseguido el objetivo del juego, se pierde la partida.
  * Ejemplo con 10 timesteps:
    ```
    Timeout limit=10 win=False
    ```

* El número de timesteps se incrementará a 5.000.
* Para modificarlo en vuestro ordenador:
  * En src/core/competition/CompetitionParameters.java:
    ```java
    public static final int MAX_TIMESTEPS = 5000;
    ```
  * En examples/gridphysics/labyrinthdual.txt:
    ```
    Timeout limit=5000 win=False
    ```

## Consideraciones: Determinismo y expansión de nodos

* Todos los algoritmos propuestos tienen un comportamiento determinista
  * Todas las implementaciones correctas proporcionarán los mismos resultados (mismo recorrido, mismo número de nodos expandidos), a excepción del tiempo de ejecución, que variará de una máquina (e implementación) a otra.
  * Para ello SIEMPRE se expandirán los vecinos de un nodo en el siguiente orden: DERECHA, IZQUIERDA, ARRIBA, ABAJO

* Algunos algoritmos visitan los nodos usando información heurística
  * Por ejemplo, en A* los nodos se visitan según el valor de f(n), desempatando por g(n). ¿Y si también hay un empate en g(n)?
    * Se dará prioridad a los nodos más antiguos de la lista de abiertos
  * Para los vecinos de un nodo, se usará el orden de expansión arriba indicado para cualquier desempate final

## Consideraciones: Runtime

* GVGAI descalifica al agente si:
  * El tiempo empleado en el constructor es mayor que 1s
  * El tiempo empleado en el método "act" es mayor que 50ms
    * Es decir, cada tick, el motor del juego llama a act, y si el return de act tarda en llegar más de 50ms → pierdes!
    * Si el tiempo de act está en [40,50]ms, se ignora la acción calculada y realiza un ACTIONS.NIL

* En la práctica NO se permite el uso del constructor para ninguna operación relacionada directamente con los algoritmos de búsqueda. Todo el algoritmo de búsqueda se realizará en el método "act".
  * En el constructor sí se podría inicializar alguna variable (factor de escala, posición de la casilla objetivo, etc.) o tarea menor que no tenga relación con el algoritmo de búsqueda propiamente dicho.

* Por tanto, una estructura de datos eficiente es crucial para el correcto funcionamiento de los agentes

## Pasos iniciales

* Pasos a seguir:
  1. Descargar e instalar el entorno GVGAI.
  2. Seguir las indicaciones anteriores para probar varios juegos y niveles.
  3. Consultar y revisar los materiales proporcionados con la práctica:
     * Esta presentación
     * Enunciado de la práctica (y la competición)
     * Tutorial sobre GVGAI
     * Consultar la documentación sobre el código en caso de que sea necesario.
       * La estructura del código y documentación básica está en https://github.com/EssexUniversityMCTS/gvgai/wiki/Code-Structure
  4. Explorar y ejecutar el juego de la carrera de camellos (Camel Race), del que se proporciona un script sencillo (y manifiestamente mejorable).
  5. Comenzar la práctica: se recomienda implementar los algoritmos en el orden propuesto.

## Material a entregar

* El material a entregar a través de PRADO será un fichero ZIP con el siguiente contenido:
  * Un fichero PDF con la memoria
  * La carpeta del paquete Java anteriormente descrito y atendiendo a las siguientes consideraciones:
    * Únicamente debe imprimir por pantalla los mensajes especificados en el enunciado.
    * El código debe estar bien comentado.

* Fecha de entrega:
  * 13 de abril a las 23:59 horas

NOTA: se recomienda encarecidamente revisar las restricciones de la entrega en el enunciado de la práctica, y seguir escrupulosamente todas las indicaciones allí incluidas. 