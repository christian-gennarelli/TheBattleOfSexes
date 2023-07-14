package Individual;

public class Fast extends Female{

    @Override
    public void getPayoff(Individual parent) {
        happiness += parent.getKind().equals("Faithful") ? (evolutionGain-(parentingCost/2)) : (evolutionGain-parentingCost);
        updateReproductionResources();
    }
}
