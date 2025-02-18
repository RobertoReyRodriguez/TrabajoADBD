import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Obtener la conexión mediante el Singleton
        Conexion conexion = Conexion.getInstancia();
        if (conexion.getConexion() == null) {
            System.err.println("No se pudo establecer la conexión a la base de datos.");
            return;
        }

        // 2. Crear las tablas y generar datos de prueba
        Creadorbd.crearTablas(conexion.getConexion());
        Generadorbd.generarDatos(conexion.getConexion());

        // 3. Instanciar el DAO
        AnalisisDAO dao = new AnalisisDAO(conexion.getConexion());

        // 4. Menú interactivo
        Scanner sc = new Scanner(System.in);
        int opcion = -1;
        
        while (opcion != 0) {
            System.out.println("\n================ MENÚ PRINCIPAL ================");
            System.out.println("1. Número de personajes por usuario (>=1)");
            System.out.println("2. Número de personajes de un usuario X");
            System.out.println("3. Personajes de un usuario X (nombre, personaje y servidor)");
            System.out.println("4. Número de personajes de cada usuario en cada servidor");
            System.out.println("5. Los X servidores con más personajes (ordenados de mayor a menor)");
            System.out.println("6. Número de servidores de X región");
            System.out.println("7. Número de servidores por región");
            System.out.println("8. Las zonas de un mapa con id X (nombre, alto y ancho)");
            System.out.println("0. Salir");
            System.out.print("Elija una opción: ");
            
            opcion = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea

            try {
                switch(opcion) {
                    case 1:
                        // 1. Número de personajes por usuario (>=1)
                        List<AnalisisDAO.UsuarioPersonajesDTO> lista1 = dao.numeroPersonajesPorUsuario();
                        System.out.println("\n--- Número de personajes por usuario (>=1) ---");
                        for (AnalisisDAO.UsuarioPersonajesDTO dto : lista1) {
                            System.out.println(dto.getUsuario() + " tiene " + dto.getTotalPersonajes() + " personajes");
                        }
                        break;
                        
                    case 2:
                        // 2. Número de personajes de un usuario X
                        System.out.print("Ingrese el nombre del usuario: ");
                        String nombre = sc.nextLine();
                        AnalisisDAO.UsuarioPersonajesDTO dto2 = dao.numeroPersonajesDeUsuarioX(nombre);
                        if(dto2 != null)
                            System.out.println(nombre + " tiene " + dto2.getTotalPersonajes() + " personajes");
                        else
                            System.out.println("Usuario no encontrado o sin personajes.");
                        break;
                        
                    case 3:
                        // 3. Personajes de un usuario X
                        System.out.print("Ingrese el nombre del usuario: ");
                        String user = sc.nextLine();
                        AnalisisDAO.UsuarioConPersonajesDTO dto3 = dao.personajesDeUsuarioX(user);
                        if(dto3 != null) {
                            System.out.println("\nUsuario: " + dto3.getUsuario());
                            System.out.println("Personajes y servidores:");
                            for(AnalisisDAO.PersonajeServidorDTO ps : dto3.getPersonajes()) {
                                System.out.println(" - " + ps.getPersonaje() + " en " + ps.getServidor());
                            }
                        } else {
                            System.out.println("Usuario no encontrado o sin personajes.");
                        }
                        break;
                        
                    case 4:
                        // 4. Número de personajes de cada usuario en cada servidor
                        List<AnalisisDAO.UsuarioServidorPersonajesDTO> lista4 = dao.numeroPersonajesPorUsuarioServidor();
                        System.out.println("\n--- Número de personajes por usuario en cada servidor ---");
                        for(AnalisisDAO.UsuarioServidorPersonajesDTO dto : lista4) {
                            System.out.println(dto.getUsuario() + " tiene " + dto.getTotalPersonajes() +
                                               " personajes en el servidor " + dto.getServidor());
                        }
                        break;
                        
                    case 5:
                        // 5. Los X servidores con más personajes (ordenados de mayor a menor)
                        System.out.print("Ingrese el número X: ");
                        int x = sc.nextInt();
                        sc.nextLine();
                        List<AnalisisDAO.ServidorPersonajesDTO> lista5 = dao.servidoresConMasPersonajes(x);
                        System.out.println("\n--- Los " + x + " servidores con más personajes ---");
                        for(AnalisisDAO.ServidorPersonajesDTO dto : lista5) {
                            System.out.println("El servidor " + dto.getServidor() + " con " +
                                               dto.getTotalPersonajes() + " personajes");
                        }
                        break;
                        
                    case 6:
                        // 6. Número de servidores de X región
                        System.out.print("Ingrese la región: ");
                        String region = sc.nextLine();
                        int totalServ = dao.numeroServidoresDeRegion(region);
                        System.out.println("La región " + region + " tiene " + totalServ + " servidores");
                        break;
                        
                    case 7:
                        // 7. Número de servidores por región
                        List<AnalisisDAO.RegionServidoresDTO> lista7 = dao.numeroServidoresPorRegion();
                        System.out.println("\n--- Número de servidores por región ---");
                        for(AnalisisDAO.RegionServidoresDTO dto : lista7) {
                            System.out.println("Región " + dto.getRegion() + " tiene " + dto.getTotalServidores() + " servidores");
                        }
                        break;
                        
                    case 8:
                        // 8. Las zonas de un mapa con id X (nombre, alto y ancho)
                        System.out.print("Ingrese el ID del mapa: ");
                        int mapId = sc.nextInt();
                        sc.nextLine();
                        List<AnalisisDAO.ZonaDTO> lista8 = dao.zonasDeMapa(mapId);
                        if(lista8.isEmpty()) {
                            System.out.println("No se encontraron zonas para el mapa con id " + mapId);
                        } else {
                            System.out.println("\n--- Zonas del mapa con id " + mapId + " ---");
                            for(AnalisisDAO.ZonaDTO dto : lista8) {
                                System.out.println("Zona " + dto.getNombre() + ": Ancho = " + dto.getAncho() +
                                                   ", Alto = " + dto.getAlto());
                            }
                        }
                        break;
                        
                    case 0:
                        System.out.println("Saliendo...");
                        break;
                        
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } catch(SQLException ex) {
                System.err.println("Error en la consulta: " + ex.getMessage());
            }
        }
        
        sc.close();
        conexion.cerrarConexion();
    }
}
