package com.example.colorgalaga;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.SoundPool;
import android.media.AudioManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;
import static java.lang.Thread.sleep;








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

    // touch variables
    int updatedY;
    int updatedX;

    // VISIBLE GAME PLAY AREA
    // These variables are set in the constructor
    int VISIBLE_LEFT;
    int VISIBLE_TOP;
    int VISIBLE_RIGHT;
    int VISIBLE_BOTTOM;

    //Random
    int randX = 0;
    int randY = 0;

    //variables
    int i;


    //timer
    // Minimal x and y axis swipe distance.
    int MIN_SWIPE_DISTANCE_X = 100;
    int MIN_SWIPE_DISTANCE_Y = 100;

    // Maximal x and y axis swipe distance.
    int MAX_SWIPE_DISTANCE_X = 1000;
    int MAX_SWIPE_DISTANCE_Y = 1000;

    int touchUpx;
    int touchUpy;


    // ----------------------------
    // ## SPRITES
    // ----------------------------

    // Characters
    Sprite player;
    //Sprite enemy1;
    Sprite bg;
    Sprite blast;
    Sprite theEnd;
    Sprite bomb;
    Sprite shield;


    Enemy enemy;
    Square bullet;

    ArrayList <Enemy> enemy2 = new ArrayList<Enemy>();
    ArrayList <Enemy> enemyRed = new ArrayList<Enemy>();
    ArrayList <Enemy> enemyBlue = new ArrayList<Enemy>();

    ArrayList <Square> bullets = new ArrayList<Square>();



    //Sound
    SoundPool sounds;
    int bulletsound;




    // GAME STATS
    int score = 0;
    int lives = 3;
    int count = 0;

    //flags
    boolean gameOver = false;
    boolean enemyHit = false;
    boolean bulletHit = false;
    boolean rewardbomb = false;
    boolean shieldpop = false;
    boolean newbullet = false;



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
        this.bg = new Sprite(this.getContext(), 0, -4000, R.drawable.background3);
        this.player = new Sprite(this.getContext(), 400, 1300, R.drawable.player_ship);
       // this.enemy1 = new Sprite(this.getContext(), 100, 200, R.drawable.alien_ship1);

        this.blast = new Sprite(this.getContext(), 400, 1450, R.drawable.boom);
        this.theEnd = new Sprite(this.getContext(), 400, 1450, R.drawable.gameover);
        this.bomb = new Sprite(this.getContext(), 700, 100, R.drawable.explosive);
        this.shield = new Sprite(this.getContext(), 700, 100, R.drawable.shield);


        this.spawnEnemy();
        this.spawnbullets();


        //Sound
        this.sounds = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
        this.bulletsound = sounds.load(context,R.raw.bulletsound,1);
    }


    int enemy2initialX = 100;
    int enemy2initialY  = 200;

    int enemyRedinitialX = 100;
    int enemyRedinitialY  = 400;

    int enemyBlueinitialX = 100;
    int enemyBlueinitialY  = 600;

    private void spawnEnemy() {
        // put player in middle of screen --> you may have to adjust the Y position
        // depending on your device / emulator


        // Adding multiple enemies to arraylist
        //Random rand = new Random();

        for (i = 0; i<5; i++) {

            enemy2initialX = enemy2initialX + 130;

            enemy = new Enemy(this.getContext(), enemy2initialX, enemy2initialY);
            enemy2.add(enemy);
            // Log.d("enemy","added");

            enemyRedinitialX = enemyRedinitialX + 130;

            enemy = new Enemy(this.getContext(), enemyRedinitialX, enemyRedinitialY);
            enemyRed.add(enemy);
            // Log.d("enemy","added");

            enemyBlueinitialX = enemyBlueinitialX + 130;

            enemy = new Enemy(this.getContext(), enemyBlueinitialX, enemyBlueinitialY);
            enemyBlue.add(enemy);
            //Log.d("enemy","added");
        }



    }


    private void spawnbullets() {
        // put player in middle of screen --> you may have to adjust the Y position
        // depending on your device / emulator


        // Adding multiple enemies to arraylist
        //Random rand = new Random();

        for (i = 0; i<3; i++) {

            this.bullet = new Square(this.getContext(), this.player.getxPosition(), this.player.getyPosition());
            bullets.add(bullet);

        }



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

    boolean balls = true;
    final int BALL_SPEED = 5;

    public void updatePositions() {
        // @TODO: Update position of Background to make it loop

        this.bg.setyPosition(this.bg.getyPosition() + 25);

        if(this.bg.getyPosition() > this.VISIBLE_BOTTOM - 2500 ){
            this.bg.setxPosition(0);
            this.bg.setyPosition(-4200);
        }




       // @TODO: Update position of enemy ships right to left

            // move all ememies right to left

            if (balls == true) {
                for (Enemy temp : enemy2) {
                    temp.setXPosition(temp.xPosition + BALL_SPEED);
                }

                for (Enemy temp : enemyRed) {
                    temp.setXPosition(temp.xPosition + BALL_SPEED);
                }

                for (Enemy temp : enemyBlue) {
                    temp.setXPosition(temp.xPosition + BALL_SPEED);
                }
            }
            else {
                for (Enemy temp : enemy2) {
                    temp.setXPosition(temp.xPosition - BALL_SPEED);
                }

                for (Enemy temp : enemyRed) {
                    temp.setXPosition(temp.xPosition - BALL_SPEED);
                }

                for (Enemy temp : enemyBlue) {
                    temp.setXPosition(temp.xPosition - BALL_SPEED);
                }
            }

        for (Enemy temp : enemy2) {

            if (temp.xPosition > screenWidth - 200) {
               // Log.d(TAG, "Ball reached RIGHT of screen. Changing direction!");
                balls = false;
                // update score

            }

            if (temp.xPosition < 100) {
               // Log.d(TAG, "Ball reached LEFT of screen. Changing direction!");
                balls = true;

            }

        }
            for (Enemy temp1 : enemyRed) {

                if (temp1.xPosition > screenWidth - 200) {
                   // Log.d(TAG, "Ball reached RIGHT of screen. Changing direction!");
                    balls = false;
                    // update score

                }

                if (temp1.xPosition < 100) {
                   // Log.d(TAG, "Ball reached LEFT of screen. Changing direction!");
                    balls = true;

                }
            }

                for (Enemy temp2 : enemyBlue) {

                    if (temp2.xPosition > screenWidth - 200) {
                       // Log.d(TAG, "Ball reached RIGHT of screen. Changing direction!");
                        balls = false;
                        // update score

                    }

                    if (temp2.xPosition < 100) {
                   //     Log.d(TAG, "Ball reached LEFT of screen. Changing direction!");
                        balls = true;

                    }
                }


        // @TODO: Update position of enemy ships to fall down

        //Move all ememies of line 1 towardes the player

        for (int i = 0; i < this.enemy2.size(); i++) {
            Enemy last = this.enemy2.get(this.enemy2.size() - 1);

            // 1. calculate distance between bullet and enemy
            double a1 = this.player.getxPosition() - last.xPosition;
            double b1 = this.player.getyPosition() - last.yPosition;

            // d = sqrt(a^2 + b^2)

            double d1 = Math.sqrt((a1 * a1) + (b1 * b1));

            Log.d(TAG, "Distance to enemy: " + d1);

            // 2. calculate xn and yn constants
            // (amount of x to move, amount of y to move)
            double xn1 = (a1 / d1);
            double yn1 = (b1 / d1);

            // 3. calculate new (x,y) coordinates
            int newX1 = last.xPosition + (int) (xn1 * 3);
            int newY1 = last.yPosition + (int) (yn1 * 3);

            last.setXPosition(newX1);
            last.setYPosition(newY1);

            //Upate enemy hitbox
            last.updateHitbox();

            if (last.yPosition == newY1 && last.xPosition == newX1) {

                last.setXPosition(last.xPosition);
                last.setYPosition(last.yPosition + 5);

                //Upate enemy hitbox
                last.updateHitbox();

            }

            if (last.yPosition > this.VISIBLE_BOTTOM) {

                enemy2.remove(last);

                //Upate enemy hitbox
                last.updateHitbox();
            }

        }

        // if line 1 is over then line 2 starts falling

        if(enemy2.isEmpty()) {

            for (int a = 0; a < this.enemyRed.size(); a++) {
                Enemy last1 = this.enemyRed.get(this.enemyRed.size() - 1);

                // 1. calculate distance between bullet and enemy
                double a2 = this.player.getxPosition() - last1.xPosition;
                double b2 = this.player.getyPosition() - last1.yPosition;

                // d = sqrt(a^2 + b^2)

                double d2 = Math.sqrt((a2 * a2) + (b2 * b2));

                Log.d(TAG, "Distance to enemy: " + d2);

                // 2. calculate xn and yn constants
                // (amount of x to move, amount of y to move)
                double xn2 = (a2 / d2);
                double yn2 = (b2 / d2);

                // 3. calculate new (x,y) coordinates
                int newX2 = last1.xPosition + (int) (xn2 * 3);
                int newY2 = last1.yPosition + (int) (yn2 * 3);

                last1.setXPosition(newX2);
                last1.setYPosition(newY2);

                //Upate enemy hitbox
                last1.updateHitbox();

                if (last1.yPosition == newY2 && last1.xPosition == newX2) {

                    last1.setXPosition(last1.xPosition);
                    last1.setYPosition(last1.yPosition + 5);

                    //Upate enemy hitbox
                    last1.updateHitbox();

                }

                if (last1.yPosition > this.VISIBLE_BOTTOM) {

                    enemyRed.remove(last1);

                    //Upate enemy hitbox
                    last1.updateHitbox();
                }


            }
        }

        // if line 1 is over then line 3 starts falling

        if(enemyRed.isEmpty()) {

            for (int c = 0; c < this.enemyBlue.size(); c++) {
                Enemy last1 = this.enemyBlue.get(this.enemyBlue.size() - 1);

                // 1. calculate distance between bullet and enemy
                double a2 = this.player.getxPosition() - last1.xPosition;
                double b2 = this.player.getyPosition() - last1.yPosition;

                // d = sqrt(a^2 + b^2)

                double d2 = Math.sqrt((a2 * a2) + (b2 * b2));

                Log.d(TAG, "Distance to enemy: " + d2);

                // 2. calculate xn and yn constants
                // (amount of x to move, amount of y to move)
                double xn2 = (a2 / d2);
                double yn2 = (b2 / d2);

                // 3. calculate new (x,y) coordinates
                int newX2 = last1.xPosition + (int) (xn2 * 3);
                int newY2 = last1.yPosition + (int) (yn2 * 3);

                last1.setXPosition(newX2);
                last1.setYPosition(newY2);

                //Upate enemy hitbox
                last1.updateHitbox();

                if (last1.yPosition == newY2 && last1.xPosition == newX2) {

                    last1.setXPosition(last1.xPosition);
                    last1.setYPosition(last1.yPosition + 10);

                    //Upate enemy hitbox
                    last1.updateHitbox();

                }

                if (last1.yPosition > this.VISIBLE_BOTTOM) {

                    enemyBlue.remove(last1);

                    //Upate enemy hitbox
                    last1.updateHitbox();
                }


            }
        } // enemy fall down finishes here




//        // @TODO: Collision detection between player and enemy
//
//        // Colision of player and enemy
//         if (player.getHitbox().intersect(enemy1.getHitbox())) {
//            this.enemyHit = true;
//
//            this.enemy1.setxPosition(this.randX);
//            this.enemy1.setyPosition(this.randY);
//            this.lives = this.lives - 1;
//
//            if (this.lives < 3) {
//                this.shieldpop = true;
//
//            }
//
//            if(this.lives <= 0){
//                this.gameOver = true;
//            }
//
//            //Upate enemy hitbox
//            this.enemy1.updateHitbox();
//
//            //Upate player hitbox
//           this.player.updateHitbox();
//        }

        //}



        // @TODO: Chasing code form bullet to enemy
//
        for (int i = 0; i < this.bullets.size(); i++) {
            Square firstbullet = this.bullets.get(0);

            firstbullet.setyPosition(firstbullet.getyPosition() - 20);

            if (firstbullet.getyPosition() < this.VISIBLE_TOP) {

                bullets.remove(firstbullet);

                //Upate enemy hitbox
                firstbullet.updateHitbox();

                this.newbullet = false;

            }

        }






        // @TODO: Update position of bomb

            this.bomb.setyPosition(this.bomb.getyPosition() - 20);


        if (this.bomb.getyPosition() > this.VISIBLE_TOP ){
           //this.rewardbomb = false;
        }

        // @TODO: Collision detection between enemy and bomb

//
//        // @TODO: Update position of shield ships
//
//        this.shield.setyPosition(this.shield.getyPosition() + 20);
//
//
//        if (this.shield.getyPosition() > this.VISIBLE_BOTTOM ){
//            this.rewardbomb = false;
//
//            //Random bomb position
//            Random r = new Random();
//            this.randX = r.nextInt(this.screenWidth) +1 ;
//            this.randY = r.nextInt(this.screenHeight) + 1;
//
//            this.shield.setxPosition(randX);
//            this.shield.setyPosition(randY);
//        }
//
//        // @TODO: Collision detection between player and shield
//
//        // Colision of player and enemy
//        if (player.getHitbox().intersect(shield.getHitbox())) {
//
//            this.shield.setxPosition(this.randX);
//            this.shield.setyPosition(this.randY);
//
//            this.rewardbomb = false;
//
//            //Upate sheild hitbox
//            this.shield.updateHitbox();
//
//            //Upate player hitbox
//            this.player.updateHitbox();
//        }


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
           // canvas.drawBitmap(this.enemy1.getImage(), this.enemy1.getxPosition(), this.enemy1.getyPosition(), paintbrush);

            for (Enemy temp : enemy2) {
                canvas.drawBitmap(temp.getBitmap(), temp.getXPosition(), temp.getYPosition(), paintbrush);

                // 3. draw the enemies's hitbox
               paintbrush.setColor(Color.GREEN);
                paintbrush.setStyle(Paint.Style.STROKE);
                canvas.drawRect(
                        temp.getHitbox(),
                        paintbrush
                );

            }

            for (Enemy temp : enemyRed) {
                canvas.drawBitmap(temp.getImage2(), temp.getXPosition(), temp.getYPosition(), paintbrush);

                // 3. draw the enemies's hitbox
                paintbrush.setColor(Color.GREEN);
                paintbrush.setStyle(Paint.Style.STROKE);
                canvas.drawRect(
                        temp.getHitbox(),
                        paintbrush
                );

            }

            for (Enemy temp : enemyBlue) {
                canvas.drawBitmap(temp.getImage3(), temp.getXPosition(), temp.getYPosition(), paintbrush);

                // 3. draw the enemies's hitbox
                paintbrush.setColor(Color.GREEN);
                paintbrush.setStyle(Paint.Style.STROKE);
                canvas.drawRect(
                        temp.getHitbox(),
                        paintbrush
                );

            }









            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            Rect r = player.getHitbox();
            paintbrush.setColor(Color.BLACK);
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paintbrush);


            // DRAW GAME STATS / HUDs

            paintbrush.setTextSize(60);     // set font size
            paintbrush.setColor(Color.RED);
            paintbrush.setStrokeWidth(5);  // make text narrow
            canvas.drawText("Score: " + this.score, 50, 100, paintbrush);


            paintbrush.setTextSize(60);// set font size
            paintbrush.setColor(Color.RED);
            paintbrush.setStrokeWidth(5);  // make text narrow
            canvas.drawText("Lives: " + this.lives, 830, 100, paintbrush);



            //-------------------------------------
            // Colision Updates
            //--------------------------------------

            if (this.newbullet) {

                for (Square temp : bullets) {
                    canvas.drawBitmap(temp.getImage1(), temp.getxPosition(), temp.getyPosition(), paintbrush);

                    // 3. draw the bullet's hitbox
                    paintbrush.setColor(Color.RED);
                    paintbrush.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(
                            temp.getHitbox(),
                            paintbrush
                    );

                }

            }

            if (this.enemyHit) {
                canvas.drawBitmap(this.blast.getImage(), this.player.getxPosition()- 50, this.player.getyPosition()- 50 , paintbrush);
            }

            if (this.gameOver) {
                canvas.drawBitmap(this.theEnd.getImage(), this.screenWidth/2 - 300, this.screenHeight/2 - 400 , paintbrush);
                try {
                    gameThread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (this.bulletHit == true) {


            }

            if(this.rewardbomb){
                // 2. Reward
                canvas.drawBitmap(this.bomb.getImage(),this.bomb.getxPosition(), this.bomb.getyPosition() , paintbrush);

                //hit box on bomb
                Rect bl = bomb.getHitbox();
                paintbrush.setColor(Color.BLACK);
                paintbrush.setStyle(Paint.Style.STROKE);
                canvas.drawRect(bl, paintbrush);

            }

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setFPS() {
        try {
            sleep(17);
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
          //  Log.d(TAG, "Person tapped the screen");

            //---------------------
            // Swipe Effect
            //---------------------


            // Get swipe delta value in y axis.
            int deltaY = (int)event.getY() - this.touchUpy;


            if((deltaY>= MIN_SWIPE_DISTANCE_Y) && (deltaY <= MAX_SWIPE_DISTANCE_Y))
            {
                if(deltaY > 0)
                {
                    //this.activity.displayMessage("Swipe to up");
                    Log.d(TAG, "Swipe to up");

                    this.bomb.setxPosition(this.player.getxPosition());
                    this.bomb.setyPosition(this.player.getyPosition());

                    rewardbomb = true;

                }else
                {
                    //this.activity.displayMessage("Swipe to down");
                    Log.d(TAG, "Swipe to down");

                    rewardbomb = true;
                }
            }


            //----------------------
            // Player Movement
            //-----------------------

            int Jump = 160;
            this.enemyHit = false;

            if (event.getX() < this.screenWidth / 2 && this.player.getxPosition() > this.VISIBLE_LEFT + this.player.image.getWidth() ) {
               // Log.d(TAG, "Person clicked LEFT side");




                    this.updatedX = (int) event.getX();
                    this.updatedY = (int) event.getY();

                    // update player position
                    this.player.setxPosition(this.player.getxPosition() - Jump);

                    this.newbullet = true;

                // reset bullet position
//
//                for (int i = 0; i < this.bullets.size(); i++) {
//                    Square firstbullet = this.bullets.get(0);
//
//                    firstbullet.setxPosition(this.player.getxPosition());
//                    firstbullet.setyPosition(this.player.getyPosition());
//
//
//                }

//                    this.bullet.setxPosition(this.player.getxPosition());
//                    this.bullet.setyPosition(this.player.getyPosition());


                    //Upate both player and bullet hitbox
                    this.player.updateHitbox();

                    //this.bullet.updateHitbox();

                //sounds.play(bulletsound,1.0f,1.0f,0,0,1.5f);




                    moving = false;

            }
            else if (event.getX() > this.screenWidth / 2 && this.player.getxPosition() < this.VISIBLE_RIGHT - this.player.image.getWidth()   ) {
               // Log.d(TAG, "Person clicked RIGHT side");

                // player moves to right
                this.player.setxPosition(this.player.getxPosition() + Jump);

                this.updatedX = (int)event.getX();
                this.updatedY = (int)event.getY();


                //Upate both player and bullet hitbox
                this.player.updateHitbox();

                this.newbullet = true;



                //sounds.play(bulletsound,1.0f,1.0f,0,0,1.5f);

            }

             }
        else if (userAction == MotionEvent.ACTION_UP) {
           // Log.d(TAG, "Person lifted finger");

            this.touchUpx = (int) event.getX();
            this.touchUpx = (int) event.getY();



//            for (int i = 0; i < this.bullets.size(); i++) {
//                Square firstbullet = this.bullets.get(0);
//
//                firstbullet.setxPosition(this.player.getxPosition());
//                firstbullet.setyPosition(this.player.getyPosition());
//
//
//            }


//               this.bullet.setxPosition(this.player.getxPosition());
//               this.bullet.setyPosition(this.player.getyPosition());


            //Upate both player and bullet hitbox
           // this.bullet.updateHitbox();

            //sounds.play(bulletsound,1.0f,1.0f,0,0,1.5f);


        }

        return true;
    }
}
