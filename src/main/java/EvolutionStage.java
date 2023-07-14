import Individual.*;
import java.util.Random;

public class EvolutionStage {

    private static IndividualPool<Faithful> faithPool = new IndividualPool<>();
    private static IndividualPool<Philanderer> philPool = new IndividualPool<>();
    private static IndividualPool<Coy> coyPool = new IndividualPool<>();
    private static IndividualPool<Fast> fastPool = new IndividualPool<>();

    private static int startingPoolSize = 500000;

    public static void initPools() {
        for (int i = 0; i < startingPoolSize; i++) {
            faithPool.add(new Faithful());
            philPool.add(new Philanderer());
            coyPool.add(new Coy());
            fastPool.add(new Fast());
        }
    }

    public static void setStartingPoolSize(int startingPoolSize) {
        EvolutionStage.startingPoolSize = startingPoolSize;
    }

    private synchronized static <T extends Male, S extends Female> void generateIndividual(T father, S mother) {
        Random rand = new Random();
        boolean coin = rand.nextBoolean();

        if (coin) { // If coin is True, then the child will be a Female
            switch (mother.getKind()) {
                case "Coy":
                    coyPool.add(new Coy());
                    break;
                case "Fast":
                    fastPool.add(new Fast());
                    break;
            }
        } else { // Otherwise, it will be a Male
            switch (father.getKind()) {
                case "Faithful":
                    faithPool.add(new Faithful());
                    break;
                case "Philanderer":
                    philPool.add(new Philanderer());
                    break;
            }
        }
    }

    private synchronized static <T extends Individual> void killOrPush(T dude) {
        if (dude.canLive()) {
            switch (dude.getKind()) {
                case "Faithful":
                    faithPool.add((Faithful) dude);
                    break;
                case "Philanderer":
                    philPool.add((Philanderer) dude);
                    break;
                case "Coy":
                    coyPool.add((Coy) dude);
                    break;
                case "Fast":
                    fastPool.add((Fast) dude);
                    break;
            }
        }
    }

    private synchronized static Female drawMother() {
        Random rand = new Random();
        boolean coin = rand.nextBoolean();

        Female mother = null;
        if (coin && !coyPool.empty() || fastPool.empty()) {
            mother = coyPool.remove();

        } else if (!fastPool.empty() || coyPool.empty()) {
            mother = fastPool.remove();
        }
        return mother;
    }

    public static int[] getPopulationSize() {
        int[] size = {coyPool.getSize(), fastPool.getSize(), faithPool.getSize(), philPool.getSize()};
        return size;
    }

    public static void throwDisease() {
        int coyToKill = coyPool.getSize() / 2;
        int fastToKill = fastPool.getSize() / 2;
        int faithToKill = faithPool.getSize() / 2;
        int philToKill = philPool.getSize() / 2;

        Individual grave;

        for (int i = 0; i < Math.max(Math.max(coyToKill, fastToKill), Math.max(faithToKill, philToKill)); i++) {
            grave = (i < coyToKill) ? (coyPool.remove()) : (null);
            grave = (i < fastToKill) ? (fastPool.remove()) : (null);
            grave = (i < faithToKill) ? (faithPool.remove()) : (null);
            grave = (i < philToKill) ? (philPool.remove()) : (null);
        }
    }

    public static void main(String[] args) {
        UserInterface.getUserInput();
        initPools();
        ActivePool<Faithful> faithfulHandler = new ActivePool<Faithful>(faithPool);
        ActivePool<Philanderer> philandererHandler = new ActivePool<Philanderer>(philPool);
        UserInterface ui = new UserInterface();

        ui.start();
        faithfulHandler.start();
        philandererHandler.start();

        while (ui.isAlive()) {
            continue;
        }

        System.exit(0);
    }

    static class ActivePool<T extends Male> extends Thread {
        private IndividualPool<T> activePool;

        public ActivePool(IndividualPool<T> activePool) {
            this.activePool = activePool;
        }

        @Override
        public void run() {
            while (true) {
                T father = activePool.remove();
                Female mother = null;

                if (father == null) {
                    break;
                }

                if (father.getKind().equals("Philanderer") && !fastPool.empty()) {
                    mother = fastPool.remove();
                } else if (!father.getKind().equals("Philanderer")) {
                    mother = drawMother();
                }

                if (mother == null) {
                    continue;
                }

                father.incrementLivingGenerations();
                mother.incrementLivingGenerations();
                generateIndividual(father, mother);
                father.markReproduction();
                mother.markReproduction();
                father.getPayoff(mother);
                mother.getPayoff(father);
                killOrPush(father);
                killOrPush(mother);
            }
        }
    }
}

/*
    Methods from the EvolutionStage class MUST be synchronized since its methods and pools are accessed by more than one thread (actually, two),
    so we need to ensure memory consistency among them, in order to not mess things up.
 */