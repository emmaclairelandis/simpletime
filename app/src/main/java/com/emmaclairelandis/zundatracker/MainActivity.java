package com.emmaclairelandis.zundatracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMNS = 4;
    private int timerCount = 17; // <-- change this freely

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout grid = findViewById(R.id.timerGrid);
        grid.setColumnCount(COLUMNS);

        createTimerButtons(grid, timerCount);
    }

    private void createTimerButtons(GridLayout grid, int count) {
        grid.removeAllViews();

        for (int i = 0; i < count; i++) {
            Button button = new Button(this);
            int timerIndex = i;

            button.setText("Timer " + (i + 1));

            button.setOnClickListener(v -> {
                onTimerButtonPressed(timerIndex);
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();

            // Make buttons evenly fill 4 columns
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(
                    GridLayout.UNDEFINED, 1f
            );
            params.setMargins(8, 8, 8, 8);

            button.setLayoutParams(params);
            grid.addView(button);
        }
    }

    private void onTimerButtonPressed(int timerIndex) {
        // This replaces your CLI command logic
        // e.g. start/stop timer, write JSON, etc.

        System.out.println("Pressed timer " + timerIndex);
    }
}
