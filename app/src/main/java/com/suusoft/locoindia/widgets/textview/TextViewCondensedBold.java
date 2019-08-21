package com.suusoft.locoindia.widgets.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by SuuSoft.com on 12/29/2015.
 */
public class TextViewCondensedBold extends TextView {

    public TextViewCondensedBold(Context context) {
        super(context);

        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/RobotoCondensed-Bold.ttf");
        this.setTypeface(face);
    }

    public TextViewCondensedBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/RobotoCondensed-Bold.ttf");
        this.setTypeface(face);
    }

    public TextViewCondensedBold(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/RobotoCondensed-Bold.ttf");
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

}
