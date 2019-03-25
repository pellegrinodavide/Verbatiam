package com.davide.verbatiam;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;

public class ExplosionEnemy extends AppCompatImageView {

    private Handler handler = new Handler();
    private Runnable runnable;
    final AnimationDrawable runExplosion;
    private int speedY = 3;

    public ExplosionEnemy(Context context, float x, float y) {
        super(context);
        this.setLayoutParams(new ConstraintLayout.LayoutParams(150, 150));
        this.setBackgroundResource(R.drawable.run_explosion);
        this.setX(x);
        this.setY(y);
        this.setVisibility(INVISIBLE);
        runExplosion = (AnimationDrawable)this.getBackground();
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

    public int getSpeedY()
    {
        return speedY;
    }

    public void setSpeedY(int speedY)
    {
        this.speedY = speedY;
    }

    public void startAnimation()
    {
        runExplosion.start();
    }
}
