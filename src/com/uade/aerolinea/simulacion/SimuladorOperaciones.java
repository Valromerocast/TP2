package com.uade.aerolinea.simulacion;

import java.util.Scanner;
import com.uade.aerolinea.rutas.PlanificadorRutas;
import com.uade.aerolinea.flota.GestorFlota;
import com.uade.structure.definition.GraphADT;

public class SimuladorOperaciones {
    private PlanificadorRutas planificador;
    private GestorFlota gestorFlota;

    private int maxVuelos;
    private boolean[] vueloActivo;
    private String[] origenVuelo;
    private String[] destinoVuelo;
    private int[][] contadorRutas;

    /**
     * @param capacidadAeropuertos máximo de aeropuertos (p.ej. 20)
     * @param capacidadVuelos      máximo de vuelos (p.ej. 50)
     * @param capacidadAviones     máximo de aviones (p.ej. 10)
     */
    public SimuladorOperaciones(int capacidadAeropuertos,
                                int capacidadVuelos,
                                int capacidadAviones) {
        this.planificador   = new PlanificadorRutas(capacidadAeropuertos);
        this.gestorFlota    = new GestorFlota(capacidadAviones, capacidadVuelos);
        this.maxVuelos      = capacidadVuelos;

        this.vueloActivo    = new boolean[capacidadVuelos];
        this.origenVuelo    = new String[capacidadVuelos];
        this.destinoVuelo   = new String[capacidadVuelos];
        for (int v = 0; v < capacidadVuelos; v++) {
            vueloActivo[v] = false;
        }

        this.contadorRutas  = new int[capacidadAeropuertos][capacidadAeropuertos];

        cargarAeropuertosIniciales();   // 15 aeropuertos
        cargarFlotaInicial();           // 10 aviones
        cargarVuelosIniciales();        // 50 vuelos planificados
    }

    /** Carga los 15 aeropuertos predefinidos */
    private void cargarAeropuertosIniciales() {
        String[] codigos = {
                "ATL","PEK","LHR","CDG","HND",
                "DXB","LAX","SIN","FRA","AMS",
                "GRU","SYD","JFK","ICN","MAD"
        };
        String[] descripciones = {
                "Hartsfield-Jackson Atlanta Intl. (EE. UU.)",
                "Beijing Capital Intl. (China)",
                "London Heathrow (Reino Unido)",
                "Charles de Gaulle (Francia)",
                "Tokyo Haneda (Japón)",
                "Dubai International (Emiratos Árabes Unidos)",
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
        for (int i = 0; i < codigos.length; i++) {
            planificador.agregarAeropuerto(codigos[i], descripciones[i]);
        }
    }

    /** Carga una flota inicial de 10 aviones de muestra */
    private void cargarFlotaInicial() {
        String[] matriculas = {
                "AA101","BB202","CC303","DD404","EE505",
                "FF606","GG707","HH808","II909","JJ010"
        };
        String[] tipos = {
                "Boeing 737","Airbus A320","Boeing 777","Embraer 190","Airbus A330",
                "Boeing 747","Airbus A350","Boeing 787","Embraer 175","Bombardier CRJ"
        };
        for (int i = 0; i < matriculas.length; i++) {
            gestorFlota.agregarAvion(matriculas[i], tipos[i]);
        }
    }

    /** Planifica 50 vuelos iniciales (carga, internacional, nacional) */
    private void cargarVuelosIniciales() {
        int nAer = planificador.getNumeroAeropuertos();
        for (int id = 0; id < maxVuelos; id++) {
            String origen  = planificador.getCodigoAeropuerto(id % nAer);
            String destino = planificador.getCodigoAeropuerto((id + 1) % nAer);
            String tipo;
            switch (id % 3) {
                case 0: tipo = "Carga"; break;
                case 1: tipo = "Internacional"; break;
                default: tipo = "Nacional";
            }
            agregarNuevoVuelo(id, origen, destino, tipo);
        }
    }

    /** Agrega un nuevo avión a la flota */
    public void agregarNuevoAvion(String matricula, String tipo) {
        gestorFlota.agregarAvion(matricula, tipo);
    }

    /** Agrega una nueva ruta posible (no modifica vuelos ya planeados) */
    public void agregarNuevaRuta(String origen, String destino, int peso) {
        planificador.agregarAeropuerto(origen, "");
        planificador.agregarAeropuerto(destino, "");
        planificador.agregarRuta(origen, destino, peso);
    }

    /** Planifica un nuevo vuelo, asigna avión y cuenta la ruta */
    public void agregarNuevoVuelo(int idVuelo, String origen, String destino, String tipoVuelo) {
        if (idVuelo < 0 || idVuelo >= maxVuelos) {
            System.out.println("ID de vuelo inválido: " + idVuelo);
            return;
        }
        origenVuelo[idVuelo]  = origen;
        destinoVuelo[idVuelo] = destino;
        vueloActivo[idVuelo]  = true;

        planificador.agregarAeropuerto(origen, "");
        planificador.agregarAeropuerto(destino, "");
        gestorFlota.asignarAvion(idVuelo);

        int idO = planificador.buscarIdAeropuerto(origen);
        int idD = planificador.buscarIdAeropuerto(destino);
        if (idO >= 0 && idD >= 0) {
            contadorRutas[idO][idD]++;
        }
    }

    /** Cancela un vuelo activo: libera avión y ajusta contadores */
    public void cancelarVuelo(int idVuelo) {
        if (idVuelo < 0 || idVuelo >= maxVuelos || !vueloActivo[idVuelo]) {
            System.out.println("Vuelo inválido o no activo: " + idVuelo);
            return;
        }
        vueloActivo[idVuelo] = false;
        gestorFlota.liberarAvion(idVuelo);

        int idO = planificador.buscarIdAeropuerto(origenVuelo[idVuelo]);
        int idD = planificador.buscarIdAeropuerto(destinoVuelo[idVuelo]);
        if (idO >= 0 && idD >= 0) {
            contadorRutas[idO][idD]--;
        }
    }

    /** Reprograma un vuelo activo: cambia ruta y ajusta contadores */
    public void reprogramarVuelo(int idVuelo, String nuevoOrigen, String nuevoDestino) {
        if (idVuelo < 0 || idVuelo >= maxVuelos || !vueloActivo[idVuelo]) {
            System.out.println("Vuelo inválido o no activo: " + idVuelo);
            return;
        }
        int antiguoO = planificador.buscarIdAeropuerto(origenVuelo[idVuelo]);
        int antiguoD = planificador.buscarIdAeropuerto(destinoVuelo[idVuelo]);
        if (antiguoO >= 0 && antiguoD >= 0) {
            contadorRutas[antiguoO][antiguoD]--;
        }

        origenVuelo[idVuelo]  = nuevoOrigen;
        destinoVuelo[idVuelo] = nuevoDestino;

        planificador.agregarAeropuerto(nuevoOrigen, "");
        planificador.agregarAeropuerto(nuevoDestino, "");
        planificador.agregarRuta(nuevoOrigen, nuevoDestino, 0);

        int idO = planificador.buscarIdAeropuerto(nuevoOrigen);
        int idD = planificador.buscarIdAeropuerto(nuevoDestino);
        if (idO >= 0 && idD >= 0) {
            contadorRutas[idO][idD]++;
        }
        System.out.println("Vuelo " + idVuelo +
                " reprogramado a: " + nuevoOrigen + "→" + nuevoDestino);
    }

    /** Muestra aeropuertos desconectados tras las operaciones */
    public void analizarImpactoConectividad() {
        System.out.println("=== Aeropuertos desconectados tras simulación ===");
        planificador.imprimirAeropuertosDesconectados();
    }

    /** Reporte: utilización promedio de la flota */
    public void generarReporteUtilizacionPromedio() {
        int totalAsignaciones = 0;
        int nAviones = gestorFlota.getNumeroAviones();
        for (int i = 0; i < nAviones; i++) {
            totalAsignaciones += gestorFlota.getContadorAsignaciones(i);
        }
        double promedio = nAviones > 0
                ? (double) totalAsignaciones / nAviones
                : 0.0;
        System.out.println("Utilización promedio de aviones: " + promedio);
    }

    /** Reporte: rutas más frecuentes y menos utilizadas */
    public void generarReporteConexionesFrecuentesYMenos() {
        int n = planificador.getNumeroAeropuertos();
        int max = 0;
        int min = Integer.MAX_VALUE;

        // hallar max y min (>0)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int c = contadorRutas[i][j];
                if (c > max) max = c;
                if (c > 0 && c < min) min = c;
            }
        }

        if (max > 0) {
            System.out.println("=== Rutas más frecuentes (" + max + " vuelos) ===");
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
            System.out.println("=== Rutas menos utilizadas (" + min + " vuelos) ===");
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
        } else {
            System.out.println("Todas las rutas existentes no han sido voladas aún.");
        }
    }

    /** Reporte: aeropuertos con más vuelos entrantes y salientes */
    public void reportarEntrantesYSalientes() {
        int n = planificador.getNumeroAeropuertos();
        int[] salientes = new int[n], entrantes = new int[n];

        for (int v = 0; v < maxVuelos; v++) {
            if (vueloActivo[v]) {
                int o = planificador.buscarIdAeropuerto(origenVuelo[v]);
                int d = planificador.buscarIdAeropuerto(destinoVuelo[v]);
                if (o >= 0) salientes[o]++;
                if (d >= 0) entrantes[d]++;
            }
        }

        int maxSal = 0, maxEnt = 0;
        for (int i = 0; i < n; i++) {
            if (salientes[i] > maxSal) maxSal = salientes[i];
            if (entrantes[i] > maxEnt) maxEnt = entrantes[i];
        }

        System.out.println("=== Aeropuertos con más vuelos salientes (" + maxSal + ") ===");
        for (int i = 0; i < n; i++) {
            if (salientes[i] == maxSal) {
                System.out.println("- " + planificador.getCodigoAeropuerto(i));
            }
        }

        System.out.println("=== Aeropuertos con más vuelos entrantes (" + maxEnt + ") ===");
        for (int i = 0; i < n; i++) {
            if (entrantes[i] == maxEnt) {
                System.out.println("- " + planificador.getCodigoAeropuerto(i));
            }
        }
    }

    /** Reporte: aeropuertos con más conexiones disponibles */
    public void reportarAeropuertosConMasConexiones() {
        GraphADT grafo = planificador.getGrafo();
        int n = planificador.getNumeroAeropuertos();
        int maxCon = 0;

        for (int i = 0; i < n; i++) {
            int cuenta = 0;
            for (int j = 0; j < n; j++) {
                if (grafo.existsEdge(i, j) || grafo.existsEdge(j, i)) {
                    cuenta++;
                }
            }
            if (cuenta > maxCon) maxCon = cuenta;
        }

        System.out.println("=== Aeropuertos con más conexiones (" + maxCon + ") ===");
        for (int i = 0; i < n; i++) {
            int cuenta = 0;
            for (int j = 0; j < n; j++) {
                if (grafo.existsEdge(i, j) || grafo.existsEdge(j, i)) {
                    cuenta++;
                }
            }
            if (cuenta == maxCon) {
                System.out.println("- " + planificador.getCodigoAeropuerto(i));
            }
        }
    }

    /** Menú interactivo de simulación y reportes */
    public void iniciarMenuSimulacion() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== SIMULADOR DE OPERACIONES Y REPORTES ===");
            System.out.println("1) Agregar avión");
            System.out.println("2) Agregar ruta");
            System.out.println("3) Agregar vuelo");
            System.out.println("4) Cancelar vuelo");
            System.out.println("5) Reprogramar vuelo");
            System.out.println("6) Analizar conectividad");
            System.out.println("7) Informe: uso promedio de aviones");
            System.out.println("8) Informe: rutas más/menos usadas");
            System.out.println("9) Informe: aeropuertos entrantes/salientes");
            System.out.println("10) Informe: aeropuertos con más conexiones");
            System.out.println("0) Salir");
            System.out.print("Opción: ");

            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1:
                    System.out.print("Matrícula del avión: ");
                    String m = sc.nextLine().trim();
                    System.out.print("Tipo de avión: ");
                    String t = sc.nextLine().trim();
                    agregarNuevoAvion(m, t);
                    break;
                case 2:
                    System.out.print("Origen: ");
                    String o2 = sc.nextLine().trim();
                    System.out.print("Destino: ");
                    String d2 = sc.nextLine().trim();
                    System.out.print("Peso (distancia): ");
                    int p2 = sc.nextInt(); sc.nextLine();
                    agregarNuevaRuta(o2, d2, p2);
                    break;
                case 3:
                    System.out.print("ID de vuelo: ");
                    int v3 = sc.nextInt(); sc.nextLine();
                    System.out.print("Origen: ");
                    String o3 = sc.nextLine().trim();
                    System.out.print("Destino: ");
                    String d3 = sc.nextLine().trim();
                    System.out.print("Tipo (Carga/Internacional/Nacional): ");
                    String t3 = sc.nextLine().trim();
                    agregarNuevoVuelo(v3, o3, d3, t3);
                    break;
                case 4:
                    System.out.print("ID de vuelo a cancelar: ");
                    cancelarVuelo(sc.nextInt()); sc.nextLine();
                    break;
                case 5:
                    System.out.print("ID de vuelo a reprogramar: ");
                    int vr = sc.nextInt(); sc.nextLine();
                    System.out.print("Nuevo origen: ");
                    String no = sc.nextLine().trim();
                    System.out.print("Nuevo destino: ");
                    String nd = sc.nextLine().trim();
                    reprogramarVuelo(vr, no, nd);
                    break;
                case 6:
                    analizarImpactoConectividad();
                    break;
                case 7:
                    generarReporteUtilizacionPromedio();
                    break;
                case 8:
                    generarReporteConexionesFrecuentesYMenos();
                    break;
                case 9:
                    reportarEntrantesYSalientes();
                    break;
                case 10:
                    reportarAeropuertosConMasConexiones();
                    break;
                case 0:
                    System.out.println("Cerrando simulador. ¡Hasta luego!");
                    sc.close();
                    return;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

}