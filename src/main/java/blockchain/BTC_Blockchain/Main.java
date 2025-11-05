package blockchain.BTC_Blockchain;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain(1000);

        blockchain.transactionGenerating(10000);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press enter to continue");
        scanner.nextLine();
        blockchain.autoMining();
    }
}
