package modelo;

import java.sql.Date;

/**
 * Préstamo de un libro a un estudiante.
 */
public class Prestamo {

    private int id;
    private int idEstudiante;
    private int idLibro;
    private Date fechaPrestamo;
    private Date fechaDevolucion; // fecha de vencimiento del préstamo
    private boolean devuelto;

    // Datos legibles cargados vía JOIN, para mostrar en tablas/reportes
    private String nombreEstudiante;
    private String tituloLibro;

    public Prestamo(int idEstudiante, int idLibro, Date fechaPrestamo, Date fechaDevolucion) {
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = false;
    }

    public Prestamo(int id, int idEstudiante, int idLibro, Date fechaPrestamo, Date fechaDevolucion, boolean devuelto) {
        this.id = id;
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    public Date getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(Date fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public Date getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(Date fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public boolean isDevuelto() { return devuelto; }
    public void setDevuelto(boolean devuelto) { this.devuelto = devuelto; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }

    /**
     * Un préstamo está atrasado si aún no se ha devuelto y la fecha actual
     * ya pasó la fecha de vencimiento (fechaDevolucion).
     */
    public boolean estaAtrasado() {
        if (devuelto) return false;
        Date hoy = new Date(System.currentTimeMillis());
        return hoy.after(fechaDevolucion);
    }

    @Override
    public String toString() {
        return "Préstamo #" + id + " [estudiante=" + idEstudiante + ", libro=" + idLibro + "]";
    }
}
