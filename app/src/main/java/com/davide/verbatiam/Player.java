package com.davide.verbatiam;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;

import java.lang.reflect.Field;

public class Player extends AppCompatImageView{

    private Costants costants = new Costants();
    private DrawableCostants drawableCostants = new DrawableCostants();
    private Rect playerRect;

    public Player(Context context, float x, float y) {
        super(context);
        String string = drawableCostants.ship[drawableCostants.getPos()];
        int id = getResources().getIdentifier(string, "drawable", context.getPackageName());
        Drawable drawable = getResources().getDrawable(id, null);
        this.setImageDrawable(drawable);
        this.setX(x);
        this.setY(y);
        this.setMaxHeight(77);
        this.setMaxWidth(100);
        playerRect = new Rect((int)this.getX(),(int)this.getY(),(int)this.getX()+250, (int)this.getY()+185);
    }

    public Rect getRect()
    {
        return playerRect;
    }

    public Rect setRect(int left, int top, int right, int bottom)
    {
        playerRect.set(left + 80 ,top + 80,right + 300, bottom + 184);
        return playerRect;
    }
}
