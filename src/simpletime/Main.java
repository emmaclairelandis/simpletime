package simpletime;

// Some useful tutorials I used (ΦωΦ)
// https://www.youtube.com/watch?v=wAEPokhj5Q4
// https://www.youtube.com/watch?v=49bIIa6id08
// https://www.youtube.com/watch?v=ScUJx4aWRi0

// Not sure if we need some of these packages in main specifically 
import java.io.IOException;
import java.util.Scanner;
import java.util.Iterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode; 


public class Main {

    // Declare some variables
    // static File dataFile = new File("data/data.json"); // The variable to refer to our little data.json file
    static Scanner scanner = new Scanner(System.in); // When you want the user to press enter to continue
    static int choice;
    static boolean isRunning = true;

    public static void main(String[] args) {
        while(isRunning) {
            ConsoleUtils.clearConsole();
            System.out.println("Welcome to SimpleTime!");
            System.out.println("\nSimpleTime is a simple and open-source\ntime tracking software made in Java.\n");
            //System.out.println("\n");
            //System.out.println("Timers currently running:");
            //System.out.println(currentlyRunning);
            //System.out.println("\n");
            System.out.println("1. Start Timer");
            System.out.println("2. Stop Timer");
            System.out.println("3. Manage Timers");
            System.out.println("4. Help");
            System.out.println("5. Exit");
            //System.out.println("\n");

            System.out.print("\nEnter your choice (1-5): ");
            choice = scanner.nextInt();
            ConsoleUtils.clearConsole();

            switch(choice){
                case 1 -> TimerManager.startTimer();
                case 2 -> TimerManager.stopTimer();
                case 3 -> TimerManager.manageTimer();
                case 4 -> helpMenu();
                case 5 -> isRunning = false;
            } 
        }
    }
        
    public static void helpMenu() {
        ConsoleUtils.clearConsole();
        System.out.println("There is no help for you! GAHAHAHAHAHAH!");
        System.out.print("\nPlease press enter to return to the main menu...");
        if(scanner.hasNextLine()) scanner.nextLine(); // If we don't put this then it just skips. I don't know why 
        scanner.nextLine();
        ConsoleUtils.clearConsole();
    }
        
}
