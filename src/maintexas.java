import java.util.*;

public class maintexas {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Configuración inicial
        System.out.println("¡Bienvenido a Texas Hold'em Poker!");
        System.out.print("Ingrese número de jugadores (2-8): ");
        int numJugadores = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        List<String> nombres = new ArrayList<>();
        for (int i = 1; i <= numJugadores; i++) {
            System.out.print("Nombre del Jugador " + i + ": ");
            nombres.add(scanner.nextLine());
        }

        // Inicializar el juego
        TexasHoldem juego = new TexasHoldem();
        for (String nombre : nombres) {
            juego.agregarJugador(new Jugador(nombre));
        }

        // Iniciar partida
        boolean continuarJuego = true;
        while (continuarJuego) {
            jugarMano(juego, scanner);

            System.out.print("\n¿Otra mano? (s/n): ");
            continuarJuego = scanner.nextLine().equalsIgnoreCase("s");

            // Reset para nueva mano
            if (continuarJuego) {
                juego = new TexasHoldem(); // Crear nueva instancia
                for (String nombre : nombres) {
                    juego.agregarJugador(new Jugador(nombre));
                }
            }
        }

        System.out.println("\n¡Gracias por jugar!");
        scanner.close();
    }

    private static void jugarMano(TexasHoldem juego, Scanner scanner) {
        // Pre-flop
        juego.iniciarJuego();
        System.out.println("\n=== Pre-flop ===");
        mostrarEstadoJuego(juego);

        // Flop
        esperarEnter(scanner, "Presione Enter para ver el Flop...");
        juego.repartirFlop();
        juego.rondaDeApuestas();
        System.out.println("\n=== Flop ===");
        mostrarEstadoJuego(juego);

        // Turn
        esperarEnter(scanner, "Presione Enter para ver el Turn...");
        juego.repartirTurn();
        juego.rondaDeApuestas();
        System.out.println("\n=== Turn ===");
        mostrarEstadoJuego(juego);

        // River
        esperarEnter(scanner, "Presione Enter para ver el River...");
        juego.repartirRiver();
        juego.rondaDeApuestas();
        System.out.println("\n=== River ===");
        mostrarEstadoJuego(juego);

        // Showdown
        esperarEnter(scanner, "Presione Enter para ver el resultado...");
        juego.determinarGanador();
        System.out.println("\n=== Resultado Final ===");
        mostrarResultadosFinales(juego);
    }

    private static void mostrarEstadoJuego(TexasHoldem juego) {
        System.out.println("\nCartas Comunitarias:");
        for (Carta c : juego.getCartasComunitarias()) {
            System.out.print(c + " ");
        }

        System.out.println("\n\nEstado de los Jugadores:");
        for (Jugador jugador : juego.getJugadores()) {
            System.out.print("\n" + jugador.getNombre() + " (" + jugador.getFichas() + " fichas): ");
            if (jugador.estaRetirado()) {
                System.out.print("(Retirado)");
            } else {
                for (Carta c : jugador.getMano()) {
                    System.out.print(c + " ");
                }
            }
        }
        System.out.println("\n\nBote: " + juego.getPozo() + " fichas");
    }

    private static void mostrarResultadosFinales(TexasHoldem juego) {
        for (Jugador j : juego.getJugadores()) {
            System.out.println(j.getNombre() + ": " + j.getFichas() + " fichas");
        }
    }

    private static void esperarEnter(Scanner scanner, String mensaje) {
        System.out.print("\n" + mensaje);
        scanner.nextLine();
    }
}