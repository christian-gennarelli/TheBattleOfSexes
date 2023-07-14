package Individual;

public class Faithful extends Male{

    @Override
    public void getPayoff(Individual parent) {
        happiness += parent.getKind().equals("Coy") ? (evolutionGain-(parentingCost/2)-courtshipCost) : (evolutionGain-(parentingCost/2));
        updateReproductionResources();
    }
}
