package blockchain.BTC_Blockchain;

public class UTXO {
    private String txId;
    private int vout;   //index of output in transaction
    private long value;  //in satoshi
    //private boolean reserved = false;

    public UTXO(String txId, int vout, long value){
        if (vout < 0 || value <= 0){
            throw new IllegalArgumentException("UTXO parameters are not acceptable");
        }
        this.txId = txId;
        this.vout = vout;
        this.value = value;
    }

    public String getTxid() {
        return txId;
    }

    public int getVout() {
        return vout;
    }

    public long getValue() {
        return value;
    }

}
