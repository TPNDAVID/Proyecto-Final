import java.util.List;

public abstract class JuegoPoker {
    protected List<Jugador> jugadores;
    protected Baraja baraja;
    protected int pozo;

    public abstract void iniciarJuego();
    public abstract void rondaDeApuestas();
    public abstract void determinarGanador();

    public void agregarJugador(Jugador jugador) {
        jugadores.add(jugador);
    }

    protected void barajarBaraja() {
        baraja.barajar();
    }
}
