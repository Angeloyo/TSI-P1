package tracks.singlePlayer.src_SANCHEZ_GUERRERO_ANGEL;

import java.util.*;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteAStar extends AbstractPlayer {
    
    // Factores de escala para convertir entre coordenadas de píxeles y grid
    private Vector2d fescala;
    
    // Tipos de casillas
    private boolean[][] casillasProhibidas;
    private boolean[][] trampasCasillas;
    private boolean[][] paredesRojas;
    private boolean[][] paredesAzules;
    private int[][] capasRojas;
    private int[][] capasAzules;
    private int[][] portales;
    
    // Coordenadas del portal (objetivo)
    private int portalX = -1;
    private int portalY = -1;
    
    // Variables para registrar métricas
    private int nodosExpandidos;
    private long tiempoEjecucion;
    private int tamañoRuta;
    
    // Variables para el camino
    private ArrayList<Types.ACTIONS> caminoCompleto;
    private int pasoActual;
    
    public AgenteAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // Calculamos el factor de escala entre mundos (píxeles -> grid)
        fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
                              stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);
        
        // Obtenemos dimensiones del grid
        int gridWidth = stateObs.getObservationGrid().length;
        int gridHeight = stateObs.getObservationGrid()[0].length;
        
        // Inicializamos matrices para los diferentes tipos de casillas
        casillasProhibidas = new boolean[gridWidth][gridHeight];
        trampasCasillas = new boolean[gridWidth][gridHeight];
        paredesRojas = new boolean[gridWidth][gridHeight];
        paredesAzules = new boolean[gridWidth][gridHeight];
        capasRojas = new int[gridWidth][gridHeight];
        capasAzules = new int[gridWidth][gridHeight];
        portales = new int[gridWidth][gridHeight];
        
        // Identificamos las casillas del mapa
        identificarCasillasEspeciales(stateObs);
        
        // Imprimir información sobre casillas especiales detectadas
        // System.out.println("Casillas especiales detectadas:");
        // imprimirCasillasEspeciales();
        
        // Inicializamos variables para el camino
        caminoCompleto = null;
        pasoActual = 0;
        
        // Inicializamos métricas
        nodosExpandidos = 0;
        tiempoEjecucion = 0;
        tamañoRuta = 0;
    }
    
    private void imprimirCasillasEspeciales() {
        // Contar casillas especiales
        int contMuros = 0;
        int contTrampas = 0;
        int contParedesRojas = 0;
        int contParedesAzules = 0;
        int contCapasRojas = 0;
        int contCapasAzules = 0;
        int contPortales = 0;
        
        for (int i = 0; i < casillasProhibidas.length; i++) {
            for (int j = 0; j < casillasProhibidas[0].length; j++) {
                if (casillasProhibidas[i][j]) contMuros++;
                if (trampasCasillas[i][j]) contTrampas++;
                if (paredesRojas[i][j]) contParedesRojas++;
                if (paredesAzules[i][j]) contParedesAzules++;
                if (capasRojas[i][j] == 1) contCapasRojas++;
                if (capasAzules[i][j] == 1) contCapasAzules++;
                if (portales[i][j] == 1) contPortales++;
            }
        }
        
        System.out.println("- Muros normales: " + contMuros);
        System.out.println("- Trampas: " + contTrampas);
        System.out.println("- Paredes Rojas: " + contParedesRojas);
        System.out.println("- Paredes Azules: " + contParedesAzules);
        System.out.println("- Capas Rojas: " + contCapasRojas);
        System.out.println("- Capas Azules: " + contCapasAzules);
        System.out.println("- Portales: " + contPortales);
    }
    
    private void identificarCasillasEspeciales(StateObservation stateObs) {
        // Muros (obstáculos inmóviles)
        ArrayList<Observation>[] inmovables = stateObs.getImmovablePositions();
        if (inmovables != null) {
            for (int i = 0; i < inmovables.length; i++) {
                ArrayList<Observation> inmovableType = inmovables[i];
                for (Observation obs : inmovableType) {
                    int x = (int) Math.floor(obs.position.x / fescala.x);
                    int y = (int) Math.floor(obs.position.y / fescala.y);
                    
                    // Debug: imprimir tipos de inmovables
                    // System.out.println("Inmovable tipo " + i + " (itype=" + obs.itype + ") en [" + x + "," + y + "]");
                    
                    // Trampa (itype=3)
                    if (obs.itype == 3) {
                        trampasCasillas[x][y] = true;
                    }
                    // Muro normal (itype=5)
                    else if (obs.itype == 5) {
                        casillasProhibidas[x][y] = true;
                    }
                    // Muro rojo (itype=6)
                    else if (obs.itype == 6) {
                        paredesRojas[x][y] = true;
                    }
                    // Muro azul (itype=7)
                    else if (obs.itype == 7) {
                        paredesAzules[x][y] = true;
                    }
                }
            }
        }
        
        // Capas (recursos)
        ArrayList<Observation>[] resources = stateObs.getResourcesPositions();
        if (resources != null) {
            for (int i = 0; i < resources.length; i++) {
                ArrayList<Observation> resourceType = resources[i];
                for (Observation obs : resourceType) {
                    int x = (int) Math.floor(obs.position.x / fescala.x);
                    int y = (int) Math.floor(obs.position.y / fescala.y);
                    
                    // Debug: imprimir tipos de recursos
                    // System.out.println("Recurso tipo " + i + " (itype=" + obs.itype + ") en [" + x + "," + y + "]");
                    
                    // Capa roja (itype=8)
                    if (obs.itype == 8) {
                        capasRojas[x][y] = 1;
                    }
                    // Capa azul (itype=9)
                    else if (obs.itype == 9) {
                        capasAzules[x][y] = 1;
                    }
                }
            }
        }
        
        // Portales de salida
        ArrayList<Observation>[] exits = stateObs.getPortalsPositions();
        if (exits != null) {
            for (int i = 0; i < exits.length; i++) {
                ArrayList<Observation> exitType = exits[i];
                for (Observation obs : exitType) {
                    int x = (int) Math.floor(obs.position.x / fescala.x);
                    int y = (int) Math.floor(obs.position.y / fescala.y);
                    
                    // Debug: imprimir tipos de portales
                    // System.out.println("Portal tipo " + i + " (itype=" + obs.itype + ") en [" + x + "," + y + "]");
                    
                    // Asegurarnos de que el itype corresponde al portal (itype=4)
                    if (obs.itype == 4) {
                        portales[x][y] = 1;
                        // Guardamos la posición del portal para la heurística
                        portalX = x;
                        portalY = y;
                    }
                }
            }
        }
    }
    
    /**
     * Calcula la heurística (distancia Manhattan) desde un nodo al portal
     * @param nodo El nodo para el que calcular la heurística
     * @return Valor de la heurística
     */
    private int calcularHeuristica(NodoAStar nodo) {
        // Si no hemos encontrado el portal, la heurística es 0
        if (portalX == -1 || portalY == -1) {
            return 0;
        }
        
        // Distancia Manhattan: |x1 - x2| + |y1 - y2|
        return Math.abs(nodo.x - portalX) + Math.abs(nodo.y - portalY);
    }
    
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // Si ya tenemos un camino calculado, seguimos ese camino
        if (caminoCompleto != null) {
            if (pasoActual < caminoCompleto.size()) {
                Types.ACTIONS siguienteAccion = caminoCompleto.get(pasoActual++);
                // System.out.println("Siguiendo camino: Paso " + pasoActual + "/" + caminoCompleto.size() + " - Acción: " + siguienteAccion);
                return siguienteAccion;
            } else {
                // Hemos completado el camino, devolvemos ACTION_NIL para indicar que hemos terminado
                // System.out.println("Camino completado. Ejecutando ACTION_NIL");
                return Types.ACTIONS.ACTION_NIL;
            }
        }
        
        // Si no tenemos un camino, lo calculamos (solo se ejecutará una vez)
        long tInicio = System.nanoTime();
        
        // Obtener posición del avatar
        Vector2d avatarPosition = new Vector2d(
            stateObs.getAvatarPosition().x / fescala.x,
            stateObs.getAvatarPosition().y / fescala.y
        );
        
        // Imprimir la posición inicial del avatar para seguimiento
        System.out.println("Posición inicial del avatar: [" + (int)avatarPosition.x + "," + (int)avatarPosition.y + "]");
        
        // Determinar tipo de capa inicial ( asumimos que siempre al comienzo es ninguna)
        NodoAStar.TipoCapa capaInicial = NodoAStar.TipoCapa.NINGUNA;
        
        // Inicializamos el nodo inicial
        NodoAStar nodoInicial = new NodoAStar((int) avatarPosition.x, (int) avatarPosition.y, capaInicial);
        
        // Calculamos la heurística del nodo inicial
        nodoInicial.valorHeuristico = calcularHeuristica(nodoInicial);
        nodoInicial.valorF = nodoInicial.coste + nodoInicial.valorHeuristico;
        
        // Ejecutamos A*
        caminoCompleto = buscarCaminoAStar(nodoInicial);
        pasoActual = 0;
        
        long tFin = System.nanoTime();
        tiempoEjecucion = (tFin - tInicio) / 1000000; // En milisegundos
        
        // Si encontramos un camino, mostrar solo la información final
        if (caminoCompleto != null && !caminoCompleto.isEmpty()) {
    
            System.out.println("Camino encontrado!");
            System.out.println("Runtime: " + tiempoEjecucion + " ms | Ruta: " + tamañoRuta + " pasos | Nodos: " + nodosExpandidos);

            // for (int i = 0; i < caminoCompleto.size(); i++) {
            //     System.out.println("- Paso " + (i+1) + ": " + caminoCompleto.get(i));
            // }
            return caminoCompleto.get(pasoActual++);
        } else {
            System.out.println("No se encontró camino hasta el portal!");
            return Types.ACTIONS.ACTION_NIL;
        }
    }
    
    private ArrayList<Types.ACTIONS> buscarCaminoAStar(NodoAStar nodoInicial) {
        // Cola de prioridad para nodos abiertos (ordenada por f = g + h)
        PriorityQueue<NodoAStar> abiertos = new PriorityQueue<>();
        
        // Conjunto de nodos cerrados usando un HashSet con verificación de igualdad personalizada
        Set<NodoAStar> cerrados = new HashSet<>();
        
        // Mapa para nodos ya visitados
        Map<String, NodoAStar> nodosVisitados = new HashMap<>();
        
        // Inicializamos el nodo raíz
        abiertos.add(nodoInicial);
        nodosVisitados.put(nodoToKey(nodoInicial), nodoInicial);
        
        // System.out.println("Iniciando búsqueda Dijkstra desde nodo [" + nodoInicial.x + "," + nodoInicial.y + "]");
        
        // Contador para el timestamp
        long timestamp = 1;
        
        while (!abiertos.isEmpty()) {
            // Extraemos el nodo con menor valor f = g + h
            NodoAStar actual = abiertos.poll();
            
            // Incrementamos contador de nodos expandidos
            nodosExpandidos++;
            
            // Si encontramos el portal, reconstruimos el camino
            if (portales[actual.x][actual.y] == 1) {
                // System.out.println("Portal encontrado en [" + actual.x + "," + actual.y + "]!");
                ArrayList<Types.ACTIONS> caminoCalculado = actual.reconstruirCamino();
                tamañoRuta = caminoCalculado.size();
                return caminoCalculado;
            }
            
            // Añadimos el nodo a cerrados
            cerrados.add(actual);
            
            // Expandimos los vecinos
            ArrayList<NodoAStar> vecinos = actual.expandir(casillasProhibidas, trampasCasillas, 
                                                   paredesRojas, paredesAzules, 
                                                   capasRojas, capasAzules, timestamp);
            
            // System.out.println("Expandiendo nodo [" + actual.x + "," + actual.y + "] - Vecinos generados: " + vecinos.size());
            
            for (NodoAStar vecino : vecinos) {
                String vecinoKey = nodoToKey(vecino);
                
                // Calculamos la heurística y f para el vecino
                vecino.valorHeuristico = calcularHeuristica(vecino);
                vecino.valorF = vecino.coste + vecino.valorHeuristico;
                
                // Verificamos si el nodo ya está en cerrados
                if (cerrados.contains(vecino)) {
                    continue;
                }
                
                // Si el nodo no estaba en abiertos o tiene un coste menor, lo añadimos/actualizamos
                NodoAStar nodoExistente = nodosVisitados.get(vecinoKey);
                if (nodoExistente == null || vecino.coste < nodoExistente.coste) {
                    timestamp++;
                    vecino.timestamp = timestamp;
                    abiertos.add(vecino);
                    nodosVisitados.put(vecinoKey, vecino);
                    // System.out.println("  Añadido/actualizado vecino [" + vecino.x + "," + vecino.y + "] con coste " + vecino.coste);
                }
            }
        }
        
        // System.out.println("No se encontró camino al portal después de expandir " + nodosExpandidos + " nodos");
        // Si no encontramos camino, devolvemos null
        return null;
    }
    
    // Método para convertir un nodo a una clave única para el mapa
    private String nodoToKey(NodoAStar nodo) {
        return nodo.x + "," + nodo.y + "," + nodo.capa;
    }
}
