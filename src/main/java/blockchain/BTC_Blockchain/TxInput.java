package blockchain.BTC_Blockchain;

public class TxInput {
    private String txId;
    private long value;

    public TxInput(String txId, long value){
        if (txId.length() != 64){
            throw new IllegalArgumentException("txId is wrong");
        }
        if (value < 1){
            throw new IllegalArgumentException("Value can not be less than 1");
        }
        this.txId = txId;
        this.value = value;
    }

    public String getTxId() {
        return txId;
    }

    public long getValue() {
        return value;
    }
}
