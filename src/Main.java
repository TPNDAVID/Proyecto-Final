import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FiveCardDraw juego = new FiveCardDraw();

        System.out.println("¡Bienvenido al 5 Card Draw!");
        System.out.print("¿Cuántos jugadores? (2-7): ");
        int numJugadores = Integer.parseInt(scanner.nextLine()); // Usar nextLine() para evitar conflictos

        for (int i = 0; i < numJugadores; i++) {
            System.out.print("Nombre del jugador " + (i + 1) + ": ");
            String nombre = scanner.nextLine(); // Permite nombres con espacios
            juego.agregarJugador(new Jugador(nombre));
        }

        juego.iniciarJuego();
        scanner.close();
    }
}
