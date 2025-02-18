import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dto.*;
public class AnalisisDAO {

    private final Connection connection;

    public AnalisisDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * rankServers():
     * Devuelve los 5 servidores con más personajes.
     * Usamos Statement porque es una consulta simple sin parámetros.
     */
    public List<ServidorRankingDTO> rankServers() throws SQLException {
        List<ServidorRankingDTO> lista = new ArrayList<>();

        String sql = """
            SELECT s.nombre AS servidor, COUNT(p.id) AS totalPersonajes
            FROM Servidor s
            LEFT JOIN Personaje p ON s.id = p.servidor_id
            GROUP BY s.id
            ORDER BY totalPersonajes DESC
            LIMIT 5
        """;

        // try-with-resources para cerrar Statement y ResultSet
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String nombreServidor = rs.getString("servidor");
                int total = rs.getInt("totalPersonajes");
                lista.add(new ServidorRankingDTO(nombreServidor, total));
            }
        }

        return lista;
    }

    /**
     * getUserPjs():
     * Consulta con parámetro (nombre de usuario).
     * Usamos PreparedStatement.
     */
    public UserPjsDTO getUserPjs(String userName) throws SQLException {
        String sql = """
            SELECT u.nombre AS usuario,
                   COUNT(p.id) AS totalPersonajes,
                   GROUP_CONCAT(DISTINCT s.nombre SEPARATOR ', ') AS servidores
            FROM Usuario u
            LEFT JOIN Personaje p ON p.usuario_id = u.id
            LEFT JOIN Servidor s ON p.servidor_id = s.id
            WHERE u.nombre = ?
            GROUP BY u.id
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreUsuario = rs.getString("usuario");
                    int total = rs.getInt("totalPersonajes");
                    String servidores = rs.getString("servidores");
                    return new UserPjsDTO(nombreUsuario, total, servidores);
                }
            }
        }

        // Si no existe el usuario o no tiene personajes, devolvemos null (o podrías retornar un DTO vacío)
        return null;
    }

    /**
     * userP3():
     * Retorna todos los usuarios con más de 3 personajes (o el número que indique el PDF).
     * Usamos Statement porque no hay parámetros.
     */
    public List<UsuarioPersonajesDTO> userP3() throws SQLException {
        List<UsuarioPersonajesDTO> lista = new ArrayList<>();

        String sql = """
            SELECT u.nombre AS usuario, COUNT(p.id) AS totalPersonajes
            FROM Usuario u
            LEFT JOIN Personaje p ON p.usuario_id = u.id
            GROUP BY u.id
            HAVING COUNT(p.id) > 3
            ORDER BY totalPersonajes DESC
        """;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String usuario = rs.getString("usuario");
                int total = rs.getInt("totalPersonajes");
                lista.add(new UsuarioPersonajesDTO(usuario, total));
            }
        }

        return lista;
    }

    /**
     * Ejemplo extra: Si necesitaras hacer inserts o updates en lote,
     * usarías PreparedStatement con addBatch() y executeBatch().
     */
    public void ejemploBatchInsert(List<String> nombres) throws SQLException {
        String sql = "INSERT INTO TablaDeEjemplo (nombre) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String n : nombres) {
                ps.setString(1, n);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

}
