import java.sql.Connection;
import java.sql.Statement;

public class Creadorbd {
    public static void crearTablas(Connection conexion) {
        try (Statement stmt = conexion.createStatement()) {
            // Crea tabla Servidor
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS Servidor (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL,
                            region VARCHAR(100) NOT NULL
                        )
                    """);

            // Crea tabla Usuario
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS Usuario (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL,
                            codigo CHAR(4) NOT NULL UNIQUE
                        )
                    """);

            // Crear tabla Personaje
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS Personaje (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL,
                            usuario_id INT NOT NULL,
                            servidor_id INT NOT NULL,
                            FOREIGN KEY (usuario_id) REFERENCES Usuario(id) ON DELETE CASCADE,
                            FOREIGN KEY (servidor_id) REFERENCES Servidor(id) ON DELETE CASCADE
                        )
                    """);

            // Crea tabla Mapa
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS Mapa (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL,
                            dificultad TINYINT NOT NULL CHECK (dificultad BETWEEN 0 AND 9)
                        )
                    """);

            // Crea tabla Servidor_Mapa (relación N:M entre Servidor y Mapa)
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS Servidor_Mapa (
                            servidor_id INT NOT NULL,
                            mapa_id INT NOT NULL,
                            PRIMARY KEY (servidor_id, mapa_id),
                            FOREIGN KEY (servidor_id) REFERENCES Servidor(id) ON DELETE CASCADE,
                            FOREIGN KEY (mapa_id) REFERENCES Mapa(id) ON DELETE CASCADE
                        )
                    """);

            // Crea tabla Zona
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS Zona (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            mapa_id INT NOT NULL,
                            ancho INT NOT NULL,
                            alto INT NOT NULL,
                            nombre VARCHAR(100) NOT NULL,
                            FOREIGN KEY (mapa_id) REFERENCES Mapa(id) ON DELETE CASCADE
                        )
                    """);

            System.out.println("Tablas creadas o ya existentes.");
        } catch (Exception e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
        }
    }
}
