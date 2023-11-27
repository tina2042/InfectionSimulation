import javax.swing.*;
import java.awt.*;



public class SimulationGUI extends JFrame {
    private static final int DEFAULTINITIALPEOPLE = 30;
    private final JComboBox<String> variantComboBox;
    private final JTextField initialPeopleField;

    public SimulationGUI() {
        setTitle("Simulation Configuration");
        setSize(300, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new FlowLayout());

        add( new JLabel("Variant 1: Initial population don't have immunity. \n"));
        add( new JLabel("Variant 2: Initial people might have immunity. \n"));
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

        int variant = Integer.parseInt((String) variantComboBox.getSelectedItem());
        int initialPeople;
        try{
            initialPeople= Integer.parseInt(initialPeopleField.getText());
        }
        catch( Exception e) {
            initialPeople = DEFAULTINITIALPEOPLE;
        }

        int finalInitialPeople = initialPeople;
        SwingUtilities.invokeLater(() -> {
            SimulationWindow simulationWindow = new SimulationWindow(variant, finalInitialPeople);
            simulationWindow.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulationGUI().setVisible(true));
    }
}