package Individual;

public abstract class Individual {

    protected static int evolutionGain = 15;
    protected static int parentingCost = 20;
    protected static int courtshipCost = 3;

    protected int happiness = 0;
    protected int reproductionResources = 1;
    protected int threshHold = 2;
    protected int livingGenerations = 0;
    protected static int maxLivingGenerations = 5;
    private static boolean maxGenSignal = false;

    // Setters
    public static void setEvolutionGain(int evolutionGain) { Individual.evolutionGain = evolutionGain; }

    public static void setParentingCost(int parentingCost) {
        Individual.parentingCost = parentingCost;
    }

    public static void setCourtshipCost(int courtshipCost) {
        Individual.courtshipCost = courtshipCost;
    }

    public static void setMaxGenSignal(boolean maxGenSignal) {
        Individual.maxGenSignal = maxGenSignal;
    }

    public static void setMaxLivingGenerations(int value) {
        maxLivingGenerations = value;
    }


    public String getKind () {
        return this.getClass().getSimpleName();
    }

    public void incrementLivingGenerations() {livingGenerations++;}

    protected void updateReproductionResources () {
        while (happiness >= threshHold) {
            reproductionResources++;
            threshHold *= 2; // Exponential
            //threshHold += 2; // Linear
        }
    }

    public boolean canLive() {
        if (maxGenSignal) {
            return (reproductionResources >= 1 && livingGenerations < maxLivingGenerations) ? (true) : (false);
        } else {
            return (reproductionResources >= 1) ? (true) : (false);
        }
    }

    public void markReproduction () {
        reproductionResources--;
    }

    public abstract void getPayoff(Individual parent);
}
