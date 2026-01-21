package com.emmaclairelandis.zundatracker;

import android.content.Context;
//Might want to swap this for something more robust
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TimerManager {
    //static File dataFile = new File("data.json"); // The variable to refer to our little data.json file
    private static final String FILE_NAME = "data.json";

    public static boolean startTimer(Context context, String timerName) {
        File dataFile = new File(context.getFilesDir(), FILE_NAME);
        ObjectMapper mapper = new ObjectMapper();

        try {
            ObjectNode root = (ObjectNode) mapper.readTree(dataFile);
            ObjectNode timers = (ObjectNode) root.get("timers");
            ObjectNode timerNode = (ObjectNode) timers.get(timerName);

            if (timerNode == null) return false;

            ObjectNode sessions = (ObjectNode) timerNode.get("sessions");
            if (sessions == null) {
                sessions = mapper.createObjectNode();
                timerNode.set("sessions", sessions);
            }

            ObjectNode last = getMostRecentSession(sessions);
            if (last != null && last.has("start") && !last.has("stop")) {
                return false; // already running
            }

            int next = 1;
            Iterator<String> it = sessions.fieldNames();
            while (it.hasNext()) {
                next = Math.max(next, Integer.parseInt(it.next()) + 1);
            }

            ObjectNode session = mapper.createObjectNode();
            session.put("start", System.currentTimeMillis() / 1000);
            sessions.set(String.valueOf(next), session);

            mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, root);
            return true;

        } catch (IOException e) {
            Log.e("TimerManager", "startTimer failed", e);
            return false;
        }
    }


    public static boolean stopTimer(Context context, String timerName) {
        File dataFile = new File(context.getFilesDir(), FILE_NAME);
        ObjectMapper mapper = new ObjectMapper();

        try {
            ObjectNode root = (ObjectNode) mapper.readTree(dataFile);
            ObjectNode timers = (ObjectNode) root.get("timers");
            ObjectNode timerNode = (ObjectNode) timers.get(timerName);

            if (timerNode == null) return false;

            ObjectNode sessions = (ObjectNode) timerNode.get("sessions");
            if (sessions == null || sessions.size() == 0) return false;

            ObjectNode last = getMostRecentSession(sessions);
            if (last.has("start") && !last.has("stop")) {
                last.put("stop", System.currentTimeMillis() / 1000);
                mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, root);
                return true;
            }

            return false; // not running

        } catch (IOException e) {
            Log.e("TimerManager", "stopTimer failed", e);
            return false;
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



    public static void deleteTimer(Context context, String timerName) {
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
                // Nothing to delete
                return;
            }

            if (timersNode.has(timerName)) {
                timersNode.remove(timerName);
                // Write updated JSON back
                mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, root);
                System.out.println("Timer \"" + timerName + "\" deleted successfully!");
            } else {
                System.out.println("Timer \"" + timerName + "\" not found.");
            }

        } catch (IOException e) {
            Log.e("ZundaTracker", "Failed to export file", e);
        }
    }



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
            // Sends the logs to logcat, not to the user or any persistent file
            Log.e("ZundaTracker", "Failed to export file", e);
        }
    }


    public static Long getRunningStartTime(ObjectNode sessions) {
        if (sessions == null || sessions.size() == 0) return null;

        ObjectNode last = getMostRecentSession(sessions);
        if (last != null && last.has("start") && !last.has("stop")) {
            return last.get("start").asLong();
        }
        return null;
    }


}
