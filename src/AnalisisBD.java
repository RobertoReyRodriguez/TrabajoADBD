import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnalisisBD {

    private Connection connection;

    public AnalisisBD(Connection connection) {
        this.connection = connection;
    }

    /**
     * rankServers():
     * Muestra por pantalla los 5 servidores con más personajes
     * con el formato "El servidor X con Y personajes".
     */
    public void rankServers() throws SQLException {
        String sql = """
            SELECT s.nombre AS servidor, COUNT(p.id) AS total
            FROM Servidor s
            LEFT JOIN Personaje p ON s.id = p.servidor_id
            GROUP BY s.id
            ORDER BY total DESC
            LIMIT 5
        """;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n=== TOP 5 SERVIDORES CON MÁS PERSONAJES ===");
            while (rs.next()) {
                String servidor = rs.getString("servidor");
                int total = rs.getInt("total");
                // Formato: "El servidor X con Y personajes"
                System.out.println("El servidor " + servidor + " con " + total + " personajes");
            }
        }
    }

    /**
     * listServers():
     * Muestra el nombre de los servidores por región de la forma:
     * 
     * Región X
     *   Servidor1
     *   Servidor2
     * 
     * Región Y
     *   Servidor3
     */
    public void listServers() throws SQLException {
        String sql = """
            SELECT region, nombre
            FROM Servidor
            ORDER BY region, nombre
        """;

        // Usamos un LinkedHashMap para agrupar servidores por región y mantener el orden
        Map<String, List<String>> regionServidores = new LinkedHashMap<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String region = rs.getString("region");
                String servidor = rs.getString("nombre");

                regionServidores.putIfAbsent(region, new ArrayList<>());
                regionServidores.get(region).add(servidor);
            }
        }

        System.out.println("\n=== LISTADO DE SERVIDORES POR REGIÓN ===");
        for (String region : regionServidores.keySet()) {
            System.out.println("Región " + region);
            for (String servidor : regionServidores.get(region)) {
                System.out.println("  " + servidor);
            }
            System.out.println(); // línea en blanco entre regiones
        }
    }

    /**
     * getUserPJ(id):
     * Muestra por pantalla:
     * 
     *   X (Y personajes)
     *   Servidor1
     *     pj1
     *     pj2
     *   Servidor2
     *     pj3
     *     pj4
     * 
     * Donde X es el nombre del usuario, Y es el total de personajes,
     * y se agrupan los personajes por servidor.
     */
    public void getUserPJ(int userId) throws SQLException {
        // 1) Obtener nombre del usuario y total de personajes
        String sqlUsuario = """
            SELECT u.nombre AS usuario, COUNT(p.id) AS total
            FROM Usuario u
            LEFT JOIN Personaje p ON u.id = p.usuario_id
            WHERE u.id = ?
        """;

        String nombreUsuario = null;
        int totalPersonajes = 0;

        try (PreparedStatement ps = connection.prepareStatement(sqlUsuario)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombreUsuario = rs.getString("usuario");
                    totalPersonajes = rs.getInt("total");
                }
            }
        }

        if (nombreUsuario == null) {
            System.out.println("\nNo se encontró el usuario con id " + userId);
            return;
        }

        // 2) Obtener personajes agrupados por servidor
        String sqlServPjs = """
            SELECT s.nombre AS servidor, p.nombre AS personaje
            FROM Personaje p
            JOIN Servidor s ON p.servidor_id = s.id
            WHERE p.usuario_id = ?
            ORDER BY s.nombre, p.nombre
        """;

        // servidor -> lista de personajes
        Map<String, List<String>> servidorPersonajes = new LinkedHashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(sqlServPjs)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String servidor = rs.getString("servidor");
                    String pj = rs.getString("personaje");
                    servidorPersonajes.putIfAbsent(servidor, new ArrayList<>());
                    servidorPersonajes.get(servidor).add(pj);
                }
            }
        }

        // 3) Imprimir en el formato:
        //   X (Y personajes)
        //   Servidor1
        //     pj1
        //     pj2
        //   Servidor2
        //     pj3
        //     pj4

        System.out.println("\n" + nombreUsuario + " (" + totalPersonajes + " personajes)");
        for (String serv : servidorPersonajes.keySet()) {
            System.out.println(serv);
            for (String personaje : servidorPersonajes.get(serv)) {
                System.out.println("  " + personaje);
            }
        }
    }

    /**
     * userPJs():
     * Muestra por pantalla todos los usuarios y el número de personajes que tienen,
     * mostrando 5 por línea con el número entre paréntesis.
     * Ejemplo:
     *   usuario1(3)  usuario2(5)  usuario3(2)  usuario4(0)  usuario5(7)
     *   usuario6(1)  ...
     */
    public void userPJs() throws SQLException {
        String sql = """
            SELECT u.nombre AS usuario, COUNT(p.id) AS total
            FROM Usuario u
            LEFT JOIN Personaje p ON u.id = p.usuario_id
            GROUP BY u.id
            ORDER BY u.nombre
        """;

        List<String> lista = new ArrayList<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String usuario = rs.getString("usuario");
                int total = rs.getInt("total");
                // Formato: usuarioX(Y)
                lista.add(usuario + "(" + total + ")");
            }
        }

        System.out.println("\n=== LISTADO DE USUARIOS Y NÚMERO DE PERSONAJES ===");
        // Mostrar 5 por línea
        int count = 0;
        for (String info : lista) {
            System.out.print(info + "  ");
            count++;
            if (count % 5 == 0) {
                System.out.println(); // salto de línea cada 5
            }
        }
        System.out.println(); // salto de línea final
    }

    /**
     * areaMap(id):
     * Muestra el área de un mapa con un id en concreto (área = ancho x alto).
     * El enunciado anterior habla de "las zonas de un mapa con id X, devolviendo
     * el nombre de la zona, el alto y el ancho".
     * 
     * Aquí imprimimos cada zona y su área.
     */
    public void areaMap(int mapId) throws SQLException {
        String sql = """
            SELECT m.nombre AS mapa, z.nombre AS zona,
                   z.ancho, z.alto
            FROM Mapa m
            JOIN Zona z ON z.mapa_id = m.id
            WHERE m.id = ?
            ORDER BY z.nombre
        """;

        String nombreMapa = null;
        List<String> zonasInfo = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, mapId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (nombreMapa == null) {
                        nombreMapa = rs.getString("mapa");
                    }
                    String zona = rs.getString("zona");
                    int ancho = rs.getInt("ancho");
                    int alto = rs.getInt("alto");
                    int area = ancho * alto;
                    zonasInfo.add(zona + " => " + ancho + "x" + alto + " = " + area);
                }
            }
        }

        if (nombreMapa == null) {
            System.out.println("\nNo se encontró el mapa con id " + mapId);
            return;
        }

        System.out.println("\n=== ZONAS DEL MAPA: " + nombreMapa + " (id=" + mapId + ") ===");
        for (String info : zonasInfo) {
            System.out.println(info);
        }
    }
}
