package blockchain.BTC_Blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Users {
    private Map<String, User> users = new HashMap<>();

    public Users(int usersNumber){
        if(usersNumber < 0){
            throw new IllegalArgumentException("usersNumber can not be negative");
        }
        HashFunction hashFunction = new HashFunction();

        String name = "Satoshi Nakamoto";
        User newUser = new User(name);
        String newUserAddress = hashFunction.hashString(newUser.getPublicKey());
        users.put(newUserAddress, newUser);

        for (int i = 0; i < usersNumber; i++){
            name = "User" + i;
            newUser = new User(name);
            newUserAddress = hashFunction.hashString(newUser.getPublicKey());
            if (users.containsKey(newUserAddress)){
                i--;
                continue;
            }
            users.put(newUserAddress, newUser);
        }
    }

    public Map<String, User> getUsers(){
        return users;
    }

    public User getUser(String address){
        return users.get(address);
    }

    public boolean contains(String address){
        return users.containsKey(address);
    }

    public String getAddress(String username){
        return users.entrySet().stream().filter(u -> u.getValue().getName().equals("Satoshi Nakamoto"))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public String getRandomUserAddress(){
        Random random = new Random();
        int index = random.nextInt(users.size());
        return new ArrayList<>(users.keySet()).get(index);
    }
}
