package com.davide.verbatiam;

import android.content.Context;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class EnemyLife extends ProgressBar {

    private Handler handler = new Handler();
    private Runnable runnable;

    private int speedY = 3;

    public EnemyLife(final Context context, AttributeSet attrs, int defStyleAttr, float x, float y, int maxLife) {
        super(context, attrs, defStyleAttr);
        this.setLayoutParams(new ConstraintLayout.LayoutParams(140, 15));
        this.setX(x);
        this.setY(y);
        this.setMax(maxLife);
        this.setProgress(maxLife);
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
