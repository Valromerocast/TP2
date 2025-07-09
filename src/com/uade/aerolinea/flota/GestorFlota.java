package com.uade.aerolinea.flota;


import com.uade.structure.definition.LinkedListADT;
import com.uade.structure.implementation.DynamicLinkedList;

public class GestorFlota {
    private String[] matriculasAviones;      // índice = id de avión
    private String[] tiposAviones;           // índice = id de avión
    private boolean[] disponibles;           // índice = id de avión
    private int[] contadorAsignaciones;      // índice = id de avión
    private int proximoIdAvion;              // siguiente posición libre en los arrays

    private int[] avionAsignadoPorVuelo;     // índice = id de vuelo → id de avión (-1 si ninguno)
    private int maxVuelos;

    /**
     * @param capacidadAviones número máximo de aviones en la flota (p. ej. 10)
     * @param capacidadVuelos  número máximo de vuelos a planificar (p. ej. 50)
     */
    public GestorFlota(int capacidadAviones, int capacidadVuelos) {
        this.matriculasAviones    = new String[capacidadAviones];
        this.tiposAviones         = new String[capacidadAviones];
        this.disponibles          = new boolean[capacidadAviones];
        this.contadorAsignaciones = new int[capacidadAviones];
        this.proximoIdAvion       = 0;

        this.maxVuelos            = capacidadVuelos;
        this.avionAsignadoPorVuelo = new int[capacidadVuelos];
        for (int i = 0; i < capacidadVuelos; i++) {
            avionAsignadoPorVuelo[i] = -1;
        }
    }

    /** Agrega un nuevo avión (matrícula + tipo) si no existe aún */
    public void agregarAvion(String matricula, String tipo) {
        if (buscarIdAvion(matricula) < 0 && proximoIdAvion < matriculasAviones.length) {
            int id = proximoIdAvion++;
            matriculasAviones[id]    = matricula;
            tiposAviones[id]         = tipo;
            disponibles[id]          = true;
            contadorAsignaciones[id] = 0;
        }
    }

    /** Devuelve el id interno de un avión según su matrícula, o -1 si no existe */
    private int buscarIdAvion(String matricula) {
        for (int i = 0; i < proximoIdAvion; i++) {
            if (matriculasAviones[i].equals(matricula)) {
                return i;
            }
        }
        return -1;
    }

    /** Marca un avión (por matrícula) como disponible u ocupado */
    public void actualizarDisponibilidad(String matricula, boolean disponible) {
        int id = buscarIdAvion(matricula);
        if (id >= 0) {
            disponibles[id] = disponible;
        }
    }

    /**
     * Asigna al vuelo indicado (por idVuelo) el avión libre con menos asignaciones.
     * Si no hay aviones disponibles o el id de vuelo es inválido, lo informa por consola.
     */
    public void asignarAvion(int idVuelo) {
        if (idVuelo < 0 || idVuelo >= maxVuelos) {
            System.out.println("ID de vuelo inválido: " + idVuelo);
            return;
        }
        int mejorId = -1;
        for (int i = 0; i < proximoIdAvion; i++) {
            if (disponibles[i] &&
                    (mejorId < 0 || contadorAsignaciones[i] < contadorAsignaciones[mejorId])) {
                mejorId = i;
            }
        }
        if (mejorId >= 0) {
            disponibles[mejorId] = false;
            contadorAsignaciones[mejorId]++;
            avionAsignadoPorVuelo[idVuelo] = mejorId;
            System.out.println("Vuelo " + idVuelo +
                    " → Avión asignado: " + matriculasAviones[mejorId]);
        } else {
            System.out.println("No hay aviones disponibles para el vuelo " + idVuelo);
        }
    }

    /**
     * Libera el avión asignado al vuelo indicado (por idVuelo).
     * Si no había ningún avión asignado, informa un mensaje.
     */
    public void liberarAvion(int idVuelo) {
        if (idVuelo < 0 || idVuelo >= maxVuelos) {
            System.out.println("ID de vuelo inválido: " + idVuelo);
            return;
        }
        int idAvion = avionAsignadoPorVuelo[idVuelo];
        if (idAvion >= 0) {
            disponibles[idAvion] = true;
            avionAsignadoPorVuelo[idVuelo] = -1;
            System.out.println("Avión " + matriculasAviones[idAvion] +
                    " liberado del vuelo " + idVuelo);
        } else {
            System.out.println("El vuelo " + idVuelo + " no tenía avión asignado.");
        }
    }

    /**
     * Imprime los aviones con el mayor número de asignaciones realizadas.
     * Si hay varios empatados, los lista todos.
     */
    public void imprimirAvionesConMasAsignaciones() {
        // 1) Encontrar el máximo contador
        int max = -1;
        for (int i = 0; i < proximoIdAvion; i++) {
            if (contadorAsignaciones[i] > max) {
                max = contadorAsignaciones[i];
            }
        }
        // 2) Reunir los aviones que alcanzaron ese máximo
        LinkedListADT lista = new DynamicLinkedList();
        for (int i = 0; i < proximoIdAvion; i++) {
            if (contadorAsignaciones[i] == max) {
                lista.add(i);
            }
        }
        // 3) Mostrar resultados
        System.out.println("Aviones con más asignaciones (" + max + "):");
        for (int i = 0; i < lista.size(); i++) {
            int id = lista.get(i);
            System.out.println("- " + matriculasAviones[id] +
                    " (" + tiposAviones[id] + ")");
        }
    }

    public int getNumeroAviones() {
        return proximoIdAvion;
    }
    public int getContadorAsignaciones(int idAvion) {
        return contadorAsignaciones[idAvion];
    }


}