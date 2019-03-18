package com.davide.verbatiam;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.widget.AppCompatImageView;
import java.util.Random;

public class Enemy extends AppCompatImageView {

    private Random random = new Random();
    private Handler handler = new Handler();
    private Runnable runnable;
    private int[] images = {
            R.drawable.enemy1,
            R.drawable.enemy2,
            R.drawable.enemy3,
            R.drawable.enemy4
    };
    private int path;
    private Rect boundsEnemy;
    private int speedY = 3;

    public Enemy(Context context, float x, float y)
    {
        super(context);
        path = random.nextInt(images.length);
        Drawable drawable = getResources().getDrawable(images[path]);
        this.setLayoutParams(new ConstraintLayout.LayoutParams(150, 150));
        this.setImageDrawable(drawable);
        this.setX(x);
        this.setY(y);
        boundsEnemy = new Rect((int)this.getX(),(int)this.getY(),(int)this.getX()+150, (int)this.getY()+150);
        handler.post(runnable = new Runnable() {
            @Override
            public void run() {
                updateY();
                handler.postDelayed(this,10);
            }
        });
    }

    public void updateY()
    {
        this.setY(this.getY() + speedY);
        boundsEnemy.set((int)this.getX(),(int)this.getY() + speedY,(int)this.getX()+150, (int)(this.getY()+150)+ speedY);
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

    public boolean collide(Player player)
    {
        if(boundsEnemy.intersect(player.getRect()))
        {
            return true;
        }
        return false;
    }

    public boolean collideShield(ShieldPlayer shieldPlayer)
    {
        if(boundsEnemy.intersect(shieldPlayer.getRect()))
        {
            return true;
        }
        return false;
    }

    public Rect getBoundsEnemy()
    {
        return boundsEnemy;
    }
}
