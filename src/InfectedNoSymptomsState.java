import java.awt.*;
import java.io.Serializable;

public class InfectedNoSymptomsState implements HealthState, Serializable {
    private Color color;
    private double timeSinceInfection;
    public InfectedNoSymptomsState(){
        this.color=Color.ORANGE;
        this.timeSinceInfection=0;

    }

    @Override
    public void updateTimeSinceInfection(){
        this.timeSinceInfection += SimulationWindow.SIMULATION_DELAY / 1000.0;
    }
    @Override
    public double getTimeSinceInfection(){
        return timeSinceInfection;
    }
    @Override
    public Color getFillColor(){
        return this.color;
    }
}
