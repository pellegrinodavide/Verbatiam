package com.davide.verbatiam;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class EnemyLife extends ProgressBar {

    private Handler handler = new Handler();
    private Runnable runnable;
    private int speedY = 3;
    private int maxLife = 50;

    public EnemyLife(Context context, AttributeSet attrs, int defStyleAttr, float x, float y) {
        super(context, attrs, defStyleAttr);
        this.setLayoutParams(new ConstraintLayout.LayoutParams(140, 15));
        this.setX(x);
        this.setY(y);
        this.setProgress(getMaxLife());
        this.setMax(getMaxLife());
        this.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        handler.post(runnable = new Runnable() {
            @Override
            public void run() {
                updateY();
                handler.postDelayed(this,10);
            }
        });
    }

    public void setMaxLife(int life)
    {
        maxLife = life;
    }

    public int getMaxLife()
    {
        return maxLife;
    }

    public void updateY()
    {
        this.setY(this.getY() + speedY);
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
}
