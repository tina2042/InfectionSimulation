import java.awt.*;
import java.io.Serializable;

public class InfectedHasSymptomsState implements HealthState, Serializable {
    private Color color;
    private double timeSinceInfection;
    public InfectedHasSymptomsState(){
        this.color=Color.RED;
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
