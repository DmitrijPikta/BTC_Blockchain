package blockchain.BTC_Blockchain;

import java.util.ArrayList;
import java.util.List;

public class Block {
    String prevBlockHash;
    String timestamp;
    String version = "v1.0";
    String txHash;
    int nonce;
    int difficultyTarget;

    List<Transaction> transactions = new ArrayList<>();

    public Block(String prevBlockHash, String timestamp, int nonce, int difficultyTarget, List<Transaction> transactions){
        if (difficultyTarget < 0){
            throw new IllegalArgumentException("Difficulty target can not be negative");
        }

        StringBuilder transactionsSummary = new StringBuilder();
        for (Transaction tx : transactions){
            transactionsSummary.append(tx.getTxid());
        }
        HashFunction hashFunction = new HashFunction();
        txHash = hashFunction.hashString(transactionsSummary.toString());
        String hashingString = prevBlockHash + timestamp + version + txHash + difficultyTarget + nonce;
        String blockHash = hashFunction.hashString(hashingString);

        if (!blockHash.startsWith("0".repeat(difficultyTarget))){
            throw new IllegalArgumentException("Block is not acceptable: block hash do not achieve difficulty target");
        }

        this.prevBlockHash = prevBlockHash;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.difficultyTarget = difficultyTarget;
        this.transactions = transactions;
    }

    public String getBlockHash() {
        HashFunction hashFunction = new HashFunction();
        return hashFunction.hashString(prevBlockHash + timestamp + version + txHash + difficultyTarget + nonce);
    }
}
