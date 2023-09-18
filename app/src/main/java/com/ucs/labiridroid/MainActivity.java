package com.ucs.labiridroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MazeView mazeView = (MazeView) findViewById(R.id.mazeView);
        Button returnButton = (Button) findViewById(R.id.returnButton);
        Button resetButton = (Button) findViewById(R.id.resetButton);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mazeView.returnMaze();
                mazeView.invalidate();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mazeView.resetMaze();
                mazeView.invalidate();
            }
        });

    }
}