package blockchain.BTC_Blockchain;
import java.util.*;
import java.util.stream.IntStream;

import static blockchain.BTC_Blockchain.MathUtils.*;


public class HashFunction {
    public String hashFile(String filename, String salt){
        FileReader reader = new FileReader();
        String data = reader.readFile(filename);
        if (data == null){
            return null;
        }
        return hashString(data, salt);
    }

    public String hashString(String input, String salt){
        input += salt;
        if (input.isEmpty()){
            input = " ";
        }
        // Convert symbols to int
        int[] inputArray = input.chars().toArray();

        List<Integer> inputList = new ArrayList<>(IntStream.of(inputArray).boxed().toList());

        int inputIntegersSum = sumListIntegers(inputList);
        // In case if sum turned over and is negative
        inputIntegersSum = (inputIntegersSum >= 0) ? inputIntegersSum : inputIntegersSum * -1;
        int additionalInteger = inputIntegersSum % 37 + 7;
        for (int i = 0; i < inputList.size(); i++){
            inputList.set(i, (i * 7 + inputList.get(i)) % additionalInteger);
        }

        // Transform input to be equal or more than 64 integers
        while(inputList.size() < 64){
            int inputSize = inputList.size();
            for (int i = 0; i < inputSize; i++){
                inputList.add(inputList.get(i) + i % 2 + 1);
            }
        }

        // Transform input to 64 integers
        while (inputList.size() != 64) {
            int counter = 0;
            while (inputList.size() > 64 && counter < inputList.size()) {
                int integer = (inputList.get(counter) + 7) * (inputList.getLast() + 7);
                integer = (integer < 0) ? integer * -1 : integer;
                inputList.set(counter, integer);
                inputList.removeLast();
                counter++;
            }
        }

        // Mix list
        inputList = getMixedIntegers(inputList);

        // Make each integer be less than 16
        for (int i = 0; i < inputList.size(); i++){
            inputList.set(i, inputList.get(i) % 16);
        }

        // Transform list to hex format
        StringBuilder hex = new StringBuilder();
        for (int integer : inputList){
            hex.append(toHex(integer));
        }

        return hex.toString();
    }

    private List<Integer> getMixedIntegers(List<Integer> input){
        int inputSize = input.size();
        int inputIntegersSum = sumListIntegers(input);
        // In case if sum turned over and is negative
        inputIntegersSum = (inputIntegersSum >= 0) ? inputIntegersSum : inputIntegersSum * -1;
        
        int mixPediod = inputIntegersSum % 7 + 2;

        List<Integer> mixedInput = Arrays.asList(new Integer[inputSize]);
        int iterationIntegersNumber = inputSize / mixPediod;

        int counter = 0;
        for (int i = 0; i < mixPediod; i++){
            int insidePediodMix;
            if (i % 2 == 0){
                insidePediodMix = i / 2;
            } else {
                insidePediodMix = mixPediod - i + (i / 2);
            }
            for (int j = 1; j <= iterationIntegersNumber; j++){
                int index = inputSize - mixPediod * j + insidePediodMix;
                mixedInput.set(index, input.get(counter));
                counter++;
            }
        }

        // add to mixedInput last elements and encrypt them
        for (int i = 0; i < inputSize % mixPediod; i++){
            int encryptedInteger = input.get(counter) * mixPediod / (i + 1);
            encryptedInteger = (encryptedInteger < 0) ? encryptedInteger * -1 : encryptedInteger;
            mixedInput.set(i, encryptedInteger);
            counter++;
        }
        return mixedInput;
    }
}
