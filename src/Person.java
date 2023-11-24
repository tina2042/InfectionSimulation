import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class Person implements Serializable {
    private final int id;
    private Vector2D position;
    private Vector2D velocity;
    private HealthState healthState;
    private final Map<Integer, Double> timeInProximity;
    private static final int minTimeToInfect = 3;
    private static final double ONESECOND = 1000.0;
    private static final double MAX_SPEED = 2.5/SimulationWindow.CELL_SIZE;

    public Person(Vector2D position, int id, int variant) {
        Random random = new Random();
        this.id=id;
        this.position = position;
        this.velocity = new Vector2D(random.nextDouble(MAX_SPEED), random.nextDouble(MAX_SPEED));
        timeInProximity = new HashMap<>();

        if (isInfected()) {
            if(isImmuneOrHasSymptoms())
                this.healthState=new InfectedHasSymptomsState();
            else
                this.healthState= new InfectedNoSymptomsState();
        } else {
            if(variant==1) {
                this.healthState = new HealthyNotImmuneState();
            }else {
                if (isImmuneOrHasSymptoms())
                    this.healthState = new HealthyImmuneState();
                else
                    this.healthState = new HealthyNotImmuneState();
            }
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

    public Vector2D generateRandomVelocityAndDirection() {
        Random random = new Random();
        double speed = random.nextDouble() * (MAX_SPEED);  // Losowa prędkość w zakresie od 0 do 2.5 m/s

        double vx = speed * (random.nextBoolean() ? 1 : -1);
        double vy = speed * (random.nextBoolean() ? 1 : -1);
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
        double xDistance = this.position.getComponents()[0] - other.position.getComponents()[0];
        double yDistance = this.position.getComponents()[1] - other.position.getComponents()[1];

        // Uwzględnij rozmiar komórki (CELL_SIZE)
        xDistance -= SimulationWindow.CELL_SIZE / 2.0;
        yDistance -= SimulationWindow.CELL_SIZE / 2.0;

        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
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
                    this.updateProximityTime(SimulationWindow.SIMULATION_DELAY / ONESECOND, other);
                    // Sprawdź, czy czas w odpowiedniej odległości jest większy niż 3 sekundy
                    /*System.out.println("Person " + this.getId()+ " at (" + this.getPosition().getComponents()[0] + ", " + this.getPosition().getComponents()[1] +
                            ") is in proximity with person "+ other.getId()+ "at ("+ other.getPosition().getComponents()[0] + ", " + other.getPosition().getComponents()[1] +" for "+
                            this.getTimeInProximity(other) + " seconds");
                    */
                    return this.getTimeInProximity(other) >= minTimeToInfect;
                } else {
                    //jeżeli osobnik nie znajduje sie w odpowiednije odleglosci to wyzeruj jego czas w otoczeniu
                    this.resetProximityTime(other);
                }
            }
        }
        return false;
    }

    public void infect(Person other) {
        Random random = new Random();
        if (this.healthState instanceof InfectedHasSymptomsState || random.nextDouble() < 0.5) {
            if (!(other.getHealthState() instanceof HealthyImmuneState) ) {
                if(isImmuneOrHasSymptoms()) {
                    other.healthState = new InfectedHasSymptomsState();
                    /*System.out.println("Person at (" + other.getPosition().getComponents()[0] + ", " + other.getPosition().getComponents()[1] +
                            ") got infected with symptoms");*/
                } else {
                    other.healthState = new InfectedNoSymptomsState();
                    /*System.out.println("Person at (" + other.getPosition().getComponents()[0] + ", " + other.getPosition().getComponents()[1] +
                            ") got infected with symptoms");*/
                }
            }
        }
    }


    public void updateInfectionTime() {
        healthState.updateTimeSinceInfection();
        if (healthState.getTimeSinceInfection() >= 20 && healthState.getTimeSinceInfection() <= 30) {
            Random random = new Random();
            int randomRecoveryTime = 20 + random.nextInt(11);  // Losowy czas w zakresie od 20 do 30 sekund
            if (healthState.getTimeSinceInfection() >= randomRecoveryTime) {

                //System.out.println("Person at (" + this.getPosition().getComponents()[0] + ", " + this.getPosition().getComponents()[1] +
                //        ") recovered from infection in "+ healthState.getTimeSinceInfection() + " second");
                healthState = new HealthyImmuneState();
            }
        }
    }

    public void updateProximityTime(double deltaTimeInSeconds, Person other) {
        double newTime;
        if (timeInProximity.containsKey(other.getId())) {
            newTime = timeInProximity.get(other.getId()) + deltaTimeInSeconds;
        } else {
            newTime=deltaTimeInSeconds;
        }
        timeInProximity.put(other.getId(), newTime);
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
