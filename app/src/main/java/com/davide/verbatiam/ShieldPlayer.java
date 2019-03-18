package com.davide.verbatiam;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;

public class ShieldPlayer extends AppCompatImageView {

    private Rect shieldRect;

    public ShieldPlayer(Context context, float x, float y) {
        super(context);
        Drawable drawable = getResources().getDrawable(R.drawable.shield);
        this.setLayoutParams(new ConstraintLayout.LayoutParams(550, 550));
        this.setImageDrawable(drawable);
        this.setX(x);
        this.setY(y);
        shieldRect = new Rect((int)this.getX(),(int)this.getY(),(int)this.getX()+550, (int)this.getY()+550);
    }

    public Rect getRect()
    {
        return shieldRect;
    }

    public Rect setRect(int left, int top, int right, int bottom)
    {
        shieldRect.set(left + 80 ,top + 80,right + 300, bottom + 184);
        return shieldRect;
    }
}
