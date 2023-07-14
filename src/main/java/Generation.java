public class Generation {

    private int nGeneration;
    private double coyState;
    private double fastState;
    private double faithfulState;
    private double philandererState;

    public Generation(int nGeneration, int coySize, int fastSize, int faithSize, int philSize, int individualSize) {
        this.nGeneration = nGeneration;
        coyState = getPercentage(coySize, individualSize);
        fastState = getPercentage(fastSize, individualSize);
        faithfulState = getPercentage(faithSize, individualSize);
        philandererState = getPercentage(philSize, individualSize);
    }

    public static double getPercentage (int subPopulation, int nIndividuals) {
        return ((double) subPopulation / (double) nIndividuals * 100);
    }

    public double getCoyState() {
        return coyState;
    }

    public double getFastState() {
        return fastState;
    }

    public double getFaithfulState() {
        return faithfulState;
    }

    public double getPhilandererState() {
        return philandererState;
    }
}
