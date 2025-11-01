package blockchain.BTC_Blockchain;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

public class FileReader {
    public String readFile(String filename){
        StringJoiner data = new StringJoiner(System.lineSeparator());
        File file = new File(filename);
        try (Scanner reader = new Scanner(file)){
            while (reader.hasNextLine()){
                data.add(reader.nextLine());
            }
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
            return null;
        }
        return data.toString();
    }

    public List<List<String>> readCsvFile(String filename){
        List<List<String>> linesList = new ArrayList<>();
        File file = new File(filename);
        try (Scanner reader = new Scanner(file)){
            while (reader.hasNextLine()){
                linesList.add(getRecordFromLine(reader.nextLine()));
            }
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
            return null;
        }
        return linesList;
    }

    private List<String> getRecordFromLine(String line){
        String[] records = line.split(",");
        return List.of(records);
    }
}
