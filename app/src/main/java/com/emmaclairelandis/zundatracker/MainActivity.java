package com.emmaclairelandis.zundatracker;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;


public class MainActivity extends AppCompatActivity {

    private static final int COLUMNS = 4;
    private int timerCount = 17;
    private static final int COPY_FILE_REQUEST_CODE = 2;
    private String filename = "data.json";
    private String content = "testing1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //timerCount = timerCount(this);

        GridLayout grid = findViewById(R.id.timerGrid);
        grid.setColumnCount(COLUMNS);

        createTimerButtons(grid, timerCount);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void createTimerButtons(GridLayout grid, int count) {
        grid.removeAllViews();

        for (int i = 0; i < count; i++) {
            final int timerIndex = i;

            Button button = new Button(this);

            // Text (simple index for clean look)
            button.setText(String.valueOf(i + 1));
            button.setTextSize(14);
            button.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
            button.setAllCaps(false);

            // Circular outline background
            button.setBackgroundResource(R.drawable.timer_button_circle);

            // Remove default button padding & tint
            button.setPadding(0, 0, 0, 0);
            button.setBackgroundTintList(null);

            // Click behavior
            button.setOnClickListener(v -> onTimerButtonPressed(timerIndex));

            // Layout params (DO NOT use weight with circles)
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(16, 16, 16, 16);

            button.setLayoutParams(params);
            grid.addView(button);
        }


    }


    private void onTimerButtonPressed(int timerIndex) {
        if (timerIndex == 0) { // Timer 1: Create the file in app storage
            createInternalFile();
        } else if (timerIndex == 1) { // Timer 2: Copy file to user directory
            copyFileToUserLocation(filename);
        } else {
            Toast.makeText(this, "Timer " + (timerIndex + 1) + " pressed", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Step 1: Create internal file ---
    private void createInternalFile() {
        try (FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE)) {
            fos.write(content.getBytes());
            Toast.makeText(this, "File created internally: " + filename, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // --- Step 2: Open Save As dialog ---
    public void copyFileToUserLocation(String suggestedFileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // choose type as needed
        intent.putExtra(Intent.EXTRA_TITLE, suggestedFileName);

        startActivityForResult(intent, COPY_FILE_REQUEST_CODE);
    }

    // --- Step 3: Handle user's selection ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COPY_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                File srcFile = new File(getFilesDir(), filename);
                copyFileToUri(srcFile, uri);
            }
        }
    }

    // --- Step 4: Copy internal file to the chosen location ---
    private void copyFileToUri(File srcFile, Uri destUri) {
        try (FileInputStream in = new FileInputStream(srcFile);
             OutputStream out = getContentResolver().openOutputStream(destUri)) {

            if (out == null) throw new IOException("Failed to open output stream");

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            Toast.makeText(this, "File copied successfully!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to copy file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static int getTimerCount(Context context) {
        File file = new File(context.getFilesDir(), "data.json");

        if (!file.exists() || file.length() == 0) {
            return 0;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(file);
            JsonNode timersNode = root.path("timers");

            return timersNode.isObject() ? timersNode.size() : 0;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
