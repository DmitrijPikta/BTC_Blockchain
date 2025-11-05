package blockchain.BTC_Blockchain;

public class User {
    private final String name;
    private final String publicKey;

    public User(String name){
        if (name.isBlank()){
            throw new IllegalArgumentException("User name can not be blank");
        }
        this.name = name;

        RandomStringGenerator randomGenerator = new RandomStringGenerator();
        HashFunction hashFunction = new HashFunction();
        String hash = hashFunction.hashString(randomGenerator.generateString(100) + name, "For public key");
        publicKey = "0" + hash;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getName(){
        return name;
    }
}
