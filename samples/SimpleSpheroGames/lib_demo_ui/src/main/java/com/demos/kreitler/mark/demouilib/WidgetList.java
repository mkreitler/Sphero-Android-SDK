package com.demos.kreitler.mark.demouilib;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Vector;

/**
 * Created by Mark on 6/26/2016.
 */
public class WidgetList extends WidgetBase implements IWidgetListener {
    // Interface ///////////////////////////////////////////////////////////////////////////////////
    // Static --------------------------------------------------------------------------------------

    // Instance ------------------------------------------------------------------------------------
    public WidgetList(WidgetBase parent, float align, float vSpace, WidgetLabel title, Vector<WidgetLabel> entries) {
        super(parent, 0, 0, 0, 0);

        childAlign = align;
        vertSpace = vSpace;

        if (title != null) {
            title.SetTouchable(false);
            title.SetTouchable(false);
            AddChild(title);
        }

        if (entries != null) {
            for (int i = 0; i <entries.size(); ++i) {
                entries.elementAt(i).AddListener(this);
                AddChild(entries.elementAt(i));
            }
        }
    }

    // IWidgetListener -----------------------------------------------------------------------------
    public boolean OnWidgetTouchStart(WidgetBase widget, int localX, int localY) {
        for (int i=0; i<listeners.size(); ++i) {
            listeners.elementAt(i).OnWidgetTouchStart(widget, localX, localY);
        }

        return true;
    }

    public boolean OnWidgetTouchEnd(WidgetBase widget, int localX, int localY) {
        for (int i=0; i<listeners.size(); ++i) {
            listeners.elementAt(i).OnWidgetTouchEnd(widget, localX, localY);
        }

        return true;
    }

    public boolean OnWidgetDrag(WidgetBase widget, int localX, int localY) {
        for (int i=0; i<listeners.size(); ++i) {
            listeners.elementAt(i).OnWidgetDrag(widget, localX, localY);
        }

        return true;
    }

    public boolean OnWidgetTouchCancel(WidgetBase widget, int localX, int localY) {
        for (int i=0; i<listeners.size(); ++i) {
            listeners.elementAt(i).OnWidgetTouchCancel(widget, localX, localY);
        }

        return true;
    }

    // Implementation //////////////////////////////////////////////////////////////////////////////
    // Static --------------------------------------------------------------------------------------

    // Instance ------------------------------------------------------------------------------------
    private float childAlign    = 0.5f;
    private float vertSpace     = 0.0f;

    @Override
    protected void ComputeBounds() {
        if (children.size() > 0) {
            int maxWidth    = 0;
            int totalHeight = 0;

            for (int i=0; i<children.size(); ++i) {
                Rect childBounds = children.elementAt(i).GetWorldBounds();
                maxWidth = Math.max(maxWidth, childBounds.width());
                totalHeight += childBounds.height();

                if (i != children.size() - 1) {
                    totalHeight += Math.round(childBounds.height() * vertSpace);
                }
            }

            int left = x - Math.round(anchorX * maxWidth);
            int top  = y - Math.round(anchorY * totalHeight);
            int right = left + maxWidth;
            int bottom = top + totalHeight;

            bounds.set(left, top, right, bottom);

            ComputeChildBounds();
        }
    }

    @Override
    protected void ComputeChildBounds() {
        int childTop = bounds.top;
        for (int i=0; i<children.size(); ++i) {
            WidgetBase child = children.elementAt(i);

            Rect childBounds = child.GetWorldBounds();
            int childLeft = bounds.left + Math.round(childAlign * (bounds.width() - childBounds.width()));
            child.SetTopLeft(childTop, childLeft);

            childTop += Math.round(childBounds.height() * (1.0f + vertSpace));
        }
    }
}
