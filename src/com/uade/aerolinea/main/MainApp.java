package com.uade.aerolinea.main;

import com.uade.aerolinea.simulacion.SimuladorOperaciones;

public class MainApp {
    public static void main(String[] args) {
        new SimuladorOperaciones(20, 50, 10)
                .iniciarMenuSimulacion();
    }
}