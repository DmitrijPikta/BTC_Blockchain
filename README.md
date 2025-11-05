# BTC Blockchain v0.1
## Description
In this repository I have create my prototype of BTC blockchain. Also like hash function was taken custom hash generator.
## How it works?
### Constructor
```
public Blockchain(int usersNumber)
```
Constructor takes number of users to create. Users are creating with names: User1, User2, User3 and so. Also always created user - Satoshi Nakamoto.

### Manual methods
Where are 2 main manual methods:
```
 public void mineBlock(String minerAddress)
```
mineBlock methods generate new block and gives block rewards and fee to minerAddress. It put to block as maximum 100 transactions (it is default variable - txInBlock), but if where are not that quantity of transactions in mempool, it reduce transactions number in block.
After block is mined and added to blockchain, in terminal you will see it's information.

<img width="955" height="438" alt="Screenshot 2025-11-06 002638" src="https://github.com/user-attachments/assets/d518729d-682f-4f1c-a0e1-62b1718ee065" />


```
public void createTx(String senderAddress, String receiverAddress, double valueBTC)
```
createTx method creates new transaction and put it to mempool. It takes senderAddress, receiverAddress and valueBTC. Where are fullly validation that addressed are without mistakes and sender have enought coins(value in UTXO) for making this transaction. 
After creating new transaction, in terminal you will see it's information. 

<img width="671" height="235" alt="image" src="https://github.com/user-attachments/assets/066e82b5-0a24-4757-b3d1-632bbbe871f7" />

### Auto methods
```
public void generateTransaction(int txNumberToReach)
```
generateTransaction method generates blocks and transactions, until mempool have txNumberToReach of transactions.

```
public void autoMining()
```
autoMining method created new blocks for blockchain, until mempool do not have transactions.

## Interesting solutions
1. Implemented UTXO model
2. Implemented inputs and outputs 
3. Implemeted block reward and transactions fee for miner
4. Implemented user address creating be hashing public key
5. Implemented BTC transformation to Satoshi for storing in blockchain and inside countings 


