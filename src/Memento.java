import java.io.Serializable;
import java.util.List;

class Memento implements Serializable {
    private final List<Person> people;
    private final double simulationTime;

    public Memento(List<Person> people, double simulationTime) {
        this.people = people;
        this.simulationTime = simulationTime;
    }

    public List<Person> getPeople() {
        return people;
    }

    public double getSimulationTime() {
        return simulationTime;
    }
}
