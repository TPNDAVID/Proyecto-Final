import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PokerGUI extends JFrame {
    private TexasHoldem juego;
    private JPanel panelCartasComunitarias;
    private JPanel panelJugador1, panelJugador2;
    private JLabel labelEstado;
    private JButton btnCheck, btnCall, btnRaise, btnFold;
    private JLabel labelTurno;
    private JTextField fieldRaise;
    private int turnoActual;

    public PokerGUI() {
        juego = new TexasHoldem();
        // Supón que ya agregaste los jugadores por consola antes o los agregas aquí:
        juego.getJugadores().add(new Jugador("Jugador 1", 1000));
        juego.getJugadores().add(new Jugador("Jugador 2", 1000));
        juego.iniciarJuego();

        setTitle("Texas Hold'em Poker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Estado y turno
        labelEstado = new JLabel("¡Bienvenido a Texas Hold'em!");
        add(labelEstado, BorderLayout.NORTH);

        // Panel de cartas comunitarias (centro)
        panelCartasComunitarias = new JPanel(new FlowLayout());
        panelCartasComunitarias.setBorder(BorderFactory.createTitledBorder("Cartas Comunitarias"));
        add(panelCartasComunitarias, BorderLayout.CENTER);

        // Paneles de jugadores (sur y norte)
        panelJugador1 = crearPanelJugador(juego.getJugadores().get(0));
        add(panelJugador1, BorderLayout.SOUTH);

        panelJugador2 = crearPanelJugador(juego.getJugadores().get(1));
        add(panelJugador2, BorderLayout.NORTH);

        // Panel de acciones (este)
        JPanel panelAcciones = new JPanel();
        btnCheck = new JButton("Check");
        btnCall = new JButton("Call");
        btnRaise = new JButton("Raise");
        btnFold = new JButton("Fold");
        fieldRaise = new JTextField("20", 5);

        panelAcciones.add(btnCheck);
        panelAcciones.add(btnCall);
        panelAcciones.add(btnRaise);
        panelAcciones.add(fieldRaise);
        panelAcciones.add(btnFold);

        add(panelAcciones, BorderLayout.EAST);

        // Turno actual
        labelTurno = new JLabel();
        add(labelTurno, BorderLayout.WEST);

        // Listeners de botones
        btnCheck.addActionListener(e -> accionJugador("check"));
        btnCall.addActionListener(e -> accionJugador("call"));
        btnRaise.addActionListener(e -> accionJugador("raise"));
        btnFold.addActionListener(e -> accionJugador("fold"));

        // Inicializa turno
        turnoActual = 0;
        actualizarVista();

        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Panel para cada jugador
    private JPanel crearPanelJugador(Jugador jugador) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder(jugador.getNombre() + " (" + jugador.getFichas() + " fichas)"));
        // Añadir dos cartas (por defecto volteadas)
        for (int i = 0; i < 2; i++) {
            JLabel carta = new JLabel(new ImageIcon(getClass().getResource("/cartas/reverso.png")));
            panel.add(carta);
        }
        return panel;
    }

    // Actualiza la vista gráfica para reflejar el estado del juego
    private void actualizarVista() {
        // Actualizar cartas comunitarias
        panelCartasComunitarias.removeAll();
        List<Carta> comunitarias = juego.getCartasComunitarias();
        for (Carta c : comunitarias) {
            panelCartasComunitarias.add(new JLabel(new ImageIcon(getClass().getResource("/cartas/" + c.getCodigo() + ".png"))));
        }
        for (int i = comunitarias.size(); i < 5; i++) {
            panelCartasComunitarias.add(new JLabel(new ImageIcon(getClass().getResource("/cartas/reverso.png"))));
        }

        // Actualizar paneles de jugadores
        actualizarPanelJugador(panelJugador1, juego.getJugadores().get(0), turnoActual == 0);
        actualizarPanelJugador(panelJugador2, juego.getJugadores().get(1), turnoActual == 1);

        // Actualizar estado y turno
        labelEstado.setText("Bote: " + juego.getPozo() + " fichas");
        labelTurno.setText("Turno de: " + juego.getJugadores().get(turnoActual).getNombre());

        // Refrescar GUI
        revalidate();
        repaint();
    }

    // Muestra cartas reales si es el turno del jugador, sino reverso
    private void actualizarPanelJugador(JPanel panel, Jugador jugador, boolean mostrarCartas) {
        panel.removeAll();
        panel.setBorder(BorderFactory.createTitledBorder(jugador.getNombre() +
                (jugador.estaRetirado() ? " (Retirado)" : "") +
                " (" + jugador.getFichas() + " fichas)"));
        List<Carta> mano = jugador.getMano();
        for (int i = 0; i < 2; i++) {
            String img = mostrarCartas && i < mano.size()
                    ? "/cartas/" + mano.get(i).getCodigo() + ".png"
                    : "/cartas/reverso.png";
            panel.add(new JLabel(new ImageIcon(getClass().getResource(img))));
        }
    }

    // Acción del jugador
    private void accionJugador(String accion) {
        Jugador jugador = juego.getJugadores().get(turnoActual);
        switch (accion) {
            case "check":
                // Lógica de check
                // (Ajusta esto a tu lógica de TexasHoldem)
                break;
            case "call":
                // Lógica de call
                break;
            case "raise":
                int cantidad;
                try {
                    cantidad = Integer.parseInt(fieldRaise.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Ingresa una cantidad válida.");
                    return;
                }
                // Lógica de raise
                break;
            case "fold":
                jugador.setRetirado(true);
                break;
        }
        // Cambia turno al siguiente jugador (simplificado)
        turnoActual = (turnoActual + 1) % juego.getJugadores().size();
        actualizarVista();

        // Aquí podrías comprobar si termina la ronda / repartir cartas / showdown, etc.
        // Ejemplo: si todos han actuado, avanzar a siguiente ronda o determinar ganador
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PokerGUI::new);
    }
}