package com.lbt.demos.kreitler.mark.lbt;

/**
 * Created by mark on 11/3/16.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lbt.demos.kreitler.mark.demoUI.Widget;
import com.lbt.demos.kreitler.mark.demoUI.WidgetLabel;
import com.lbt.demos.kreitler.mark.states.GameState;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class CanvasView extends SurfaceView implements SurfaceHolder.Callback {
    // Static --------------------------------------------------------------------------------------
    private static final int INPUT_QUEUE_CAPACITY = 100;

    public static Paint PAINT = new Paint();

    private static CanvasView THIS = null;

    public static interface Placeable {
        public void setPosition(int x, int y);
        public int getWidth();
        public int getHeight();
    }

    public static void PlacePlaceable(Placeable placeable, float anchorX, float anchorY) {
        if (THIS != null) {
            THIS.placePlaceable(placeable, anchorX, anchorY);
        }
    }

    public static void PlacePlaceables(ArrayList<Placeable> placeables,
                                       float margin,
                                       float spacing,
                                       float anchor,
                                       boolean bVertically) {
        if (THIS != null) {
            THIS.placePlaceables(placeables, margin, spacing, anchor, bVertically);
        }
    }

    // Instance ------------------------------------------------------------------------------------
    private Canvas canvas = null;
    private Context appContext = null;
    private SurfaceHolder surfaceHolder = null;
    private ArrayBlockingQueue<MotionEvent> inputQueue = new ArrayBlockingQueue<MotionEvent>(INPUT_QUEUE_CAPACITY);

    private WidgetLabel testLabel;

    public CanvasView(Context context) {
        super(context);

        appContext = context;
        getHolder().addCallback(this);

        // Make the GamePanel focusable so it can handle events.
        setFocusable(true);

        THIS = this;
    }

    // Adds or removes an event to the queue. Combining into a single accessor should make
    // queue access thread safe.
    public synchronized MotionEvent accessEvents(MotionEvent event) {
        MotionEvent eventOut = null;

        if (event != null) {
            inputQueue.add(event);
        }
        else {
            eventOut = inputQueue.poll();
        }

        return eventOut;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        accessEvents(event);
        return super.onTouchEvent(event);
    }

    public Canvas lock() {
        return surfaceHolder != null ? surfaceHolder.lockCanvas() : null;
    }

    public void unlock(Canvas canvas) {
        if (surfaceHolder != null) {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            GameState.Draw(canvas);
        }
    }

    // Implementation //////////////////////////////////////////////////////////////////////////////
    private void placePlaceable(Placeable placeable, float anchorX, float anchorY) {
        int x = Math.round(anchorX * getWidth());
        int y = Math.round(anchorY * getHeight());

        if (placeable != null) {
            placeable.setPosition(x,  y);
        }
    }

    private void placePlaceables(ArrayList<Placeable> placeables,
                                 float margin,
                                 float spacing,
                                 float anchor,
                                 boolean bVertically) {

        margin = Math.max(Math.min(1.0f, margin), 0.0f);

        int w = getWidth();
        int h = getHeight();
        if (bVertically) {
            // Distribute placeables vertically.
            int x = Math.round(w * anchor);
            int y = Math.round(h * margin);

            for (int i=0; i<placeables.size(); ++i) {
                Placeable placeable = placeables.get(i);

                if (placeable != null) {
                    placeables.get(i).setPosition(x, y);
                    y += placeables.get(i).getHeight();
                    if (i > 0) {
                        y += Math.round(placeables.get(i).getHeight() * spacing);
                    }
                }
            }
        }
        else {
            // Distribute placeables horizontally.
            int x = Math.round(w * margin);
            int y = Math.round(h * anchor);

            for (int i=0; i<placeables.size(); ++i) {
                Placeable placeable = placeables.get(i);

                if (placeable != null) {
                    placeables.get(i).setPosition(x, y);
                    x += placeables.get(i).getWidth();
                    if (i > 0) {
                        x += Math.round(placeables.get(i).getWidth() * spacing);
                    }
                }
            }
        }
    }
}
