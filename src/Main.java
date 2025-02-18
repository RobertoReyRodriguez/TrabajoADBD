public class Main {
    public static void main(String[] args) {
        Conexion conexion = Conexion.getInstancia();

        if (conexion.getConexion() != null) {
            System.out.println("Conexión establecida con éxito.");
            
            Creadorbd.crearTablas(conexion.getConexion());
            
            Generadorbd.generarDatos(conexion.getConexion());
        } else {
            System.err.println("No se pudo establecer la conexión a la base de datos.");
        }

        conexion.cerrarConexion();
    }
}

