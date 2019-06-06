package com.example.colorgalaga;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {

    GameEngine tappySpaceship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get size of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Initialize the GameEngine object
        // Pass it the screen size (height & width)
        tappySpaceship = new GameEngine(this, size.x, size.y);

        // Make GameEngine the view of the Activity
        setContentView(tappySpaceship);
    }

    // Android Lifecycle function
    @Override
    protected void onResume() {
        super.onResume();
        tappySpaceship.startGame();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        tappySpaceship.pauseGame();
    }
}
