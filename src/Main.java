import java.sql.SQLException;
import java.util.List;

import dto.*;
public class Main {
    public static void main(String[] args) {
        Conexion conexion = Conexion.getInstancia();
        if (conexion.getConexion() != null) {
            Creadorbd.crearTablas(conexion.getConexion());
            Generadorbd.generarDatos(conexion.getConexion());

            AnalisisDAO dao = new AnalisisDAO(conexion.getConexion());
            try {
                // rankServers
                List<ServidorRankingDTO> topServ = dao.rankServers();
                System.out.println("=== Top 5 Servidores ===");
                topServ.forEach(System.out::println);

                // getUserPjs 
                UserPjsDTO userAimar = dao.getUserPjs("AIMAR");
                if (userAimar != null) {
                    System.out.println("\n=== Personajes de AIMAR ===");
                    System.out.println(userAimar);
                } else {
                    System.out.println("\nNo se encontró el usuario 'AIMAR' o no tiene personajes.");
                }

                // userP3
                List<UsuarioPersonajesDTO> usuariosMas3 = dao.userP3();
                System.out.println("\n=== Usuarios con más de 3 personajes ===");
                usuariosMas3.forEach(System.out::println);

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                conexion.cerrarConexion();
            }
        } else {
            System.err.println("No se pudo establecer la conexión a la base de datos.");
        }
    }
}
