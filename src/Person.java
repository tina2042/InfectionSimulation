import java.awt.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class Person implements Serializable {
    private int id;
    private Vector2D position;
    private Vector2D velocity;
    private HealthState healthState;
    private Map<Integer, Double> timeInProximity;


    public Person(Vector2D position, int id) {
        Random random = new Random();
        this.id=id;
        this.position = position;
        this.velocity = new Vector2D(random.nextDouble(2.5), random.nextDouble(2.5));  // Inicjalizacja losową prędkością
        timeInProximity = new HashMap<>();

        if (isInfected()) {
            if(isImmuneOrHasSymptoms())
                this.healthState=new InfectedHasSymptomsState();
            else
                this.healthState= new InfectedNoSymptomsState();
        } else {
            if(isImmuneOrHasSymptoms())
                this.healthState=new HealthyImmuneState();
            else
                this.healthState= new HealthyNotImmuneState();
        }

    }

    public Vector2D getPosition() {
        return position;
    }

    public HealthState getHealthState() {
        return healthState;
    }
    public Vector2D getVelocity() {
        return velocity;
    }
    public int getId(){
        return id;
    }

    public Vector2D generateRandomVelocity() {
        Random random = new Random();
        double speed = 2.5;  // Prędkość 2.5 m/s

        // Skalowanie prędkości do rozmiaru CELL_SIZE
        speed /= SimulationWindow.CELL_SIZE;

        // Losowe ustawienie kierunku
        double angle = Math.toRadians(random.nextDouble() * 360);
        double vx = speed * Math.cos(angle);
        double vy = speed * Math.sin(angle);

        return new Vector2D(vx, vy);
    }

    private boolean isInfected() {
        Random random = new Random();
        int randomValue = random.nextInt(100);
        return randomValue < 10;
    }

    private boolean isImmuneOrHasSymptoms() {
        Random random = new Random();
        int randomValue = random.nextInt(100);
        return randomValue < 50;
    }

    public double distanceTo(Person other) {
        return Math.sqrt(Math.pow(this.position.getComponents()[0] - other.position.getComponents()[0], 2)
                + Math.pow(this.position.getComponents()[1] - other.position.getComponents()[1], 2));
    }

    public void move(Vector2D delta, int maxX, int maxY) {
        double[] deltaComponents = delta.getComponents();
        double newX = (position.getComponents()[0] + deltaComponents[0] + maxX) % maxX;
        double newY = (position.getComponents()[1] + deltaComponents[1] + maxY) % maxY;
        position = new Vector2D(newX, newY);
    }

    public boolean canInfect(Person other, int maxDistance) {
        // Sprawdź, czy obiekt nie infekuje samego siebie
        if (this != other) {
            // Sprawdź, czy  osobnik jest zakażone
            if (this.getHealthState() instanceof InfectedHasSymptomsState ||
            this.getHealthState() instanceof InfectedNoSymptomsState) {
                // Sprawdź, czy odległość między osobnikami jest mniejsza lub równa maksymalnej odległości
                if (this.distanceTo(other) <= maxDistance) {
                    // Jeśli tak, zaktualizuj czas w odpowiedniej odległości
                    this.updateProximityTime(SimulationWindow.SIMULATION_DELAY / 1000.0, other);
                    // Sprawdź, czy czas w odpowiedniej odległości jest większy niż 3 sekundy
                    return this.getTimeInProximity(other) >= 3;
                }
            }
        }
        return false;
    }

    public void infect(Person other, Graphics graphics) {
        Random random = new Random();
        if (this.healthState instanceof InfectedHasSymptomsState || random.nextDouble() < 0.5) {
            if (!(other.getHealthState() instanceof HealthyImmuneState) ) {
                if(isImmuneOrHasSymptoms()) {
                    other.healthState = new InfectedHasSymptomsState();
                    //System.out.println("Person at (" + other.getX() + ", " + other.getY() +
                    //        ") got infected with symptoms"
                } else {
                    other.healthState = new InfectedNoSymptomsState();
                    //System.out.println("Person at (" + other.getX() + ", " + other.getY() +
                    //        ") got infected with no symptoms"
                }
            }
        }
    }


    public void updateInfectionTime(Graphics graphics) {

        healthState.updateTimeSinceInfection();
        // Dodano warunek zmiany na HealthyState po 20-30 sekundach
        if (healthState.getTimeSinceInfection() >= 20 && healthState.getTimeSinceInfection() <= 30) {
            healthState = new HealthyImmuneState();
            //System.out.println("Person at (" + getX() + ", " + getY() +
            //        ") recovered from infection and is now healthy");
        }
    }
    public void updateProximityTime(double deltaTimeInSeconds, Person other) {
        timeInProximity.put(other.getId(), deltaTimeInSeconds);
    }

    public void resetProximityTime(Person other) {
        timeInProximity.put(other.getId(), 0.0);
    }

    public double getTimeInProximity(Person other) {
        return timeInProximity.get(other.getId());
    }

    public void setVelocity(Vector2D newVelocity) {
        this.velocity=newVelocity;
    }
}
