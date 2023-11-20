import javax.swing.*;
import java.awt.*;



public class SimulationGUI extends JFrame {
    private final JTextField heightField;
    private final JTextField widthField;
    private final JComboBox<String> variantComboBox;
    private final JTextField initialPeopleField;

    public SimulationGUI() {
        setTitle("Simulation Configuration");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new FlowLayout());

        // Dodaj pola tekstowe i etykiety dla wysokości, szerokości, wariantu i liczby początkowych osobników
        add(new JLabel("Height: "));
        heightField = new JTextField(5);
        add(heightField);

        add(new JLabel("Width: "));
        widthField = new JTextField(5);
        add(widthField);

        add(new JLabel("Variant (1 or 2): "));
        String[] variants = {"1", "2"};
        variantComboBox = new JComboBox<>(variants);
        add(variantComboBox);

        add(new JLabel("Initial People: "));
        initialPeopleField = new JTextField(5);
        add(initialPeopleField);

        // Dodaj przycisk do rozpoczęcia symulacji
        JButton startButton = new JButton("Start Simulation");
        startButton.addActionListener(e -> startSimulation());
        add(startButton);
    }

    private void startSimulation() {

        int height = Integer.parseInt(heightField.getText());
        int width = Integer.parseInt(widthField.getText());
        int variant = Integer.parseInt((String) variantComboBox.getSelectedItem());
        int initialPeople = Integer.parseInt(initialPeopleField.getText());

        SwingUtilities.invokeLater(() -> {
            SimulationWindow simulationWindow = new SimulationWindow(height, width, variant, initialPeople);
            simulationWindow.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulationGUI().setVisible(true));
    }
}