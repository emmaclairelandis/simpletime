package com.emmaclairelandis.zundatracker;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode; 

public class TimerManager {
    static Scanner scanner;
    //static File dataFile = new File("data.json"); // The variable to refer to our little data.json file
    private static final String FILE_NAME = "data.json";
    /*
    public static void startTimer() {
        ObjectMapper mapper = new ObjectMapper();
    
            // Which timer do you want to start
            String timerName = scanner.nextLine();

            ObjectNode sessionsNode = (ObjectNode) timerNode.get("sessions");
            if (sessionsNode == null) {
                sessionsNode = mapper.createObjectNode();
                timerNode.set("sessions", sessionsNode);
            }
    
            // Check if most recent session is running
            ObjectNode lastSession = getMostRecentSession(sessionsNode);
            if (lastSession != null && lastSession.has("start") && !lastSession.has("stop")) {
                System.out.println("Timer is already running!");
                System.out.print("Please press enter to return to the main menu...");
                scanner.nextLine();
                return;
            }
    
            // Determine next session number
            int nextSession = 1;
            Iterator<String> fieldNames = sessionsNode.fieldNames();
            while (fieldNames.hasNext()) {
                String name = fieldNames.next();
                try {
                    int n = Integer.parseInt(name);
                    if (n >= nextSession) nextSession = n + 1;
                } catch (NumberFormatException e) { }
            }
    
            // Create new session
            ObjectNode newSession = mapper.createObjectNode();
            long unixTime = System.currentTimeMillis() / 1000;
            newSession.put("start", unixTime);
            sessionsNode.set(String.valueOf(nextSession), newSession);
    
            // Write to file
            mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, root);
            System.out.println("Timer started successfully.");
            System.out.print("Please press enter to return to the main menu...");
            scanner.nextLine();
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void stopTimer() {
        if (!dataFile.exists()) {
            System.out.println("You currently have no timers.");
            System.out.print("\nPress enter to return to the main menu...");
            scanner.nextLine();
            return;
        }
    
        ObjectMapper mapper = new ObjectMapper();
    
        try {
            // Read root JSON
            ObjectNode root = (ObjectNode) mapper.readTree(dataFile);
    
            // Get timers
            ObjectNode timersNode = (ObjectNode) root.get("timers");
            if (timersNode == null || !root.isObject()) {
                System.out.println("You currently have no timers.");
                System.out.print("Press enter to return to the main menu...");
                scanner.nextLine();
                return;
            }
    
            // Ask user which timer to stop
            System.out.print("Which timer would you like to stop: ");
            String timerName = scanner.nextLine();
    
            ObjectNode timerNode = (ObjectNode) timersNode.get(timerName);
            if (timerNode == null) {
                System.out.println("No timer with that name exists.");
                System.out.print("Press enter to return to the main menu...");
                scanner.nextLine();
                return;
            }
    
            ObjectNode sessionsNode = (ObjectNode) timerNode.get("sessions");
            if (sessionsNode == null || sessionsNode.size() == 0) {
                System.out.println("Timer has no sessions.");
                System.out.print("Press enter to return to the main menu...");
                scanner.nextLine();
                return;
            }
    
            // Check if last session is running
            ObjectNode lastSession = getMostRecentSession(sessionsNode);
            if (lastSession.has("start") && !lastSession.has("stop")) {
                long unixTime = System.currentTimeMillis() / 1000;
                lastSession.put("stop", unixTime);
    
                // Write back to file
                mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, root);
                System.out.println("Timer stopped successfully.");
                System.out.print("Press enter to return to the main menu...");
                scanner.nextLine();
            } else {
                System.out.println("Timer is not currently running!");
                System.out.print("Press enter to return to the main menu...");
                scanner.nextLine();
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    

    // Used when trying to figure out whether a timer is running or not 
    private static ObjectNode getMostRecentSession(ObjectNode sessionsNode) {
        int max = -1;
        ObjectNode lastSession = null;
        Iterator<String> fieldNames = sessionsNode.fieldNames();
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            try {
                int n = Integer.parseInt(name);
                if (n > max) {
                    max = n;
                    lastSession = (ObjectNode) sessionsNode.get(name);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return lastSession;
    }    

    public static void deleteTimer() {

        if(scanner.hasNextLine()) scanner.nextLine();

        System.out.print("Which timer would you like to delete: ");
        String timerName = scanner.nextLine();

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(new File("data/data.json"));

            ObjectNode rootObj = (ObjectNode) root;
            ObjectNode timersNode = (ObjectNode) rootObj.get("timers");

            timersNode.remove(timerName);

            mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, root);
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nTimer has successfully been deleted.");
        if(scanner.hasNextLine()) scanner.nextLine();
    }
    */
    public static void newTimer(Context context, String timerName) {

        ObjectMapper mapper = new ObjectMapper();
        File dataFile = new File(context.getFilesDir(), FILE_NAME);

        try {

            ObjectNode root;
            if (dataFile.exists() && dataFile.length() > 0) {
                root = (ObjectNode) mapper.readTree(dataFile);
            } else {
                root = mapper.createObjectNode();
            }

            ObjectNode timersNode;
            if (root.has("timers")) {
                timersNode = (ObjectNode) root.get("timers");
            } else {
                timersNode = mapper.createObjectNode();
                root.set("timers", timersNode);
            }

            ObjectNode timerNode = mapper.createObjectNode();
            timerNode.set("sessions", mapper.createObjectNode());

            timersNode.set(timerName, timerNode);

            mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
