// https://www.youtube.com/watch?v=JFc_8oq7yLM

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Data {

    public static void dataCollection() {
        String fileName = "numbers.csv";

        //writeToCSVFile(fileName);
        readCSVFile(fileName);
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
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line = "";

            while ( (line = br.readLine()) != null ) {
                System.out.println(line);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error reading file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("File could not be closed.");
            e.printStackTrace();
        }
    }
}
