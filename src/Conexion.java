import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // Constantes internas para los datos de la conexión
    private static final String DB_URL = "jdbc:mysql://213.32.47.49:3333/addb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "abc123.";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Instancia única para implementar el patrón Singleton
    private static Conexion instancia;
    private Connection conexion;

    // Constructor privado para evitar instancias externas
    private Conexion() {
        try {
            Class.forName(DB_DRIVER);
            conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Conexión establecida con éxito.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al establecer la conexión: " + e.getMessage());
        }
    }

    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada con éxito.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    
    
}
