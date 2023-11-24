import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SimulationWindow extends JFrame {
    public static final int CELL_SIZE = 10;
    public static final int SIMULATION_DELAY = 40;
    private static final int WIDTH=800;
    private static final int HEIGHT=800;
    private List<Person> people;
    private double simulationTime;
    private final JLabel timerLabel;

    public SimulationWindow( int variant, int initialPeople, Graphics graphics) {
        setTitle("Simulation");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Główny panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Panel dla symulacji
        JPanel simulationPanel = new JPanel();
        simulationPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        mainPanel.add(simulationPanel, BorderLayout.CENTER);

        // Panel na górze dla timera
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timerLabel = new JLabel("Time: 0s");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(timerLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Panel na dole dla przycisków
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Zapisz Symulację");
        saveButton.addActionListener(e -> saveSimulationState());
        //add(saveButton);
        JButton openButton = new JButton("Otwórz Symulację z Pliku");
        openButton.addActionListener(e -> openSimulationFromFile());
        //add(openButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(openButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        initializeSimulation(variant, initialPeople, graphics);

        Timer timer = new Timer(SIMULATION_DELAY, e -> {
            updateSimulation(variant, initialPeople, graphics);
            repaint();
        });
        timer.start();

        setLayout(new FlowLayout(FlowLayout.CENTER));
    }

    private void saveSimulationState() {
        Memento memento = new Memento(new ArrayList<>(people), simulationTime);
        String fileName = "simulation_moment.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(memento);
            System.out.println("Simulation state saved to file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openSimulationFromFile() {
        String fileName = "simulation_moment.ser";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Memento memento = (Memento) ois.readObject();
            restoreSimulationState(memento);
            System.out.println("Simulation state loaded from file: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void restoreSimulationState(Memento memento) {
        people.clear();
        people.addAll(memento.getPeople());
        simulationTime = memento.getSimulationTime();
        updateTimerLabel();
        repaint();
    }

    private void initializeSimulation( int variant, int initialPeople, Graphics graphics) {
        people = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < initialPeople; i++) {
            double x = random.nextDouble(WIDTH/CELL_SIZE);
            double y = random.nextDouble(HEIGHT/CELL_SIZE);
            people.add(new Person(new Vector2D(x * CELL_SIZE , y * CELL_SIZE), i));
        }
    }

    private void updateSimulation(int variant, int populationSize, Graphics graphics) {
        Random random = new Random();
        List<Person> peopleToRemove = new ArrayList<>();

        if (random.nextDouble() < 0.05) {
            double x = random.nextDouble(WIDTH/ CELL_SIZE);
            double y = random.nextDouble(HEIGHT / CELL_SIZE);
            people.add(new Person(new Vector2D(x * CELL_SIZE, y * CELL_SIZE), populationSize));
            populationSize++;
        }

        for (Person person : people) {
            // Losowa zmiana prędkości i kierunku z 10% szansą
            if (random.nextDouble() < 0.1) {
                Vector2D newVelocity = person.generateRandomVelocity();
                person.setVelocity(newVelocity);
            }

            double dx = person.getVelocity().getComponents()[0];
            double dy = person.getVelocity().getComponents()[1];

            if (isAtEdge(person, WIDTH, HEIGHT)) {
                if (random.nextDouble() < 0.5) {
                    // 50% przypadków - zmiana kierunku do środka
                    Vector2D centerVector = new Vector2D(WIDTH / 2 - person.getPosition().getComponents()[0],
                            HEIGHT / 2 - person.getPosition().getComponents()[1]);
                    //centerVector.normalize();
                    person.setVelocity(centerVector);
                } else {
                    // 50% przypadków - usunięcie osobnika
                    peopleToRemove.add(person);
                    continue;
                }
            }
            person.move(new Vector2D(dx * CELL_SIZE, dy * CELL_SIZE), getWidth(), getHeight());

            for (Person other : people) {
                if (person.canInfect(other, 2 * CELL_SIZE)) {
                    person.infect(other, graphics);
                    person.resetProximityTime(other);
                }
            }

            person.updateInfectionTime(graphics);
        }
        people.removeAll(peopleToRemove);
        simulationTime += SIMULATION_DELAY / 1000.0;
        updateTimerLabel();
    }
    private boolean isAtEdge(Person person, int maxWidth, int maxHeight) {
        double x = person.getPosition().getComponents()[0];
        double y = person.getPosition().getComponents()[1];
        return x <= 0 || x >= maxWidth - CELL_SIZE || y <= 0 || y >= maxHeight - CELL_SIZE;
    }

    private void updateTimerLabel() {
        DecimalFormat df = new DecimalFormat("#.##");
        timerLabel.setText("Time: " + df.format(simulationTime) + "s");
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (Person person : people) {
            drawPerson(g, person);
        }
    }

    private void drawPerson(Graphics g, Person person) {
        g.setColor(person.getHealthState().getFillColor());
        int x = (int) person.getPosition().getComponents()[0];
        int y = (int) person.getPosition().getComponents()[1];
        g.fillOval(x, y, CELL_SIZE, CELL_SIZE);


        if (person.getHealthState() instanceof InfectedHasSymptomsState ||
                person.getHealthState() instanceof InfectedNoSymptomsState) {
            double circleRadius = 2.0 * CELL_SIZE;
            int circleX = x + CELL_SIZE / 2 - (int) circleRadius;
            int circleY = y + CELL_SIZE / 2 - (int) circleRadius;
            int diameter = (int) (circleRadius * 2);
            g.drawOval(circleX, circleY, diameter, diameter);
        }
    }
}
