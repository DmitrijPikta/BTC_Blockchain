package blockchain.BTC_Blockchain;

public class TxOutput {
    private String address;
    private long value;

    public TxOutput(String address, long value){
        if (address.length() != 64){
            throw new IllegalArgumentException("Address is wrong");
        }
        if (value < 1){
            throw new IllegalArgumentException("Value can not be less than 1");
        }
        this.address = address;
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public String getAddress() {
        return address;
    }
}
