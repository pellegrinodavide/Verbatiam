package com.davide.verbatiam;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;

public class ExplosionPlayer extends AppCompatImageView {

    private Handler handler = new Handler();
    private Runnable runnable;
    final AnimationDrawable runExplosion;
    private Costants costants = new Costants();

    public ExplosionPlayer(Context context, float x, float y) {
        super(context);
        this.setBackgroundResource(R.drawable.run_explosion);
        this.setX(x);
        this.setY(y);
        this.setMaxHeight(77);
        this.setMaxWidth(100);
        this.setVisibility(INVISIBLE);
        runExplosion = (AnimationDrawable)this.getBackground();
    }

    public void changePos(boolean rect_actionFlagDX, boolean rect_actionFlagSX)
    {
        //Move rectangle
        if(rect_actionFlagDX && (this.getX()+350) < costants.SCREEN_WIDTH)
        {
            //Destra
            this.setX(this.getX() + 15);
        }
        if(rect_actionFlagSX && this.getX() > 0)
        {
            //Sinistra
            this.setX(this.getX() - 15);
        }
    }

    public void startAnimation()
    {
        runExplosion.start();
    }
}
