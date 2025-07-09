# TP2

Descripción
El proyecto simula la gestión operativa de una aerolínea, incluyendo:
Planificación y gestión de rutas aéreas entre aeropuertos.
Gestión de vuelos con prioridad y asignación de aviones.
Simulación de operaciones con menú interactivo.
Análisis y reportes sobre conexiones y tráfico de aeropuertos.


Módulos principales
PlanificadorRutas: Representa aeropuertos y rutas entre ellos en un grafo, permite agregar aeropuertos y rutas, imprimir rutas disponibles y aeropuertos desconectados.
GestorFlota: Administra aviones, disponibilidad y asignación de aviones a vuelos.
GestorPrioridadVuelos: Maneja vuelos con prioridad, estados y asignación de vuelos a la flota.
SimuladorOperaciones: Integra la simulación general con vuelos, flota y rutas, y presenta un menú para operar la simulación.
AnalizadorConexiones: Realiza análisis estadístico y reportes sobre aeropuertos y conexiones en la red y operaciones de vuelos.

Estructura de paquetes
com.uade.aerolinea.rutas: gestión de aeropuertos y rutas.
com.uade.aerolinea.flota: gestión de aviones.
com.uade.aerolinea.vuelos: gestión de vuelos y prioridades.
com.uade.aerolinea.simulacion: simulador con menú de operaciones.
com.uade.aerolinea.analytics: análisis y reportes.
