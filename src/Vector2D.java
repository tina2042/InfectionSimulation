import java.io.Serializable;

public class Vector2D implements IVector, Serializable {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double[] getComponents() {
        return new double[]{x, y};
    }

}
