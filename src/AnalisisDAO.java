import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO que implementa los 8 métodos solicitados en el enunciado,
 * devolviendo objetos DTO o colecciones de DTO.
 */
public class AnalisisDAO {

    private Connection connection;

    public AnalisisDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * 1) El número de personajes por usuario, devolviendo el nombre de usuario
     * y número de personajes siempre que el número sea >= 1.
     */
    public List<UsuarioPersonajesDTO> numeroPersonajesPorUsuario() throws SQLException {
        List<UsuarioPersonajesDTO> resultado = new ArrayList<>();

        String sql = """
            SELECT u.nombre AS usuario, COUNT(p.id) AS total
            FROM Usuario u
            LEFT JOIN Personaje p ON u.id = p.usuario_id
            GROUP BY u.id
            HAVING COUNT(p.id) >= 1
            ORDER BY total DESC
        """;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String usuario = rs.getString("usuario");
                int total = rs.getInt("total");
                resultado.add(new UsuarioPersonajesDTO(usuario, total));
            }
        }

        return resultado;
    }

    /**
     * 2) El número de personajes de un usuario X, devolviendo su nombre y número.
     *    Si el usuario no existe o no tiene personajes, podrías devolver null
     *    o un DTO con total=0, según prefieras.
     */
    public UsuarioPersonajesDTO numeroPersonajesDeUsuarioX(String userName) throws SQLException {
        String sql = """
            SELECT u.nombre AS usuario, COUNT(p.id) AS total
            FROM Usuario u
            LEFT JOIN Personaje p ON u.id = p.usuario_id
            WHERE u.nombre = ?
            GROUP BY u.id
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String usuario = rs.getString("usuario");
                    int total = rs.getInt("total");
                    return new UsuarioPersonajesDTO(usuario, total);
                }
            }
        }

        return null; // o un DTO con total=0
    }

    /**
     * 3) Los personajes de un usuario X, devolviendo el nombre del usuario,
     *    el nombre de cada personaje y el servidor en el que están.
     */
    public UsuarioConPersonajesDTO personajesDeUsuarioX(String userName) throws SQLException {
        // 1) Primero comprobamos el nombre real del usuario (por si no existe)
        String sqlUsuario = """
            SELECT nombre
            FROM Usuario
            WHERE nombre = ?
        """;

        String nombreUsuario = null;
        try (PreparedStatement ps = connection.prepareStatement(sqlUsuario)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombreUsuario = rs.getString("nombre");
                }
            }
        }

        if (nombreUsuario == null) {
            // Usuario no encontrado
            return null;
        }

        // 2) Obtener los personajes y el servidor de cada uno
        String sqlPjs = """
            SELECT p.nombre AS personaje, s.nombre AS servidor
            FROM Personaje p
            JOIN Servidor s ON p.servidor_id = s.id
            JOIN Usuario u ON p.usuario_id = u.id
            WHERE u.nombre = ?
            ORDER BY s.nombre, p.nombre
        """;

        List<PersonajeServidorDTO> listaPjs = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sqlPjs)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String pj = rs.getString("personaje");
                    String serv = rs.getString("servidor");
                    listaPjs.add(new PersonajeServidorDTO(pj, serv));
                }
            }
        }

        return new UsuarioConPersonajesDTO(nombreUsuario, listaPjs);
    }

    /**
     * 4) El número de personajes de cada usuario en cada servidor,
     *    devolviendo el nombre de usuario, número de personajes y nombre del servidor.
     */
    public List<UsuarioServidorPersonajesDTO> numeroPersonajesPorUsuarioServidor() throws SQLException {
        List<UsuarioServidorPersonajesDTO> resultado = new ArrayList<>();

        String sql = """
            SELECT u.nombre AS usuario, s.nombre AS servidor, COUNT(p.id) AS total
            FROM Usuario u
            JOIN Personaje p ON u.id = p.usuario_id
            JOIN Servidor s ON p.servidor_id = s.id
            GROUP BY u.id, s.id
            ORDER BY u.nombre, s.nombre
        """;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String usuario = rs.getString("usuario");
                String servidor = rs.getString("servidor");
                int total = rs.getInt("total");
                resultado.add(new UsuarioServidorPersonajesDTO(usuario, servidor, total));
            }
        }

        return resultado;
    }

    /**
     * 5) Los X servidores con más personajes ordenados de mayor a menor,
     *    devolviendo el nombre y el número. X es el parámetro que determina
     *    el número a delimitar.
     */
    public List<ServidorPersonajesDTO> servidoresConMasPersonajes(int x) throws SQLException {
        List<ServidorPersonajesDTO> lista = new ArrayList<>();

        String sql = """
            SELECT s.nombre AS servidor, COUNT(p.id) AS total
            FROM Servidor s
            LEFT JOIN Personaje p ON s.id = p.servidor_id
            GROUP BY s.id
            ORDER BY total DESC
            LIMIT ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, x);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String servidor = rs.getString("servidor");
                    int total = rs.getInt("total");
                    lista.add(new ServidorPersonajesDTO(servidor, total));
                }
            }
        }

        return lista;
    }

    /**
     * 6) El número de servidores de X región.
     *    Puedes devolver un int, o un pequeño DTO si prefieres.
     */
    public int numeroServidoresDeRegion(String region) throws SQLException {
        String sql = """
            SELECT COUNT(*) AS total
            FROM Servidor
            WHERE region = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, region);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }

        return 0; // si no hay coincidencias
    }

    /**
     * 7) El número de servidores de cada región.
     *    Devuelve una lista donde cada elemento indica la región y cuántos servidores tiene.
     */
    public List<RegionServidoresDTO> numeroServidoresPorRegion() throws SQLException {
        List<RegionServidoresDTO> lista = new ArrayList<>();

        String sql = """
            SELECT region, COUNT(*) AS total
            FROM Servidor
            GROUP BY region
            ORDER BY region
        """;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String region = rs.getString("region");
                int total = rs.getInt("total");
                lista.add(new RegionServidoresDTO(region, total));
            }
        }

        return lista;
    }

    /**
     * 8) Las zonas de un mapa con id X, devolviendo el nombre de la zona,
     *    el alto y el ancho.
     */
    public List<ZonaDTO> zonasDeMapa(int mapId) throws SQLException {
        List<ZonaDTO> zonas = new ArrayList<>();

        String sql = """
            SELECT nombre, ancho, alto
            FROM Zona
            WHERE mapa_id = ?
            ORDER BY nombre
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, mapId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nombreZona = rs.getString("nombre");
                    int ancho = rs.getInt("ancho");
                    int alto = rs.getInt("alto");
                    zonas.add(new ZonaDTO(nombreZona, ancho, alto));
                }
            }
        }

        return zonas;
    }

    /* ==================== DTOs INTERNOS (opcional separarlos en ficheros) ==================== */

    /**
     * DTO para (1) y (2): Usuario + total de personajes
     */
    public static class UsuarioPersonajesDTO {
        private String usuario;
        private int totalPersonajes;

        public UsuarioPersonajesDTO(String usuario, int totalPersonajes) {
            this.usuario = usuario;
            this.totalPersonajes = totalPersonajes;
        }

        public String getUsuario() {
            return usuario;
        }

        public int getTotalPersonajes() {
            return totalPersonajes;
        }
    }

    /**
     * DTO para (3): Usuario + lista de (Personaje, Servidor)
     */
    public static class UsuarioConPersonajesDTO {
        private String usuario;
        private List<PersonajeServidorDTO> personajes;

        public UsuarioConPersonajesDTO(String usuario, List<PersonajeServidorDTO> personajes) {
            this.usuario = usuario;
            this.personajes = personajes;
        }

        public String getUsuario() {
            return usuario;
        }

        public List<PersonajeServidorDTO> getPersonajes() {
            return personajes;
        }
    }

    /**
     * Sub-DTO para un personaje y el servidor donde está
     */
    public static class PersonajeServidorDTO {
        private String personaje;
        private String servidor;

        public PersonajeServidorDTO(String personaje, String servidor) {
            this.personaje = personaje;
            this.servidor = servidor;
        }

        public String getPersonaje() {
            return personaje;
        }

        public String getServidor() {
            return servidor;
        }
    }

    /**
     * DTO para (4): nombre de usuario, servidor, total de personajes
     */
    public static class UsuarioServidorPersonajesDTO {
        private String usuario;
        private String servidor;
        private int totalPersonajes;

        public UsuarioServidorPersonajesDTO(String usuario, String servidor, int totalPersonajes) {
            this.usuario = usuario;
            this.servidor = servidor;
            this.totalPersonajes = totalPersonajes;
        }

        public String getUsuario() {
            return usuario;
        }

        public String getServidor() {
            return servidor;
        }

        public int getTotalPersonajes() {
            return totalPersonajes;
        }
    }

    /**
     * DTO para (5): servidor + total de personajes
     */
    public static class ServidorPersonajesDTO {
        private String servidor;
        private int totalPersonajes;

        public ServidorPersonajesDTO(String servidor, int totalPersonajes) {
            this.servidor = servidor;
            this.totalPersonajes = totalPersonajes;
        }

        public String getServidor() {
            return servidor;
        }

        public int getTotalPersonajes() {
            return totalPersonajes;
        }
    }

    /**
     * DTO para (7): región + número de servidores
     */
    public static class RegionServidoresDTO {
        private String region;
        private int totalServidores;

        public RegionServidoresDTO(String region, int totalServidores) {
            this.region = region;
            this.totalServidores = totalServidores;
        }

        public String getRegion() {
            return region;
        }

        public int getTotalServidores() {
            return totalServidores;
        }
    }

    /**
     * DTO para (8): nombre de la zona, alto y ancho
     */
    public static class ZonaDTO {
        private String nombre;
        private int ancho;
        private int alto;

        public ZonaDTO(String nombre, int ancho, int alto) {
            this.nombre = nombre;
            this.ancho = ancho;
            this.alto = alto;
        }

        public String getNombre() {
            return nombre;
        }

        public int getAncho() {
            return ancho;
        }

        public int getAlto() {
            return alto;
        }
    }
}
