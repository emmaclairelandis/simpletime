package com.emmaclairelandis.zundatracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMNS = 1;
    private static final int COPY_FILE_REQUEST_CODE = 2;

    private String filename = "data.json";
    private String content = "{\n" +
            "  \"timers\" : { }\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout grid = findViewById(R.id.timerGrid);
        grid.setColumnCount(COLUMNS);

        int timerCount = getTimerCount(this);
        createTimerButtons(grid, timerCount);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        Button kebabButton = findViewById(R.id.button);
        kebabButton.setOnClickListener(v -> showKebabMenu((Button) v));
    }

    // ---------------- KEBAB MENU ----------------

    private void showKebabMenu(Button anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.export_button) {
                copyFileToUserLocation(filename);
                return true;
            }

            if (id == R.id.create_button) {
                showCreateTimerDialog();
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
                        TimerManager.newTimer(timerName);
                        recreate(); // refresh UI
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- TIMER BUTTONS ----------------

    private void createTimerButtons(GridLayout grid, int count) {
        grid.removeAllViews();

        for (int i = 0; i < count; i++) {
            final int timerIndex = i;

            Button button = new Button(this);
            button.setText(String.valueOf(i + 1));
            button.setTextSize(14);
            button.setAllCaps(false);
            button.setBackgroundResource(R.drawable.timer_button_circle);
            button.setPadding(0, 0, 0, 0);
            button.setBackgroundTintList(null);

            button.setOnClickListener(v -> onTimerButtonPressed(timerIndex));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(16, 16, 16, 16);
            button.setLayoutParams(params);

            grid.addView(button);
        }
    }

    private void onTimerButtonPressed(int timerIndex) {
        Toast.makeText(this, "Timer " + (timerIndex + 1), Toast.LENGTH_SHORT).show();
    }

    // ---------------- FILE EXPORT ----------------

    private void createInternalFile() {
        try (FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE)) {
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyFileToUserLocation(String suggestedFileName) {
        createInternalFile();

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, suggestedFileName);

        startActivityForResult(intent, COPY_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COPY_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                File srcFile = new File(getFilesDir(), filename);
                copyFileToUri(srcFile, data.getData());
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
        }
    }

    // ---------------- TIMER COUNT ----------------

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
}
