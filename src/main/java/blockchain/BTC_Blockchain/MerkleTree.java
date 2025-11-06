package blockchain.BTC_Blockchain;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    public String getRootHash(List<Transaction> transactions){
        if (transactions == null || transactions.isEmpty()){
            throw new IllegalArgumentException("Transactions do not have element");
        }
        HashFunction hashFunction = new HashFunction();
        List<String> hashedTx = new ArrayList<>();
        for (Transaction tx : transactions){
            hashedTx.add(tx.getTxid());
        }

        while(hashedTx.size() != 1){
            List<String> newHashedTx = new ArrayList<>();
            for (int i = 0; i < hashedTx.size() - 1; i += 2){
                newHashedTx.add(hashFunction.hashString(hashedTx.get(i) + hashedTx.get(i + 1)));
            }
            if (hashedTx.size() % 2 != 0){
                String last = hashedTx.getLast();
                newHashedTx.add(hashFunction.hashString(last + last));
            }
            hashedTx = newHashedTx;
        }
        return hashedTx.getFirst();
    }
}
