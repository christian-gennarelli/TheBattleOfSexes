package Individual;

public class Philanderer extends Male{

    @Override
    public void getPayoff(Individual parent) {
        happiness += (evolutionGain);
        updateReproductionResources();
    }
}
