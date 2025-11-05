package blockchain.BTC_Blockchain;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain(1000);
        Map<String, User> users = blockchain.getUsers().getUsers();
        String satoshi = users.entrySet().stream().filter(u -> u.getValue().getName().equals("Satoshi Nakamoto"))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        String user0 = users.entrySet().stream().filter(u -> u.getValue().getName().equals("User0"))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);



        blockchain.mineBlock(satoshi);
        blockchain.createTx(satoshi, user0, 1);
        blockchain.mineBlock(satoshi);
    }
}
