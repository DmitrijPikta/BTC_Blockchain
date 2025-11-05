# BTC Blockchain v0.1
## Description
In this repository I have create my prototype of BTC blockchain. Also like hash function was taken custom hash generator.
## How it works?
### Constructor
```
public Blockchain(int usersNumber){}
```
Constructor takes number of users to create. Users are creating with names: User1, User2, User3 and so. Also always created user - Satoshi Nakamoto.

### Manual methods
Where are 2 main manual methods:
```
 public void mineBlock(String minerAddress){}
```
mineBlock methods generate new block and gives block rewards and fee to minerAddress. It put to block as maximum 100 transactions (it is default variable - txInBlock), but if where are not that quantity of transactions in mempool, it reduce transactions number in block.
After block is mined and added to blockchain, in terminal you will see it's information.

<img width="955" height="438" alt="Screenshot 2025-11-06 002638" src="https://github.com/user-attachments/assets/d518729d-682f-4f1c-a0e1-62b1718ee065" />


```
public void createTx(String senderAddress, String receiverAddress, double valueBTC){}
```
createTx method creates new transaction and put it to mempool. It takes senderAddress, receiverAddress and valueBTC. Where are fullly validation that addressed are without mistakes and sender have enought coins(value in UTXO) for making this transaction. 
After creating new transaction, in terminal you will see it's information. 
