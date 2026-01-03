package blockchain.BTC_Blockchain;

import java.util.*;

public class UTXOMap {
    private Map<String, List<UTXO>> utxoMap = new HashMap<>();  //Map<UserAddress, UTXO>

    public List<UTXO> getUsersUTXO(String address){
        return utxoMap.get(address);
    }

    public long getUTXOSum(String address){
        List<UTXO> userUTXOList = utxoMap.get(address);
        long sum = 0;
        for (UTXO utxo : userUTXOList){
            sum += utxo.getValue();
        }
        return sum;
    }

    public List<String> getUsersAddressesWithUTXO(){
        List<String> addresses = new ArrayList<>();
        for (String key : utxoMap.keySet()){
            if (!utxoMap.get(key).isEmpty()){
                addresses.add(key);
            }
        }
        return addresses;
    }

    public void addUTXO(String address, UTXO newUTXO){
        utxoMap.computeIfAbsent(address, k -> new LinkedList<>()).add(newUTXO);
    }

    public void removeLastUsersUTXO(String address){
        utxoMap.get(address).removeLast();
    }
}
