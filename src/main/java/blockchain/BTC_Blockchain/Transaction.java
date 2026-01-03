package blockchain.BTC_Blockchain;

import java.time.Instant;
import java.util.List;

public class Transaction {
    private String txid;
    private List<TxInput> inputs;
    private List<TxOutput> outputs;
    private String senderAddress;
    private String timestamp;

    public Transaction(List<TxInput> inputs, List<TxOutput> outputs, String senderAddress){
        this.inputs = inputs;
        this.outputs = outputs;
        this.senderAddress = senderAddress;

        StringBuilder hashingString = new StringBuilder();

        for (TxInput input : inputs) {
            hashingString.append(input.getTxId());
            hashingString.append(input.getValue());
        }
        for (TxOutput output : outputs){
            hashingString.append(output.getValue());
            hashingString.append(output.getAddress());
        }
        timestamp = Instant.now().toString();
        hashingString.append(timestamp);

        HashFunction hashFunction = new HashFunction();
        txid = hashFunction.hashString(hashingString.toString());
    }

    public String getTxid() {
        return txid;
    }

    public long getFee(){
        long inputsSum = 0;
        for (TxInput input : inputs){
            inputsSum += input.getValue();
        }
        long outputsSum = 0;
        for (TxOutput output : outputs){
            outputsSum += output.getValue();
        }
        return inputsSum - outputsSum;
    }

    public List<TxOutput> getOutputs(){
        return outputs;
    }

    public String getSenderAddress() {
        return senderAddress;
    }
}
