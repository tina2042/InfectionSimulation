
import java.util.Random;

class Person {
    private int x;
    private int y;
    private HealthState healthState;
    private double timeInProximity;

    public Person(int x, int y, int simulationVariant) {
        this.x = x;
        this.y = y;

        if (generateInfectionStatus())
            this.healthState = new InfectedState(generateSymptomsOrImmuneStatus());
        else if (simulationVariant == 1)
            this.healthState = new HealthyState(false);
        else
            this.healthState = new HealthyState(generateSymptomsOrImmuneStatus());
        this.timeInProximity = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public HealthState getHealthState() {
        return healthState;
    }

    private boolean generateInfectionStatus() {
        Random random = new Random();
        int randomValue = random.nextInt(100);
        return randomValue < 10;
    }

    private boolean generateSymptomsOrImmuneStatus() {
        Random random = new Random();
        int randomValue = random.nextInt(100);
        return randomValue < 50;
    }

    public double distanceTo(Person other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    public void move(int dx, int dy, int maxX, int maxY) {
        x = (x + dx + maxX) % maxX;
        y = (y + dy + maxY) % maxY;
    }

    public boolean canInfect(Person other, int maxDistance) {
        // Sprawdź, czy obiekt nie infekuje samego siebie
        if (this != other) {
            // Sprawdź, czy  osobnik jest zakażone
            if (this.getHealthState() instanceof InfectedState ) {
                // Sprawdź, czy odległość między osobnikami jest mniejsza lub równa maksymalnej odległości
                if (this.distanceTo(other) <= maxDistance) {
                    // Jeśli tak, zaktualizuj czas w odpowiedniej odległości
                    this.updateProximityTime(SimulationWindow.SIMULATION_DELAY / 1000.0);
                    // Sprawdź, czy czas w odpowiedniej odległości jest większy niż 3 sekundy
                    return this.getTimeInProximity() >= 3;
                }
            }
        }
        return false;
    }

    public void infect(Person other) {
        Random random = new Random();
        if (healthState.handleHealthState(this) ) {
            if (!other.getHealthState().hasImmunity()) {
                other.healthState = new InfectedState(generateSymptomsOrImmuneStatus());
                //System.out.println("Person at (" + other.getX() + ", " + other.getY() +
                //        ") got infected with symptoms: " + other.getHealthState());

            }
        } else if (random.nextDouble() < 0.5) {
            if (!other.getHealthState().hasImmunity()) {
                other.healthState = new InfectedState(generateSymptomsOrImmuneStatus());
                //System.out.println("Person at (" + other.getX() + ", " + other.getY() +
                //        ") got infected without symptoms");

            }
        }
    }


    public void updateInfectionTime() {
        if (healthState instanceof InfectedState infectedState) {
            infectedState.updateTimeSinceInfection();
            // Dodano warunek zmiany na HealthyState po 20-30 sekundach
            if (infectedState.getTimeSinceInfection() >= 20 && infectedState.getTimeSinceInfection() <= 30) {
                healthState = new HealthyState(true);
                //System.out.println("Person at (" + getX() + ", " + getY() +
                //        ") recovered from infection and is now healthy");

            }
        }
    }
    public void updateProximityTime(double deltaTimeInSeconds) {
        timeInProximity += deltaTimeInSeconds;
    }

    public void resetProximityTime() {
        timeInProximity = 0;
    }

    public double getTimeInProximity() {
        return timeInProximity;
    }
}
