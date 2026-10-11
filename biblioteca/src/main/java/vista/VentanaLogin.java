package vista;

import controlador.LoginController;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de autenticación. Según el rol del usuario que inicia sesión
 * (BIBLIOTECARIO o ESTUDIANTE), VentanaPrincipal habilita distintas opciones.
 */
public class VentanaLogin extends JFrame {

    private final LoginController controller = new LoginController();

    private final JTextField txtCorreo = new JTextField(20);
    private final JPasswordField txtContrasena = new JPasswordField(20);

    public VentanaLogin() {
        super("Biblioteca - Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 220);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panelFormulario.add(new JLabel("Correo:"));
        panelFormulario.add(txtCorreo);
        panelFormulario.add(new JLabel("Contraseña:"));
        panelFormulario.add(txtContrasena);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(e -> intentarLogin());

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnIngresar);

        JLabel lblTitulo = new JLabel("Sistema de Gestión de Biblioteca", SwingConstants.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));

        add(lblTitulo, BorderLayout.NORTH);
        add(panelFormulario, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);

        // Permite presionar Enter en el campo de contraseña para iniciar sesión
        txtContrasena.addActionListener(e -> intentarLogin());
    }

    private void intentarLogin() {
        String correo = txtCorreo.getText();
        String contrasena = new String(txtContrasena.getPassword());

        Usuario usuario = controller.login(correo, contrasena);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido/a, " + usuario.getNombre() + " (" + usuario.getRol() + ")");
            new VentanaPrincipal(usuario).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Correo o contraseña incorrectos.",
                    "Error de autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }
}
