package blockchain.BTC_Blockchain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Blockchain {
    Blocks blocks = new Blocks();
    Users users;
    UTXOMap utxoMap = new UTXOMap();
    Mempool mempool = new Mempool();

    int satoshiInBTC = 100000000;
    double txFeePercent = 0.5; // %
    long blockReward = (long)50 * satoshiInBTC;

    public Blockchain(int usersNumber){
        users = new Users(usersNumber);
    }

    public void mineBlock(String minerAddress){
        if (!users.contains(minerAddress)){
            throw new IllegalArgumentException("Miner address is wrong: where are no such address");
        }

        List<Transaction> transactions = mempool.getTx(100);

        StringBuilder transactionsSummary = new StringBuilder();
        long fee = 0;
        for (Transaction tx : transactions){
            fee += tx.getFee();
            transactionsSummary.append(tx.getTxid());
        }
        // create tx for miner reward
        List<TxOutput> minerOutput = new ArrayList<>();
        minerOutput.add(new TxOutput(minerAddress, fee + blockReward));
        List<TxInput> minerInputs = new ArrayList<>();
        transactions.addFirst(new Transaction(minerInputs, minerOutput));
        transactionsSummary.insert(0, transactions.getFirst().getTxid());

        HashFunction hashFunction = new HashFunction();
        String txHash = hashFunction.hashString(transactionsSummary.toString());
        String prevBlockHash = blocks.getLastBlockHash();
        String timestamp = Instant.now().toString();
        String version = "v1.0";
        int nonce = 0;
        int difficultyTarget = 3;

        String preHashingString = prevBlockHash + timestamp + version + txHash + difficultyTarget;
        String blockHash = hashFunction.hashString(preHashingString + nonce);
        Random random = new Random();
        while(!blockHash.startsWith("0".repeat(difficultyTarget))){
            nonce = random.nextInt();
            blockHash = hashFunction.hashString(preHashingString + nonce);
        }
        blocks.addNewBlock(new Block(prevBlockHash, timestamp, nonce, difficultyTarget, transactions));
        // create UTXO and delete made tx from mempool
        int i = 0;
        for (Transaction madeTx : transactions){
            if (i > 0){
                mempool.deleteTx(madeTx.getTxid());
            }
            i++;
            List<TxOutput> madeTxOutputs = madeTx.getOutputs();
            int counter = 0;
            for (TxOutput madeTxOutput : madeTxOutputs){
                utxoMap.addUTXO(madeTxOutput.getAddress(), new UTXO(madeTx.getTxid(), counter, madeTxOutput.getValue()));
                counter++;
            }
        }
        // delete tx from mempool
    }



    public void createTx(String senderAddress, String receiverAddress, double valueBTC){
        if (!users.contains(receiverAddress)){
            throw new IllegalArgumentException("Receiver address is wrong: where is not user with such address");
        }
        if (valueBTC <= 0){
            throw new IllegalArgumentException("Value must be positive number");
        }
        long valueSatoshi = (long)(valueBTC * satoshiInBTC);
        long valueWithFeeSatoshi = (long)(valueSatoshi + (double)valueSatoshi / 100 * txFeePercent);

        List<UTXO> usersUTXO = utxoMap.getUsersUTXO(senderAddress);

        int usersUTXOSize = usersUTXO.size();
        int counter = usersUTXOSize - 1;
        long inputsSum = 0;
        List<UTXO> inputsUTXO = new ArrayList<>();
        while (counter >= 0 && inputsSum < valueWithFeeSatoshi){
            inputsUTXO.add(usersUTXO.get(counter));
            inputsSum += inputsUTXO.getLast().getValue();
            counter--;
        }
        if (inputsSum < valueWithFeeSatoshi){
            throw new IllegalArgumentException("User do not have enough coins");
        }
        // Deleting taken UTXO
        for (int i = 0; i < usersUTXOSize - 1 - counter; i++){
            utxoMap.removeLastUsersUTXO(senderAddress);
        }

        // Making inputs
        List<TxInput> inputs = new ArrayList<>();
        for (UTXO inputUTXO : inputsUTXO){
            inputs.add(new TxInput(inputUTXO.getTxid(), inputUTXO.getValue()));
        }
        // Making outputs
        List<TxOutput> outputs = new ArrayList<>();
        outputs.add(new TxOutput(receiverAddress, valueSatoshi)); // Main output
        long inputsRemainder = inputsSum - valueWithFeeSatoshi;
        if (inputsRemainder > 0){
            outputs.add(new TxOutput(senderAddress, inputsRemainder));
        }

        mempool.addTx(new Transaction(inputs, outputs));
    }

    public Users getUsers(){
        return users;
    }
}
