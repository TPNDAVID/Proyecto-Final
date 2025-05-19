import java.util.*;
import java.util.stream.Collectors;

public class EvaluadorMano {
    // Rankings de manos (de mayor a menor valor)
    public static final int ESCALERA_REAL = 10;
    public static final int ESCALERA_COLOR = 9;
    public static final int POQUER = 8;
    public static final int FULL_HOUSE = 7;
    public static final int COLOR = 6;
    public static final int ESCALERA = 5;
    public static final int TERCIO = 4;
    public static final int DOBLE_PAR = 3;
    public static final int PAR = 2;
    public static final int CARTA_ALTA = 1;

    public static int evaluar(List<Carta> mano) {
        if (esEscaleraReal(mano)) return ESCALERA_REAL;
        if (esEscaleraColor(mano)) return ESCALERA_COLOR;
        if (esPoquer(mano)) return POQUER;
        if (esFullHouse(mano)) return FULL_HOUSE;
        if (esColor(mano)) return COLOR;
        if (esEscalera(mano)) return ESCALERA;
        if (esTercia(mano)) return TERCIO;
        if (esDoblePar(mano)) return DOBLE_PAR;
        if (esPar(mano)) return PAR;
        return CARTA_ALTA;
    }

    // --- Métodos de validación ---
    private static boolean esEscaleraReal(List<Carta> mano) {
        Set<String> valoresReales = Set.of("As", "Rey", "Reina", "Jota", "10");
        String palo = mano.get(0).getPalo();

        return mano.stream()
                .allMatch(carta -> carta.getPalo().equals(palo)) &&
                mano.stream()
                        .map(Carta::getValor)
                        .collect(Collectors.toSet())
                        .equals(valoresReales);
    }

    private static boolean esEscaleraColor(List<Carta> mano) {
        return esColor(mano) && esEscalera(mano);
    }

    private static boolean esPoquer(List<Carta> mano) {
        Map<String, Long> conteo = contarValores(mano);
        return conteo.containsValue(4L);
    }

    private static boolean esFullHouse(List<Carta> mano) {
        Map<String, Long> conteo = contarValores(mano);
        return conteo.containsValue(3L) && conteo.containsValue(2L);
    }

    private static boolean esColor(List<Carta> mano) {
        return mano.stream()
                .map(Carta::getPalo)
                .distinct()
                .count() == 1;
    }

    private static boolean esEscalera(List<Carta> mano) {
        List<Integer> valores = convertirValoresANumeros(mano);
        if (valores.size() != 5) return false;

        Collections.sort(valores);

        // Escalera normal (2-3-4-5-6)
        boolean normal = valores.get(4) - valores.get(0) == 4;

        // Escalera baja (A-2-3-4-5)
        boolean baja = valores.equals(List.of(2, 3, 4, 5, 14));

        return normal || baja;
    }

    private static boolean esTercia(List<Carta> mano) {
        return contarValores(mano).containsValue(3L);
    }

    private static boolean esDoblePar(List<Carta> mano) {
        return contarValores(mano).values().stream()
                .filter(count -> count == 2L)
                .count() == 2;
    }

    private static boolean esPar(List<Carta> mano) {
        return contarValores(mano).containsValue(2L);
    }

    // --- Métodos auxiliares ---
    private static Map<String, Long> contarValores(List<Carta> mano) {
        return mano.stream()
                .collect(Collectors.groupingBy(
                        Carta::getValor,
                        Collectors.counting()
                ));
    }

    static List<Integer> convertirValoresANumeros(List<Carta> mano) {
        Map<String, Integer> valorANumero = Map.ofEntries(
                Map.entry("2", 2), Map.entry("3", 3), Map.entry("4", 4),
                Map.entry("5", 5), Map.entry("6", 6), Map.entry("7", 7),
                Map.entry("8", 8), Map.entry("9", 9), Map.entry("10", 10),
                Map.entry("Jota", 11), Map.entry("Reina", 12), Map.entry("Rey", 13),
                Map.entry("As", 14)
        );

        return mano.stream()
                .map(carta -> valorANumero.getOrDefault(carta.getValor(), 0))
                .filter(num -> num > 0)
                .collect(Collectors.toList());
    }

    public static String nombreMano(int puntaje) {
        switch (puntaje) {
            case ESCALERA_REAL: return "Escalera Real";
            case ESCALERA_COLOR: return "Escalera de Color";
            case POQUER: return "Póquer";
            case FULL_HOUSE: return "Full House";
            case COLOR: return "Color";
            case ESCALERA: return "Escalera";
            case TERCIO: return "Tercia";
            case DOBLE_PAR: return "Doble Par";
            case PAR: return "Par";
            default: return "Carta Alta";
        }
    }
}