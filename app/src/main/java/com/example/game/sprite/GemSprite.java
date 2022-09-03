package com.example.game.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class GemSprite {

    private Bitmap image;
    private int left;
    private int top;
    private int form;
    private int color;
    private boolean selected = false;
    private boolean destroyed = false;

    private String[][] names = {{"", ""}, {"", ""}};

    int topp = 30;
    int bott = 52;

    private Rect[][] rects = {{
        new Rect(  0, 0,  42, 30),
        new Rect( 48, 0,  90, 30),
        new Rect( 98, 0, 140, 30),
        new Rect(148, 0, 190, 30),
        new Rect(198, 0, 240, 30),
        new Rect(248, 0, 290, 30),
        new Rect(298, 0, 340, 30)
    },{
        new Rect(  0, 30,  42, 52),
        new Rect( 48, 30,  90, 52),
        new Rect( 98, 30, 140, 52),
        new Rect(148, 30, 190, 52),
        new Rect(198, 30, 240, 52),
        new Rect(248, 30, 290, 52),
        new Rect(298, 30, 340, 52)
    },{
        new Rect(  0, 54,  42, 102),
        new Rect( 48, 54,  90, 102),
        new Rect( 98, 54, 140, 102),
        new Rect(148, 54, 190, 102),
        new Rect(198, 54, 240, 102),
        new Rect(248, 54, 290, 102),
        new Rect(298, 54, 340, 102)
    },{
        new Rect(  0, 102,  42, 155),
        new Rect( 48, 102,  90, 155),
        new Rect( 98, 102, 140, 155),
        new Rect(148, 102, 190, 155),
        new Rect(198, 102, 240, 155),
        new Rect(248, 102, 290, 155),
        new Rect(298, 102, 340, 155)
    },{
        new Rect(  0, 152,  42, 198),
        new Rect( 48, 152,  90, 198),
        new Rect( 98, 152, 140, 198),
        new Rect(148, 152, 190, 198),
        new Rect(198, 152, 240, 198),
        new Rect(248, 152, 290, 198),
        new Rect(298, 152, 340, 198)
    }};

    public GemSprite(Bitmap bmp, int left, int top, int form, int color){
        this.image = bmp;
        this.left = left;
        this.top = top;
        this.form = form;
        this.color = color;
    }

    //public void draw(Canvas canvas) {
        //canvas.drawBitmap(image, new Rect(10, 10, 10, 10), new Rect(10, 10, 10, 10),null);
        //canvas.drawBitmap(image, left, top,null);
    //}

    public void draw(Canvas canvas) {
        if(selected) {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(3);
            canvas.drawRect(left - 3, top - 3, left + 132, top + 96, paint);

            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
            canvas.drawRect(left - 1, top - 1, left + 128, top + 92, paint);
        }

        if(destroyed) {
            Paint paint = new Paint();
            paint.setColor(getRealColor());
            paint.setStrokeWidth(3);
            canvas.drawRect(left - 4, top - 4, left + 134, top + 98, paint);

            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
            canvas.drawRect(left - 1, top - 1, left + 128, top + 92, paint);
        }

        canvas.drawBitmap(image, rects[form][color], new Rect(left, top, left + 126, top + 90),null);
    }

    private int getRealColor() {
        switch (getColor()){
            case 1: return 0xFF6060e0;
            case 2: return 0xFFe0c000;
            case 3: return 0xFF80e000;
            case 4: return 0xFFe060c0;
            case 5: return 0xFFc0c0e0;
            case 6: return 0xFFa0c0e0;
            default: return 0xFFe08000;
        }
    }

    public void update(int left, int top) {
        this.left = left;
        this.top = top;
    }

    public Bitmap getImage(){
        return image;
    }

    public int getLeft(){
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getColor() {
        return color;
    }

    public void click(int x, int y) {
        if(x > left && x < left + 126){
            if(y > top && y < top + 90){
                //if(selected)
                //   destroy();
                selected = true;
                //return;
            }
        }

        //selected = false;
    }

    public void destroy(){
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setForm(int form) {
        this.form = form;
    }

    public void unselect() {
        selected = false;
    }

    public void select() {
        selected = true;
    }

    public int getForm() {
        return form;
    }

    public void undestroy() {
        destroyed = false;
    }

    public int getCenterX() {
        return left + 63;
    }
    public int getCenterY() {
        return top + 45;
    }

    public Rect getRect(){
        return rects[form][color];
    }
}
