import java.io.Serializable;

public class InfectedState implements HealthState, Serializable {
    private final boolean hasSymptoms;
    private double timeSinceInfection;
    private final boolean hasImmunity;
    public InfectedState(boolean hasSymptoms) {
        this.hasSymptoms = hasSymptoms;
        this.timeSinceInfection = 0;
        this.hasImmunity = false;
    }

    @Override
    public boolean handleHealthState(Person context) {
        //String info="Osoba jest chora z objawami.";
        // Dodatkowa logika dla chorego z objawami
        // String info="Osoba jest chora bez objawów.";
        // Dodatkowa logika dla chorego bez objawów
        return hasSymptoms;
    }

    public double getTimeSinceInfection(){
        return timeSinceInfection;
    }
    public void updateTimeSinceInfection(){
        this.timeSinceInfection += SimulationWindow.SIMULATION_DELAY / 1000.0;
    }
    public boolean hasImmunity() {
        return hasImmunity;
    }

}
