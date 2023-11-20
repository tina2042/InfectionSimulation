

public class HealthyState implements HealthState{
    private final boolean isImmune;

    public HealthyState(boolean isImmune) {
        this.isImmune = isImmune;
    }

    @Override
    public boolean handleHealthState(Person context) {
        //String info="Osoba jest zdrowa i odporna.";
        // Dodatkowa logika dla chorego z objawami
        //String info="Osoba jest zdrowa i nieodporna.";
        // Dodatkowa logika dla chorego bez objawów
        return isImmune;
    }
    @Override
    public boolean hasImmunity(){
        return isImmune;
    }

}
