import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Klasa Memento przeniesiona na zewnątrz
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
