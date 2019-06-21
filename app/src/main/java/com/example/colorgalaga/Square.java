package com.example.colorgalaga;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.colorgalaga.R.drawable.alien_ship1;

public class Square {

    private int xPosition;
    private int yPosition;
    private int width;

    private int initialX;
    private int initialY;

    Bitmap image1;

    // make the hitbox
    Rect hitbox;

    // vector variables
    double xn = 0;
    double yn = 0;

    public Square(Context context, int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.image1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lazer);

        this.initialX = x;
        this.initialY = y;

        this.hitbox = new Rect(
                this.xPosition,
                this.yPosition,
                this.xPosition + this.width,
                this.yPosition + this.width
        );

    }

    public Rect getHitbox() {
        return hitbox;
    }

    public void updateHitbox() {
        this.hitbox.left = this.xPosition;
        this.hitbox.top = this.yPosition;
        this.hitbox.right = this.xPosition + this.width;
        this.hitbox.bottom = this.yPosition + this.width;
    }


    // ---------------------------------
    // sets or gets the xd variable for this sprite
    // ---------------------------------


    public int getInitialX() {
        return initialX;
    }

    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }

    public double getXn() {
        return xn;
    }

    public void setXn(double xn) {
        this.xn = xn;
    }

    public double getYn() {
        return yn;
    }

    public void setYn(double yn) {
        this.yn = yn;
    }

    public void setyPosition(int yPosition) { this.yPosition = yPosition; }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getWidth() { return width; }

    public void setWidth(int width) { this.width = width; }

    public void setHitbox(Rect hitbox) {
        this.hitbox = hitbox;
    }


    public Bitmap getImage1() { return image1; }

    public void setImage1(Bitmap image1) { this.image1 = image1; }

}