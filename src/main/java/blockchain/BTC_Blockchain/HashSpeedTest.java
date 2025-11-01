package blockchain.BTC_Blockchain;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class HashSpeedTest {
    public List<Long> test(String filename){
        FileReader reader = new FileReader();
        String data = reader.readFile(filename);
        if (data == null){
            throw new IllegalArgumentException("File not found");
        }

        // To not include in test first call factor
        getHashingTime("");

        String[] dataLines = data.split(System.lineSeparator());
        int linesNumber = dataLines.length;

        List<Long> testSpeeds = new ArrayList<>();
        for (int i = 1; i < linesNumber; i *= 2){
            StringJoiner testString = new StringJoiner(System.lineSeparator());
            for (int j = 0; j < i; j++){
                testString.add(dataLines[j]);
            }
            testSpeeds.add(getHashingTime(testString.toString()));
        }
        return testSpeeds;
    }

    private long getHashingTime(String input){
        String salt = "vdsvdfvregthtr";
        HashFunction hasher = new HashFunction();
        Timer timer = new Timer();
        return timer.measure(() -> hasher.hashString(input, salt));
    }
}
