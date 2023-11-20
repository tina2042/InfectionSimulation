public interface HealthState {
    boolean handleHealthState(Person context);

    boolean hasImmunity();
}
