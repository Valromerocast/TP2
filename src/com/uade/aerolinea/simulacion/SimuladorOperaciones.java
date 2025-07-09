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
    private String[] origenVuelo, destinoVuelo;
    private int[][] contadorRutas;

    public SimuladorOperaciones(int capAeropuertos,
                                int capVuelos,
                                int capAviones) {
        this.planificador  = new PlanificadorRutas(capAeropuertos);
        this.gestorFlota   = new GestorFlota(capAviones, capVuelos);
        this.maxVuelos     = capVuelos;

        this.vueloActivo   = new boolean[capVuelos];
        this.origenVuelo   = new String[capVuelos];
        this.destinoVuelo  = new String[capVuelos];
        for (int i = 0; i < capVuelos; i++) vueloActivo[i] = false;

        this.contadorRutas = new int[capAeropuertos][capAeropuertos];

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

    private void cargarVuelosIniciales() {
        int nAer = planificador.getNumeroAeropuertos();
        int nAv  = gestorFlota.getNumeroAviones();
        for (int id = 0; id < maxVuelos; id++) {
            String ori = planificador.getCodigoAeropuerto(id % nAer);
            String dst = planificador.getCodigoAeropuerto((id + 1) % nAer);

            // Asegurar la ruta en el grafo
            planificador.agregarRuta(ori, dst, 1);

            // Registrar vuelo y contar ruta
            origenVuelo[id]  = ori;
            destinoVuelo[id] = dst;
            vueloActivo[id]  = true;
            int io  = planificador.buscarIdAeropuerto(ori);
            int idd = planificador.buscarIdAeropuerto(dst);
            if (io >= 0 && idd >= 0) contadorRutas[io][idd]++;

            // Asignar avión solo a los primeros nAv vuelos
            if (id < nAv) {
                gestorFlota.asignarAvion(id);
            }
        }
    }

    public void agregarNuevoAvion(String matricula, String tipo) {
        gestorFlota.agregarAvion(matricula, tipo);
    }

    public void agregarNuevaRuta(String origen, String destino, int peso) {
        planificador.agregarAeropuerto(origen, "");
        planificador.agregarAeropuerto(destino, "");
        planificador.agregarRuta(origen, destino, peso);
    }

    public void agregarNuevoVuelo(int idVuelo,
                                  String origen,
                                  String destino,
                                  String tipoVuelo) {
        if (idVuelo < 0 || idVuelo >= maxVuelos) {
            System.out.println("ID de vuelo inválido: " + idVuelo);
            return;
        }
        origenVuelo[idVuelo]  = origen;
        destinoVuelo[idVuelo] = destino;
        vueloActivo[idVuelo]  = true;

        planificador.agregarAeropuerto(origen, "");
        planificador.agregarAeropuerto(destino, "");
        planificador.agregarRuta(origen, destino, 1);

        int io  = planificador.buscarIdAeropuerto(origen);
        int idd = planificador.buscarIdAeropuerto(destino);
        if (io >= 0 && idd >= 0) contadorRutas[io][idd]++;

        gestorFlota.asignarAvion(idVuelo);
    }

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

    public void reprogramarVuelo(int idVuelo,
                                 String nuevoOrigen,
                                 String nuevoDestino) {
        if (idVuelo < 0 || idVuelo >= maxVuelos || !vueloActivo[idVuelo]) {
            System.out.println("Vuelo inválido o no activo: " + idVuelo);
            return;
        }
        int aO = planificador.buscarIdAeropuerto(origenVuelo[idVuelo]);
        int aD = planificador.buscarIdAeropuerto(destinoVuelo[idVuelo]);
        if (aO >= 0 && aD >= 0) contadorRutas[aO][aD]--;

        origenVuelo[idVuelo]  = nuevoOrigen;
        destinoVuelo[idVuelo] = nuevoDestino;
        planificador.agregarAeropuerto(nuevoOrigen, "");
        planificador.agregarAeropuerto(nuevoDestino, "");
        planificador.agregarRuta(nuevoOrigen, nuevoDestino, 1);

        int io  = planificador.buscarIdAeropuerto(nuevoOrigen);
        int idd = planificador.buscarIdAeropuerto(nuevoDestino);
        if (io >= 0 && idd >= 0) contadorRutas[io][idd]++;
        System.out.println("Vuelo " + idVuelo +
                " reprogramado: " + nuevoOrigen + "→" + nuevoDestino);
    }

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

    public void identificarAeropuertosDesconectados() {
        System.out.println("=== Aeropuertos desconectados ===");
        planificador.imprimirAeropuertosDesconectados();
    }

    public void mostrarAvionesConMasAsignaciones() {
        System.out.println("=== Aviones con más asignaciones ===");
        gestorFlota.imprimirAvionesConMasAsignaciones();
    }

    /**
     * Opción 9: libera el avión asignado al vuelo y
     * permite cancelar o reasignar inmediatamente.
     */
    public void liberarOReasignarAvion(int idVuelo) {
        gestorFlota.liberarAvion(idVuelo);
        Scanner sc = new Scanner(System.in);
        System.out.print("¿Cancelar vuelo " + idVuelo + "? (s/n): ");
        String resp = sc.nextLine().trim().toLowerCase();
        if (resp.startsWith("s")) {
            cancelarVuelo(idVuelo);
            return;
        }
        System.out.print("¿Reasignar otro avión? (s/n): ");
        resp = sc.nextLine().trim().toLowerCase();
        if (resp.startsWith("s")) {
            gestorFlota.asignarAvion(idVuelo);
        }
    }

    public void asignarAvionAStandby(int idVuelo) {
        gestorFlota.asignarAvion(idVuelo);
    }

    public void generarReporteUtilizacionPromedio() {
        int total = 0, n = gestorFlota.getNumeroAviones();
        for (int i = 0; i < n; i++) {
            total += gestorFlota.getContadorAsignaciones(i);
        }
        double prom = n > 0 ? (double) total / n : 0.0;
        System.out.println("Utilización promedio de aviones: " + prom);
    }

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
                                planificador.getCodigoAeropuerto(i) + "→" +
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
                                planificador.getCodigoAeropuerto(i) + "→" +
                                planificador.getCodigoAeropuerto(j));
                    }
                }
            }
        }
    }

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
        int ms = 0, me = 0;
        for (int i = 0; i < n; i++) {
            if (sal[i] > ms) ms = sal[i];
            if (ent[i] > me) me = ent[i];
        }
        System.out.println("=== Aeropuertos con más salientes (" + ms + ") ===");
        for (int i = 0; i < n; i++) if (sal[i] == ms)
            System.out.println("- " + planificador.getCodigoAeropuerto(i));
        System.out.println("=== Aeropuertos con más entrantes (" + me + ") ===");
        for (int i = 0; i < n; i++) if (ent[i] == me)
            System.out.println("- " + planificador.getCodigoAeropuerto(i));
    }

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

    public void iniciarMenuSimulacion() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== SIMULADOR DE OPERACIONES ===");
            System.out.println(" 1) Agregar avión");
            System.out.println(" 2) Agregar ruta");
            System.out.println(" 3) Agregar vuelo");
            System.out.println(" 4) Cancelar vuelo");
            System.out.println(" 5) Reprogramar vuelo");
            System.out.println(" 6) Determinar rutas posibles");
            System.out.println(" 7) Aeropuertos desconectados");
            System.out.println(" 8) Aviones con más asignaciones");
            System.out.println(" 9) Liberar/reasignar avión de vuelo");
            System.out.println("10) Uso promedio de aviones");
            System.out.println("11) Rutas más/menos usadas");
            System.out.println("12) Entrantes/salientes");
            System.out.println("13) Conexiones disponibles");
            System.out.println("14) Asignar avión a vuelo pendiente");
            System.out.println(" 0) Salir");
            System.out.print("Opción: ");

            int op = sc.nextInt(); sc.nextLine();
            switch (op) {
                case 1:
                    System.out.print("Matrícula: ");
                    String m = sc.nextLine().trim();
                    System.out.print("Tipo: ");
                    String t = sc.nextLine().trim();
                    agregarNuevoAvion(m, t);
                    break;
                case 2:
                    System.out.print("Origen: ");
                    String o2 = sc.nextLine().trim();
                    System.out.print("Destino: ");
                    String d2 = sc.nextLine().trim();
                    System.out.print("Peso: ");
                    int p2 = sc.nextInt(); sc.nextLine();
                    agregarNuevaRuta(o2, d2, p2);
                    break;
                case 3:
                    System.out.print("ID vuelo: ");
                    int v3 = sc.nextInt(); sc.nextLine();
                    System.out.print("Origen: ");
                    String o3 = sc.nextLine().trim();
                    System.out.print("Destino: ");
                    String d3 = sc.nextLine().trim();
                    System.out.print("Tipo: ");
                    String ty = sc.nextLine().trim();
                    agregarNuevoVuelo(v3, o3, d3, ty);
                    break;
                case 4:
                    System.out.print("ID a cancelar: ");
                    cancelarVuelo(sc.nextInt()); sc.nextLine();
                    break;
                case 5:
                    System.out.print("ID a reprogramar: ");
                    int vr = sc.nextInt(); sc.nextLine();
                    System.out.print("Nuevo origen: ");
                    String no = sc.nextLine().trim();
                    System.out.print("Nuevo destino: ");
                    String nd = sc.nextLine().trim();
                    reprogramarVuelo(vr, no, nd);
                    break;
                case 6:
                    determinarRutasPosibles();
                    break;
                case 7:
                    identificarAeropuertosDesconectados();
                    break;
                case 8:
                    mostrarAvionesConMasAsignaciones();
                    break;
                case 9:
                    System.out.print("ID de vuelo: ");
                    liberarOReasignarAvion(sc.nextInt());
                    sc.nextLine();
                    break;
                case 10:
                    generarReporteUtilizacionPromedio();
                    break;
                case 11:
                    generarReporteConexionesFrecuentesYMenos();
                    break;
                case 12:
                    reportarEntrantesYSalientes();
                    break;
                case 13:
                    reportarAeropuertosConMasConexiones();
                    break;
                case 14:
                    System.out.print("ID vuelo pendiente: ");
                    asignarAvionAStandby(sc.nextInt());
                    sc.nextLine();
                    break;
                case 0:
                    System.out.println("¡Hasta luego!");
                    sc.close();
                    return;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

}
