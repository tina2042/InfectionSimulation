import java.awt.*;

public interface HealthState {

    void updateTimeSinceInfection();
    double getTimeSinceInfection();
    Color getFillColor();

}
