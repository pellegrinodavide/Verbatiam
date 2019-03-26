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

    private Handler handlerLife = new Handler();
    private Runnable runnableLife;
    private int speedY = 3;
    private int life = 50;
    private int contLife = 0;

    public EnemyLife(final Context context, AttributeSet attrs, int defStyleAttr, float x, float y) {
        super(context, attrs, defStyleAttr);
        this.setLayoutParams(new ConstraintLayout.LayoutParams(140, 15));
        this.setX(x);
        this.setY(y);
        this.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);

        handlerLife.post(runnableLife = new Runnable() {
            @Override
            public void run() {
                setMaxLife();
                setProgressLife();
                handlerLife.postDelayed(this,10000);
                contLife = contLife + 25;
            }
        });

        handler.post(runnable = new Runnable() {
            @Override
            public void run() {
                updateY();
                handler.postDelayed(this,10);
            }
        });
    }

    public void setMaxLife()
    {
        this.setMax(life + contLife);
    }

    public void setProgressLife()
    {
        this.setProgress(life + contLife);
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
