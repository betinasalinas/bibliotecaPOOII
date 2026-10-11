# Sistema de Gestión de Biblioteca

Proyecto Maven en Java (Swing + JDBC + MySQL) que implementa un sistema de
biblioteca con autenticación por roles, CRUD de libros/categorías/estudiantes,
préstamos y devoluciones con concurrencia, y reportes.

## Arquitectura (MVC + DAO + Singleton)

```
vista        -> Ventanas Swing (JFrame, JTable, JComboBox, JButton...)
controlador  -> Validaciones de negocio e intermediario entre vista y DAO
dao          -> Acceso a datos puro: create(), readAll(), update(), delete()
modelo       -> Clases POJO (Usuario, Estudiante, Libro, Categoria, Prestamo)
conexion     -> DatabaseConnection: Singleton que entrega la única conexión a MySQL
```

- **MVC**: las vistas no contienen lógica de negocio ni SQL; delegan en los
  controladores, que a su vez delegan en los DAO.
- **DAO**: cada entidad tiene su propia clase DAO, responsable únicamente de
  hablar con la base de datos usando `PreparedStatement`/`ResultSet`.
- **Singleton**: `DatabaseConnection.getInstance()` garantiza una única
  instancia de conexión compartida por toda la aplicación.

## Concurrencia

- **Hilo en segundo plano para préstamos**: `PrestamoController.registrarPrestamoEnSegundoPlano()`
  lanza un `Thread` que descuenta el stock del libro y registra el préstamo
  (lo que a su vez queda en el historial del estudiante) sin congelar la
  interfaz. El resultado se comunica a la vista mediante un callback
  (`PrestamoCallback`) que usa `SwingUtilities.invokeLater()` para actualizar
  componentes Swing de forma segura.
- **`synchronized` en el stock**: `LibroDAO.descontarStock()` y
  `LibroDAO.reponerStock()` son métodos `static synchronized`. Esto evita que,
  si dos préstamos del mismo libro se procesan al mismo tiempo (dos hilos en
  paralelo), el stock quede inconsistente o en negativo por una condición de
  carrera. Internamente, `descontarStock()` además usa una transacción
  (`SELECT ... FOR UPDATE` + `UPDATE` + `commit`/`rollback`) para reforzar la
  consistencia a nivel de base de datos.

## Base de datos

Ejecuta en MySQL Workbench, en este orden:
1. `sql/Script_crea_tablas_biblioteca.sql` — crea la base `biblioteca_db` y las tablas:
   `usuarios`, `estudiantes`, `categorias`, `libros`, `prestamos`.
2. `sql/Script_poblado_tablas_biblioteca.sql` — inserta datos de ejemplo
   (usuarios, estudiantes, categorías, libros y 2 préstamos de prueba).

### Usuarios de prueba (para el login)
| Correo | Contraseña | Rol |
|---|---|---|
| ana.soto@biblioteca.cl | 1234 | BIBLIOTECARIO |
| pedro.ramirez@duoc.cl | 1234 | ESTUDIANTE |

## Configuración antes de ejecutar

1. Abre el proyecto en IntelliJ IDEA como proyecto Maven (abre la carpeta que
   contiene `pom.xml`) y haz clic en "Load" si aparece el aviso de Maven.
2. Edita `src/main/java/conexion/DatabaseConnection.java` y reemplaza `CLAVE`
   por tu contraseña real de MySQL.
3. Ejecuta los dos scripts SQL en MySQL Workbench (en el orden indicado arriba).
4. Crea una Run/Debug Configuration: tipo Application, Main class = `app.Main`.
5. Ejecuta. Se abrirá la ventana de login.

## Funcionalidad por rol

### BIBLIOTECARIO (gestión total)
- Gestión de Libros (CRUD + categoría vía combo).
- Gestión de Categorías (CRUD).
- Gestión de Estudiantes (CRUD).
- Préstamos y Devoluciones.
- Reportes.

### ESTUDIANTE (solo préstamo, devolución y consulta)
- Préstamos y Devoluciones.
- Reportes (solo lectura).

## Reportes disponibles
- Libros más prestados (ranking por cantidad de préstamos).
- Préstamos actualmente vigentes (no devueltos), marcando si están atrasados.
- Historial de préstamos por estudiante.

## Manejo de errores

Todas las operaciones DAO usan `try-with-resources` para cerrar
`PreparedStatement`/`ResultSet` automáticamente (la `Connection` del
Singleton se mantiene abierta y se reutiliza) y capturan `SQLException`
mostrando el error en consola. Las ventanas muestran siempre un
`JOptionPane` de éxito o error, y los controladores validan los campos
obligatorios antes de llegar a la base de datos.
