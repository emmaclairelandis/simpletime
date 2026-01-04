import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Data {

    public static void dataCollection() {
        String fileName = "numbers.csv";

        writeToCSVFile(fileName);
    }

    private static void writeToCSVFile(String fileName) {
        //System.out.println(System.currentTimeMillis() / 1000L);
        try (PrintWriter pw = new PrintWriter(fileName)) {
            
            pw.write(12 + ",");
            pw.write(25 + ",");
            pw.write(50 + ",");

            System.out.println("Finished writing to file.");


        } catch (FileNotFoundException e) {
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }
    }

    private static void readCSVFile(String fileName) {

    }
}
