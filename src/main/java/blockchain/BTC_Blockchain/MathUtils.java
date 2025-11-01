package blockchain.BTC_Blockchain;
import java.util.HashMap;
import java.util.List;

public final class MathUtils {
    private MathUtils(){}

    public static int sumListIntegers(List<Integer> list){
        int sum = 0;
        for (int number : list){
            sum += number;
        }
        return sum;
    }

    public static String toHex(int integer){
        return String.format("%x", integer);
    }

    public static String hexToBinary(String hex){
        StringBuilder binary = new StringBuilder();
        hex = hex.toUpperCase();

        HashMap<Character, String> hexToBinaryMap = getHexToBinaryMap();

        for (int i = 0; i < hex.length(); i++){
            char hexSymbol = hex.charAt(i);
            if (hexToBinaryMap.containsKey(hexSymbol)){
                binary.append(hexToBinaryMap.get(hexSymbol));
            } else {
                throw new IllegalArgumentException("Error: hex must consist only hex symbols");
            }
        }
        return binary.toString();
    }

    private static HashMap<Character, String> getHexToBinaryMap() {
        HashMap<Character, String> hexToBinaryMap = new HashMap<>();
        hexToBinaryMap.put('0', "0000");
        hexToBinaryMap.put('1', "0001");
        hexToBinaryMap.put('2', "0010");
        hexToBinaryMap.put('3', "0011");
        hexToBinaryMap.put('4', "0100");
        hexToBinaryMap.put('5', "0101");
        hexToBinaryMap.put('6', "0110");
        hexToBinaryMap.put('7', "0111");
        hexToBinaryMap.put('8', "1000");
        hexToBinaryMap.put('9', "1001");
        hexToBinaryMap.put('A', "1010");
        hexToBinaryMap.put('B', "1011");
        hexToBinaryMap.put('C', "1100");
        hexToBinaryMap.put('D', "1101");
        hexToBinaryMap.put('E', "1110");
        hexToBinaryMap.put('F', "1111");
        return hexToBinaryMap;
    }
}
