package com.davide.verbatiam;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;

public class Shield extends AppCompatImageView {

    private Handler handler = new Handler();
    private Runnable runnable;
    private Rect shieldRect;
    private int speedY = 3;

    public Shield(Context context, float x, float y) {
        super(context);
        Drawable drawable = getResources().getDrawable(R.drawable.blu);
        this.setLayoutParams(new ConstraintLayout.LayoutParams(75, 75));
        this.setImageDrawable(drawable);
        this.setX(x);
        this.setY(y);
        shieldRect = new Rect((int)this.getX(),(int)this.getY(),(int)this.getX()+75, (int)this.getY()+75);
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
        shieldRect.set((int)this.getX(),(int)this.getY() + speedY,(int)this.getX()+75, (int)(this.getY()+75)+ speedY);
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
        if(shieldRect.intersect(player.getRect()))
        {
            return true;
        }
        return false;
    }

    public Rect getBoundsEnemy()
    {
        return shieldRect;
    }
}
