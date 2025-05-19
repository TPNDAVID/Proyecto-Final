public class Carta {
    private final String palo;   // Ej: "Corazones", "Picas"
    private final String valor;  // Ej: "As", "Rey", "10"

    public Carta(String palo, String valor) {
        this.palo = palo;
        this.valor = valor;
    }

    // --- Getters ---
    public String getPalo() {
        return palo;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor + " de " + palo;  // Ej: "As de Corazones"
    }
}
