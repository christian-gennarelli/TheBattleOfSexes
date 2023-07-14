import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Scanner;
import java.lang.Math;
import Individual.*;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UserInterface extends Thread {

    private static int faithSize;
    private static int philSize;
    private static int coySize;
    private static int fastSize;
    private static int maleSize;
    private static int femaleSize;
    private static int individualSize;

    private static double faithStability = 0;
    private static double philStability = 0;
    private static double coyStability = 0;
    private static double fastStability = 0;

    private static double faithPercentage;
    private static double philPercentage;
    private static double coyPercentage;
    private static double fastPercentage;
    private static double malePercentage;
    private static double femalePercentage;
    private static double lastFaithPercentage = 25;
    private static double lastPhilPercentage = 25;
    private static double lastCoyPercentage = 25;
    private static double lastFastPercentage = 25;
    private static int nGeneration = 0;

    static final String ANSI_BOLD = "\033[0;1m";
    static final String ANSI_UNBOLD = "\033[0;0m";
    static final String ANSI_CLEAR = "\033[H\033[2J";
    final private static String ANSI_RED = "\u001b[31;1m";
    final private static String ANSI_GREEN = "\u001B[32;1m";
    final private static String ANSI_RESET = "\u001B[0m";

    private static int maxIterations = 1000;
    private static int refreshTimer = 1000;
    private static int stabilityThreshold = 1;
    private static int evolTraceLen = 10;
    private static int diseaseThreshold = 5000000;
    private static ArrayDeque<Generation> evolutionTrace = new ArrayDeque<>(evolTraceLen);

    public static void getUserInput () {


        System.out.printf(ANSI_CLEAR);
        System.out.flush();

        System.out.printf("\n%sWelcome to Battle Of Sexes simulation.%s\n\n", ANSI_BOLD, ANSI_UNBOLD);
        Scanner keyboard = new Scanner(System.in);

        System.out.printf("Enter the number of starting individuals for each subpopulation (leave blank to use default value 500.000): ");
        String nSubIndividuals = keyboard.nextLine();

        System.out.printf("\nEnter the evolution gain value (leave blank to use default value 15): ");
        String evolutionGain = keyboard.nextLine();

        System.out.printf("Enter the parenting cost value (leave blank to use default value 20): ");
        String parentingCost = keyboard.nextLine();

        System.out.printf("Enter the courtship cost value (leave blank to use default value 3): ");
        String courtshipCost = keyboard.nextLine();

        System.out.printf("\nAfter what number of individuals do you want to introduce a disease to halve the population? (leave blank to use default value 5.000.000): ");
        String diseaseThreshold = keyboard.nextLine();

        System.out.printf("Do you want an individual to live for a specific number of generations AT MOST? (y/N) (default = no): ");
        String choice = keyboard.nextLine();
        String maxLivingGenerations = "";
        boolean maxGenSignal = false;

        if (choice.equals("y") || choice.equals("Y") || choice.isEmpty()) {
            maxGenSignal = true;
            System.out.printf("How many generations can an individual live at most? (leave blank to use default value 5): ");
            maxLivingGenerations = keyboard.nextLine();
        }

        System.out.printf("\nHow many generations do you want to compute stability? (leave blank to use default value 10): ");
        String evolTraceLen = keyboard.nextLine();

        System.out.printf("\nEnter the minimum value to define the stable inter-generation variance. (leave blank to use default value 1): ");
        String stabilityThreshold = keyboard.nextLine();

        System.out.printf("\nAfter how many milliseconds do you want the console to refresh? (leave blank to use default value 1000): ");
        String refreshTimer = keyboard.nextLine();

        System.out.printf("\nFor how many iterations should the simulation run? (leave blank to use default value 1000): ");
        String maxIterations = keyboard.nextLine();

        applySettings(nSubIndividuals, evolutionGain, parentingCost, courtshipCost, diseaseThreshold, maxLivingGenerations,
                maxGenSignal, stabilityThreshold, refreshTimer, evolTraceLen, maxIterations);
    }

    private static void applySettings(String nSubIndividuals, String evolutionGain, String parentingCost, String courtshipCost,
                                      String diseaseThreshold, String maxLivingGenerations, Boolean maxGenSignal, String stabilityThreshold,
                                      String refreshTimer, String evolTraceLen, String maxIterations) {

        if (!nSubIndividuals.isEmpty()) {
            EvolutionStage.setStartingPoolSize(Integer.parseInt(nSubIndividuals));
        }

        if (!evolutionGain.isEmpty()) {
            Individual.setEvolutionGain(Integer.parseInt(evolutionGain));
        }

        if (!parentingCost.isEmpty()) {
            Individual.setParentingCost(Integer.parseInt(parentingCost));
        }

        if (!courtshipCost.isEmpty()) {
            Individual.setCourtshipCost(Integer.parseInt(courtshipCost));
        }

        if (!diseaseThreshold.isEmpty()) {
            UserInterface.diseaseThreshold = Integer.parseInt(diseaseThreshold);
        }

        if (!stabilityThreshold.isEmpty()) {
            setStabilityThreshold(Integer.parseInt(stabilityThreshold));
        }

        if (!refreshTimer.isEmpty()) {
            setRefreshTimer(Integer.parseInt(refreshTimer));
        }

        if (!maxGenSignal) {
            Individual.setMaxGenSignal(false);
        }

        if (maxGenSignal && !maxLivingGenerations.isEmpty()) {
            Individual.setMaxLivingGenerations(Integer.parseInt(maxLivingGenerations));
        }

        if (!evolTraceLen.isEmpty()) {
            UserInterface.evolTraceLen = Integer.parseInt(evolTraceLen);
        }

        if (!maxIterations.isEmpty()) {
            UserInterface.maxIterations = Integer.parseInt(maxIterations);
        }
    }

    @Override
    public void run() {
        try {
            // vvv Insert right path vvv
            FileInputStream inputStream = new FileInputStream(new File("/Users/genna/Documents/Java/The-Battle-Of-Sexes/data/simulation_data.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            while (nGeneration < maxIterations) {
                setData();
                updateEvolutionTrace();
                printStats();

                lastCoyPercentage = coyPercentage;
                lastFastPercentage = fastPercentage;
                lastFaithPercentage = faithPercentage;
                lastPhilPercentage = philPercentage;

                Number[] genData = {coyPercentage, fastPercentage, faithPercentage, philPercentage, coyStability,
                                    fastStability, faithStability, philStability};

                int rowCount = sheet.getLastRowNum();
                Row row = sheet.createRow(++rowCount);
                int columnCount = 0;
                Cell cell = row.createCell(columnCount);
                cell.setCellValue(nGeneration);

                for (Number field : genData) {
                    cell = row.createCell(++columnCount);
                    if (field instanceof Double) {
                        cell.setCellValue((Double) field);
                    }
                }

                if (individualSize > diseaseThreshold) {
                    EvolutionStage.throwDisease();
                }

                nGeneration++;
                sleep(refreshTimer);
            }

            inputStream.close();
            // vvv Insert right path vvv
            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Giovanni\\Documents\\GitHub\\The-Battle-Of-Sexes\\data\\simulation_data.xlsx");
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

        } catch (InterruptedException | IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }
    }

    private static void printStats () {
        System.out.print(ANSI_CLEAR);
        System.out.flush();

        System.out.printf("\nGeneration: %d \n", nGeneration);
        System.out.printf("Tot individuals: %d | Females percentage: %.2f | Males percentage: %.2f)\n", individualSize, femalePercentage, malePercentage);
        System.out.printf("Variance: %sGreen = Stable%s | %sRed = Unstable%s\n", ANSI_GREEN, ANSI_RESET, ANSI_RED, ANSI_RESET);

        if (coyPercentage < lastCoyPercentage) {
            System.out.printf("\nCoys: %d | Percentage: %s%.2f%s", coySize, ANSI_RED, coyPercentage, ANSI_RESET);
        } else {
            System.out.printf("\nCoys: %d | Percentage: %s%.2f%s", coySize, ANSI_GREEN, coyPercentage, ANSI_RESET);
        } if (coyStability > stabilityThreshold) {
            System.out.printf("\nVariance over 10 generations: %s%.2f%s", ANSI_RED, coyStability, ANSI_RESET);
        } else if (coyStability <= stabilityThreshold) {
            System.out.printf("\nVariance over 10 generations: %s%.2f%s", ANSI_GREEN, coyStability, ANSI_RESET);
        } printHistogram(coyPercentage, lastCoyPercentage);

        if (fastPercentage < lastFastPercentage) {
            System.out.printf("\nFasts: %d | Percentage: %s%.2f%s", fastSize, ANSI_RED, fastPercentage, ANSI_RESET);
        } else {
            System.out.printf("\nFasts: %d | Percentage: %s%.2f%s", fastSize, ANSI_GREEN, fastPercentage, ANSI_RESET);
        } if (faithStability > stabilityThreshold) {
            System.out.printf("\nVariance over 10 generations: %s%.2f%s", ANSI_RED, faithStability, ANSI_RESET);
        } else if (fastStability <= stabilityThreshold) {
            System.out.printf("\nVariance over 10 generations: %s%.2f%s", ANSI_GREEN, faithStability, ANSI_RESET);
        } printHistogram(fastPercentage, lastFastPercentage);

        if (faithPercentage < lastFaithPercentage) {
            System.out.printf("\nFaithfuls: %d | Percentage: %s%.2f%s", faithSize, ANSI_RED, faithPercentage, ANSI_RESET);
        } else {
            System.out.printf("\nFaithfuls: %d | Percentage: %s%.2f%s", faithSize, ANSI_GREEN, faithPercentage, ANSI_RESET);
        } if (faithStability > stabilityThreshold) {
            System.out.printf("\nVariance over 10 generations: %s%.2f%s", ANSI_RED, faithStability, ANSI_RESET);
        } else if (faithStability <= stabilityThreshold) {
            System.out.printf("\nVariance over 10 generations: %s%.2f%s", ANSI_GREEN, faithStability, ANSI_RESET);
        } printHistogram(faithPercentage, lastFaithPercentage);

        if (philPercentage < lastPhilPercentage) {
            System.out.printf("\nPhilanderers: %d | Percentage: %s%.2f%s", philSize, ANSI_RED, philPercentage, ANSI_RESET);
        } else {
            System.out.printf("\nPhilanderers: %d | Percentage: %s%.2f%s", philSize, ANSI_GREEN, philPercentage, ANSI_RESET);
        } if (philStability > stabilityThreshold) {
            System.out.printf("\nVariance over 10 generations: %s%.2f%s", ANSI_RED, philStability, ANSI_RESET);
        } else if (philStability <= stabilityThreshold) {
            System.out.printf("\nVariance over 10 generations: %s%.2f%s", ANSI_GREEN, philStability, ANSI_RESET);
        } printHistogram(philPercentage, lastPhilPercentage);
    }

    private static void setStabilityThreshold (int value) {
        stabilityThreshold = value;
    }

    private static void setData () {
        int[] populationSize = EvolutionStage.getPopulationSize();

        coySize = populationSize[0];
        fastSize = populationSize[1];
        faithSize = populationSize[2];
        philSize = populationSize[3];

        maleSize = faithSize + philSize;
        femaleSize = coySize + fastSize;
        individualSize = maleSize + femaleSize;

        faithPercentage = Generation.getPercentage(faithSize, individualSize);
        philPercentage = Generation.getPercentage(philSize, individualSize);
        coyPercentage = Generation.getPercentage(coySize, individualSize);
        fastPercentage = Generation.getPercentage(fastSize, individualSize);
        malePercentage = Generation.getPercentage(maleSize, individualSize);
        femalePercentage = Generation.getPercentage(femaleSize, individualSize);

        if (nGeneration >= 10) {
            getGenVariance();
        }
    }

    private static void updateEvolutionTrace () {
        if (evolutionTrace.size() == 10) {
            evolutionTrace.remove();
        } evolutionTrace.add(new Generation(nGeneration, coySize, fastSize, faithSize, philSize, individualSize));
    }

    public static void getGenVariance () {
        double coyVar = 0;
        double fastVar = 0;
        double faithfulVar = 0;
        double philandererVar = 0;
        double coyAvg = 0;
        double fastAvg = 0;
        double faithfulAvg = 0;
        double philandererAvg = 0;

        for (Generation g : evolutionTrace) {
            coyAvg += g.getCoyState();
            fastAvg += g.getFastState();
            faithfulAvg += g.getFaithfulState();
            philandererAvg += g.getPhilandererState();
        }

        coyAvg /= evolTraceLen;
        fastAvg /= evolTraceLen;
        faithfulAvg /= evolTraceLen;
        philandererAvg /= evolTraceLen;

        for (Generation g : evolutionTrace) {
            coyVar += Math.pow((g.getCoyState() - coyAvg), 2);
            fastVar += Math.pow((g.getFastState() - fastAvg), 2);
            faithfulVar += Math.pow((g.getFaithfulState() - faithfulAvg), 2);
            philandererVar += Math.pow((g.getPhilandererState() - philandererAvg), 2);
        }

        coyStability = coyVar / evolTraceLen;
        fastStability = fastVar / evolTraceLen;
        faithStability = faithfulVar / evolTraceLen;
        philStability = philandererVar / evolTraceLen;
    }

    public static void setRefreshTimer (int value) {
        refreshTimer = value;
    }

    private static void printHistogram (double percentage, double lastPercentage) {
        int nChuncks = (int) (4 * percentage / 10);
        System.out.printf("\n[");
        for (int i = 0; i < 40; i += 1) {
            if (i <= nChuncks) {
                System.out.printf((percentage < lastPercentage) ? String.format("%s#%s", ANSI_RED, ANSI_RESET) : String.format("%s#%s", ANSI_GREEN, ANSI_RESET));
            } else {
                System.out.printf(" ");
            }
        }
        System.out.printf("]\n");
    }
}

