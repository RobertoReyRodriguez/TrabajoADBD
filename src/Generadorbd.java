import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class Generadorbd {

    public static void generarDatos(Connection conexion) {
        try {
            insertarServidores(conexion);

            insertarUsuarios(conexion);

            insertarPersonajes(conexion);

            insertarMapas(conexion);

            insertarZonas(conexion);

            System.out.println("Datos generados e insertados correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al generar datos: " + e.getMessage());
        }
    }

    private static void insertarServidores(Connection conexion) throws SQLException {
        String[] regiones = {"Europa", "América", "Asia"};
        String sql = "INSERT INTO Servidor (nombre, region) VALUES (?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            for (int i = 1; i <= 10; i++) {
                stmt.setString(1, "Servidor" + i);
                stmt.setString(2, regiones[(i - 1) % regiones.length]);
                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println("Servidores insertados.");
        }
    }

    private static void insertarUsuarios(Connection conexion) throws SQLException {
        String[] nombresUsuarios = {
            "AIMAR", "MARKEL", "JULEN", "MARTIN", "IZEI", "AIUR", "IBAI", "JON", "UNAX", "MIKEL", "OIHAN", "OIER",
            "DANEL", "ALEX", "HUGO", "LUKA", "MATEO", "IAN", "IKER", "HODEI", "MARTIN", "HUGO", "MANUEL", "ALEJANDRO",
            "MATEO", "LUCAS", "PABLO", "DANIEL", "ALVARO", "LEO", "CARLOS", "MARIO", "MARCOS", "ADRIAN", "JAVIER",
            "ANTONIO", "GONZALO", "MIGUEL", "JUAN", "DAVID", "MARTIN", "LEO", "HUGO", "LUCAS", "MATEO", "DANIEL",
            "MARC", "ALEJANDRO", "ENZO", "MANUEL"
        };

        String sql = "INSERT INTO Usuario (nombre, codigo) VALUES (?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            for (int i = 0; i < nombresUsuarios.length; i++) {
                stmt.setString(1, nombresUsuarios[i]);
                stmt.setString(2, String.format("%04d", i + 1)); // Código único de 4 dígitos
                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println("Usuarios insertados.");
        }
    }

    private static void insertarPersonajes(Connection conexion) throws SQLException {
        Random random = new Random();
        String sql = "INSERT INTO Personaje (nombre, usuario_id, servidor_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            for (int i = 1; i <= 100; i++) {
                stmt.setString(1, "Personaje" + i);
                stmt.setInt(2, random.nextInt(50) + 1); // Usuario entre 1 y 50
                stmt.setInt(3, random.nextInt(10) + 1); // Servidor entre 1 y 10
                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println("Personajes insertados.");
        }
    }

    private static void insertarMapas(Connection conexion) throws SQLException {
        String sql = "INSERT INTO Mapa (nombre, dificultad) VALUES (?, ?)";
        Random random = new Random();
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            for (int i = 1; i <= 20; i++) {
                stmt.setString(1, "Mapa" + i);
                stmt.setInt(2, random.nextInt(10)); // Dificultad entre 0 y 9
                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println("Mapas insertados.");
        }
    }

    private static void insertarZonas(Connection conexion) throws SQLException {
        String sql = "INSERT INTO Zona (mapa_id, ancho, alto, nombre) VALUES (?, ?, ?, ?)";
        Random random = new Random();
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            for (int mapaId = 1; mapaId <= 20; mapaId++) {
                int numZonas = random.nextInt(5) + 1; // Entre 1 y 5 zonas por mapa
                for (int j = 1; j <= numZonas; j++) {
                    stmt.setInt(1, mapaId);
                    stmt.setInt(2, random.nextInt(100) + 50); // Ancho entre 50 y 150
                    stmt.setInt(3, random.nextInt(100) + 50); // Alto entre 50 y 150
                    stmt.setString(4, "Zona" + j + "_Mapa" + mapaId);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
            System.out.println("Zonas insertadas.");
        }
    }
}
