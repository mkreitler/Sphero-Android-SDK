package com.lbt.demos.kreitler.mark.demoUI;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by Mark on 11/6/2016.
 */

public class WidgetButton extends Widget {
    public interface Listener {
        void onPressed(WidgetButton button);
    }

    private ArrayList<Listener> listeners   = null;
    private String label                    = null;
    private String fontName                 = null;
    private int textColor                   = Color.WHITE;
    private Typeface font                   = null;
    private float textSize                  = WidgetLabel.DEFAULT_TEXT_SIZE;
    private Rect textBounds                 = new Rect();
    private boolean bTouched                = false;

    // Interface ///////////////////////////////////////////////////////////////////////////////////
    // Instance ------------------------------------------------------------------------------------
    public WidgetButton(Widget parent, int x, int y, int width, int height, float hAnchor, float vAnchor, String text, String fontName) {
        super(parent, x, y, width, height, hAnchor, vAnchor);

        listeners = new ArrayList<WidgetButton.Listener>();

        setFont(fontName);
        setText(text);
    }

    public void addListener(WidgetButton.Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(WidgetButton.Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setText(String newText) {
        label = newText;

        _paint.setTextSize(textSize);
        _paint.getTextBounds(newText, 0, newText.length() - 1, textBounds);
    }

    public void setFont(String fontName) {
        if (fontName != null && _context != null) {
            Resources res = _context.getResources();
            font = Typeface.createFromAsset(res.getAssets(), fontName);
        }
        else {
            font = Typeface.DEFAULT;
        }
    }

    public void setTextColor(int color) {
        textColor = color;
    }

    public void onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                swapColors();
                bTouched = true;
                break;
            case (MotionEvent.ACTION_MOVE) :
                break;
            case (MotionEvent.ACTION_UP) :
                onTouched();
                untouch();
                break;
            case (MotionEvent.ACTION_CANCEL) :
                untouch();
                break;
            case (MotionEvent.ACTION_OUTSIDE) :
                untouch();
                break;
            default :
                untouch();
                break;
        }
    }

    // Implementation //////////////////////////////////////////////////////////////////////////////
    @Override
    protected void draw(Canvas c) {
        if (c != null && _paint != null) {
            super.draw(c);

            _paint.setTypeface(font);
            _paint.setStyle(Paint.Style.FILL);
            _paint.setColor(textColor);

            int textWidth = textBounds.right - textBounds.left + 1;
            int textHeight = textBounds.bottom - textBounds.top + 1;
            c.drawText(label,
                    Math.round((worldBounds.left + worldBounds.right + textWidth) / 2),
                    Math.round((worldBounds.top + worldBounds.bottom + textHeight) / 2) - _paint.ascent(), _paint);
        }

        if (bDebugMode) {
            super.draw(c);
        }
    }

    protected void swapColors() {
        int temp = lineColor;
        lineColor = fillColor;
        fillColor = temp;
    }

    private void untouch() {
        if (bTouched) {
            swapColors();
        }

        bTouched = false;
    }

    private void onTouched() {
        for (int i=0; i<listeners.size(); ++i) {
            listeners.get(i).onPressed(this);
        }
    }
}
