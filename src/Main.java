//package src;
// Some useful tutorials I used (ΦωΦ)
// https://www.youtube.com/watch?v=wAEPokhj5Q4
// https://www.youtube.com/watch?v=49bIIa6id08
// https://www.youtube.com/watch?v=ScUJx4aWRi0

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Iterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode; 


public class Main {

    // Declare some variables
    static File dataFile = new File("data.json"); // The variable to refer to our little data.json file
    static Scanner scanner = new Scanner(System.in); // When you want the user to press enter to continue
    static int choice;
    static boolean isRunning = true;

    public static void main(String[] args) {
        while(isRunning) {
            clearConsole();
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
            clearConsole();

            switch(choice){
                case 1 -> startTimer();
                case 2 -> stopTimer();
                case 3 -> manageTimer();
                case 4 -> helpMenu();
                case 5 -> isRunning = false;
            }


            
        }

    }

    public static void startTimer() {
        clearConsole();
        if (dataFile.exists()) {
            clearConsole();

            if(scanner.hasNextLine()) scanner.nextLine();

            System.out.println("You have the following timers: \n");

            /*
            if (timersNode.isObject()) {
                ObjectNode timersObj = (ObjectNode) timersNode;
                timersObj.fieldNames().forEachRemaining(name -> System.out.println("- " + name));
            }
            */

            System.out.print("\nWhich timer would you like to start: ");
            String timerName = scanner.nextLine();

            ObjectMapper mapper = new ObjectMapper();

            try {
                ObjectNode root = (ObjectNode) mapper.readTree(dataFile);

                ObjectNode timersNode = (ObjectNode) root.get("timers");

                // I don't know if this is required for the code to run 
                if (timersNode == null) {
                    timersNode = mapper.createObjectNode();
                    root.set("timers", timersNode);
                }

                // I don't know why we have this many if-statements but things break if I don't put them?????
                if (timersNode.isObject()) {
                    ObjectNode timersObj = (ObjectNode) timersNode;
                    timersObj.fieldNames().forEachRemaining(name -> System.out.println("- " + name));
                }

                ObjectNode timerNode = (ObjectNode) timersNode.get(timerName);
                if (timerNode == null) {
                    clearConsole();
                    System.out.println("You currently have no timer with that name.");
                    System.out.print("\nPlease press enter to return to the main menu...");
                    if(scanner.hasNextLine()) scanner.nextLine();
                    return;
                }

                ObjectNode sessionsNode = (ObjectNode) timerNode.get("sessions");
                if (sessionsNode == null) {
                    sessionsNode = mapper.createObjectNode();
                    timerNode.set("sessions", sessionsNode);
                }

                // Which session are we on
                int nextSession = 1;
                Iterator<String> fieldNames = sessionsNode.fieldNames();
                while (fieldNames.hasNext()) {
                    String name = fieldNames.next();
                    try {
                        int n = Integer.parseInt(name);
                        if (n >= nextSession) {
                            nextSession = n + 1;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                // Create a new session based on which number we're on
                ObjectNode newSession = mapper.createObjectNode();
                long unixTime = System.currentTimeMillis() / 1000;
                newSession.put("start", unixTime);

                sessionsNode.set(String.valueOf(nextSession), newSession);

                // Write it out :3c
                mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, root);


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("You currently have no timers.");
            System.out.print("\nPlease press enter to return to the main menu...");

            scanner.nextLine();
            if(scanner.hasNextLine()) scanner.nextLine();
        }
        clearConsole();
    }

    public static void stopTimer() {
        clearConsole();
        if (dataFile.exists()) {
            return;
        } else {
            System.out.println("You currently have no timers.");
            System.out.print("\nPlease press enter to return to the main menu...");

            scanner.nextLine();
            if(scanner.hasNextLine()) scanner.nextLine();
        }
        clearConsole();
    }

    public static void manageTimer() {
        clearConsole();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root;

        try {
            
            // If there is no data file, then let's make it. Not sure if this should go higher up or if it's fine in this little method...
            // https://stackoverflow.com/questions/9620683/java-fileoutputstream-create-file-if-not-exists
            dataFile.createNewFile(); // Won't do anything if the file already exists :3c

            // Read JSON from file if it has content, otherwise create empty root
            if (dataFile.length() > 0) {
                root = (ObjectNode) mapper.readTree(dataFile);
            } else {
                root = mapper.createObjectNode();
            }

            // Check if "timers" exists and has entries
            JsonNode timersNode = root.get("timers");
            if (timersNode == null || timersNode.isEmpty()) {
                System.out.println("You currently have no timers.");
                System.out.println("Would you like to create one?");
                System.out.println("\n");
                System.out.println("1. Yes");
                System.out.println("2. No\n");

                choice = scanner.nextInt();
                switch(choice){
                    case 1 -> newTimer();
                }
            } else {
                // There are already timers
                System.out.println("You currently have the following timers:\n");

                if (timersNode.isObject()) {
                    ObjectNode timersObj = (ObjectNode) timersNode;
                    timersObj.fieldNames().forEachRemaining(name -> System.out.println("- " + name));
                }

                System.out.println("\n1. Create Timer");
                System.out.println("2. Delete Timer");
                System.out.println("3. Back\n");

                choice = scanner.nextInt();
                switch(choice){
                    case 1 -> newTimer();
                    case 2 -> deleteTimer();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTimer() {
        clearConsole();

        if(scanner.hasNextLine()) scanner.nextLine();

        System.out.print("Which timer would you like to delete: ");
        String timerName = scanner.nextLine();

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(new File("data.json"));

            ObjectNode rootObj = (ObjectNode) root;
            ObjectNode timersNode = (ObjectNode) rootObj.get("timers");

            timersNode.remove(timerName);

            mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, root);
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nTimer has successfully been deleted.");
        if(scanner.hasNextLine()) scanner.nextLine();
        clearConsole();
    }

    public static void newTimer() {
        clearConsole();

        if(scanner.hasNextLine()) scanner.nextLine();

        System.out.print("What would you like to name your timer: ");
        String timerName = scanner.nextLine();

        ObjectMapper mapper = new ObjectMapper();

        try {

            // Read existing JSON if file exists, otherwise create root
            ObjectNode root;
            if (dataFile.exists() && dataFile.length() > 0) {
                root = (ObjectNode) mapper.readTree(dataFile);
            } else {
                root = mapper.createObjectNode();
            }
        
            // Ensure "timers" node exists
            ObjectNode timersNode;
            if (root.has("timers")) {
                timersNode = (ObjectNode) root.get("timers");
            } else {
                timersNode = mapper.createObjectNode();
                root.set("timers", timersNode);
            }
        
            // Create the new timer node
            ObjectNode timerNode = mapper.createObjectNode();
            ObjectNode sessionsNode = mapper.createObjectNode(); // empty sessions for now
            timerNode.set("sessions", sessionsNode);
        
            // Add the timer to timers
            timersNode.set(timerName, timerNode);
        
            // Write back to file
            mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, root);
        
            clearConsole();
            System.out.println("Timer \"" + timerName + "\" created successfully!");
            System.out.print("\nPlease press enter to return to the main menu...");
            if(scanner.hasNextLine()) scanner.nextLine();
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
    public static void helpMenu() {
        clearConsole();
        System.out.println("There is no help for you! GAHAHAHAHAHAH!");
        System.out.print("\nPlease press enter to return to the main menu...");
        if(scanner.hasNextLine()) scanner.nextLine(); // If we don't put this then it just skips. I don't know why 
        scanner.nextLine();
        clearConsole();
    }
        

    private static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback: print newlines
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }

}
