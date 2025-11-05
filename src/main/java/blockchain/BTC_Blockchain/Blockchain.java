package blockchain.BTC_Blockchain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

public class Blockchain {
    private Blocks blocks = new Blocks();
    private Users users;
    private UTXOMap utxoMap = new UTXOMap();
    private Mempool mempool = new Mempool();

    private int satoshiInBTC = 100000000;
    private double txFeePercent = 0.5; // %
    private long blockReward = (long)50 * satoshiInBTC;
    private int txInBlock = 100;

    public Blockchain(int usersNumber){
        users = new Users(usersNumber);
    }

    public void mineBlock(String minerAddress){
        if (!users.contains(minerAddress)){
            throw new IllegalArgumentException("Miner address is wrong: where are no such address");
        }

        List<Transaction> transactions = mempool.getTx(txInBlock - 1);

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
        transactions.addFirst(new Transaction(minerInputs, minerOutput, "0"));
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
        //-------------------------------
        StringJoiner blockPrintScreen = new StringJoiner(System.lineSeparator());
        blockPrintScreen.add("-".repeat(80));
        blockPrintScreen.add("Block info:");
        blockPrintScreen.add("Block number:        " + blocks.getBlocksNumber());
        blockPrintScreen.add("Block hash:          " + blockHash);
        blockPrintScreen.add("Previous block hash: " + prevBlockHash);
        blockPrintScreen.add("Timestamp:           " + timestamp);
        blockPrintScreen.add("Nonce:               " + nonce);
        blockPrintScreen.add("Difficulty target:   " + difficultyTarget);
        blockPrintScreen.add("Transactions hash:   " + txHash);
        blockPrintScreen.add("List of transactions:");
        int counter = 0;
        for (Transaction tx : transactions){
            String senderAddress = tx.getSenderAddress();
            TxOutput mainOutput = tx.getOutputs().getFirst();
            String receiverAddress = mainOutput.getAddress();
            String receiverName = users.getUser(receiverAddress).getName();
            String senderName;
            if (senderAddress.equals("0")){
                senderName = "Coinbase";
            } else {
                senderName = users.getUser(senderAddress).getName();
            }
            blockPrintScreen.add(counter + ". " + tx.getTxid() + " | " + senderName + " -> " + receiverName + " | "
                    + mainOutput.getValue() / (double)satoshiInBTC + " BTC");
            counter++;
        }
        blockPrintScreen.add("-".repeat(80));
        System.out.println(blockPrintScreen);
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

        Transaction newTx = new Transaction(inputs, outputs, senderAddress);
        mempool.addTx(newTx);

        //-----------------------------------------

        StringJoiner txCreatingPrint = new StringJoiner(System.lineSeparator());
        txCreatingPrint.add("-".repeat(80));
        txCreatingPrint.add("Transaction info:");
        txCreatingPrint.add("Transaction hash: " + newTx.getTxid());
        String senderName = users.getUser(senderAddress).getName();
        txCreatingPrint.add("Sender: " + senderName);
        String receiverName = users.getUser(receiverAddress).getName();
        txCreatingPrint.add("Receiver: " + receiverName);
        txCreatingPrint.add("Inputs:");
        for (TxInput input : inputs){
            txCreatingPrint.add(input.getValue() / (double)satoshiInBTC + " BTC");
        }
        txCreatingPrint.add("Outputs:");
        txCreatingPrint.add("Main: " + outputs.getFirst().getValue() / (double)satoshiInBTC + " BTC");
        if (outputs.size() > 1) {
            txCreatingPrint.add("Remainder: " + outputs.getFirst().getValue() / (double) satoshiInBTC + " BTC");
        }
        txCreatingPrint.add("-".repeat(80));
        System.out.println(txCreatingPrint);
    }

    public void transactionGenerating(int txNumberToReach){
        if (txNumberToReach < 0) {
            throw new IllegalArgumentException("txNumberToReach can not be negative");
        }
        String satoshi = users.getAddress("Satoshi Nakamoto");
        while(true){
            mineBlock(satoshi);
            for (String senderAddress : utxoMap.getUsersAddressesWithUTXO()){
                String receiverAddress = users.getRandomUserAddress();
                while(receiverAddress.equals(senderAddress)){
                    receiverAddress = users.getRandomUserAddress();
                }
                double valueBTC = utxoMap.getUTXOSum(senderAddress) / (double)satoshiInBTC / 2;
                createTx(senderAddress, receiverAddress, valueBTC);
                if (mempool.getTxNumber() == txNumberToReach){
                    return;
                }
            }
        }
    }

    public void autoMining(){
        String satoshi = users.getAddress("Satoshi Nakamoto");
        while (mempool.getTxNumber() > 0){
            mineBlock(satoshi);
        }
    }
}
