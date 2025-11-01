package blockchain.BTC_Blockchain;
import java.io.IOException;

public class FileWriter {
    public void write(String filename, String data){
        try(java.io.FileWriter writer = new java.io.FileWriter(filename)) {
            writer.write(data);
        } catch (IOException e){
            System.out.println("An error occurred trying write to file");
            e.printStackTrace();
        }
    }
}
