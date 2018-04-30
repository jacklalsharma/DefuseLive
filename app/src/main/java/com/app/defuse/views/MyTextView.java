package com.app.defuse.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView{

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Typeface fonts = Typeface.createFromAsset(context.getAssets(),
                "fonts/skranji_bold.ttf");
        setTypeface(fonts);
    }
}
