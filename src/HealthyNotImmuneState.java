import java.awt.*;
import java.io.Serializable;

public class HealthyNotImmuneState implements HealthState, Serializable {
    private Color color;
    public HealthyNotImmuneState(){
        this.color=Color.YELLOW;
    }

    @Override
    public void updateTimeSinceInfection(){
    }
    @Override
    public double getTimeSinceInfection(){
        return 0;
    }
    @Override
    public Color getFillColor(){
        return this.color;
    }
}
