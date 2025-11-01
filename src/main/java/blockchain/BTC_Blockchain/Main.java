package blockchain.BTC_Blockchain;

public class Main {
    public static void main(String[] args) {
        HashFunction hasher = new HashFunction();
        RandomStringGenerator random = new RandomStringGenerator();
        String hash = "vdfsvfdvfdvfvfdvfdvfd";
        while(!hash.startsWith("0000")){
            hash = hasher.hashString(random.generateString(100), "");
        }
        System.out.println(hash);
    }
}
