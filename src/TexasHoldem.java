import java.util.*;
import java.util.stream.Collectors;

public class TexasHoldem extends JuegoPoker {
    private List<Carta> cartasComunitarias;
    private int ciegaPequena = 10;
    private int ciegaGrande = 20;
    private int dealerIndex = 0;
    private int apuestaActual = 0;
    private int jugadorActualIndex = 0;

    public TexasHoldem() {
        this.jugadores = new ArrayList<>();
        this.baraja = new Baraja();
        this.pozo = 0;
        this.cartasComunitarias = new ArrayList<>();
    }

    @Override
    public void iniciarJuego() {
        barajarBaraja();
        repartirCiegas();
        repartirCartasIniciales();
        rondaDeApuestas(); // Preflop
    }

    private void repartirCiegas() {
        int smallBlindIndex = (dealerIndex + 1) % jugadores.size();
        int bigBlindIndex = (dealerIndex + 2) % jugadores.size();

        jugadores.get(smallBlindIndex).apostar(ciegaPequena);
        jugadores.get(bigBlindIndex).apostar(ciegaGrande);

        apuestaActual = ciegaGrande;
        pozo += (ciegaPequena + ciegaGrande);
    }

    private void repartirCartasIniciales() {
        for (Jugador jugador : jugadores) {
            jugador.recibirCarta(baraja.sacarCarta());
            jugador.recibirCarta(baraja.sacarCarta());
        }
    }

    public void repartirFlop() {
        baraja.sacarCarta(); // Quemar carta
        for (int i = 0; i < 3; i++) {
            cartasComunitarias.add(baraja.sacarCarta());
        }
    }

    public void repartirTurn() {
        baraja.sacarCarta(); // Quemar carta
        cartasComunitarias.add(baraja.sacarCarta());
    }

    public void repartirRiver() {
        baraja.sacarCarta(); // Quemar carta
        cartasComunitarias.add(baraja.sacarCarta());
    }

    @Override
    public void rondaDeApuestas() {
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugadorActual = jugadores.get(jugadorActualIndex);
            if (!jugadorActual.estaRetirado() && jugadorActual.getFichas() > 0) {
                int apuestaNecesaria = apuestaActual - jugadorActual.getApuestaRonda();
                if (apuestaNecesaria > 0) {
                    jugadorActual.apostar(apuestaNecesaria);
                    jugadorActual.setApuestaRonda(apuestaActual);
                    pozo += apuestaNecesaria;
                }
            }
            jugadorActualIndex = (jugadorActualIndex + 1) % jugadores.size();
        }
        apuestaActual = 0;
    }

    @Override
    public void determinarGanador() {
        List<Jugador> jugadoresActivos = jugadores.stream()
                .filter(j -> !j.estaRetirado())
                .collect(Collectors.toList());

        if (jugadoresActivos.size() == 1) {
            Jugador ganador = jugadoresActivos.get(0);
            ganador.setFichas(ganador.getFichas() + pozo);
            System.out.println(ganador.getNombre() + " gana automáticamente (" + pozo + " fichas)");
        } else {
            Map<Jugador, Integer> puntuaciones = new HashMap<>();
            Map<Jugador, String> nombresManos = new HashMap<>();
            Map<Jugador, List<Carta>> mejoresCombinaciones = new HashMap<>();

            // 1. Evaluar todas las manos
            for (Jugador jugador : jugadoresActivos) {
                List<Carta> manoEvaluable = new ArrayList<>();
                manoEvaluable.addAll(jugador.getMano());
                manoEvaluable.addAll(cartasComunitarias);

                List<Carta> mejorCombo = EvaluadorMano.obtenerMejorCombinacion(manoEvaluable);
                int puntuacion = EvaluadorMano.evaluar(mejorCombo);

                puntuaciones.put(jugador, puntuacion);
                nombresManos.put(jugador, EvaluadorMano.nombreMano(puntuacion));
                mejoresCombinaciones.put(jugador, mejorCombo);
            }

            // 2. Encontrar la máxima puntuación
            int maxPuntuacion = Collections.max(puntuaciones.values());

            // 3. Filtrar ganadores (puede haber empate)
            List<Jugador> ganadores = puntuaciones.entrySet().stream()
                    .filter(entry -> entry.getValue() == maxPuntuacion)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // 4. Calcular bote por jugador
            int botePorJugador = pozo / ganadores.size();

            // 5. Mostrar resultados
            for (Jugador ganador : ganadores) {
                String cartasGanadoras = mejoresCombinaciones.get(ganador).stream()
                        .map(Carta::toString)
                        .collect(Collectors.joining(", "));

                System.out.println(ganador.getNombre() + " gana con " + nombresManos.get(ganador) +
                        " (" + cartasGanadoras + ") (" + botePorJugador + " fichas)");

                // 6. Asignar fichas al ganador
                ganador.setFichas(ganador.getFichas() + botePorJugador);
            }
        }
        pozo = 0;
    }

    // --- Getters ---
    public List<Carta> getCartasComunitarias() {
        return cartasComunitarias;
    }

    public int getDealerIndex() {
        return dealerIndex;
    }
    public List<Jugador> getJugadores() {
        return this.jugadores;
    }

    public int getPozo() {
        return this.pozo;
    }
}