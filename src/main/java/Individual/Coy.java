package Individual;

public class Coy extends Female{

    @Override
    public void getPayoff (Individual parent) {
        happiness += (evolutionGain-(parentingCost/2)-courtshipCost);
        updateReproductionResources();
    }
}
