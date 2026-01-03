package blockchain.BTC_Blockchain;

import java.util.ArrayList;
import java.util.List;

public class Block {
    private String prevBlockHash;
    private String timestamp;
    private String version = "v1.0";
    private String txHash;
    private int nonce;
    private int difficultyTarget;

    private List<Transaction> transactions = new ArrayList<>();

    public Block(String prevBlockHash, String timestamp, int nonce, int difficultyTarget, List<Transaction> transactions){
        if (difficultyTarget < 0){
            throw new IllegalArgumentException("Difficulty target can not be negative");
        }

        HashFunction hashFunction = new HashFunction();
        MerkleTree merkleTree = new MerkleTree();
        txHash = merkleTree.getRootHash(transactions);
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

    public List<Transaction> getTransactions(){
        return new ArrayList<>(transactions);
    }

    public String getPrevBlockHash() {
        return prevBlockHash;
    }

    public int getDifficultyTarget() {
        return difficultyTarget;
    }

    public int getNonce() {
        return nonce;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTxHash() {
        return txHash;
    }

    public String getVersion() {
        return version;
    }
}
