package com.lbt.demos.kreitler.mark.demoUI;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

public class WidgetLabel extends Widget {
    // Interface ///////////////////////////////////////////////////////////////////////////////////
    // Static --------------------------------------------------------------------------------------
    public static final float DEFAULT_TEXT_SIZE = 16.0f;
    private static final int ERR_LINE_COLOR     = Color.YELLOW;
    private static final int ERR_FILL_COLOR     = Color.RED;

    // Instance ------------------------------------------------------------------------------------
    public WidgetLabel(Widget parent, int x, int y, float hAnchor, float vAnchor, String text, float textSize, String color, String fontName) {
        super(parent, 0, 0, 0, 0, hAnchor, vAnchor);

        setFont(fontName);
        setTextSize(textSize);
        setColor(color, color);
        setText(text);

        bAutoBound = true;
        computeBounds(x, y);
    }

    public WidgetLabel(Widget parent, int x, int y, String text, float textSize, String color, String fontName) {
        this(parent, x, y, 0.5f, 0.5f, text, textSize, color, fontName);
    }

    @Override
    public void setColor(int lineColor, int fillColor) {
        if(errMsg != null) {
            super.setColor(ERR_LINE_COLOR, ERR_FILL_COLOR);
        }
        else {
            super.setColor(lineColor, fillColor);
        }
    }

    public void setTextSize(float size) {
        if (size > 0.0f) {
            textSize = size;
        }
        else {
            textSize = DEFAULT_TEXT_SIZE;
            setError(ERR_INVALID_TEXT_SIZE);
        }

        computeBounds(localBounds.left + Math.round(hAnchor * (localBounds.right - localBounds.left + 1)),
                localBounds.top + Math.round(vAnchor * (localBounds.top - localBounds.bottom + 1)));
    }

    @Override
    public void setText(String newText) {
        if (errMsg == null) {
            text = newText;

            computeBounds(localBounds.left + Math.round(hAnchor * (localBounds.right - localBounds.left + 1)),
                    localBounds.top + Math.round(vAnchor * (localBounds.top - localBounds.bottom + 1)));
        }
    }

    @Override
    public String getText() {
        return text;
    }

    // Implementation //////////////////////////////////////////////////////////////////////////////
    // Static --------------------------------------------------------------------------------------
    private static final String ERR_INVALID_TEXT_SIZE   = "Invalid Text Size!";

    // Instance ------------------------------------------------------------------------------------
    private String text         = null;
    private Typeface font       = null;
    private float textSize      = DEFAULT_TEXT_SIZE;
    private int color           = Color.WHITE;
    private String errMsg       = null;
    private boolean bAutoBound  = false;

    protected void computeBounds(int x, int y) {
        if (bAutoBound) {
            _paint.setTextSize(textSize);
            _paint.getTextBounds(text, 0, text.length() - 1, _workBounds);

            localBounds.set(Math.round(x - hAnchor * _workBounds.width()),
                    Math.round(y - vAnchor * _workBounds.height()),
                    Math.round(x - hAnchor * _workBounds.width()) + _workBounds.width(),
                    Math.round(y - vAnchor * _workBounds.height()) + _workBounds.height());

            updateWorldBounds();
        }
    }

    @Override
    protected void draw(Canvas c) {
        if (c != null && _paint != null) {
            if (fillColor >= 0) {
                _paint.setColor(fillColor);
                c.drawRect(worldBounds.left,
                        worldBounds.top,
                        Math.max(worldBounds.left + 1, worldBounds.right),
                        Math.max(worldBounds.top + 1, worldBounds.bottom),
                        _paint);
            }

            if (lineColor >= 0) {
                _paint.setTypeface(font);
                _paint.setStyle(Paint.Style.FILL);
                _paint.setColor(lineColor);

                c.drawText(text, worldBounds.left, worldBounds.top - _paint.ascent(), _paint);
            }
        }
    }

    private void setError(String errMsg) {
        setColor("yellow", "red");
        setText(errMsg);

        Log.d("DemoUiLib", errMsg);
    }

    private void setFont(String fontName) {
        if (fontName != null && _context != null) {
            Resources res = _context.getResources();
            font = Typeface.createFromAsset(res.getAssets(), fontName);
        }
        else {
            font = Typeface.DEFAULT;
        }
    }
}
