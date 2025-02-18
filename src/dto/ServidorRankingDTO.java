package dto;

public class ServidorRankingDTO {
    private final String nombreServidor;
    private final int totalPersonajes;

    public ServidorRankingDTO(String nombreServidor, int totalPersonajes) {
        this.nombreServidor = nombreServidor;
        this.totalPersonajes = totalPersonajes;
    }

    public String getNombreServidor() {
        return nombreServidor;
    }

    public int getTotalPersonajes() {
        return totalPersonajes;
    }

    @Override
    public String toString() {
        return "Servidor: " + nombreServidor + " | Personajes: " + totalPersonajes;
    }
}
