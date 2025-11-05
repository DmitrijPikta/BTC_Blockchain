package blockchain.BTC_Blockchain;

import java.util.LinkedList;
import java.util.List;

public class Mempool {
    private List<Transaction> mempool = new LinkedList<>();

    public void addTx(Transaction newTx){
        if (newTx == null){
            throw new IllegalArgumentException("Transaction can not be null");
        }
        mempool.add(newTx);
    }

    public List<Transaction> getTx(int txNumber){
        if (txNumber < 0){
            throw new IllegalArgumentException("txNumber can not be negative");
        }
        if (txNumber > mempool.size()){
            txNumber = mempool.size();
        }
        return new LinkedList<>(mempool.subList(0, txNumber));
    }

    public void deleteTx(String txId){
        int counter = 0;
        for (Transaction tx : mempool){
            if (tx.getTxid().equals(txId)){
                mempool.remove(counter);
                return;
            }
            counter++;
        }
    }
}
