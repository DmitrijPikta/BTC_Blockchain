package blockchain.BTC_Blockchain;

import java.util.*;

public class UTXOMap {
    private Map<String, List<UTXO>> utxoMap = new HashMap<>();  //Map<UserAddress, UTXO>

    public List<UTXO> getUsersUTXO(String address){
        return utxoMap.get(address);
    }

    public void addUTXO(String address, UTXO newUTXO){
        utxoMap.computeIfAbsent(address, k -> new LinkedList<>()).add(newUTXO);
    }

    public void removeLastUsersUTXO(String address){
        utxoMap.get(address).removeLast();
    }
}
