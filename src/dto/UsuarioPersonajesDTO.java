package dto;

public class UsuarioPersonajesDTO {
    private final String nombreUsuario;
    private final int totalPersonajes;

    public UsuarioPersonajesDTO(String nombreUsuario, int totalPersonajes) {
        this.nombreUsuario = nombreUsuario;
        this.totalPersonajes = totalPersonajes;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getTotalPersonajes() {
        return totalPersonajes;
    }

    @Override
    public String toString() {
        return nombreUsuario + " => " + totalPersonajes + " personajes";
    }
}
