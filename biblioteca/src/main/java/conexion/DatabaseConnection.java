package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de entregar la conexión a la base de datos biblioteca_db.
 * Implementa el patrón Singleton: existe UNA sola instancia de conexión
 * compartida por toda la aplicación, obtenida mediante getInstance().
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_db";
    private static final String USUARIO = "root";
    private static final String CLAVE = "1234abc.";

    // Única instancia de la clase (patrón Singleton)
    private static DatabaseConnection instancia;

    private Connection conexion;

    // Constructor privado: nadie fuera de esta clase puede crear instancias directamente
    private DatabaseConnection() throws SQLException {
        this.conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    /**
     * Devuelve la única instancia de DatabaseConnection que existe en toda
     * la aplicación. Si no existe aún, o si la conexión anterior se cerró,
     * crea una nueva.
     */
    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instancia == null || instancia.getConnection().isClosed()) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    public Connection getConnection() {
        return conexion;
    }
}
