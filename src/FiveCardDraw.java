import java.util.*;
import java.util.stream.Collectors;

public class FiveCardDraw extends JuegoPoker {
    private int maxDescarte = 3;
    private int apuestaActual = 0;
    private Scanner scanner = new Scanner(System.in);

    public FiveCardDraw() {
        this.baraja = new Baraja();  // ✅ Inicializa la baraja
        this.jugadores = new ArrayList<>();  // ✅ Inicializa la lista
    }

    public void agregarJugador(Jugador jugador) {
        if (jugadores.size() < 7) {
            jugadores.add(jugador);
            System.out.println("Jugador " + jugador.getNombre() + " añadido.");
        } else {
            System.out.println("¡Máximo 7 jugadores permitidos!");
        }
    }

    @Override
    public void iniciarJuego() {
        System.out.println("\n=== ¡COMIENZA EL JUEGO ===");
        barajarBaraja();  // Ahora this.baraja NO es null
        repartirCartasIniciales();
        mostrarManos();
        rondaDeApuestas();
        rondaDescarte();
        rondaDeApuestas();
        determinarGanador();
    }

    @Override
    public void rondaDeApuestas() {
        System.out.println("\n--- Ronda de Apuestas ---");
        for (Jugador jugador : jugadores) {
            if (!jugador.estaRetirado()) {
                System.out.println("\nTurno de: " + jugador.getNombre());
                System.out.println("Tu mano: " + jugador.getMano());
                System.out.println("Fichas disponibles: " + jugador.getFichas());
                System.out.println("Apuesta actual: " + apuestaActual);
                System.out.println("Opciones: [1] Igualar, [2] Subir, [3] Retirarse");

                int opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        jugador.apostar(apuestaActual);
                        System.out.println(jugador.getNombre() + " iguala la apuesta.");
                        break;
                    case 2:
                        System.out.print("¿Cuánto quieres subir? ");
                        int subida = scanner.nextInt();
                        apuestaActual += subida;
                        jugador.apostar(apuestaActual);
                        System.out.println(jugador.getNombre() + " sube la apuesta a " + apuestaActual);
                        break;
                    case 3:
                        jugador.setRetirado(true);
                        System.out.println(jugador.getNombre() + " se retira.");
                        break;
                    default:
                        System.out.println("Opción inválida. Se pasa el turno.");
                }
            }
        }
    }

    public void rondaDescarte() {
        System.out.println("\n--- Ronda de Descarte ---");
        for (Jugador jugador : jugadores) {
            if (!jugador.estaRetirado()) {
                System.out.println("\nTurno de: " + jugador.getNombre());
                System.out.println("Tu mano actual: " + jugador.getMano());
                System.out.print("¿Cuántas cartas quieres descartar? (0-" + maxDescarte + "): ");
                int numDescarte = scanner.nextInt();

                if (numDescarte > 0) {
                    System.out.print("Ingresa los índices de las cartas a descartar (ej: 1 3 5): ");
                    scanner.nextLine(); // Limpiar buffer
                    String input = scanner.nextLine();
                    String[] indicesStr = input.split(" ");
                    List<Integer> indices = new ArrayList<>();

                    for (String s : indicesStr) {
                        int idx = Integer.parseInt(s) - 1; // Convertir a índice base 0
                        if (idx >= 0 && idx < jugador.getMano().size()) {
                            indices.add(idx);
                        }
                    }

                    // Ordenar índices de mayor a menor para evitar problemas al eliminar
                    indices.sort(Collections.reverseOrder());
                    for (int idx : indices) {
                        jugador.descartarCarta(idx);
                        jugador.recibirCarta(baraja.sacarCarta());
                    }
                }
            }
        }
    }

    public List<Jugador> desempatar(List<Jugador> jugadores) {
        if (jugadores.size() == 1) return jugadores;

        // 1. Convertir manos a listas de valores numéricos ordenados (descendente)
        Map<Jugador, List<Integer>> manosNumericas = new HashMap<>();
        for (Jugador jugador : jugadores) {
            List<Integer> valores = EvaluadorMano.convertirValoresANumeros(jugador.getMano());
            valores.sort(Collections.reverseOrder()); // Ordenar de mayor a menor
            manosNumericas.put(jugador, valores);
        }

        // 2. Comparar cartas en orden (primera, segunda, etc.)
        for (int i = 0; i < 5; i++) { // 5 cartas por mano
            final int posicion = i;
            jugadores.sort((j1, j2) -> {
                List<Integer> valores1 = manosNumericas.get(j1);
                List<Integer> valores2 = manosNumericas.get(j2);
                return Integer.compare(valores2.get(posicion), valores1.get(posicion)); // Descendente
            });

            // 3. Filtrar jugadores con la carta más alta en esta posición
            List<Jugador> mejores = new ArrayList<>();
            mejores.add(jugadores.get(0));

            for (int j = 1; j < jugadores.size(); j++) {
                List<Integer> valoresActuales = manosNumericas.get(jugadores.get(j));
                if (valoresActuales.get(posicion).equals(manosNumericas.get(mejores.get(0)).get(posicion))) {
                    mejores.add(jugadores.get(j));
                } else {
                    break;
                }
            }

            // 4. Si queda un solo ganador, retornarlo
            if (mejores.size() == 1) {
                return mejores;
            }

            // 5. Si no, continuar con la siguiente carta
            jugadores = mejores;
        }

        return jugadores; // Empate total (todas las cartas iguales)
    }

    @Override
    public void determinarGanador() {
        System.out.println("\n--- Resultado Final ---");
        List<Jugador> jugadoresActivos = jugadores.stream()
                .filter(j -> !j.estaRetirado())
                .collect(Collectors.toList());

        // 1. Encontrar el mejor puntaje
        int mejorPuntaje = jugadoresActivos.stream()
                .mapToInt(j -> EvaluadorMano.evaluar(j.getMano()))
                .max()
                .orElse(-1);

        // 2. Filtrar jugadores con ese puntaje
        List<Jugador> empatados = jugadoresActivos.stream()
                .filter(j -> EvaluadorMano.evaluar(j.getMano()) == mejorPuntaje)
                .collect(Collectors.toList());

        // 3. Desempatar
        List<Jugador> ganadores = desempatar(empatados);

        // 4. Mostrar resultado
        if (ganadores.size() > 1) {
            System.out.println("\n¡EMPATE! Ganadores:");
            ganadores.forEach(j -> System.out.printf(
                    "- %s con %s (%s)\n",
                    j.getNombre(),
                    j.getMano(),
                    EvaluadorMano.nombreMano(mejorPuntaje)
            ));
            System.out.printf("El pozo se divide entre %d jugadores.\n", ganadores.size());
        } else {
            System.out.printf("\n¡GANADOR: %s con %s (%s)!\n",
                    ganadores.get(0).getNombre(),
                    ganadores.get(0).getMano(),
                    EvaluadorMano.nombreMano(mejorPuntaje));
        }
    }

    private void repartirCartasIniciales() {
        for (Jugador jugador : jugadores) {
            jugador.limpiarMano();
            for (int i = 0; i < 5; i++) {
                jugador.recibirCarta(baraja.sacarCarta());
            }
        }
    }

    private void mostrarManos() {
        System.out.println("\n--- Manos Iniciales ---");
        for (Jugador jugador : jugadores) {
            System.out.println(jugador.getNombre() + ": " + jugador.getMano());
        }
    }
}