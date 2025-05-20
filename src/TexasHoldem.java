import java.util.*;
import java.util.stream.Collectors;

public class TexasHoldem extends JuegoPoker {
    private List<Carta> cartasComunitarias;
    private int ciegaPequena = 10;
    private int ciegaGrande = 20;
    private int dealerIndex = 0;
    private int apuestaActual = 0;
    private int jugadorActualIndex = 0;
    private Scanner scanner = new Scanner(System.in);

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

        Jugador smallBlind = jugadores.get(smallBlindIndex);
        Jugador bigBlind = jugadores.get(bigBlindIndex);

        smallBlind.apostar(ciegaPequena);
        smallBlind.setApuestaRonda(ciegaPequena);
        bigBlind.apostar(ciegaGrande);
        bigBlind.setApuestaRonda(ciegaGrande);

        apuestaActual = ciegaGrande;
        pozo += (ciegaPequena + ciegaGrande);
        jugadorActualIndex = (bigBlindIndex + 1) % jugadores.size();
    }

    private void repartirCartasIniciales() {
        for (Jugador jugador : jugadores) {
            jugador.limpiarMano();
            jugador.setApuestaRonda(0);
            jugador.setRetirado(false);
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
        System.out.println("\n--- Ronda de Apuestas ---");

        for (Jugador jugador : jugadores) {
            if (jugador.getApuestaRonda() != ciegaPequena && jugador.getApuestaRonda() != ciegaGrande) {
                jugador.setApuestaRonda(0);
            }
        }

        int jugadoresActivos = (int) jugadores.stream().filter(j -> !j.estaRetirado() && j.getFichas() > 0).count();
        int jugadoresIgualaron = 0;
        int ultimaApuesta = apuestaActual;

        boolean primeraVuelta = true;
        Set<Jugador> jugadoresQueSubieron = new HashSet<>();

        while (true) {
            jugadoresIgualaron = 0;
            for (int i = 0; i < jugadores.size(); i++) {
                Jugador jugador = jugadores.get((jugadorActualIndex + i) % jugadores.size());
                if (jugador.estaRetirado() || jugador.getFichas() == 0) continue;

                System.out.println("\nTurno de: " + jugador.getNombre());
                System.out.println("Tus cartas: " + jugador.getMano());
                System.out.println("Cartas comunitarias: " + cartasComunitarias);
                System.out.println("Fichas disponibles: " + jugador.getFichas());
                System.out.println("Apuesta actual a igualar: " + apuestaActual);
                System.out.println("Tu apuesta esta ronda: " + jugador.getApuestaRonda());

                int apuestaNecesaria = apuestaActual - jugador.getApuestaRonda();
                boolean puedeCheck = (apuestaNecesaria == 0);

                System.out.println("Opciones: ");
                if (puedeCheck) {
                    System.out.println("[1] Check");
                } else {
                    System.out.println("[1] Call (" + apuestaNecesaria + ")");
                }
                System.out.println("[2] Raise");
                System.out.println("[3] Fold");

                int opcion = leerOpcionValida(1, 3);

                if ((opcion == 1 && puedeCheck)) {
                    System.out.println(jugador.getNombre() + " hace check.");
                } else if (opcion == 1 && !puedeCheck) {
                    if (apuestaNecesaria >= jugador.getFichas()) {
                        apuestaNecesaria = jugador.getFichas(); // All-in
                        System.out.println(jugador.getNombre() + " va all-in con " + apuestaNecesaria);
                    } else {
                        System.out.println(jugador.getNombre() + " iguala (" + apuestaNecesaria + ")");
                    }
                    jugador.apostar(apuestaNecesaria);
                    jugador.setApuestaRonda(jugador.getApuestaRonda() + apuestaNecesaria);
                    pozo += apuestaNecesaria;
                } else if (opcion == 2) {
                    System.out.print("¿Cuánto deseas subir? (mínimo " + (apuestaActual + ciegaGrande) + "): ");
                    int subida = leerCantidadValida(jugador.getFichas(), apuestaActual + ciegaGrande);
                    int totalApuesta = apuestaActual + subida;
                    int diferencia = totalApuesta - jugador.getApuestaRonda();
                    if (diferencia > jugador.getFichas()) diferencia = jugador.getFichas(); // All-in

                    jugador.apostar(diferencia);
                    jugador.setApuestaRonda(jugador.getApuestaRonda() + diferencia);
                    apuestaActual = jugador.getApuestaRonda();
                    pozo += diferencia;
                    jugadoresQueSubieron.add(jugador);
                    System.out.println(jugador.getNombre() + " sube a " + apuestaActual);
                } else if (opcion == 3) {
                    jugador.setRetirado(true);
                    System.out.println(jugador.getNombre() + " se retira.");
                }

                // Si solo queda 1 jugador, termina la ronda
                if (jugadores.stream().filter(j -> !j.estaRetirado() && j.getFichas() > 0).count() == 1) {
                    return;
                }
            }

            // Verifica si todos los jugadores activos han igualado la apuesta
            boolean todosIgualaron = true;
            for (Jugador jugador : jugadores) {
                if (!jugador.estaRetirado() && jugador.getFichas() > 0 && jugador.getApuestaRonda() != apuestaActual) {
                    todosIgualaron = false;
                    break;
                }
            }
            if (todosIgualaron) break;
        }

        // Reinicia las apuestas de la ronda para la siguiente ronda
        for (Jugador jugador : jugadores) {
            jugador.setApuestaRonda(0);
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

            // Mostrar mano y jugada aunque gane por retiro
            List<Carta> manoEvaluable = new ArrayList<>();
            manoEvaluable.addAll(ganador.getMano());
            manoEvaluable.addAll(cartasComunitarias);

            List<Carta> mejorCombo = EvaluadorMano.obtenerMejorCombinacion(manoEvaluable);
            int puntuacion = EvaluadorMano.evaluar(mejorCombo);
            String nombreMano = EvaluadorMano.nombreMano(puntuacion);

            String cartasGanadoras = mejorCombo.stream()
                    .map(Carta::toString)
                    .collect(Collectors.joining(", "));

            System.out.println(ganador.getNombre() + " gana automáticamente (" + pozo + " fichas)");
            System.out.println("Mejor jugada: " + nombreMano + " (" + cartasGanadoras + ")");
            System.out.println("Mano completa: " + ganador.getMano());
            System.out.println("Cartas comunitarias: " + cartasComunitarias);

            ganador.setFichas(ganador.getFichas() + pozo);
        } else {
            // ... (tu lógica avanzada de showdown como ya tienes)
            // (¡No cambies nada aquí!)
            Map<Jugador, Integer> puntuaciones = new HashMap<>();
            Map<Jugador, String> nombresManos = new HashMap<>();
            Map<Jugador, List<Carta>> mejoresCombinaciones = new HashMap<>();

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

            int maxPuntuacion = Collections.max(puntuaciones.values());
            List<Jugador> ganadores = puntuaciones.entrySet().stream()
                    .filter(entry -> entry.getValue() == maxPuntuacion)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            int botePorJugador = pozo / ganadores.size();

            for (Jugador ganador : ganadores) {
                String cartasGanadoras = mejoresCombinaciones.get(ganador).stream()
                        .map(Carta::toString)
                        .collect(Collectors.joining(", "));

                System.out.println(ganador.getNombre() + " gana con " + nombresManos.get(ganador) +
                        " (" + cartasGanadoras + ") (" + botePorJugador + " fichas)");

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

    private int leerOpcionValida(int min, int max) {
        int op;
        while (true) {
            try {
                System.out.print("Elige una opción [" + min + "-" + max + "]: ");
                op = Integer.parseInt(scanner.nextLine());
                if (op >= min && op <= max) break;
            } catch (Exception e) {}
            System.out.println("Opción inválida. Intenta de nuevo.");
        }
        return op;
    }

    private int leerCantidadValida(int maxFichas, int minimo) {
        int cantidad;
        while (true) {
            try {
                cantidad = Integer.parseInt(scanner.nextLine());
                if (cantidad >= minimo && cantidad <= maxFichas) break;
            } catch (Exception e) {}
            System.out.println("Cantidad inválida. Intenta de nuevo.");
        }
        return cantidad;
    }
}