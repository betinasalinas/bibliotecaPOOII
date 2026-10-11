package dao;

import conexion.DatabaseConnection;
import modelo.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Libro. CRUD completo con PreparedStatement/ResultSet.
 *
 * Los métodos que modifican el stock (descontarStock/reponerStock) son
 * 'synchronized' para evitar condiciones de carrera cuando varios hilos
 * de préstamo intentan descontar el mismo libro al mismo tiempo.
 */
public class LibroDAO {

    public boolean create(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) libro.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear libro: " + e.getMessage());
            return false;
        }
    }

    public List<Libro> readAll() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, l.id_categoria, " +
                "c.nombre AS nombre_categoria " +
                "FROM libros l LEFT JOIN categorias c ON l.id_categoria = c.id ORDER BY l.id";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar libros: " + e.getMessage());
        }
        return lista;
    }

    public Libro buscarPorId(int id) {
        String sql = "SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, l.id_categoria, " +
                "c.nombre AS nombre_categoria " +
                "FROM libros l LEFT JOIN categorias c ON l.id_categoria = c.id WHERE l.id = ?";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar libro: " + e.getMessage());
        }
        return null;
    }

    public boolean update(Libro libro) {
        String sql = "UPDATE libros SET titulo=?, autor=?, isbn=?, editorial=?, stock=?, id_categoria=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());
            ps.setInt(7, libro.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Descuenta 1 unidad del stock de un libro, solo si hay stock disponible.
     * Es 'synchronized' a nivel de clase (static) para que, aunque varios hilos
     * de préstamo se ejecuten en paralelo, el descuento de stock se haga de
     * a uno a la vez y nunca quede en negativo por una condición de carrera.
     *
     * Devuelve true si se pudo descontar (había stock), false si no.
     */
    public static synchronized boolean descontarStock(int idLibro) {
        String sqlSelect = "SELECT stock FROM libros WHERE id = ? FOR UPDATE";
        String sqlUpdate = "UPDATE libros SET stock = stock - 1 WHERE id = ? AND stock > 0";

        try {
            Connection con = DatabaseConnection.getInstance().getConnection();
            boolean autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                int stockActual = -1;
                try (PreparedStatement ps = con.prepareStatement(sqlSelect)) {
                    ps.setInt(1, idLibro);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) stockActual = rs.getInt("stock");
                    }
                }

                if (stockActual <= 0) {
                    con.rollback();
                    return false;
                }

                try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, idLibro);
                    int filas = ps.executeUpdate();
                    con.commit();
                    return filas > 0;
                }
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(autoCommitOriginal);
            }
        } catch (SQLException e) {
            System.err.println("Error al descontar stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Repone 1 unidad de stock (al devolver un libro). También sincronizado
     * por la misma razón que descontarStock().
     */
    public static synchronized boolean reponerStock(int idLibro) {
        String sql = "UPDATE libros SET stock = stock + 1 WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al reponer stock: " + e.getMessage());
            return false;
        }
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        Libro libro = new Libro(
                rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"),
                rs.getString("isbn"), rs.getString("editorial"), rs.getInt("stock"),
                rs.getInt("id_categoria")
        );
        libro.setNombreCategoria(rs.getString("nombre_categoria"));
        return libro;
    }
}
