package blockchain.BTC_Blockchain;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Blockchain blockchain = new Blockchain(1000);
        System.out.print("How much transactions in mempool you want to generate? ");
        int txNumberToReach;
        while(true) {
            try {
                txNumberToReach = scanner.nextInt();
                if (txNumberToReach < 0) {
                    System.out.println("Number can not be negative. Try once again");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("It is not number. Try again");
                scanner.nextLine();
            }
        }
        scanner.nextLine();

        blockchain.transactionGenerating(txNumberToReach);
        System.out.println("Press enter to continue");
        scanner.nextLine();
        blockchain.autoMining();
    }
}
