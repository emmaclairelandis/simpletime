package com.emmaclairelandis.zundatracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMNS = 1;
    private static final int COPY_FILE_REQUEST_CODE = 2;
    private final String filename = "data.json";
    // Timer HH:MM:SS stuff
    private final Handler timerHandler = new Handler();
    private final Map<String, TextView> timerViews = new HashMap<>();
    private final Map<String, Long> runningStartTimes = new HashMap<>();


    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ✅ Use the activity layout, not the menu
        setContentView(R.layout.activity_main);

        ensureDataFileExists();

        GridLayout grid = findViewById(R.id.timerGrid);
        grid.setColumnCount(COLUMNS);


        createTimerButtons(grid);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        // Kebab menu button
        Button kebabButton = findViewById(R.id.button);
        kebabButton.setOnClickListener(v -> showKebabMenu((Button) v));
    }

    // ---------------- KEBAB MENU ----------------
    private void showKebabMenu(Button anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.create_button) {
                showCreateTimerDialog();
                return true;
            }

            if (id == R.id.delete_button) {
                showDeleteTimerDialog();
                return true;
            }

            if (id == R.id.export_button) {
                copyFileToUserLocation(filename);
                return true;
            }

            if (id == R.id.about_button) {
                gotoUrl("https://github.com/zundatracker/zundatracker");
                return true;
            }

            return false;
        });

        popupMenu.show();
    }

    private void showCreateTimerDialog() {
        EditText input = new EditText(this);
        input.setHint("Timer name");

        new AlertDialog.Builder(this)
                .setTitle("Create Timer")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String timerName = input.getText().toString().trim();
                    if (!timerName.isEmpty()) {
                        TimerManager.newTimer(MainActivity.this, timerName);
                        recreate(); // refresh UI
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteTimerDialog() {
        EditText input = new EditText(this);
        input.setHint("Timer name to delete");

        new AlertDialog.Builder(this)
                .setTitle("Delete Timer")
                .setView(input)
                .setPositiveButton("Delete", (dialog, which) -> {
                    String timerName = input.getText().toString().trim();
                    if (!timerName.isEmpty()) {
                        TimerManager.deleteTimer(MainActivity.this, timerName);
                        recreate(); // refresh UI so deleted timer disappears
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    // ---------------- TIMER BUTTONS ----------------
    // ---------------- TIMER BUTTONS ----------------
    private void createTimerButtons(GridLayout grid) {
        grid.removeAllViews();
        grid.setColumnCount(2);
        grid.setUseDefaultMargins(false);

        timerViews.clear();
        runningStartTimes.clear();

        ObjectMapper mapper = new ObjectMapper();
        File file = new File(getFilesDir(), filename);

        try {
            ObjectNode root = (ObjectNode) mapper.readTree(file);
            ObjectNode timers = (ObjectNode) root.get("timers");

            Iterator<String> names = timers.fieldNames();
            while (names.hasNext()) {
                String timerName = names.next();

                // -------- LEFT: TIMER NAME (TEXT-ONLY BUTTON) --------
                Button nameButton = new Button(this);
                nameButton.setText(timerName);
                nameButton.setAllCaps(false);
                nameButton.setBackground(null);
                nameButton.setTextColor(0xFFFFFFFF);
                nameButton.setTextSize(20);

                // kill default button padding / height
                nameButton.setPadding(0, 0, 0, 0);
                nameButton.setMinHeight(0);
                nameButton.setMinimumHeight(0);

                nameButton.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                nameButton.setOnClickListener(v -> onTimerButtonPressed(timerName));

                GridLayout.LayoutParams nameLp = new GridLayout.LayoutParams();
                nameLp.width = 0;
                nameLp.columnSpec = GridLayout.spec(0, 1f);
                nameLp.setMargins(16, 16, 8, 16);
                nameButton.setLayoutParams(nameLp);

                // -------- RIGHT: HH:MM:SS --------
                TextView timeView = new TextView(this);
                timeView.setText("00:00:00");
                timeView.setTextColor(0xFFFFFFFF);
                timeView.setTextSize(20);

                timeView.setPadding(0, 0, 0, 0);
                timeView.setMinHeight(0);

                timeView.setGravity(Gravity.END);
                timeView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

                GridLayout.LayoutParams timeLp = new GridLayout.LayoutParams();
                timeLp.width = 0;
                timeLp.columnSpec = GridLayout.spec(1, 1f);
                timeLp.setMargins(8, 8, 16, 8);
                timeView.setLayoutParams(timeLp);

                timerViews.put(timerName, timeView);

                // -------- CHECK IF RUNNING --------
                ObjectNode sessions = (ObjectNode) timers.get(timerName).get("sessions");
                Long start = TimerManager.getRunningStartTime(sessions);
                if (start != null) {
                    runningStartTimes.put(timerName, start);
                }

                // -------- ADD TO GRID --------
                grid.addView(nameButton);
                grid.addView(timeView);

                // -------- DIVIDER (NO EXTRA HEIGHT) --------
                View divider = new View(this);
                divider.setBackgroundColor(0xFF2A2A2A);

                GridLayout.LayoutParams divLp = new GridLayout.LayoutParams();
                divLp.width = GridLayout.LayoutParams.MATCH_PARENT;
                divLp.height = 1;
                divLp.columnSpec = GridLayout.spec(0, 2);
                divider.setLayoutParams(divLp);

                grid.addView(divider);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        startTicker();
    }


    private void addDivider(GridLayout grid) {
        View divider = new View(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.MATCH_PARENT;
        params.height = 1;
        params.setMargins(0, 8, 0, 8);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(0xFF2A2A2A);
        grid.addView(divider);
    }

    private void startTicker() {
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis() / 1000;

                for (String name : timerViews.keySet()) {
                    TextView view = timerViews.get(name);
                    Long start = runningStartTimes.get(name);

                    if (start != null) {
                        long elapsed = now - start;
                        view.setText(formatTime(elapsed));
                    } else {
                        view.setText("00:00:00");
                    }
                }

                timerHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private String formatTime(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }


    private void onTimerButtonPressed(String timerName) {
        boolean started = TimerManager.startTimer(this, timerName);

        if (started) {
            runningStartTimes.put(timerName, System.currentTimeMillis() / 1000);
            return;
        }

        boolean stopped = TimerManager.stopTimer(this, timerName);
        if (stopped) {
            runningStartTimes.remove(timerName);
        }
    }


    private static ObjectNode getMostRecentSession(ObjectNode sessionsNode) {
        int max = -1;
        ObjectNode last = null;

        Iterator<String> it = sessionsNode.fieldNames();
        while (it.hasNext()) {
            String key = it.next();
            try {
                int n = Integer.parseInt(key);
                if (n > max) {
                    max = n;
                    last = (ObjectNode) sessionsNode.get(key);
                }
            } catch (NumberFormatException ignored) {}
        }
        return last;
    }


    // ---------------- FILE EXPORT ----------------
    public void copyFileToUserLocation(String suggestedFileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, suggestedFileName);

        startActivityForResult(intent, COPY_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COPY_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                File srcFile = new File(getFilesDir(), filename);
                Uri destUri = data.getData();
                copyFileToUri(srcFile, destUri);
            }
        }
    }

    private void copyFileToUri(File srcFile, Uri destUri) {
        try (FileInputStream in = new FileInputStream(srcFile);
             OutputStream out = getContentResolver().openOutputStream(destUri)) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            Toast.makeText(this, "Export successful", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export file", Toast.LENGTH_LONG).show();
        }
    }

    // ---------------- DATA.JSON MANAGEMENT ----------------
    private void ensureDataFileExists() {
        File file = new File(getFilesDir(), "data.json");
        if (!file.exists()) createInternalFile();
    }

    private void createInternalFile() {
        File file = new File(getFilesDir(), "data.json");
        if (file.exists()) return;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.set("timers", mapper.createObjectNode());

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- TIMER STUFF ----------------
    public static int getTimerCount(Context context) {
        File file = new File(context.getFilesDir(), "data.json");
        if (!file.exists() || file.length() == 0) return 0;

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(file);
            JsonNode timers = root.path("timers");
            return timers.isObject() ? timers.size() : 0;
        } catch (IOException e) {
            return 0;
        }
    }

    private List<String> getTimerNames(Context context) {
        List<String> names = new ArrayList<>();
        File file = new File(context.getFilesDir(), "data.json");

        if (!file.exists() || file.length() == 0) return names;

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(file);
            JsonNode timers = root.path("timers");

            if (timers.isObject()) {
                Iterator<String> fieldNames = timers.fieldNames();
                while (fieldNames.hasNext()) {
                    names.add(fieldNames.next());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return names;
    }

    private View createDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(0xFF181a1d);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.MATCH_PARENT;
        params.height = 1;
        params.setMargins(0, 8, 0, 8);

        divider.setLayoutParams(params);
        return divider;
    }


    // ---------------- OPEN URL ----------------
    private void gotoUrl(String s) {
        try {
            Uri uri = Uri.parse(s);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Exception e) {
            Toast.makeText(this, "Error opening website.", Toast.LENGTH_SHORT).show();
        }
    }
}
