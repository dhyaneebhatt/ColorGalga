package com.example.colorgalaga;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    final static String TAG="ColorGala";

    // -----------------------------------
    // GAME SPECIFIC VARIABLES
    // -----------------------------------

    // screen resolution variables
    int screenHeight;
    int screenWidth;

    // game thread variables
    private Thread gameThread = null;
    private volatile boolean gameIsRunning;


    // drawing variables
    private Canvas canvas;
    private Paint paintbrush;
    private SurfaceHolder holder;

    //bullet width
    int SQUARE_WIDTH = 50;
    boolean enemyIsMovingDown = true;
    boolean colision = false;

    int initialPlayerY;
    int initialPlayerX;

    int updatedY;
    int updatedX;

    // VISIBLE GAME PLAY AREA
    // These variables are set in the constructor
    int VISIBLE_LEFT;
    int VISIBLE_TOP;
    int VISIBLE_RIGHT;
    int VISIBLE_BOTTOM;


    // Multiple bullets
    ArrayList<Square> bullets = new ArrayList<Square>();


    // ----------------------------
    // ## SPRITES
    // ----------------------------

    // Characters
    Sprite player;
    Sprite enemy1;
    Square bullet;
    Sprite bg;
    Sprite blast;


    // GAME STATS
    int score = 0;
    int lives = 3;



    // ------------------------------
    // Gane Engine Constructor
    //-------------------------------

    public GameEngine(Context context, int w, int h) {
        super(context);


        // intialize the drawing variables
        this.holder = this.getHolder();
        this.paintbrush = new Paint();


        // set screen height and width
        this.screenWidth = w;
        this.screenHeight = h;

        // setup visible game play area variables
        this.VISIBLE_LEFT = 20;
        this.VISIBLE_TOP = 10;
        this.VISIBLE_RIGHT = this.screenWidth - 20;
        this.VISIBLE_BOTTOM = (int) (this.screenHeight * 0.8);


        // @TODO: Add your sprites

        // initalize sprites
        this.bg = new Sprite(this.getContext(), 0 , -4000, R.drawable.background3);
        this.player = new Sprite(this.getContext(), 400, 1300, R.drawable.player_ship);
        this.enemy1 = new Sprite(this.getContext(), 100, 200, R.drawable.alien_ship1);
        this.bullet = new Square(context, 100, 700, SQUARE_WIDTH);
        this.blast = new Sprite(this.getContext(), 400, 1450, R.drawable.boom);


    }


    private void spawnEnemyShips() {
        Random random = new Random();

        //@TODO: Place the enemies in a random location
        this.enemy1 = new Sprite(this.getContext(), 100, 200, R.drawable.alien_ship1);
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
        // @TODO: Update position of Background to make it loop

        this.bg.setyPosition(this.bg.getyPosition() + 25);

        if(this.bg.getyPosition() > this.VISIBLE_BOTTOM - 2500 ){
            this.bg.setxPosition(0);
            this.bg.setyPosition(-4200);
        }

       // @TODO: Update position of enemy ships

        // 1. calculate distance between bullet and enemy
        double a1 = this.player.getxPosition() - this.enemy1.getxPosition();
        double b1 = this.player.getyPosition() - this.enemy1.getyPosition();

        // d = sqrt(a^2 + b^2)

        double d1 = Math.sqrt((a1 * a1) + (b1* b1));

        Log.d(TAG, "Distance to enemy: " + d1);

        // 2. calculate xn and yn constants
        // (amount of x to move, amount of y to move)
        double xn1 = (a1 / d1);
        double yn1 = (b1 / d1);

        // 3. calculate new (x,y) coordinates
        int newX1 = this.enemy1.getxPosition() + (int) (xn1 * 5);
        int newY1 = this.enemy1.getyPosition() + (int) (yn1 * 5);

        this.enemy1.setxPosition(newX1 );
        this.enemy1.setyPosition(newY1 );

        //Upate enemy hitbox
        this.enemy1.updateHitbox();

        if (this.enemy1.getyPosition() == newY1 && this.enemy1.getxPosition() == newX1 ){

            this.enemy1.setyPosition(this.enemy1.getyPosition());
            this.enemy1.setyPosition(this.enemy1.getyPosition() + 20);

        }

        if (this.enemy1.getyPosition() > this.VISIBLE_BOTTOM ){

            this.enemy1.setyPosition(100);
            this.enemy1.setyPosition(200);
        }


        // @TODO: Collision detection between player and enemy

        // Colision of bullet and enemy
        if (player.getHitbox().intersect(enemy1.getHitbox())) {
            colision = true;
        }

        // @TODO: Chasing code form bullet to enemy

        // 1. calculate distance between bullet and enemy
        double a = updatedX - this.bullet.getxPosition();
        double b = updatedY - this.bullet.getyPosition();

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

        this.bullet.setxPosition(newX );
        this.bullet.setyPosition(newY );

        if (this.bullet.getyPosition() == newY && this.bullet.getxPosition() == newX ){

            this.bullet.setyPosition(this.bullet.getyPosition());
            this.bullet.setyPosition(this.bullet.getyPosition() - 120);

        }


        //Upate hitbox
        this.bullet.updateHitbox();

        // Colision of bullet and enemy
        if (bullet.getHitbox().intersect(enemy1.getHitbox())) {
            //colision = true;
        }


}




    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------

            // set the game's background color
            canvas.drawColor(Color.argb(255,0,0,0));

            // setup stroke style and width
            paintbrush.setStyle(Paint.Style.FILL);
            paintbrush.setStrokeWidth(8);


            // Draw Background
            canvas.drawBitmap(this.bg.getImage(), this.bg.getxPosition(), this.bg.getyPosition(), paintbrush);



            // --------------------------------------------------------
            // draw player, enemy and bullet
            // --------------------------------------------------------

            // 1. Player
            canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(), this.player.getyPosition(), paintbrush);

            // 2. Enemy
            canvas.drawBitmap(this.enemy1.getImage(), this.enemy1.getxPosition(), this.enemy1.getyPosition(), paintbrush);

            //3.Bullet
            paintbrush.setColor(Color.WHITE);
            canvas.drawRect(
                    this.bullet.getxPosition(),
                    this.bullet.getyPosition(),
                    this.bullet.getxPosition() + this.bullet.getWidth(),
                    this.bullet.getyPosition() + this.bullet.getWidth(),
                    paintbrush
            );




            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            Rect r = player.getHitbox();
            paintbrush.setColor(Color.BLACK);
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paintbrush);

            //hit box on sparrow
            Rect sp = enemy1.getHitbox();
            paintbrush.setColor(Color.BLACK);
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(sp, paintbrush);

            //hit box on bullet
            Rect bu = bullet.getHitbox();
            paintbrush.setColor(Color.BLACK);
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(bu, paintbrush);




            // DRAW GAME STATS / HUDs

            paintbrush.setTextSize(60);     // set font size
            paintbrush.setColor(Color.RED);
            paintbrush.setStrokeWidth(5);  // make text narrow
            canvas.drawText("Score: " + this.score, 50, 100, paintbrush);

            paintbrush.setTextSize(60);// set font size
            paintbrush.setColor(Color.RED);
            paintbrush.setStrokeWidth(5);  // make text narrow
            canvas.drawText("Lives: " + this.lives, 830, 100, paintbrush);

            if (gameOver == true) {
                canvas.drawText("GAME OVER!", 50, 200, paintbrush);
            }

            if (colision == true) {

                canvas.drawBitmap(this.blast.getImage(), this.player.getxPosition()- 50, this.player.getyPosition()- 50 , paintbrush);



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

    boolean moving = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Person tapped the screen");

            int Jump = 160;

            if (event.getX() < this.screenWidth / 2 && this.player.getxPosition() > this.VISIBLE_LEFT + this.player.image.getWidth() ) {
                Log.d(TAG, "Person clicked LEFT side");



                    this.updatedX = (int) event.getX();
                    this.updatedY = (int) event.getY();

                    // update player position
                    this.player.setxPosition(this.player.getxPosition() - Jump);

//                // reset bullet position
//                this.bullet.setxPosition(this.player.getxPosition());
//                this.bullet.setyPosition(this.player.getyPosition());

                    //Upate both player and bullet hitbox
                    this.player.updateHitbox();
                    this.bullet.updateHitbox();


                    moving = false;

            }
            else if (event.getX() > this.screenWidth / 2 && this.player.getxPosition() < this.VISIBLE_RIGHT - this.player.image.getWidth()   ) {
                Log.d(TAG, "Person clicked RIGHT side");

                // player moves to right
                this.player.setxPosition(this.player.getxPosition() + Jump);

                this.updatedX = (int)event.getX();
                this.updatedY = (int)event.getY();

//                // reset bullet position
//                this.bullet.setxPosition(this.player.getxPosition());
//                this.bullet.setyPosition(this.player.getyPosition());

                //Upate both player and bullet hitbox
                this.player.updateHitbox();
                this.bullet.updateHitbox();


                moving = true;

            }

             }
        else if (userAction == MotionEvent.ACTION_UP) {
            Log.d(TAG, "Person lifted finger");

            this.bullet.setxPosition(this.player.getxPosition());
            this.bullet.setyPosition(this.player.getyPosition());


        }

        return true;
    }
}
