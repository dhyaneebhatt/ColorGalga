package com.example.colorgalaga;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    GameEngine ColorGala;
    MediaPlayer bkground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get size of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Initialize the GameEngine object
        // Pass it the screen size (height & width)
        ColorGala = new GameEngine(this, size.x, size.y);

        // Make GameEngine the view of the Activity
        setContentView(ColorGala);

        bkground = MediaPlayer.create(this,R.raw.backgroundsound);
        bkground.setLooping(true);
        //bkground.start();
    }



    // Android Lifecycle function
    @Override
    protected void onResume() {
        super.onResume();
        ColorGala.startGame();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        ColorGala.pauseGame();
        bkground.release();
    }
}
