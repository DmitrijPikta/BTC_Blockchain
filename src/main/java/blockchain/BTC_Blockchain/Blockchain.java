package blockchain.BTC_Blockchain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public void mineBlock(String minerAddress) {
        Block newBlock;
        try {
            newBlock = getBlock(minerAddress, false);
        } catch (InterruptedException e){
            return;
        }
        verifyBlock(newBlock);
    }

    public void mineBlock(List<String> addresses){
        if (addresses == null || addresses.isEmpty()){
            throw new IllegalArgumentException("Addresses can not be empty");
        }
        ExecutorService executor = Executors.newFixedThreadPool(
                Math.min(addresses.size(), Runtime.getRuntime().availableProcessors())
        );

        List<Callable<Block>> tasks = new ArrayList<>();
        for (String address : addresses){
            tasks.add(() -> getBlock(address, true));
        }
        Block minedBlock;
        try{
            minedBlock = executor.invokeAny(tasks);

        } catch (Exception e){
            e.printStackTrace();
            return;
        } finally {
            executor.shutdownNow();
        }
        if (minedBlock == null){
            throw new RuntimeException("Block was not mined");
        }
        verifyBlock(minedBlock);
    }

    public Block getBlock(String minerAddress, boolean parallel) throws InterruptedException {
        if (!users.contains(minerAddress)){
            throw new IllegalArgumentException("Miner address is wrong: where are no such address");
        }

        List<Transaction> transactions = mempool.getTx(txInBlock - 1);

        long fee = 0;
        for (Transaction tx : transactions){
            fee += tx.getFee();
        }
        // create tx for miner reward
        List<TxOutput> minerOutput = new ArrayList<>();
        minerOutput.add(new TxOutput(minerAddress, fee + blockReward));
        List<TxInput> minerInputs = new ArrayList<>();
        transactions.addFirst(new Transaction(minerInputs, minerOutput, "0"));


        HashFunction hashFunction = new HashFunction();
        MerkleTree merkleTree = new MerkleTree();
        String txHash = merkleTree.getRootHash(transactions);
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
            if (parallel && Thread.currentThread().isInterrupted()){
                throw new InterruptedException("Mining interrupted");
            }
        }
        if (parallel && Thread.currentThread().isInterrupted()){
            throw new InterruptedException("Mining interrupted");
        }
        return new Block(prevBlockHash, timestamp, nonce, difficultyTarget, transactions);
    }

    public void verifyBlock(Block newBlock){
        if (!newBlock.getBlockHash().startsWith("000")){
            throw new IllegalArgumentException("New block hash do not achieve difficulty target");
        }

        blocks.addNewBlock(newBlock);
        // create UTXO and delete made tx from mempool
        int i = 0;
        List<Transaction> transactions = newBlock.getTransactions();
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
        blockPrintScreen.add("Block hash:          " + newBlock.getBlockHash());
        blockPrintScreen.add("Previous block hash: " + newBlock.getPrevBlockHash());
        blockPrintScreen.add("Timestamp:           " + newBlock.getTimestamp());
        blockPrintScreen.add("Nonce:               " + newBlock.getNonce());
        blockPrintScreen.add("Difficulty target:   " + newBlock.getDifficultyTarget());
        blockPrintScreen.add("Transactions hash:   " + newBlock.getTxHash());
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
        long valueWithFeeSatoshi = valueSatoshi + (long)(valueSatoshi / (double)100 * txFeePercent);

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
            txCreatingPrint.add("Remainder: " + outputs.get(1).getValue() / (double)satoshiInBTC + " BTC");
        }
        txCreatingPrint.add("-".repeat(80));
        System.out.println(txCreatingPrint);
    }

    public void generateTransaction(int txNumberToReach){
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
                if (valueBTC == 0){
                    continue;
                }
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

    public void autoMiningParallel(){
        List<String> addresses = new ArrayList<>();
        addresses.add(users.getAddress("Satoshi Nakamoto"));
        addresses.add(users.getAddress("User1"));
        addresses.add(users.getAddress("User2"));
        addresses.add(users.getAddress("User3"));
        addresses.add(users.getAddress("User4"));
        while (mempool.getTxNumber() > 0){
            mineBlock(addresses);
        }
    }

    public String getUserAddress(String username) {
        return users.getAddress(username);
    }
}
