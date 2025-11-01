package blockchain.BTC_Blockchain;
import java.io.File;
import java.io.IOException;

public class RandomStringPairsFileGenerator {
    private final RandomStringPairsGenerator pairsGenerator;

    public RandomStringPairsFileGenerator(int stringLowBound, int stringUpBound){
        pairsGenerator = new RandomStringPairsGenerator(stringLowBound, stringUpBound);
    }

    public RandomStringPairsFileGenerator(){
        pairsGenerator = new RandomStringPairsGenerator();
    }

    public RandomStringPairsFileGenerator(boolean pairOneSymbolDifference){
        pairsGenerator = new RandomStringPairsGenerator(pairOneSymbolDifference);
    }

    public RandomStringPairsFileGenerator(int stringLowBound, int stringUpBound, boolean pairOneSymbolDifference){
        pairsGenerator = new RandomStringPairsGenerator(stringLowBound, stringUpBound, pairOneSymbolDifference);
    }

    public void generateStringPairsFile(String filename, int pairsNumber, int stringLength){
        try {
            File file = new File(filename);
            file.createNewFile();
        } catch (IOException e){
            System.out.println("An error occurred trying create file");
            e.printStackTrace();
            return;
        }

        String generatedPairs = pairsGenerator.generateStringPairs(pairsNumber, stringLength);
        FileWriter writer = new FileWriter();
        writer.write(filename, generatedPairs);
    }


}
