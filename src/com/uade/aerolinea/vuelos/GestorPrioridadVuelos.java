package com.uade.aerolinea.vuelos;


import com.uade.structure.definition.PriorityQueueADT;
import com.uade.structure.implementation.DynamicPriorityQueue;

public class GestorPrioridadVuelos {

    // Tipos de prioridad (cuanto más bajo, mayor prioridad)


    public static final int PRIORIDAD_CARGA         = 1;
    public static final int PRIORIDAD_INTERNACIONAL = 2;
    public static final int PRIORIDAD_NACIONAL      = 3;

    private int maxVuelos;
    private String[] origenes;    // índice = id de vuelo → código de aeropuerto origen
    private String[] destinos;    // índice = id de vuelo → código de aeropuerto destino
    private String[] tipos;       // índice = id de vuelo → “Nacional”/“Internacional”/“Carga”
    private int[] prioridades;    // índice = id de vuelo → prioridad actual

    private PriorityQueueADT colaVuelos;  // cola de (idVuelo, prioridad)

    /**
     * @param capacidadVuelos número máximo de vuelos a gestionar (p. ej. 50)
     */
    public GestorPrioridadVuelos(int capacidadVuelos) {
        this.maxVuelos     = capacidadVuelos;
        this.origenes      = new String[capacidadVuelos];
        this.destinos      = new String[capacidadVuelos];
        this.tipos         = new String[capacidadVuelos];
        this.prioridades   = new int[capacidadVuelos];
        this.colaVuelos    = new DynamicPriorityQueue();
    }

    /**
     * Registra un vuelo con su origen, destino y tipo.
     * Lo encola según la prioridad asociada al tipo:
     *   Carga (1), Internacional (2), Nacional (3).
     */
    public void registrarVuelo(int idVuelo, String codigoOrigen, String codigoDestino, String tipoVuelo) {
        if (idVuelo < 0 || idVuelo >= maxVuelos) {
            System.out.println("ID de vuelo inválido: " + idVuelo);
            return;
        }
        // Determinar prioridad según tipo
        int prio;
        if ("Carga".equalsIgnoreCase(tipoVuelo)) {
            prio = PRIORIDAD_CARGA;
        } else if ("Internacional".equalsIgnoreCase(tipoVuelo)) {
            prio = PRIORIDAD_INTERNACIONAL;
        } else {
            prio = PRIORIDAD_NACIONAL;
            tipoVuelo = "Nacional";
        }

        origenes[idVuelo]    = codigoOrigen;
        destinos[idVuelo]    = codigoDestino;
        tipos[idVuelo]       = tipoVuelo;
        prioridades[idVuelo] = prio;

        colaVuelos.add(idVuelo, prio);
        System.out.println("Registrado vuelo " + idVuelo +
                " [" + tipoVuelo + "] " +
                codigoOrigen + "→" + codigoDestino +
                " (prio=" + prio + ")");
    }

    /**
     * Procesa (desencola) el siguiente vuelo de mayor prioridad.
     * Imprime sus datos y lo elimina de la cola.
     */
    public int procesarSiguienteVuelo() {
        if (colaVuelos.isEmpty()) {
            System.out.println("No hay vuelos en la cola.");
            return -1;
        }
        int id   = colaVuelos.getElement();
        int prio = colaVuelos.getPriority();
        colaVuelos.remove();
        System.out.println("Procesando vuelo " + id +
                " [" + tipos[id] + "] " +
                origenes[id] + "→" + destinos[id] +
                " (prio=" + prio + ")");
        return id;
    }

    /**
     * Cambia la prioridad de un vuelo ya registrado (por cambios de condiciones).
     * Reconstruye la cola para que la nueva prioridad surta efecto.
     */
    public void cambiarPrioridadVuelo(int idVuelo, int nuevaPrioridad) {
        if (idVuelo < 0 || idVuelo >= maxVuelos || tipos[idVuelo] == null) {
            System.out.println("Vuelo inválido o no registrado: " + idVuelo);
            return;
        }
        // Actualizar el array de prioridades
        prioridades[idVuelo] = nuevaPrioridad;

        // Vaciar y almacenar temporalmente todos los vuelos de la cola
        int[] tempVuelos = new int[maxVuelos];
        int[] tempPrios = new int[maxVuelos];
        int contador = 0;
        while (!colaVuelos.isEmpty()) {
            int v = colaVuelos.getElement();
            int p = colaVuelos.getPriority();
            colaVuelos.remove();
            tempVuelos[contador] = v;
            tempPrios[contador]  = p;
            contador++;
        }

        // Reconstruir la cola aplicando la nueva prioridad al vuelo indicado
        for (int i = 0; i < contador; i++) {
            int v = tempVuelos[i];
            int p = (v == idVuelo ? nuevaPrioridad : tempPrios[i]);
            colaVuelos.add(v, p);
        }

        System.out.println("Prioridad del vuelo " + idVuelo +
                " actualizada a " + nuevaPrioridad);
    }

    /**
     * Muestra todos los vuelos actualmente en la cola,
     * de mayor a menor prioridad, sin perder el orden original.
     */
    public void mostrarColaVuelos() {
        if (colaVuelos.isEmpty()) {
            System.out.println("La cola de vuelos está vacía.");
            return;
        }

        int[] tempVuelos = new int[maxVuelos];
        int[] tempPrios = new int[maxVuelos];
        int contador = 0;
        System.out.println("Cola de vuelos (de mayor a menor prioridad):");
        while (!colaVuelos.isEmpty()) {
            int v = colaVuelos.getElement();
            int p = colaVuelos.getPriority();
            System.out.println("- Vuelo " + v +
                    " [" + tipos[v] + "] " +
                    origenes[v] + "→" + destinos[v] +
                    " (prio=" + p + ")");
            colaVuelos.remove();
            tempVuelos[contador] = v;
            tempPrios[contador]  = p;
            contador++;
        }
        // Reconstruir la cola
        for (int i = 0; i < contador; i++) {
            colaVuelos.add(tempVuelos[i], tempPrios[i]);
        }
    }

    public boolean estaRegistrado(int idVuelo) {
        return tipos[idVuelo] != null;
    }

    public String getOrigen(int idVuelo) {
        return origenes[idVuelo];
    }
    public String getDestino(int idVuelo) {
        return destinos[idVuelo];
    }

    public int getNumeroVuelos() {
        return maxVuelos;
    }



}