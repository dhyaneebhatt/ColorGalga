package com.example.colorgalaga;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG="TAPPY-SPACESHIP";

    // -----------------------------------
    // GAME SPECIFIC VARIABLES
    // -----------------------------------

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;

    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;

    //bullet width
    int SQUARE_WIDTH = 100;
    boolean enemyIsMovingDown = true;

    int initialPlayerY;
    int initialPlayerX;

    ArrayList<Square> bullets = new ArrayList<Square>();


    // ----------------------------
    // ## SPRITES
    // ----------------------------


    //Bullets
    Square bullet;

    //Playeers
    Player player;

    // Enemy variables
    Enemy enemy1;
    Enemy enemy2;


    // ----------------------------
    // ## GAME STATS
    // ----------------------------
    int score = 0;
    int lives = 3;

    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;



        this.printScreenInfo();

        // @TODO: Add your sprites
        // @TODO: Any other game setup

        // ----------------
        // PLAYER SETUP
        // ----------------
        this.initialPlayerY = this.screenHeight - 400;
        this.initialPlayerX = this.screenWidth / 2 - 100;
        this.player = new Player(context,this.initialPlayerX, this.initialPlayerY);


        // ----------------
        // ENEMY SETUP
        // ----------------
        this.enemy1 = new Enemy(context, this.screenWidth - 500, 120);
        this.enemy2 = new Enemy(context, this.screenWidth - 500, this.screenHeight - 400);

        // --------------
        //Bullet setup
        //---------------
        this.bullet = new Square(context, initialPlayerX,initialPlayerY , SQUARE_WIDTH);

    }


    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenHeight + "," + this.screenWidth);
    }

    private void spawnPlayer() {
        //@TODO: Start the player at the left side of screen
    }
    private void spawnEnemyShips() {
        Random random = new Random();

        //@TODO: Place the enemies in a random location

    }


    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------


    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------


    final int PLAYER_SPEED = 30;
    final int ENEMY_SPEED = 45;
    boolean gameOver = false;



    public void updatePositions() {
        // @TODO: Update position of player


       // @TODO: Update position of enemy ships



       // @TODO: Collision detection between player and enemy


        // @TODO: Chasing code form bullet to enemy

        // 1. calculate distance between bullet and enemy
        double a = this.enemy1.getxPosition() - this.bullet.getxPosition();
        double b = this.enemy1.getyPosition() - this.bullet.getyPosition();

        // d = sqrt(a^2 + b^2)

        double d = Math.sqrt((a * a) + (b * b));

        Log.d(TAG, "Distance to enemy: " + d);

        // 2. calculate xn and yn constants
        // (amount of x to move, amount of y to move)
        double xn = (a / d);
        double yn = (b / d);

        // 3. calculate new (x,y) coordinates
        int newX = this.bullet.getxPosition() + (int) (xn * 15);
        int newY = this.bullet.getyPosition() + (int) (yn * 15);
        this.bullet.setxPosition(newX);
        this.bullet.setyPosition(newY);

    }

    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.WHITE);


            //@TODO: Draw the player
            canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(), this.player.getyPosition(), paintbrush);


            //@TODO: Draw the enemy

            // refactored to use Enemy object
            canvas.drawBitmap(this.enemy1.getImage(), this.enemy1.getxPosition(), this.enemy1.getyPosition(), paintbrush);

            canvas.drawBitmap(this.enemy2.getImage(), this.enemy2.getxPosition(), this.enemy2.getyPosition(), paintbrush);

            //@TODO: Draw the bullet
            // draw bullet
            paintbrush.setColor(Color.BLACK);
            canvas.drawRect(
                    this.bullet.getxPosition(),
                    this.bullet.getyPosition(),
                    this.bullet.getxPosition() + this.bullet.getWidth(),
                    this.bullet.getyPosition() + this.bullet.getWidth(),
                    paintbrush
            );




            // DRAW THE PLAYER HITBOX
            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);

            // 2. draw the hitbox
            canvas.drawRect(this.player.getHitbox().left,
                    this.player.getHitbox().top,
                    this.player.getHitbox().right,
                    this.player.getHitbox().bottom,
                    paintbrush
            );


            // Draw enemy hitbox - refactored to use Enemy object
            paintbrush.setColor(Color.RED);
            canvas.drawRect(this.enemy1.getHitbox().left,
                    this.enemy1.getHitbox().top,
                    this.enemy1.getHitbox().right,
                    this.enemy1.getHitbox().bottom,
                    paintbrush
            );
            canvas.drawRect(this.enemy2.getHitbox().left,
                    this.enemy2.getHitbox().top,
                    this.enemy2.getHitbox().right,
                    this.enemy2.getHitbox().bottom,
                    paintbrush
            );


            // draw the bullet hitbox
            paintbrush.setColor(Color.RED);
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(
                    this.bullet.getHitbox(),
                    paintbrush
            );


            // DRAW GAME STATS

            paintbrush.setTextSize(100);     // set font size
            paintbrush.setStrokeWidth(5);  // make text narrow
            canvas.drawText("Lives: " + this.lives, 50, 100, paintbrush);

            if (gameOver == true) {
                canvas.drawText("GAME OVER!", 50, 200, paintbrush);
            }








            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setFPS() {
        try {
            gameThread.sleep(17);
        }
        catch (Exception e) {

        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Person tapped the screen");

            int Jump = 160;

            if (event.getX() < this.screenWidth / 2) {
                Log.d(TAG, "Person clicked LEFT side");

                this.player.setxPosition(this.player.getxPosition() - Jump);

                // update hitbox position
                this.player.getHitbox().left = this.player.getxPosition();
                this.player.getHitbox().top = this.player.getyPosition();
                this.player.getHitbox().right = this.player.getxPosition() + this.player.getImage().getWidth();
                this.player.getHitbox().bottom = this.player.getyPosition() + this.player.getImage().getHeight();

                this.bullet.setxPosition(this.player.getxPosition());
                this.bullet.setyPosition(this.player.getyPosition());

            }
            else {
                Log.d(TAG, "Person clicked RIGHT side");
                this.player.setxPosition(this.player.getxPosition() + Jump);
                // update hitbox position
                this.player.getHitbox().left = this.player.getxPosition();
                this.player.getHitbox().top = this.player.getyPosition();
                this.player.getHitbox().right = this.player.getxPosition() + this.player.getImage().getWidth();
                this.player.getHitbox().bottom = this.player.getyPosition() + this.player.getImage().getHeight();

                this.bullet.setxPosition(this.player.getxPosition());
                this.bullet.setyPosition(this.player.getyPosition());

            }

             }
        else if (userAction == MotionEvent.ACTION_UP) {
            Log.d(TAG, "Person lifted finger");
        }

        return true;
    }
}
