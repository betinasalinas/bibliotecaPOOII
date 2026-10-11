package controlador;

import dao.LibroDAO;
import dao.PrestamoDAO;
import modelo.Prestamo;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de Prestamo. Valida datos antes de llamar al DAO y ejecuta el
 * registro de un préstamo en un hilo en segundo plano (requisito de
 * concurrencia de la actividad), para no congelar la interfaz mientras se
 * actualiza el stock del libro y se guarda el préstamo.
 *
 * El descuento real de stock ocurre en LibroDAO.descontarStock(), que es
 * 'synchronized', garantizando que dos hilos de préstamo no descuenten el
 * mismo libro al mismo tiempo de forma inconsistente.
 */
public class PrestamoController {

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    public static final int DIAS_PLAZO_DEFECTO = 7;

    /**
     * Callback para recibir el resultado del registro de préstamo, que se
     * ejecuta en un hilo en segundo plano. Los métodos de esta interfaz se
     * invocan DESDE ese hilo en segundo plano: quien la implemente en la
     * vista debe usar SwingUtilities.invokeLater(...) antes de tocar
     * cualquier componente Swing.
     */
    public interface PrestamoCallback {
        void onExito(Prestamo prestamo);
        void onError(String mensaje);
    }

    /**
     * Valida los datos y, si son correctos, lanza un hilo independiente que:
     * 1) descuenta el stock del libro (de forma sincronizada),
     * 2) registra el préstamo (lo que también deja constancia en el
     *    historial del estudiante, ya que el préstamo queda asociado a él).
     * La interfaz gráfica no se bloquea mientras esto ocurre.
     */
    public void registrarPrestamoEnSegundoPlano(int idEstudiante, int idLibro, int diasPlazo, PrestamoCallback callback) {
        if (idEstudiante <= 0 || idLibro <= 0) {
            callback.onError("Debes seleccionar un estudiante y un libro.");
            return;
        }
        if (diasPlazo <= 0) {
            diasPlazo = DIAS_PLAZO_DEFECTO;
        }
        final int plazoFinal = diasPlazo;

        Runnable tareaEnSegundoPlano = () -> {
            // 1) Intenta descontar stock de forma sincronizada (evita condiciones de carrera)
            boolean stockDescontado = LibroDAO.descontarStock(idLibro);
            if (!stockDescontado) {
                callback.onError("No hay stock disponible para este libro.");
                return;
            }

            // 2) Calcula fechas y registra el préstamo (queda en el historial del estudiante)
            Date fechaPrestamo = Date.valueOf(LocalDate.now());
            Date fechaDevolucion = Date.valueOf(LocalDate.now().plusDays(plazoFinal));

            Prestamo prestamo = new Prestamo(idEstudiante, idLibro, fechaPrestamo, fechaDevolucion);
            boolean ok = prestamoDAO.create(prestamo);

            if (ok) {
                callback.onExito(prestamo);
            } else {
                // Si falló el registro del préstamo, se repone el stock descontado
                LibroDAO.reponerStock(idLibro);
                callback.onError("No se pudo registrar el préstamo en la base de datos.");
            }
        };

        Thread hilo = new Thread(tareaEnSegundoPlano, "hilo-registro-prestamo");
        hilo.start();
    }

    /**
     * Marca un préstamo como devuelto y repone el stock del libro.
     * Esta operación es rápida, por lo que se ejecuta de forma síncrona.
     */
    public boolean registrarDevolucion(Prestamo prestamo) {
        if (prestamo == null || prestamo.isDevuelto()) return false;

        boolean okDevolucion = prestamoDAO.marcarDevuelto(prestamo.getId());
        if (okDevolucion) {
            LibroDAO.reponerStock(prestamo.getIdLibro());
        }
        return okDevolucion;
    }

    public List<Prestamo> listar() {
        return prestamoDAO.readAll();
    }

    public List<Prestamo> listarVigentes() {
        return prestamoDAO.listarVigentes();
    }

    public List<Prestamo> historialPorEstudiante(int idEstudiante) {
        return prestamoDAO.historialPorEstudiante(idEstudiante);
    }

    public List<Object[]> librosMasPrestados() {
        return prestamoDAO.librosMasPrestados();
    }

    public boolean eliminar(int id) {
        return prestamoDAO.delete(id);
    }
}
