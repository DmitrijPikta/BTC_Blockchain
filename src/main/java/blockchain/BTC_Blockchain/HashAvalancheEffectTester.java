package blockchain.BTC_Blockchain;
import java.util.List;
import java.util.StringJoiner;

import static blockchain.BTC_Blockchain.MathUtils.hexToBinary;

public class HashAvalancheEffectTester {
    private double minHexSimilarityCoefficient;
    private double maxHexSimilarityCoefficient;
    private double averageHexSimilarityCoefficient;
    private double minByteSimilarityCoefficient;
    private double maxByteSimilarityCoefficient;
    private double averageByteSimilarityCoefficient;

    public String test(List<List<String>> pairs){
        if (pairs.getFirst().size() != 2){
            throw new IllegalArgumentException("Wrong data taken");
        }
        maxHexSimilarityCoefficient = 0;
        minHexSimilarityCoefficient = 1;
        double averageHexSimilarityCoefficientsSum = 0;
        double averageByteSimilarityCoefficientsSum = 0;
        HashFunction hasher = new HashFunction();
        String salt = "vdfsvfdvfdv";
        for (List<String> pair : pairs){
            String hash1 = hasher.hashString(pair.get(0), salt);
            String hash2 = hasher.hashString(pair.get(1), salt);
            double similarityCoefficient = getSimilarityCoefficient(hash1, hash2);

            if (similarityCoefficient > maxHexSimilarityCoefficient){
                maxHexSimilarityCoefficient = similarityCoefficient;
            } else if (similarityCoefficient < minHexSimilarityCoefficient) {
                minHexSimilarityCoefficient = similarityCoefficient;
            }
            averageHexSimilarityCoefficientsSum += similarityCoefficient;

            // For binary test
            String binaryHash1 = hexToBinary(hash1);
            String binaryHash2 = hexToBinary(hash2);
            similarityCoefficient = getSimilarityCoefficient(binaryHash1, binaryHash2);
            if (similarityCoefficient > maxByteSimilarityCoefficient){
                maxByteSimilarityCoefficient = similarityCoefficient;
            } else if (similarityCoefficient < minByteSimilarityCoefficient){
                minByteSimilarityCoefficient = similarityCoefficient;
            }
            averageByteSimilarityCoefficientsSum += similarityCoefficient;
        }
        int pairsSize = pairs.size();
        averageHexSimilarityCoefficient = averageHexSimilarityCoefficientsSum / pairsSize;
        averageByteSimilarityCoefficient = averageByteSimilarityCoefficientsSum / pairsSize;

        return printTestResults();
    }

    private double getSimilarityCoefficient(String hash1, String hash2){
        if (hash1.length() != hash2.length()){
            throw new IllegalArgumentException("Error: hashes have different length");
        }
        int similarSum = 0;
        for (int i = 0; i < hash1.length(); i++){
            if (hash1.charAt(i) == hash2.charAt(i)){
                similarSum += 1;
            }
        }
        return (double) similarSum / hash1.length();
    }

    private String printTestResults(){
        StringJoiner result = new StringJoiner(System.lineSeparator());
        result.add("Hex similarity test:");
        result.add("Min similarity coefficient is " + minHexSimilarityCoefficient);
        result.add("Max similarity coefficient is " + maxHexSimilarityCoefficient);
        result.add("Average similarity coefficient is " + averageHexSimilarityCoefficient);
        result.add("-".repeat(30));
        result.add("Binary similarity test:");
        result.add("Min similarity coefficient is " + minByteSimilarityCoefficient);
        result.add("Max similarity coefficient is " + maxByteSimilarityCoefficient);
        result.add("Average similarity coefficient is " + averageByteSimilarityCoefficient);
        return result.toString();
    }

}
