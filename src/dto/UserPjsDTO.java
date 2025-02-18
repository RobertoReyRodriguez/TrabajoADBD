package dto;

public class UserPjsDTO {
    private final String nombreUsuario;
    private final int totalPersonajes;
    private final String servidores; // puedes usar List<String> si prefieres

    public UserPjsDTO(String nombreUsuario, int totalPersonajes, String servidores) {
        this.nombreUsuario = nombreUsuario;
        this.totalPersonajes = totalPersonajes;
        this.servidores = servidores;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getTotalPersonajes() {
        return totalPersonajes;
    }

    public String getServidores() {
        return servidores;
    }

    @Override
    public String toString() {
        return nombreUsuario + " (" + totalPersonajes + " personajes). Servidores: " + servidores;
    }
}
