package com.example.colorgalaga;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Enemy {
    int xPosition;
    int yPosition;
    int direction;
    Bitmap image1;
    Bitmap image2;
    Bitmap image3;

    private Rect hitBox;

    public Enemy(Context context, int x, int y) {

        this.image1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.alien_ship1);
        this.image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.alien_ship2);
        this.image3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.alien_ship3);
        this.xPosition = x;
        this.yPosition = y;

        this.hitBox = new Rect(this.xPosition, this.yPosition, this.xPosition + this.image1.getWidth(), this.yPosition + this.image1.getHeight());
    }

    public void updateEnemyPosition() {
        this.xPosition = this.xPosition - 15;

        // update the position of the hitbox
        this.hitBox.left = this.xPosition;
        this.hitBox.right = this.xPosition + this.image1.getWidth();
        this.updateHitbox();
    }

    public void updateHitbox() {
        // update the position of the hitbox
        this.hitBox.top = this.yPosition;
        this.hitBox.left = this.xPosition;
        this.hitBox.right = this.xPosition + this.image1.getWidth();
        this.hitBox.bottom = this.yPosition + this.image1.getHeight();
    }

    public Rect getHitbox() {
        return this.hitBox;
    }


    public void setXPosition(int x) {
        this.xPosition = x;
        this.updateHitbox();
    }
    public void setYPosition(int y) {
        this.yPosition = y;
        this.updateHitbox();
    }
    public int getXPosition() {
        return this.xPosition;
    }
    public int getYPosition() {
        return this.yPosition;
    }

    public Bitmap getBitmap() {
        return this.image1;
    }

    public Bitmap getImage1() {
        return image1;
    }

    public void setImage1(Bitmap image1) {
        this.image1 = image1;
    }

    public Bitmap getImage2() {
        return image2;
    }

    public void setImage2(Bitmap image2) {
        this.image2 = image2;
    }

    public Bitmap getImage3() {
        return image3;
    }

    public void setImage3(Bitmap image3) {
        this.image3 = image3;
    }
}