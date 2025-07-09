package com.uade.aerolinea.analytics;

import com.uade.aerolinea.rutas.PlanificadorRutas;
import com.uade.aerolinea.vuelos.GestorPrioridadVuelos;
import com.uade.structure.definition.GraphADT;
import com.uade.structure.definition.LinkedListADT;
import com.uade.structure.implementation.DynamicLinkedList;


public class AnalizadorConexiones {
    private PlanificadorRutas planificador;
    private GestorPrioridadVuelos gestorVuelos;

    public AnalizadorConexiones(PlanificadorRutas plan, GestorPrioridadVuelos gest) {
        this.planificador = plan;
        this.gestorVuelos = gest;
    }

    /** Lista todos los aeropuertos operados por la aerolínea */
    public void imprimirAeropuertosOperados() {
        System.out.println("Aeropuertos operados:");
        int n = planificador.getNumeroAeropuertos();
        for (int i = 0; i < n; i++) {
            System.out.println("- " + planificador.getCodigoAeropuerto(i));
        }
    }

    /**
     * Reporta los aeropuertos con mayor número de vuelos salientes y de vuelos entrantes
     */
    public void reportarVuelosEntrantesYSalientes() {
        int n = planificador.getNumeroAeropuertos();
        int m = gestorVuelos.getNumeroVuelos();
        int[] salientes  = new int[n];
        int[] entrantes  = new int[n];

        // Contar
        for (int v = 0; v < m; v++) {
            if (gestorVuelos.estaRegistrado(v)) {
                String ori = gestorVuelos.getOrigen(v);
                String des = gestorVuelos.getDestino(v);
                int idO = planificador.buscarIdAeropuerto(ori);
                int idD = planificador.buscarIdAeropuerto(des);
                if (idO >= 0) salientes[idO]++;
                if (idD >= 0) entrantes[idD]++;
            }
        }

        // Encontrar máximos y listarlos
        int maxSal = -1, maxEnt = -1;
        LinkedListADT listaSal = new DynamicLinkedList();
        LinkedListADT listaEnt = new DynamicLinkedList();

        for (int i = 0; i < n; i++) {
            if (salientes[i] > maxSal) {
                maxSal = salientes[i];
                listaSal = new DynamicLinkedList();
                listaSal.add(i);
            } else if (salientes[i] == maxSal) {
                listaSal.add(i);
            }

            if (entrantes[i] > maxEnt) {
                maxEnt = entrantes[i];
                listaEnt = new DynamicLinkedList();
                listaEnt.add(i);
            } else if (entrantes[i] == maxEnt) {
                listaEnt.add(i);
            }
        }

        System.out.println("Aeropuertos con más vuelos salientes (" + maxSal + "):");
        for (int i = 0; i < listaSal.size(); i++) {
            int id = listaSal.get(i);
            System.out.println("- " + planificador.getCodigoAeropuerto(id));
        }

        System.out.println("Aeropuertos con más vuelos entrantes (" + maxEnt + "):");
        for (int i = 0; i < listaEnt.size(); i++) {
            int id = listaEnt.get(i);
            System.out.println("- " + planificador.getCodigoAeropuerto(id));
        }
    }

    /**
     * Reporta los aeropuertos con más conexiones disponibles en la red
     */
    public void reportarAeropuertosConMasConexiones() {
        GraphADT grafo = planificador.getGrafo();
        int n = planificador.getNumeroAeropuertos();
        int maxCon = -1;
        LinkedListADT listaCon = new DynamicLinkedList();

        for (int i = 0; i < n; i++) {
            int cuenta = 0;
            for (int j = 0; j < n; j++) {
                if (grafo.existsEdge(i, j) || grafo.existsEdge(j, i)) {
                    cuenta++;
                }
            }
            if (cuenta > maxCon) {
                maxCon = cuenta;
                listaCon = new DynamicLinkedList();
                listaCon.add(i);
            } else if (cuenta == maxCon) {
                listaCon.add(i);
            }
        }

        System.out.println("Aeropuertos con más conexiones (" + maxCon + "):");
        for (int i = 0; i < listaCon.size(); i++) {
            int id = listaCon.get(i);
            System.out.println("- " + planificador.getCodigoAeropuerto(id));
        }
    }
}