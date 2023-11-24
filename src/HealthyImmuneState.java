import java.awt.*;
import java.io.Serializable;

public class HealthyImmuneState implements HealthState, Serializable {

    private final Color color;

    public HealthyImmuneState(){
        this.color=Color.GREEN;
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
