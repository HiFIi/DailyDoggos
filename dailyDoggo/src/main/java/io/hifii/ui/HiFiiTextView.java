package io.hifii.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import io.hifii.R;

public class HiFiiTextView extends android.support.v7.widget.AppCompatTextView {

    public HiFiiTextView(Context context, AttributeSet attrs,
                         int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public HiFiiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        isInEditMode();
        init(attrs);

    }

    public HiFiiTextView(Context context) {
        super(context);
        isInEditMode();
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.HiFiiTextView);
            String fontName = a
                    .getString(R.styleable.HiFiiTextView_fontName);
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext()
                        .getAssets(), "fonts/" + fontName);
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }

}
