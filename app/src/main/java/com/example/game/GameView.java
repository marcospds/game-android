package com.example.game;

import android.animation.ValueAnimator;
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
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

import com.example.game.sprite.GemSprite;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Random random = new Random();
    private MainThread thread;
    private GemSprite[][] gems = new GemSprite[20][7];
    private int time = 0;
    private long pontos = 0;
    private GemSprite selecionado = null;
    private GemSprite lastSelected = null;
    private GemSprite lastNeighbor = null;
    private boolean isSplit = false;
    private Paint mPaint = new Paint();
    private List<Ball> balls = new ArrayList<>();
    private Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gems);
    private float r = 6f;
    private boolean clicked = false;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        for(int r = 0;r < gems.length;r++){
            for(int c = 0;c < gems[0].length;c++){
                gems[r][c] = new GemSprite(BitmapFactory.decodeResource(getResources(), R.drawable.gems), 100 + (c * 126), 100 + (r * 90), 0, new Random().nextInt(6));
            }
        }
        updateBall();
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {

        if(time == LocalDateTime.now().toLocalTime().toSecondOfDay())
            return;

        time = LocalDateTime.now().toLocalTime().toSecondOfDay();

        balls.removeIf(ball -> ball.time <= LocalDateTime.now().toLocalTime().toSecondOfDay() - 2);

        // 1 generateBalls(gems[0][0]);

        Arrays.stream(gems).forEach(row -> Arrays.stream(row).filter(GemSprite::isDestroyed).forEach(gem -> {
            pontos++;
            generateBalls(gem);
        }));

        for(int r = gems.length - 1;r > 0;r--){
            for(int c = 0;c < gems[r].length;c++){
                if(gems[r][c].isDestroyed()) {
                    gems[r][c] = new GemSprite(BitmapFactory.decodeResource(getResources(), R.drawable.gems), 100 + (c * 126), 100 + (r * 90), nextForm(r - 1, c), nextColor(r - 1, c));
                }
            }
        }

        for(int c = 0, r = 0;c < gems[r].length;c++){
            if(gems[r][c].isDestroyed()) {
                gems[r][c] = new GemSprite(BitmapFactory.decodeResource(getResources(), R.drawable.gems), 100 + (c * 126), 100 + (r * 90), 0, new Random().nextInt(6));
            }
        }

        for(int r = gems.length - 1;r >= 0;r--){
            int selecionados = 1;
            List<int[]> gemsSelecionadas = new ArrayList<>();
            for(int c = 0;c < gems[r].length;c++){
                if(c != gems[r].length - 1 && gems[r][c].getColor() == gems[r][c+1].getColor()){
                    selecionados++;
                    gemsSelecionadas.add(new int[]{r, c});
                    gemsSelecionadas.add(new int[]{r, c+1});
                } else {
                    if(selecionados == 3){
                        gemsSelecionadas.forEach(gem -> destroy(gems, gem[0], gem[1]));
                    }
                    if(selecionados == 4){
                        boolean last = false;
                        for (int[] gem : gemsSelecionadas) {
                            if(gems[gem[0]][gem[1]] == lastSelected || gems[gem[0]][gem[1]] == lastNeighbor){
                                last = true;
                                gems[gem[0]][gem[1]].setForm(2);
                                gems[gem[0]][gem[1]].destroy();
                            } else {
                                destroy(gems, gem[0], gem[1]);
                            }
                        }
                        if(last == false){
                            int[] gem = gemsSelecionadas.get(new Random().nextInt(gemsSelecionadas.size() - 1));
                            gems[gem[0]][gem[1]].setForm(2);
                            gems[gem[0]][gem[1]].undestroy();
                        }
                    }
                    if(selecionados >= 5){
                        boolean last = false;
                        for (int[] gem : gemsSelecionadas) {
                            if(gems[gem[0]][gem[1]] == lastSelected || gems[gem[0]][gem[1]] == lastNeighbor){
                                last = true;
                                gems[gem[0]][gem[1]].setForm(4);
                                gems[gem[0]][gem[1]].destroy();
                            } else {
                                destroy(gems, gem[0], gem[1]);
                            }
                        }
                        if(last == false){
                            int[] gem = gemsSelecionadas.get(new Random().nextInt(gemsSelecionadas.size() - 1));
                            gems[gem[0]][gem[1]].setForm(2);
                            gems[gem[0]][gem[1]].undestroy();
                        }
                    }
                    gemsSelecionadas.clear();
                    selecionados = 1;
                }
            }
        }

        for(int c = 0;c < gems[0].length;c++){
            int selecionados = 1;
            List<int[]> gemsSelecionadas = new ArrayList<>();
            for(int r = gems.length - 1;r >= 0;r--){
                if(r != 0 && gems[r][c].getColor() == gems[r-1][c].getColor()){
                    selecionados++;
                    gemsSelecionadas.add(new int[]{r, c});
                    gemsSelecionadas.add(new int[]{r - 1, c});
                } else {
                    if(selecionados == 3){
                        gemsSelecionadas.forEach(gem -> destroy(gems, gem[0], gem[1]));
                    }
                    if(selecionados == 4){
                        boolean last = false;
                        for (int[] gem : gemsSelecionadas) {
                            if(gems[gem[0]][gem[1]] == lastSelected || gems[gem[0]][gem[1]] == lastNeighbor){
                                last = true;
                                gems[gem[0]][gem[1]].setForm(2);
                                gems[gem[0]][gem[1]].destroy();
                            } else {
                                destroy(gems, gem[0], gem[1]);
                            }
                        }
                        if(last == false){
                            int[] gem = gemsSelecionadas.get(new Random().nextInt(gemsSelecionadas.size() - 1));
                            gems[gem[0]][gem[1]].setForm(2);
                            gems[gem[0]][gem[1]].undestroy();
                        }
                    }
                    if(selecionados >= 5){
                        boolean last = false;
                        for (int[] gem : gemsSelecionadas) {
                            if(gems[gem[0]][gem[1]] == lastSelected || gems[gem[0]][gem[1]] == lastNeighbor){
                                last = true;
                                gems[gem[0]][gem[1]].setForm(4);
                                gems[gem[0]][gem[1]].destroy();
                            } else {
                                destroy(gems, gem[0], gem[1]);
                            }
                        }
                        if(last == false){
                            int[] gem = gemsSelecionadas.get(new Random().nextInt(gemsSelecionadas.size() - 1));
                            gems[gem[0]][gem[1]].setForm(2);
                            gems[gem[0]][gem[1]].undestroy();
                        }
                    }
                    gemsSelecionadas.clear();
                    selecionados = 1;
                }
            }
        }

        if(lastSelected != null && lastSelected != null && !lastSelected.isDestroyed() && !lastNeighbor.isDestroyed()){
            int color = lastNeighbor.getColor();
            int form = lastNeighbor.getForm();
            lastNeighbor.setColor(lastSelected.getColor());
            lastNeighbor.setForm(lastSelected.getForm());
            lastSelected.setColor(color);
            lastSelected.setForm(form);
        }
        if(lastSelected != null && lastSelected.getForm() != 0){
            lastSelected.undestroy();
        }
        if(lastNeighbor != null && lastNeighbor.getForm() != 0){
            lastNeighbor.undestroy();
        }

        lastSelected = null;
        lastNeighbor = null;

        //updateBall();
    }

    private void destroy(GemSprite[][] gems, int r, int c){
        if(gems[r][c].isDestroyed())
            return;

        gems[r][c].destroy();

        if (gems[r][c].getForm() == 2) {
            for(int row = gems.length - 1;row >= 0;row--) {
                destroy(gems, row, c);
            }
            for (int col = 0;col < gems[r].length;col++) {
                destroy(gems, r, col);
            }
        }

        if (gems[r][c].getForm() == 4) {
            for(int row = gems.length - 1;row >= 0;row--) {
                for (int col = 0;col < gems[r].length;col++) {
                    if(gems[r][c].getColor() == gems[row][col].getColor()) {
                        destroy(gems, row, col);
                    }
                }
            }
        }

        if(gems[r][c] == lastSelected || gems[r][c] == lastNeighbor) {
            lastSelected = null;
            lastNeighbor = null;
        }
    }

    private void updateBall() {
        //isSplit = true;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(1000);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<Ball> ballsI = new ArrayList<>();
                ballsI.addAll(balls);
                ballsI.parallelStream().forEach(ball -> {
                    ball.x += ball.vX;
                    ball.y += ball.vY;
                    ball.vX += ball.aX;
                    ball.vY += ball.aY;
                });

                invalidate();
            }
        });
        animator.start();
    }

    private int nextForm(int r, int c) {
        if(r == -1)
            return 0;

        if(gems[r][c].isDestroyed())
            return nextForm(r - 1, c);

        //gems[r][c].destroy();

        return gems[r][c].getForm();
    }

    public int nextColor(int r, int c){
        if(r == -1)
            return new Random().nextInt(6);

        if(gems[r][c].isDestroyed())
            return nextColor(r - 1, c);

        gems[r][c].destroy();

        return gems[r][c].getColor();
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        if(canvas!=null){
            for(GemSprite[] row : gems){
                for(GemSprite gem : row){
                    gem.draw(canvas);
                }
            }

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            canvas.drawText("Pontos: " + pontos, 0, 50, paint);
        }

        List<Ball> balls1 = new ArrayList<>();
        balls1.addAll(balls);

        balls1.parallelStream().forEach(ball -> {
            mPaint.setColor(ball.color);
            canvas.drawCircle(ball.x, ball.y, ball.r, mPaint);
        });
    }

    private void generateBalls(GemSprite gem) {

        //balls.clear();

        //int width = mBitmap.getWidth();
        //int height = mBitmap.getHeight();

        Rect rect = gem.getRect();

        for (int i = rect.left; i < rect.right; i += 2 * r) {
            for (int j = rect.top; j < rect.bottom; j += 2 * r) {
                Ball ball = new Ball();
                ball.color = gem.getImage().getPixel(i, j);;
                ball.x = gem.getCenterX();
                ball.y = gem.getCenterY();
                ball.r = r * 1.5f;
                ball.vX = random.nextInt(5) + (-1 * random.nextInt(5)); //random.nextInt(10) + random.nextFloat() - 10.0f;//(-20 , 20)
                ball.vY = random.nextInt(10) + (-1 * random.nextInt(10)); //random.nextInt(10) + random.nextFloat() - 10.0f;//(-20 , 20)
                ball.aX = 0;
                ball.aY = 0.1f;
                ball.time = LocalDateTime.now().toLocalTime().toSecondOfDay();
                balls.add(ball);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //break;
            case MotionEvent.ACTION_MOVE:
                //break;
            case MotionEvent.ACTION_UP:
                click(x, y);
        }

        return false;
    }


    public void click(int x, int y) {

        //if(clicked)
        //    balls.clear();
        //else
        //    generateBalls(gems[0][0]);

        //clicked = !clicked;


        List<GemSprite> selecteds = new LinkedList<>();
        boolean changed = false;
        for (int r = gems.length - 1; r >= 0; r--) {
            for (int c = 0; c < gems[r].length; c++) {
                GemSprite gem = gems[r][c];
                gem.click(x, y);
                if (gem.isSelected()) {
                    selecteds.add(gem);
                    GemSprite neighbor = getNeighborSelected(r, c);
                    if (neighbor != null) {

                        lastSelected = gem;
                        lastNeighbor = neighbor;

                        int color = lastNeighbor.getColor();
                        int form = lastNeighbor.getForm();
                        lastNeighbor.setColor(lastSelected.getColor());
                        lastNeighbor.setForm(lastSelected.getForm());
                        lastSelected.setColor(color);
                        lastSelected.setForm(form);

                        neighbor.unselect();
                        gem.unselect();

                        changed = true;
                    }
                }
            }
        }

       if(selecteds.size() >= 2){
            selecteds.forEach(GemSprite::unselect);
        }

        if (!changed) {
            for (int r = gems.length - 1; r >= 0; r--) {
                for (int c = 0; c < gems[r].length; c++) {
                    GemSprite gem = gems[r][c];
                    gem.click(x, y);
                }
            }
        }
    }

    public GemSprite getNeighborSelected(int r, int c){
        if(r != 0 && gems[r - 1][c].isSelected())
            return gems[r - 1][c];
        if(r != gems.length - 1 && gems[r + 1][c].isSelected())
            return gems[r + 1][c];
        if(c != 0 && gems[r][c - 1].isSelected())
            return gems[r][c - 1];
        if(c != gems[0].length - 1 && gems[r][c + 1].isSelected())
            return gems[r][c + 1];
        return null;
    }
}
