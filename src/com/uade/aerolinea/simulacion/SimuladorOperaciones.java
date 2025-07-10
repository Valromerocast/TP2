package com.uade.aerolinea.simulacion;

import java.util.Scanner;
import com.uade.aerolinea.rutas.PlanificadorRutas;
import com.uade.aerolinea.flota.GestorFlota;
import com.uade.aerolinea.vuelos.GestorPrioridadVuelos;
import com.uade.structure.definition.GraphADT;

public class SimuladorOperaciones {
    private PlanificadorRutas planificador;
    private GestorFlota gestorFlota;
    private GestorPrioridadVuelos gestorPrioridad;

    private int maxVuelos;
    private boolean[] vueloActivo;
    private String[] origenVuelo, destinoVuelo;
    private int[][] contadorRutas;

    public SimuladorOperaciones(int capAeropuertos,
                                int capVuelos,
                                int capAviones) {
        this.planificador     = new PlanificadorRutas(capAeropuertos);
        this.gestorFlota      = new GestorFlota(capAviones, capVuelos);
        this.gestorPrioridad  = new GestorPrioridadVuelos(capVuelos);
        this.maxVuelos        = capVuelos;

        this.vueloActivo      = new boolean[capVuelos];
        this.origenVuelo      = new String[capVuelos];
        this.destinoVuelo     = new String[capVuelos];
        for (int i = 0; i < capVuelos; i++) vueloActivo[i] = false;

        this.contadorRutas    = new int[capAeropuertos][capAeropuertos];

        cargarAeropuertosIniciales();
        cargarFlotaInicial();
        cargarVuelosIniciales();
    }

    private void cargarAeropuertosIniciales() {
        String[] cod = {
                "ATL","PEK","LHR","CDG","HND",
                "DXB","LAX","SIN","FRA","AMS",
                "GRU","SYD","JFK","ICN","MAD"
        };
        String[] des = {
                "Hartsfield-Jackson Atlanta Intl. (EE. UU.)",
                "Beijing Capital Intl. (China)",
                "London Heathrow (Reino Unido)",
                "Charles de Gaulle (Francia)",
                "Tokyo Haneda (Japón)",
                "Dubai International (EAU)",
                "Los Angeles Intl. (EE. UU.)",
                "Singapore Changi (Singapur)",
                "Frankfurt am Main (Alemania)",
                "Amsterdam Schiphol (Países Bajos)",
                "São Paulo/Guarulhos (Brasil)",
                "Sydney Kingsford Smith (Australia)",
                "John F. Kennedy (EE. UU.)",
                "Incheon Intl. (Corea del Sur)",
                "Adolfo Suárez Madrid–Barajas (España)"
        };
        for (int i = 0; i < cod.length; i++) {
            planificador.agregarAeropuerto(cod[i], des[i]);
        }
    }

    private void cargarFlotaInicial() {
        String[] mat = {
                "AA101","BB202","CC303","DD404","EE505",
                "FF606","GG707","HH808","II909","JJ010"
        };
        String[] tip = {
                "Boeing 737","Airbus A320","Boeing 777","Embraer 190","Airbus A330",
                "Boeing 747","Airbus A350","Boeing 787","Embraer 175","Bombardier CRJ"
        };
        for (int i = 0; i < mat.length; i++) {
            gestorFlota.agregarAvion(mat[i], tip[i]);
        }
    }

    /**
     * Registra 50 vuelos:
     * • Inserta cada vuelo en la cola de prioridad
     * • Planifica la ruta en el grafo y cuenta su uso
     * • A los primeros N aviones les asigna automáticamente un vuelo
     */
    private void cargarVuelosIniciales() {
        int nAer = planificador.getNumeroAeropuertos();
        int nAv  = gestorFlota.getNumeroAviones();

        for (int id = 0; id < maxVuelos; id++) {
            String ori = planificador.getCodigoAeropuerto(id % nAer);
            String dst = planificador.getCodigoAeropuerto((id + 1) % nAer);
            String tipo;
            switch (id % 3) {
                case 0 -> tipo = "Carga";
                case 1 -> tipo = "Internacional";
                default -> tipo = "Nacional";
            }

            // 1) Ruta en el grafo y conteo
            planificador.agregarRuta(ori, dst, 1);
            origenVuelo[id]  = ori;
            destinoVuelo[id] = dst;
            vueloActivo[id]  = true;
            int io  = planificador.buscarIdAeropuerto(ori);
            int idd = planificador.buscarIdAeropuerto(dst);
            if (io >= 0 && idd >= 0) contadorRutas[io][idd]++;

            if (id < nAv) {
                // 2a) A los primeros N vuelos les asigno avión y NO los pongo en la cola
                gestorFlota.asignarAvion(id);
            } else {
                // 2b) A los restantes 40 los encolo en espera
                gestorPrioridad.registrarVuelo(id, ori, dst, tipo);
            }
        }
    }

    // — Operaciones de Simulación —

    /** Agrega un avión nuevo */
    public void agregarNuevoAvion(String matricula, String tipo) {
        gestorFlota.agregarAvion(matricula, tipo);
    }

    /** Agrega una ruta directa en el grafo */
    public void agregarNuevaRuta(String origen, String destino, int peso) {
        planificador.agregarAeropuerto(origen, "");
        planificador.agregarAeropuerto(destino, "");
        planificador.agregarRuta(origen, destino, peso);
    }

    /** Registra un nuevo vuelo en la cola e incrementa el grafo/contadores */
    public void registrarVuelo(int id, String ori, String dst, String tipo) {
        if (id < 0 || id >= maxVuelos) {
            System.out.println("ID de vuelo inválido: " + id);
            return;
        }
        gestorPrioridad.registrarVuelo(id, ori, dst, tipo);
        planificador.agregarAeropuerto(ori, "");
        planificador.agregarAeropuerto(dst, "");
        planificador.agregarRuta(ori, dst, 1);
        origenVuelo[id]  = ori;
        destinoVuelo[id] = dst;
        vueloActivo[id]  = true;
        int io  = planificador.buscarIdAeropuerto(ori);
        int idd = planificador.buscarIdAeropuerto(dst);
        if (io >= 0 && idd >= 0) contadorRutas[io][idd]++;
    }

    /** Modifica la prioridad de un vuelo pendiente */
    public void modificarPrioridadVuelo(int idVuelo, int nuevaPrio) {
        gestorPrioridad.cambiarPrioridadVuelo(idVuelo, nuevaPrio);
    }

    /** Muestra la cola de vuelos pendientes ordenados por prioridad */
    public void mostrarVuelosPendientes() {
        gestorPrioridad.mostrarColaVuelos();
    }

    /** Cancela un vuelo — libera avión y ajusta contadores */
    public void cancelarVuelo(int idVuelo) {
        if (idVuelo < 0 || idVuelo >= maxVuelos || !vueloActivo[idVuelo]) {
            System.out.println("Vuelo inválido o no activo: " + idVuelo);
            return;
        }
        vueloActivo[idVuelo] = false;
        gestorFlota.liberarAvion(idVuelo);
        int io  = planificador.buscarIdAeropuerto(origenVuelo[idVuelo]);
        int idd = planificador.buscarIdAeropuerto(destinoVuelo[idVuelo]);
        if (io >= 0 && idd >= 0) contadorRutas[io][idd]--;
    }

    /** Reprograma un vuelo activo */
    public void reprogramarVuelo(int idVuelo, String nuevoOri, String nuevoDst) {
        if (idVuelo < 0 || idVuelo >= maxVuelos || !vueloActivo[idVuelo]) {
            System.out.println("Vuelo inválido o no activo: " + idVuelo);
            return;
        }
        // Ajustar contador de ruta antigua
        int aO = planificador.buscarIdAeropuerto(origenVuelo[idVuelo]);
        int aD = planificador.buscarIdAeropuerto(destinoVuelo[idVuelo]);
        if (aO >= 0 && aD >= 0) contadorRutas[aO][aD]--;

        // Actualizar datos
        origenVuelo[idVuelo]  = nuevoOri;
        destinoVuelo[idVuelo] = nuevoDst;
        planificador.agregarAeropuerto(nuevoOri, "");
        planificador.agregarAeropuerto(nuevoDst, "");
        planificador.agregarRuta(nuevoOri, nuevoDst, 1);
        int io  = planificador.buscarIdAeropuerto(nuevoOri);
        int idd = planificador.buscarIdAeropuerto(nuevoDst);
        if (io >= 0 && idd >= 0) contadorRutas[io][idd]++;
        System.out.println("Vuelo " + idVuelo +
                " reprogramado a: " + nuevoOri + "→" + nuevoDst);
    }

    /** Determina rutas posibles con un máximo de escalas */
    public void determinarRutasPosibles() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Origen: ");
        String o = sc.nextLine().trim();
        System.out.print("Destino: ");
        String d = sc.nextLine().trim();
        System.out.print("Máx. escalas: ");
        int e = sc.nextInt(); sc.nextLine();
        planificador.imprimirRutas(o, d, e);
    }

    /** Muestra aeropuertos desconectados */
    public void identificarAeropuertosDesconectados() {
        System.out.println("=== Aeropuertos desconectados ===");
        planificador.imprimirAeropuertosDesconectados();
    }

    /** Reporte: aviones con más asignaciones */
    public void mostrarAvionesConMasAsignaciones() {
        System.out.println("=== Aviones con más asignaciones ===");
        gestorFlota.imprimirAvionesConMasAsignaciones();
    }

    /** Muestra todos los aeropuertos registrados */
    public void mostrarAeropuertos() {
        System.out.println("=== Aeropuertos ===");
        int n = planificador.getNumeroAeropuertos();
        for (int i = 0; i < n; i++) {
            System.out.println("- " + planificador.getCodigoAeropuerto(i));
        }
    }

    /** Muestra las conexiones creadas entre aeropuertos */
    public void mostrarRutasActuales() {
        System.out.println("=== Rutas actuales ===");
        GraphADT g = planificador.getGrafo();
        int n = planificador.getNumeroAeropuertos();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (g.existsEdge(i, j)) {
                    System.out.println("- " +
                            planificador.getCodigoAeropuerto(i) +
                            "→" +
                            planificador.getCodigoAeropuerto(j));
                }
            }
        }
    }

    /** Muestra la flota de aviones y su estado */
    public void mostrarFlota() {
        System.out.println("=== Flota de aviones ===");
        int n = gestorFlota.getNumeroAviones();
        for (int i = 0; i < n; i++) {
            String mat   = gestorFlota.getMatricula(i);
            boolean disp = gestorFlota.estaDisponible(i);
            int    cnt   = gestorFlota.getContadorAsignaciones(i);
            String estado = disp ? "Libre" : "Ocupado";
            System.out.println("- " + mat +
                    " (" + estado + ") → Asignaciones: " + cnt);
        }
    }

    /** Reporte: utilización promedio de la flota */
    public void generarReporteUtilizacionPromedio() {
        int total = 0, n = gestorFlota.getNumeroAviones();
        for (int i = 0; i < n; i++) total += gestorFlota.getContadorAsignaciones(i);
        double prom = n > 0 ? (double) total / n : 0.0;
        System.out.println("Utilización promedio de aviones: " + prom);
    }

    /** Reporte: rutas más frecuentes y menos utilizadas */
    public void generarReporteConexionesFrecuentesYMenos() {
        int n = planificador.getNumeroAeropuertos();
        int max = 0, min = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int c = contadorRutas[i][j];
                if (c > max) max = c;
                if (c > 0 && c < min) min = c;
            }
        }
        if (max > 0) {
            System.out.println("=== Rutas más frecuentes (" + max + ") ===");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (contadorRutas[i][j] == max) {
                        System.out.println("- " +
                                planificador.getCodigoAeropuerto(i) +
                                "→" +
                                planificador.getCodigoAeropuerto(j));
                    }
                }
            }
        } else {
            System.out.println("No hay vuelos registrados aún.");
        }
        if (min < Integer.MAX_VALUE) {
            System.out.println("=== Rutas menos utilizadas (" + min + ") ===");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (contadorRutas[i][j] == min) {
                        System.out.println("- " +
                                planificador.getCodigoAeropuerto(i) +
                                "→" +
                                planificador.getCodigoAeropuerto(j));
                    }
                }
            }
        }
    }

    /** Reporte: aeropuertos con más vuelos entrantes y salientes */
    public void reportarEntrantesYSalientes() {
        int n = planificador.getNumeroAeropuertos();
        int[] sal = new int[n], ent = new int[n];
        for (int v = 0; v < maxVuelos; v++) {
            if (vueloActivo[v]) {
                int o = planificador.buscarIdAeropuerto(origenVuelo[v]);
                int d = planificador.buscarIdAeropuerto(destinoVuelo[v]);
                if (o >= 0) sal[o]++;
                if (d >= 0) ent[d]++;
            }
        }
        int maxSal = 0, maxEnt = 0;
        for (int i = 0; i < n; i++) {
            if (sal[i] > maxSal) maxSal = sal[i];
            if (ent[i] > maxEnt) maxEnt = ent[i];
        }
        System.out.println("=== Aeropuertos más salientes (" + maxSal + ") ===");
        for (int i = 0; i < n; i++) {
            if (sal[i] == maxSal) {
                System.out.println("- " + planificador.getCodigoAeropuerto(i));
            }
        }
        System.out.println("=== Aeropuertos más entrantes (" + maxEnt + ") ===");
        for (int i = 0; i < n; i++) {
            if (ent[i] == maxEnt) {
                System.out.println("- " + planificador.getCodigoAeropuerto(i));
            }
        }
    }

    /** Reporte: aeropuertos con más conexiones disponibles */
    public void reportarAeropuertosConMasConexiones() {
        GraphADT g = planificador.getGrafo();
        int n = planificador.getNumeroAeropuertos(), maxC = 0;
        for (int i = 0; i < n; i++) {
            int cnt = 0;
            for (int j = 0; j < n; j++) {
                if (g.existsEdge(i, j) || g.existsEdge(j, i)) cnt++;
            }
            if (cnt > maxC) maxC = cnt;
        }
        int iguales = 0;
        for (int i = 0; i < n; i++) {
            int cnt = 0;
            for (int j = 0; j < n; j++) {
                if (g.existsEdge(i, j) || g.existsEdge(j, i)) cnt++;
            }
            if (cnt == maxC) iguales++;
        }
        if (iguales == n) {
            System.out.println("Todos los aeropuertos tienen " + maxC + " conexiones.");
        } else {
            System.out.println("=== Aeropuertos con más conexiones (" + maxC + ") ===");
            for (int i = 0; i < n; i++) {
                int cnt = 0;
                for (int j = 0; j < n; j++) {
                    if (g.existsEdge(i, j) || g.existsEdge(j, i)) cnt++;
                }
                if (cnt == maxC) {
                    System.out.println("- " + planificador.getCodigoAeropuerto(i));
                }
            }
        }
    }

    /**
     * Desencola el siguiente vuelo pendiente y le asigna el primer avión libre.
     */
    public void asignarSiguienteVuelo() {
        int idVuelo = gestorPrioridad.procesarSiguienteVuelo();
        if (idVuelo >= 0) {
            gestorFlota.asignarAvion(idVuelo);
        }
    }

    /** Menú interactivo */
    public void iniciarMenuSimulacion() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== SIMULADOR DE OPERACIONES Y REPORTES ===");
            System.out.println(" 1) Agregar avión");
            System.out.println(" 2) Agregar ruta");
            System.out.println(" 3) Registrar vuelo");
            System.out.println(" 4) Procesar siguiente vuelo (asignar avión)");
            System.out.println(" 5) Cancelar vuelo");
            System.out.println(" 6) Reprogramar vuelo");
            System.out.println(" 7) Determinar rutas posibles");
            System.out.println(" 8) Aeropuertos desconectados");
            System.out.println(" 9) Aviones con más asignaciones");
            System.out.println("10) Mostrar vuelos pendientes");
            System.out.println("11) Modificar prioridad de vuelo");
            System.out.println("12) Mostrar aeropuertos");
            System.out.println("13) Mostrar rutas actuales");
            System.out.println("14) Mostrar flota de aviones");
            System.out.println("15) Uso promedio de aviones");
            System.out.println("16) Rutas más/menos usadas");
            System.out.println("17) Entrantes/salientes");
            System.out.println("18) Conexiones disponibles");
            System.out.println(" 0) Salir");
            System.out.print("Opción: ");

            int op = sc.nextInt(); sc.nextLine();
            switch (op) {
                case 1 -> {
                    System.out.print("Matrícula: ");
                    String m = sc.nextLine().trim();
                    System.out.print("Tipo: ");
                    String t = sc.nextLine().trim();
                    agregarNuevoAvion(m, t);
                }
                case 2 -> {
                    System.out.print("Origen: ");
                    String o2 = sc.nextLine().trim();
                    System.out.print("Destino: ");
                    String d2 = sc.nextLine().trim();
                    System.out.print("Peso: ");
                    int p2 = sc.nextInt(); sc.nextLine();
                    agregarNuevaRuta(o2, d2, p2);
                }
                case 3 -> {
                    System.out.print("ID vuelo: ");
                    int idv = sc.nextInt(); sc.nextLine();
                    System.out.print("Origen: ");
                    String o3 = sc.nextLine().trim();
                    System.out.print("Destino: ");
                    String d3 = sc.nextLine().trim();
                    System.out.print("Tipo (Carga/Internacional/Nacional): ");
                    String ty = sc.nextLine().trim();
                    registrarVuelo(idv, o3, d3, ty);
                }
                case 4 -> asignarSiguienteVuelo();
                case 5 -> {
                    System.out.print("ID a cancelar: ");
                    cancelarVuelo(sc.nextInt()); sc.nextLine();
                }
                case 6 -> {
                    System.out.print("ID a reprogramar: ");
                    int vr = sc.nextInt(); sc.nextLine();
                    System.out.print("Nuevo origen: ");
                    String no = sc.nextLine().trim();
                    System.out.print("Nuevo destino: ");
                    String nd = sc.nextLine().trim();
                    reprogramarVuelo(vr, no, nd);
                }
                case 7 -> determinarRutasPosibles();
                case 8 -> identificarAeropuertosDesconectados();
                case 9 -> mostrarAvionesConMasAsignaciones();
                case 10 -> mostrarVuelosPendientes();
                case 11 -> {
                    System.out.print("ID de vuelo: ");
                    int idm = sc.nextInt(); sc.nextLine();
                    System.out.print("Nueva prioridad: ");
                    int np = sc.nextInt(); sc.nextLine();
                    modificarPrioridadVuelo(idm, np);
                }
                case 12 -> mostrarAeropuertos();
                case 13 -> mostrarRutasActuales();
                case 14 -> mostrarFlota();
                case 15 -> generarReporteUtilizacionPromedio();
                case 16 -> generarReporteConexionesFrecuentesYMenos();
                case 17 -> reportarEntrantesYSalientes();
                case 18 -> reportarAeropuertosConMasConexiones();
                case 0 -> {
                    System.out.println("¡Hasta luego!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }
}
