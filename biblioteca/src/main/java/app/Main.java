package app;

import vista.VentanaLogin;

import javax.swing.*;

/**
 * Punto de entrada de la aplicación. Abre la ventana de login;
 * desde ahí, según el rol del usuario, se navega al resto del sistema.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}
