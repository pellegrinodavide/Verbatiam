package com.davide.verbatiam;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;

public class Bullet extends AppCompatImageView {

    private Handler handler = new Handler();
    private Runnable runnable;
    private Rect boundsBullet;
    private int speedY = 15;
    private long millis;

    public Bullet(Context context, float x, float y) {
        super(context);
        this.setLayoutParams(new ConstraintLayout.LayoutParams(35, 70));
        this.setX(x);
        this.setY(y);
        this.setMaxWidth(5);
        this.setMaxHeight(11);
        boundsBullet = new Rect((int)this.getX(),(int)this.getY(),(int)this.getX()+35, (int)this.getY()+70);
        handler.post(runnable = new Runnable() {
            @Override
            public void run() {
                updateY();
                handler.postDelayed(this,millis);
            }
        });
    }

    public void updateY()
    {
        this.setY(this.getY() - speedY);
        boundsBullet.set((int)this.getX(),(int)this.getY() - 15,(int)this.getX()+35, (int)(this.getY()+70)-15);
    }

    public void setMillis(long time)
    {
        millis = time;
    }

    public int getSpeedY()
    {
        return speedY;
    }

    public void setSpeedY(int speedY)
    {
        this.speedY = speedY;
    }

    public void stopHandler()
    {
        handler.removeCallbacks(runnable);
    }

    public boolean collide(Enemy enemy)
    {
        if(boundsBullet.intersect(enemy.getBoundsEnemy()))
        {
            return true;
        }
        return false;
    }
}
