import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private String nombre;
    private List<Carta> mano = new ArrayList<>();
    private int fichas = 1000;  // Fichas iniciales
    private boolean retirado = false;
    private int apuestaRonda;
    // Constructor
    public Jugador(String nombre) {
        this.nombre = nombre;
    }

    // --- Getters ---
    public String getNombre() {
        return nombre;
    }

    public List<Carta> getMano() {
        return mano;
    }

    public int getFichas() {
        return fichas;
    }

    public boolean estaRetirado() {
        return retirado;
    }

    public int getApuestaRonda() {
        return apuestaRonda;
    }


    // --- Setters ---
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFichas(int fichas) {
        this.fichas = fichas;
    }

    public void setRetirado(boolean retirado) {
        this.retirado = retirado;
    }

    // --- Métodos de juego ---
    public void recibirCarta(Carta carta) {
        mano.add(carta);
    }

    public void descartarCarta(int indice) {
        if (indice >= 0 && indice < mano.size()) {
            mano.remove(indice);
        }
    }

    public void limpiarMano() {
        mano.clear();
    }

    public void apostar(int cantidad) {
        if (cantidad <= fichas) {
            fichas -= cantidad;
        }
    }

    public void setApuestaRonda(int apuestaRonda) {
        this.apuestaRonda = apuestaRonda;
    }

    @Override
    public String toString() {
        return nombre + " (Fichas: " + fichas + ")";
    }

    public void igualarApuesta(int apuesta) {
        int diferencia = apuesta - this.apuestaRonda;
        if (diferencia > 0 && diferencia <= fichas) {
            apostar(diferencia);
            apuestaRonda = apuesta;
        }
    }

    public void retirarse() {
        retirado = true;
    }
}


