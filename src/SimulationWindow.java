import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SimulationWindow extends JFrame {
    private static final int CELL_SIZE = 10;
    public static final int SIMULATION_DELAY = 40;
    private List<Person> people;
    private double simulationTime;
    private final JLabel timerLabel;
    private final List<Memento> savedStates = new ArrayList<>();
    public SimulationWindow(int height, int width, int variant, int initialPeople) {
        setTitle("Simulation");
        setSize(width , height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initializeSimulation(height, width, variant, initialPeople);

        Timer timer = new Timer(SIMULATION_DELAY, e -> {
            updateSimulation();
            repaint();
        });
        timer.start();
        // Utwórz etykietę na czas symulacji
        timerLabel = new JLabel("Time: 0s");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        timerLabel.setBounds(10, 10, 100, 20);
        add(timerLabel);

        // Dodaj przycisk do zapisywania symulacji
        JButton saveButton = new JButton("Zapisz Symulację");
        saveButton.addActionListener(e -> saveSimulationState());
        add(saveButton);

        // Dodaj przycisk do otwierania symulacji z pliku
        JButton openButton = new JButton("Otwórz Symulację z Pliku");
        openButton.addActionListener(e -> openSimulationFromFile());
        add(openButton);
        setLayout(new FlowLayout(FlowLayout.CENTER));
    }
    private void saveSimulationState() {
        Memento memento = new Memento(new ArrayList<>(people), simulationTime);
        savedStates.add(memento);
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
    private void initializeSimulation(int height, int width, int variant, int initialPeople) {
        people = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < initialPeople; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            people.add(new Person(x * CELL_SIZE, y * CELL_SIZE, variant));
        }
    }

    private void updateSimulation() {
        Random random = new Random();
        List<Person> peopleToRemove = new ArrayList<>();

        if (random.nextDouble() < 0.1) {
            int x = random.nextInt(getWidth() / CELL_SIZE);
            int y = random.nextInt(getHeight() / CELL_SIZE);
            int variant = 1;
            people.add(new Person(x * CELL_SIZE, y * CELL_SIZE, variant));
            //System.out.println("New person added at (" + x + ", " + y + ")");

        }

        for (Person person : people) {
            int dx = random.nextInt(3) - 1;
            int dy = random.nextInt(3) - 1;

            if (isAtEdge(person, getWidth(), getHeight())) {
                if (random.nextDouble() < 0.5) {
                    dx = -dx;
                    dy = -dy;
                    //System.out.println("Person at (" + person.getX() + ", " + person.getY() +
                    //       ") changed direction towards the center");

                } else {
                    // Dodaj osobnika do listy do usunięcia
                    peopleToRemove.add(person);
                    //System.out.println("Person at (" + person.getX() + ", " + person.getY() +
                    //      ") died at the edge");

                    continue;
                }
            }

            person.move(dx * CELL_SIZE, dy * CELL_SIZE, getWidth(), getHeight());

            for (Person other : people) {
                if (person.canInfect(other, 2 * CELL_SIZE)) {
                    person.infect(other);
                    person.resetProximityTime();
                    //System.out.println("Person at (" + person.getX() + ", " + person.getY() +
                    //        ") infected Person at (" + other.getX() + ", " + other.getY() + ")");

                }
            }

            person.updateInfectionTime();
        }

        // Usuń osobników z listy
        people.removeAll(peopleToRemove);

        simulationTime += SIMULATION_DELAY / 1000.0;
        updateTimerLabel();
    }
    private boolean isAtEdge(Person person, int maxWidth, int maxHeight) {
        int x = person.getX();
        int y = person.getY();
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
        if (person.getHealthState() instanceof HealthyState) {
            g.setColor(Color.GREEN);
        } else {
            g.setColor(Color.RED);
        }
        g.fillRect(person.getX(), person.getY(), CELL_SIZE, CELL_SIZE);
    }


}
