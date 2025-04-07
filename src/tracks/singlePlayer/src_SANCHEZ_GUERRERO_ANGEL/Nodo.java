package tracks.singlePlayer.src_SANCHEZ_GUERRERO_ANGEL;

import java.util.Objects;
import java.util.Stack;
import java.util.ArrayList;
import ontology.Types;
import ontology.Types.ACTIONS;

/**
 * Clase que representa un nodo en el algoritmo Dijkstra para el juego Labyrinth Dual.
 * Contiene el estado (posición x,y + tipo de capa), el coste, la acción que llevó a este nodo,
 * y una referencia al nodo padre.
 */
public class Nodo implements Comparable<Nodo> {
    // Coordenadas del nodo en el grid
    public int x, y;
    
    // Tipo de capa que posee el avatar en este nodo
    public TipoCapa capa;
    
    // Coste acumulado (g)
    public int coste;
    
    // Nodo padre (para reconstruir el camino)
    public Nodo padre;
    
    // Acción que llevó a este nodo
    public ACTIONS accion;
    
    // Timestamp para desempate FIFO
    public long timestamp;
    
    /**
     * Enum para representar el tipo de capa que porta el avatar.
     */
    public enum TipoCapa {
        NINGUNA, ROJA, AZUL
    }
    
    /**
     * Constructor para el nodo inicial (sin padre).
     * @param x Coordenada x en el grid
     * @param y Coordenada y en el grid
     * @param capa Tipo de capa que porta el avatar
     */
    public Nodo(int x, int y, TipoCapa capa) {
        this.x = x;
        this.y = y;
        this.capa = capa;
        this.coste = 0;
        this.padre = null;
        this.accion = null;
        this.timestamp = 0;
    }
    
    /**
     * Constructor para nodos generados durante la búsqueda.
     * @param x Coordenada x en el grid
     * @param y Coordenada y en el grid
     * @param capa Tipo de capa que porta el avatar
     * @param coste Coste acumulado hasta este nodo
     * @param padre Nodo padre del que se generó este nodo
     * @param accion Acción que llevó a este nodo
     * @param timestamp Timestamp para desempate FIFO
     */
    public Nodo(int x, int y, TipoCapa capa, int coste, Nodo padre, ACTIONS accion, long timestamp) {
        this.x = x;
        this.y = y;
        this.capa = capa;
        this.coste = coste;
        this.padre = padre;
        this.accion = accion;
        this.timestamp = timestamp;
    }
    
    /**
     * Genera los nodos sucesores aplicando las acciones posibles.
     * @param casillasProhibidas Matriz que indica las casillas con muros normales
     * @param trampasCasillas Matriz que indica las casillas con trampas
     * @param paredesRojas Matriz que indica las casillas con paredes rojas
     * @param paredesAzules Matriz que indica las casillas con paredes azules
     * @param capasRojas Matriz que indica las casillas con capas rojas
     * @param capasAzules Matriz que indica las casillas con capas azules
     * @param timestamp Contador para asignar timestamps a los nodos
     * @return Lista de nodos sucesores válidos
     */
    public ArrayList<Nodo> expandir(boolean[][] casillasProhibidas, boolean[][] trampasCasillas, 
                                   boolean[][] paredesRojas, boolean[][] paredesAzules,
                                   int[][] capasRojas, int[][] capasAzules, long timestamp) {
        ArrayList<Nodo> sucesores = new ArrayList<>();
        
        // Orden de expansión: Derecha, Izquierda, Arriba, Abajo
        ACTIONS[] acciones = {
            Types.ACTIONS.ACTION_RIGHT,
            Types.ACTIONS.ACTION_LEFT,
            Types.ACTIONS.ACTION_UP,
            Types.ACTIONS.ACTION_DOWN
        };
        
        for (ACTIONS accion : acciones) {
            Nodo sucesor = aplicarAccion(accion, casillasProhibidas, trampasCasillas, 
                                       paredesRojas, paredesAzules, capasRojas, capasAzules, timestamp);
            if (sucesor != null) {
                sucesores.add(sucesor);
            }
        }
        
        return sucesores;
    }
    
    /**
     * Aplica una acción al nodo actual generando un nuevo nodo si la acción es válida.
     * @param accion Acción a aplicar
     * @param casillasProhibidas Matriz que indica las casillas con muros normales
     * @param trampasCasillas Matriz que indica las casillas con trampas
     * @param paredesRojas Matriz que indica las casillas con paredes rojas
     * @param paredesAzules Matriz que indica las casillas con paredes azules
     * @param capasRojas Matriz que indica las casillas con capas rojas
     * @param capasAzules Matriz que indica las casillas con capas azules
     * @param timestamp Timestamp para el nuevo nodo
     * @return Nodo resultante o null si la acción no es válida
     */
    private Nodo aplicarAccion(ACTIONS accion, boolean[][] casillasProhibidas, boolean[][] trampasCasillas, 
                             boolean[][] paredesRojas, boolean[][] paredesAzules,
                             int[][] capasRojas, int[][] capasAzules, long timestamp) {
        int newX = this.x;
        int newY = this.y;
        
        // Calculamos la nueva posición según la acción
        switch (accion) {
            case ACTION_UP:
                newY--;
                break;
            case ACTION_DOWN:
                newY++;
                break;
            case ACTION_LEFT:
                newX--;
                break;
            case ACTION_RIGHT:
                newX++;
                break;
            default:
                return null;
        }
        
        // Verificamos límites del mapa
        if (newX < 0 || newY < 0 || newX >= casillasProhibidas.length || newY >= casillasProhibidas[0].length) {
            return null;
        }
        
        // Verificamos si es una casilla prohibida (muro normal)
        if (casillasProhibidas[newX][newY]) {
            return null;
        }
        
        // Verificamos si es una trampa
        if (trampasCasillas[newX][newY]) {
            return null;
        }
        
        // Verificamos muros especiales según la capa
        if (paredesRojas[newX][newY] && this.capa != TipoCapa.ROJA) {
            return null;
        }
        
        if (paredesAzules[newX][newY] && this.capa != TipoCapa.AZUL) {
            return null;
        }
        
        // Actualizamos la capa si pasamos por encima de una
        TipoCapa nuevaCapa = this.capa;
        if (capasRojas[newX][newY] == 1) {
            nuevaCapa = TipoCapa.ROJA;
        } else if (capasAzules[newX][newY] == 1) {
            nuevaCapa = TipoCapa.AZUL;
        }
        
        // Devolvemos el nuevo nodo con coste incrementado
        return new Nodo(newX, newY, nuevaCapa, this.coste + 1, this, accion, timestamp);
    }
    
    /**
     * Reconstruye el camino desde este nodo hasta el nodo raíz.
     * @return Lista de acciones que forman el camino
     */
    public ArrayList<ACTIONS> reconstruirCamino() {
        
        ArrayList<ACTIONS> camino = new ArrayList<>();
        
        Nodo actual = this;

        while (actual.padre != null) {
            camino.add(0, actual.accion);
            actual = actual.padre;
        }
        
        return camino;
    }
    
    /**
     * Compara este nodo con otro para ordenarlos por coste (y timestamp en caso de empate).
     * @param otro El nodo a comparar
     * @return -1, 0, o 1 según sea menor, igual o mayor
     */
    @Override
    public int compareTo(Nodo otro) {
        if (this.coste != otro.coste) {
            return Integer.compare(this.coste, otro.coste);
        }
        // En caso de empate, usamos criterio FIFO (el más antiguo primero)
        return Long.compare(this.timestamp, otro.timestamp);
    }
    
    /**
     * Comprueba si dos nodos son iguales (misma posición y tipo de capa).
     * @param o El objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        // Verificación rápida si es el mismo objeto
        if (this == o) return true;
        
        // Verificación de nulidad y tipo de clase
        if (o == null || getClass() != o.getClass()) return false;
        
        // Casting y comparación de atributos relevantes
        Nodo otro = (Nodo) o;
        return x == otro.x && y == otro.y && capa == otro.capa;
    }
    
    /**
     * Calcula el código hash para este nodo basado en su posición y tipo de capa.
     * @return El código hash
     */
    @Override
    public int hashCode() {
        // Consistente con equals, solo usa los campos relevantes
        return Objects.hash(x, y, capa);
    }
    
    /**
     * Devuelve una representación en texto del nodo.
     */
    @Override
    public String toString() {
        return "Nodo[" + x + "," + y + "," + capa + "]";
    }
} 