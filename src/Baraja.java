import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baraja {
    private List<Carta> cartas = new ArrayList<>();
    private List<Carta> descartes = new ArrayList<>();

    public Baraja() {
        String[] palos = {"Corazones", "Diamantes", "Picas", "Tréboles"};
        String[] valores = {"As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        for (String palo : palos) {
            for (String valor : valores) {
                cartas.add(new Carta(palo, valor));
            }
        }
    }

    public void barajar() {
        Collections.shuffle(cartas);
    }

    public Carta sacarCarta() {
        if (cartas.isEmpty()) {
            reiniciarBaraja();
        }
        return cartas.remove(0);
    }

    private void reiniciarBaraja() {
        cartas.addAll(descartes);
        descartes.clear();
        barajar();
    }
}
