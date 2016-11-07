package com.lbt.demos.kreitler.mark.demoUI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.lbt.demos.kreitler.mark.lbt.CanvasView;

import java.util.ArrayList;

/**
 * Created by mark on 11/4/16.
 */

public class Widget implements CanvasView.Placeable {
    protected static Paint _paint               = new Paint();
    private static ArrayList<Widget> Widgets    = new ArrayList<Widget>();
    protected static Rect _workBounds           = new Rect(0, 0, 0, 0);
    protected static Context _context           = null;

    protected float vAnchor             = 0.5f;
    protected float hAnchor             = 0.5f;
    protected Rect localBounds          = null;
    protected Rect worldBounds          = null;
    protected boolean bDebugMode        = false;
    protected int lineColor             = Color.WHITE;
    protected int fillColor             = Color.BLACK;
    protected Object userData           = null;

    private ArrayList<Widget> children  = new ArrayList<Widget>();
    private int lineWidth               = 1;
    private Widget parent               = null;

    // Interface ///////////////////////////////////////////////////////////////////////////////////
    // Static --------------------------------------------------------------------------------------
    public static void Add(Widget newWidget) {
        Widgets.add(newWidget);
    }

    public static void Remove(Widget oldWidget) {
        Widgets.remove(oldWidget);
    }

    public static void RemoveAll() {
        Widgets.clear();
    }

    public static void DrawAll(Canvas canvas) {
        for (int i=0; i<Widgets.size(); ++i) {
            Widgets.get(i).drawSelf(canvas, 0, 0);
        }
    }

    public static Widget WidgetAt(int x, int y) {
        Widget foundWidget = null;

        for (int i=0; i<Widgets.size(); ++i) {
            foundWidget = Widgets.get(i).getContainer(x, y);
            if (foundWidget != null) {
                break;
            }
        }

        return foundWidget;
    }

    public static void OnInputEvent(MotionEvent event) {
        Widget inputWidet = GetWidgetForEvent(event);
    }

    private static Widget GetWidgetForEvent(MotionEvent event) {
        Widget eventWidget = null;

        for (int i=0; eventWidget == null && i<Widgets.size(); ++i) {
            int x = Math.round(event.getX());
            int y = Math.round(event.getY());

            eventWidget = Widgets.get(i).getContainer(x, y);
            if (eventWidget != null) {
                eventWidget.onTouchEvent(event);
            }
        }

        return eventWidget;
    }

    // Instance ------------------------------------------------------------------------------------
    public Widget(Widget parent, int x, int y, int width, int height, float hAnchor, float vAnchor) {
        int top = Math.round(y - vAnchor * height);
        int left = Math.round(x - hAnchor * width);
        int bottom = top + Math.max(1, height) - 1;
        int right = left + Math.max(1, width) - 1;

        localBounds = new Rect(top, left, bottom, right);
        worldBounds = new Rect(top, left, bottom, right); // Will be updated if parent != null.

        setAnchor(vAnchor, hAnchor);

        setParent(parent);
    }

    public void setUserData(Object newData) {
        userData = newData;
    }

    public Object getUserData() {
        return userData;
    }

    public void onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                break;
            case (MotionEvent.ACTION_MOVE) :
                break;
            case (MotionEvent.ACTION_UP) :
                break;
            case (MotionEvent.ACTION_CANCEL) :
                break;
            case (MotionEvent.ACTION_OUTSIDE) :
                break;
            default :
                break;
        }
    }

    public void setParent(Widget newParent) {
        if (parent != newParent) {
            if (parent != null) {
                parent.removeChild(this);
            }

            parent = newParent;

            updateWorldBounds();

            if (parent != null) {
                parent.addChild(this);
            }

            Widget.Remove(this);
            if (newParent == null) {
                Widget.Add(this);
            }
        }
    }

    public void destroy() {
        Widget.Remove(this);
    }

    public void setLineWidth(int newWidth) {
        lineWidth = Math.max(newWidth, 0);
    }

    public Rect getLocalBounds() {
        return localBounds;
    }
    public Rect getWorldBounds() { return worldBounds; }

    // Updating the anchor doesn't change the widget's position.
    public void setAnchor(float hAnchor, float vAnchor) {
        this.vAnchor = vAnchor;
        this.hAnchor = hAnchor;
    }

    public void touchStart() {
        // Override to provide custom functionality.
    }

    public void touchEnd() {
        // Override to provide custom functionality.
    }

    public int getTop() {
        return localBounds.top;
    }

    public int getLeft() {
        return localBounds.left;
    }

    public int getX() {
        return Math.round(localBounds.left + (localBounds.right - localBounds.left) * hAnchor);
    }

    public int getY() {
        return Math.round(localBounds.top + (localBounds.bottom - localBounds.top) * vAnchor);
    }

    public int getWidth() { return localBounds.right - localBounds.left + 1; }

    public int getHeight() { return localBounds.bottom - localBounds.top + 1; }

    public void setColor(int newLineColor, int newFillColor) {
        lineColor = newLineColor;
        fillColor = newFillColor;
    }

    public void setColor(String lineColorStr, String fillColorStr) {
        lineColorStr = lineColorStr.trim().toLowerCase();
        fillColorStr = fillColorStr.trim().toLowerCase();
        setColor(stringToColor(lineColorStr), stringToColor(fillColorStr));
    }

    public void setText(String newText) {}

    public String getText() {
        return null;
    }

    public void drawSelf(Canvas canvas, int originX, int originY) {
        draw(canvas);
        drawChildren(canvas);
    }

    public void drawChildren(Canvas canvas) {
        for (int i=0; i<children.size(); ++i) {
            children.get(i).drawSelf(canvas, getLeft(), getTop());
        }
    }

    public void setPosition(int x, int y) {
        int width = getWidth();
        int height = getHeight();

        int newLeft = Math.round(x - vAnchor * width);
        int newTop = Math.round(y - hAnchor * height);

        localBounds.top = newTop;
        localBounds.left = newLeft;
        localBounds.right = newLeft + width - 1;
        localBounds.bottom = newTop + height - 1;

        updateWorldBounds();
    }

    public void move(int dx, int dy) {
        localBounds.top += dy;
        localBounds.left += dx;
        localBounds.right += dx;
        localBounds.bottom += dy;

        updateWorldBounds();
    }

    // Implementation //////////////////////////////////////////////////////////////////////////////
    // Protected -----------------------------------------------------------------------------------
    protected void draw(Canvas canvas) {
        if (lineWidth > 0 && lineColor >= 0) {
            _paint.setColor(lineColor);
            canvas.drawRect(worldBounds.left, worldBounds.top, worldBounds.right, worldBounds.bottom, _paint);
        }

        if (fillColor >= 0) {
            _paint.setColor(fillColor);
            canvas.drawRect(worldBounds.left + lineWidth,
                    worldBounds.top + lineWidth,
                    Math.max(worldBounds.left + 1, worldBounds.right - lineWidth),
                    Math.max(worldBounds.top + 1, worldBounds.bottom - lineWidth),
                    _paint);
        }
    }

    protected Widget getContainer(int x, int y) {
        Widget container = null;

        if (contains(x, y)) {
            container = this;

            for (int i=0; i<children.size(); ++i) {
                if (children.get(i).contains(x, y)) {
                    container = children.get(i).getContainer(x, y);
                    break;
                }
            }
        }

        return container;
    }

    protected int stringToColor(String colorStr) {
        int color = Color.BLACK;

        colorStr = colorStr != null ? colorStr.trim().toLowerCase() : "white";

        switch(colorStr) {
            case "black": {
                color = Color.BLACK;
                break;
            }

            case "blue": {
                color = Color.BLUE;
                break;
            }

            case "cyan": {
                color = Color.CYAN;
                break;
            }

            case "dkgray":case "dkgrey": {
                color = Color.DKGRAY;
                break;
            }

            case "gray":case "grey": {
                color = Color.GRAY;
                break;
            }

            case "green": {
                color = Color.GREEN;
                break;
            }

            case "ltgray":case "ltgrey": {
                color = Color.LTGRAY;
                break;
            }

            case "magenta": {
                color = Color.MAGENTA;
                break;
            }

            case "red": {
                color = Color.RED;
                break;
            }

            case "transparent": {
                color = Color.TRANSPARENT;
                break;
            }

            case "white": {
                color = Color.WHITE;
                break;
            }

            case "yellow": {
                color = Color.YELLOW;
                break;
            }

            default: {
                if (colorStr.substring(0, 0) == "#" && (colorStr.length() == 9 || colorStr.length() == 7)) {
                    int alpha = 0xff;

                    if (colorStr.length() == 9) {
                        // ARGB format.
                        alpha = Integer.parseInt("0000" + colorStr.substring(1, 2), 16);
                        colorStr = colorStr.substring(3);
                    }
                    else {
                        colorStr = colorStr.substring(1);
                    }

                    int red = Integer.parseInt("0000" + colorStr.substring(0, 1), 16);
                    int green = Integer.parseInt("0000" + colorStr.substring(2, 3), 16);
                    int blue = Integer.parseInt("0000" + colorStr.substring(4, 5), 16);

                    color = Color.argb(alpha, red, green, blue);
                }
                else {
                    color = -1;
                }

                break;
            }
        }

        return color;
    }

    protected void updateWorldBounds() {
        worldBounds.top = localBounds.top;
        worldBounds.left = localBounds.left;
        worldBounds.right = localBounds.right;
        worldBounds.bottom = localBounds.bottom;

        if (parent != null) {
            Rect parentWorldBounds = parent.getWorldBounds();

            worldBounds.top += parentWorldBounds.top;
            worldBounds.left += parentWorldBounds.left;
            worldBounds.right += parentWorldBounds.left;
            worldBounds.bottom += parentWorldBounds.bottom;
        }
    }

    // Private -------------------------------------------------------------------------------------
    private void addChild(Widget child) {
        children.add(child);
    }

    private void removeChild(Widget child) {
        children.remove(child);
    }

    private boolean contains(int x, int y) {
        return x >= worldBounds.left &&
                y >= worldBounds.top &&
                x <= worldBounds.right &&
                y <= worldBounds.bottom;
    }
}
