package blockchain.BTC_Blockchain;
import java.util.StringJoiner;

public class RandomStringPairsGenerator {
    private final RandomStringGenerator randomGenerator;
    private boolean pairOneSymbolDifference = false;

    public RandomStringPairsGenerator(int stringLowBound, int stringUpBound){
        randomGenerator = new RandomStringGenerator(stringLowBound, stringUpBound);
    }

    public RandomStringPairsGenerator(){
        randomGenerator = new RandomStringGenerator();
    }

    public RandomStringPairsGenerator(boolean pairOneSymbolDifference){
        randomGenerator = new RandomStringGenerator();
        this.pairOneSymbolDifference = pairOneSymbolDifference;
    }

    public RandomStringPairsGenerator(int stringLowBound, int stringUpBound, boolean pairOneSymbolDifference){
        randomGenerator = new RandomStringGenerator(stringLowBound, stringUpBound);
        this.pairOneSymbolDifference = pairOneSymbolDifference;
    }

    public String generateStringPairs(int pairsNumber, int stringLength){
        StringJoiner generatedPairs = new StringJoiner(System.lineSeparator());
        if (!pairOneSymbolDifference) {
            for (int i = 0; i < pairsNumber; i++) {
                StringBuilder generatedPair = new StringBuilder();
                generatedPair.append(randomGenerator.generateString(stringLength));
                generatedPair.append(",");
                generatedPair.append(randomGenerator.generateString(stringLength));
                generatedPairs.add(generatedPair);
            }
        } else {
            for (int i = 0; i < pairsNumber; i++) {
                StringBuilder generatedPair = new StringBuilder();
                String firstGeneratedString = randomGenerator.generateString(stringLength);

                StringBuilder secondString = new StringBuilder(firstGeneratedString);
                char secondStringDifferentSymbol = (char)(secondString.charAt(0) + 1);
                secondString.setCharAt(0, secondStringDifferentSymbol);

                generatedPair.append(firstGeneratedString);
                generatedPair.append(",");
                generatedPair.append(secondString);
                generatedPairs.add(generatedPair);
            }
        }
        return generatedPairs.toString();
    }
}
