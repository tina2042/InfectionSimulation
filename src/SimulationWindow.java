import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SimulationWindow extends JFrame {
    public static final int CELL_SIZE = 20;
    public static final int SIMULATION_DELAY = 40;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int INFECTIONRADIUS = 2;
    private static final double ONESECOND = 1000.0;
    private List<Person> people;
    private double simulationTime;
    private final JLabel timerLabel;
    private final JButton pauseButton;
    private boolean isPaused;
    private int populationSize;

    public SimulationWindow(int variant, int initialPeople) {
        setTitle("Simulation");
        setSize(WIDTH, HEIGHT + 100);  // Dodatkowa przestrzeń na przyciski
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Główny panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Panel dla symulacji
        JPanel simulationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
                for (Person person : people) {
                    drawPerson(g, person);
                }
            }
        };
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
        bottomPanel.add(saveButton);

        JButton openButton = new JButton("Otwórz Symulację z Pliku");
        openButton.addActionListener(e -> openSimulationFromFile());
        bottomPanel.add(openButton);

        // Dodaj przycisk pauzy
        pauseButton = new JButton("Pauza");
        pauseButton.addActionListener(e -> togglePause());
        bottomPanel.add(pauseButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        isPaused = false;
        // Inicjalizacja symulacji
        initializeSimulation(variant, initialPeople);

        Timer timer = new Timer(SIMULATION_DELAY, e -> {
            updateSimulation(variant);
            simulationPanel.repaint();
            updateTimerLabel();
        });
        timer.start();
    }
    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseButton.setText("Resume");
        } else {
            pauseButton.setText("Pauza");
        }
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
            System.out.println("First save simulation to restore it.");
        }
    }

    private void restoreSimulationState(Memento memento) {
        people.clear();
        people.addAll(memento.getPeople());
        simulationTime = memento.getSimulationTime();
        updateTimerLabel();
        repaint();
    }

    private void initializeSimulation(int variant, int initialPeople) {
        this.populationSize=initialPeople;
        people = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < initialPeople; i++) {
            double x = random.nextDouble(WIDTH*1.0 / CELL_SIZE);
            double y = random.nextDouble(HEIGHT*1.0 / CELL_SIZE);
            people.add(new Person(new Vector2D(x * CELL_SIZE, y * CELL_SIZE), i, variant));
        }
    }

    private void updateSimulation(int variant) {
        if (!isPaused) {
            Random random = new Random();
            List<Person> peopleToRemove = new ArrayList<>();

            if (random.nextDouble() < 0.05) {
                int x = random.nextInt(WIDTH / CELL_SIZE);
                int y = random.nextInt(HEIGHT / CELL_SIZE);
                people.add(new Person(new Vector2D(x * CELL_SIZE, y * CELL_SIZE), this.populationSize, variant));
                this.populationSize++;
            }

            for (Person person : people) {
                // Losowa zmiana prędkości i kierunku z 10% szansą
                if (random.nextDouble() < 0.1) {
                    Vector2D newVelocity = person.generateRandomVelocityAndDirection();
                    person.setVelocity(newVelocity);
                }

                double dx = person.getVelocity().getComponents()[0];
                double dy = person.getVelocity().getComponents()[1];

                if (isAtEdge(person)) {
                    if (random.nextDouble() < 0.5) {
                        person.setVelocity(new Vector2D(-dx, -dy));
                    } else {
                        peopleToRemove.add(person);
                        continue;
                    }
                }

                person.move(new Vector2D(dx * CELL_SIZE, dy * CELL_SIZE), getWidth(), getHeight());

                for (Person other : people) {
                    if (person.canInfect(other, INFECTIONRADIUS * CELL_SIZE)) {
                        person.infect(other);
                        person.resetProximityTime(other);
                    }
                }

                person.updateInfectionTime();
            }
            people.removeAll(peopleToRemove);
            simulationTime += SIMULATION_DELAY / ONESECOND;
            updateTimerLabel();
        }
    }

    private boolean isAtEdge(Person person) {
        double x = person.getPosition().getComponents()[0];
        double y = person.getPosition().getComponents()[1];
        return x <= 0 || x >= WIDTH - CELL_SIZE || y <= 0 || y >= HEIGHT - CELL_SIZE;
    }

    private void updateTimerLabel() {
        DecimalFormat df = new DecimalFormat("#.##");
        timerLabel.setText("Time: " + df.format(simulationTime) + "s");
    }

    private void drawPerson(Graphics g, Person person) {
        g.setColor(person.getHealthState().getFillColor());
        int x = (int) person.getPosition().getComponents()[0];
        int y = (int) person.getPosition().getComponents()[1];
        g.fillOval(x, y, CELL_SIZE, CELL_SIZE);


        if (person.getHealthState() instanceof InfectedHasSymptomsState ||
                person.getHealthState() instanceof InfectedNoSymptomsState) {
            int circleRadius = INFECTIONRADIUS * CELL_SIZE;
            int circleX = x + CELL_SIZE / INFECTIONRADIUS -  circleRadius;
            int circleY = y + CELL_SIZE / INFECTIONRADIUS -  circleRadius;
            int diameter =  (circleRadius * 2);
            g.drawOval(circleX, circleY, diameter, diameter);
        }
    }
}
