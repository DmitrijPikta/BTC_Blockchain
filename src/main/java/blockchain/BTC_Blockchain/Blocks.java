package blockchain.BTC_Blockchain;

import java.util.ArrayList;
import java.util.List;

public class Blocks {
    private List<Block> blocks = new ArrayList<>();

    public void addNewBlock(Block newBlock){
        int blocksSize = blocks.size();
        if (blocksSize > 0){
            if (!getLastBlockHash().equals(newBlock.getPrevBlockHash())){
                throw new IllegalArgumentException("New block is not accepted: previous block hash is wrong");
            }
        } else {
            if (!newBlock.getPrevBlockHash().isEmpty()){
                throw new IllegalArgumentException("New block is not accepted: previous block hash for first block should be empty");
            }
        }
        blocks.add(newBlock);
    }

    public String getLastBlockHash(){
        if (blocks.isEmpty()){
            return "";
        }
        return blocks.getLast().getBlockHash();
    }

    public int getBlocksNumber(){
        return blocks.size();
    }
}
