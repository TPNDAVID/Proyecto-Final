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

    private static final Map<String, Integer> VALOR_NUMERICO = Map.ofEntries(
            Map.entry("2", 2), Map.entry("3", 3), Map.entry("4", 4),
            Map.entry("5", 5), Map.entry("6", 6), Map.entry("7", 7),
            Map.entry("8", 8), Map.entry("9", 9), Map.entry("10", 10),
            Map.entry("Jota", 11), Map.entry("Reina", 12), Map.entry("Rey", 13),
            Map.entry("As", 14)
    );

    // Versión optimizada para Texas Hold'em (7 cartas)
    public static int evaluarMejorMano(List<Carta> manoJugador, List<Carta> cartasComunitarias) {
        List<Carta> todasLasCartas = new ArrayList<>();
        todasLasCartas.addAll(manoJugador);
        todasLasCartas.addAll(cartasComunitarias);

        // Generar todas las combinaciones posibles de 5 cartas de las 7 disponibles
        List<List<Carta>> combinaciones = generarCombinaciones(todasLasCartas, 5);

        return combinaciones.stream()
                .mapToInt(EvaluadorMano::evaluar)
                .max()
                .orElse(CARTA_ALTA);
    }

    // Evalúa una mano específica de 5 cartas
    public static int evaluar(List<Carta> mano) {
        if (mano.size() != 5) throw new IllegalArgumentException("Se requieren exactamente 5 cartas");

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

    // --- Métodos de validación optimizados ---
    private static boolean esEscaleraReal(List<Carta> mano) {
        Set<String> valoresReales = Set.of("10", "Jota", "Reina", "Rey", "As");
        String palo = mano.get(0).getPalo();

        return mano.stream().allMatch(c -> c.getPalo().equals(palo)) &&
                mano.stream().map(Carta::getValor).collect(Collectors.toSet()).equals(valoresReales);
    }

    private static boolean esEscaleraColor(List<Carta> mano) {
        return esColor(mano) && esEscalera(mano);
    }

    private static boolean esPoquer(List<Carta> mano) {
        return contarValores(mano).values().stream().anyMatch(count -> count == 4);
    }

    private static boolean esFullHouse(List<Carta> mano) {
        Map<String, Long> conteo = contarValores(mano);
        return conteo.containsValue(3L) && conteo.containsValue(2L);
    }

    private static boolean esColor(List<Carta> mano) {
        return mano.stream().map(Carta::getPalo).distinct().count() == 1;
    }

    private static boolean esEscalera(List<Carta> mano) {
        List<Integer> valores = convertirValoresANumeros(mano);
        if (valores.size() < 5) return false;

        Set<Integer> valoresUnicos = new TreeSet<>(valores); // Ordena y elimina duplicados

        // Caso especial: Escalera baja (A-2-3-4-5)
        if (valoresUnicos.containsAll(Set.of(14, 2, 3, 4, 5))) {
            return true;
        }

        // Convertir a lista ordenada
        List<Integer> listaOrdenada = new ArrayList<>(valoresUnicos);

        // Verificar escaleras normales (5 cartas consecutivas)
        for (int i = 0; i <= listaOrdenada.size() - 5; i++) {
            int primero = listaOrdenada.get(i);
            int ultimo = listaOrdenada.get(i + 4);
            if (ultimo - primero == 4) {
                return true;
            }
        }

        return false;
    }

    private static boolean esTercia(List<Carta> mano) {
        return contarValores(mano).values().stream().anyMatch(count -> count == 3);
    }

    private static boolean esDoblePar(List<Carta> mano) {
        return contarValores(mano).values().stream().filter(count -> count == 2).count() == 2;
    }

    private static boolean esPar(List<Carta> mano) {
        return contarValores(mano).values().stream().anyMatch(count -> count == 2);
    }

    // --- Métodos auxiliares ---
    private static Map<String, Long> contarValores(List<Carta> mano) {
        return mano.stream().collect(Collectors.groupingBy(Carta::getValor, Collectors.counting()));
    }

    public static List<Integer> convertirValoresANumeros(List<Carta> mano) {
        return mano.stream()
                .map(carta -> VALOR_NUMERICO.get(carta.getValor()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Genera todas las combinaciones posibles de k cartas
    private static List<List<Carta>> generarCombinaciones(List<Carta> cartas, int k) {
        List<List<Carta>> result = new ArrayList<>();
        combinacionesHelper(cartas, k, 0, new ArrayList<>(), result);
        return result;
    }

    private static void combinacionesHelper(List<Carta> cartas, int k, int start,
                                            List<Carta> current, List<List<Carta>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < cartas.size(); i++) {
            current.add(cartas.get(i));
            combinacionesHelper(cartas, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
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
    public static int evaluarMejorMano(List<Carta> cartas) {
        if (cartas.size() < 5) {
            throw new IllegalArgumentException("Se necesitan al menos 5 cartas para evaluar");
        }

        // Si tenemos exactamente 5 cartas, evaluar directamente
        if (cartas.size() == 5) {
            return evaluar(cartas);
        }

        // Generar todas las combinaciones posibles de 5 cartas
        List<List<Carta>> combinaciones = generarCombinaciones(cartas, 5);

        // Encontrar la mejor puntuación entre todas las combinaciones
        return combinaciones.stream()
                .mapToInt(EvaluadorMano::evaluar)
                .max()
                .orElse(CARTA_ALTA);
    }
    public static List<Carta> obtenerMejorCombinacion(List<Carta> cartas) {
        if (cartas.size() < 5) throw new IllegalArgumentException("Se necesitan al menos 5 cartas");

        List<List<Carta>> combinaciones = generarCombinaciones(cartas, 5);
        List<Carta> mejorCombinacion = combinaciones.get(0);
        int maxPuntuacion = evaluar(mejorCombinacion);

        for (List<Carta> combo : combinaciones) {
            int puntuacion = evaluar(combo);
            if (puntuacion > maxPuntuacion ||
                    (puntuacion == maxPuntuacion && compararCombinaciones(combo, mejorCombinacion) > 0)) {
                mejorCombinacion = combo;
                maxPuntuacion = puntuacion;
            }
        }
        return mejorCombinacion;
    }

    private static int compararCombinaciones(List<Carta> combo1, List<Carta> combo2) {
        // Usar convertirValoresANumeros() y ordenar de mayor a menor
        List<Integer> valores1 = convertirValoresANumeros(combo1).stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        List<Integer> valores2 = convertirValoresANumeros(combo2).stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // Comparar carta por carta
        for (int i = 0; i < Math.min(valores1.size(), valores2.size()); i++) {
            if (!valores1.get(i).equals(valores2.get(i))) {
                return valores1.get(i) - valores2.get(i);
            }
        }
        return 0;
    }


}