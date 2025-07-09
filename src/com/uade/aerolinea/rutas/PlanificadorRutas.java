package com.uade.aerolinea.rutas;


import com.uade.structure.definition.GraphADT;
import com.uade.structure.definition.LinkedListADT;
import com.uade.structure.implementation.DynamicGraph;
import com.uade.structure.implementation.DynamicLinkedList;


public class PlanificadorRutas {
    private GraphADT grafo;
    private String[] codigosAeropuertos;        // índice = id de vértice
    private String[] descripcionesAeropuertos;  // índice = id de vértice
    private int proximoIdVertice;               // contador de vértices creados

    /**
     * @param capacidad Número máximo de aeropuertos (p. ej. 20)
     */
    public PlanificadorRutas(int capacidad) {
        this.grafo                  = new DynamicGraph();
        this.codigosAeropuertos     = new String[capacidad];
        this.descripcionesAeropuertos = new String[capacidad];
        this.proximoIdVertice       = 0;
    }

    /** Agrega un aeropuerto (código + descripción) si no existe aún */
    public void agregarAeropuerto(String codigo, String descripcion) {
        if (buscarIdAeropuerto(codigo) < 0 && proximoIdVertice < codigosAeropuertos.length) {
            int id = proximoIdVertice++;
            codigosAeropuertos[id]      = codigo;
            descripcionesAeropuertos[id] = descripcion;
            grafo.addVertx(id);
        }
    }

    /** Devuelve el id de vértice para un código dado, o -1 si no existe */
    public int buscarIdAeropuerto(String codigo) {
        for (int i = 0; i < proximoIdVertice; i++) {
            if (codigosAeropuertos[i].equals(codigo)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Agrega una ruta (arista) desde códigoOrigen → códigoDestino con un peso dado.
     * No permite rutas duplicadas.
     */
    public void agregarRuta(String codigoOrigen, String codigoDestino, int peso) {
        int origen = buscarIdAeropuerto(codigoOrigen);
        int destino = buscarIdAeropuerto(codigoDestino);
        if (origen >= 0 && destino >= 0 && !grafo.existsEdge(origen, destino)) {
            grafo.addEdge(origen, destino, peso);
        }
    }

    /**
     * Imprime por consola todas las rutas posibles desde códigoOrigen
     * hasta códigoDestino con como máximo maxEscalas escalas intermedias.
     */
    public void imprimirRutas(String codigoOrigen, String codigoDestino, int maxEscalas) {
        int inicio = buscarIdAeropuerto(codigoOrigen);
        int fin    = buscarIdAeropuerto(codigoDestino);
        if (inicio < 0 || fin < 0) {
            System.out.println(">> Aeropuerto no encontrado: "
                    + codigoOrigen + " o " + codigoDestino);
            return;
        }
        boolean[] visitado = new boolean[proximoIdVertice];
        LinkedListADT ruta = new DynamicLinkedList(); // guardará ids de vértice
        dfsImprimir(inicio, fin, maxEscalas + 1, visitado, ruta);
    }

    /** Búsqueda en profundidad con backtracking para imprimir cada ruta */
    private void dfsImprimir(int actual, int destino, int restante,
                             boolean[] visitado, LinkedListADT ruta) {
        visitado[actual] = true;
        ruta.add(actual);  // añadimos al final

        if (actual == destino) {
            // imprimimos la ruta completa
            System.out.print("Ruta: ");
            for (int i = 0; i < ruta.size(); i++) {
                int v = ruta.get(i);
                System.out.print(codigosAeropuertos[v]);
                if (i < ruta.size() - 1) System.out.print(" → ");
            }
            System.out.println();
        } else if (restante > 1) {
            // exploramos vecinos
            for (int vecino = 0; vecino < proximoIdVertice; vecino++) {
                if (!visitado[vecino] && grafo.existsEdge(actual, vecino)) {
                    dfsImprimir(vecino, destino, restante - 1, visitado, ruta);
                }
            }
        }

        // backtrack
        visitado[actual] = false;
        ruta.remove(ruta.size() - 1);
    }

    /** Imprime los aeropuertos que no tienen conexiones entrantes ni salientes */
    public void imprimirAeropuertosDesconectados() {
        for (int v = 0; v < proximoIdVertice; v++) {
            boolean tieneConexion = false;
            for (int w = 0; w < proximoIdVertice; w++) {
                if (grafo.existsEdge(v, w) || grafo.existsEdge(w, v)) {
                    tieneConexion = true;
                    break;
                }
            }
            if (!tieneConexion) {
                System.out.println(codigosAeropuertos[v] + " — "
                        + descripcionesAeropuertos[v]);
            }
        }
    }

    public int getNumeroAeropuertos() {
        return proximoIdVertice;
    }

    public String getCodigoAeropuerto(int id) {
        return codigosAeropuertos[id];
    }

    public GraphADT getGrafo() {
        return grafo;
    }

}