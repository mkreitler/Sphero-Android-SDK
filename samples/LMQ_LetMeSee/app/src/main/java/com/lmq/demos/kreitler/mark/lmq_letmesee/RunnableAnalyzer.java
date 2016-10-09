/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lmq.demos.kreitler.mark.lmq_letmesee;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.bluetooth.BluetoothAdapter;

import java.util.Hashtable;
import java.util.List;


/**
 * View that draws, takes keystrokes, etc. for a simple LunarLander game.
 *
 * Has a mode which RUNNING, PAUSED, etc. Has a x, y, dx, dy, ... capturing the
 * current ship physics. All x/y etc. are measured with (0,0) at the lower left.
 * updatePhysics() advances the physics based on realtime. draw() renders the
 * ship, and does an invalidate() to prompt another draw() as soon as possible
 * by the system.
 */
public class RunnableAnalyzer extends SurfaceView implements SurfaceHolder.Callback {
    public static Paint _paint      = new Paint();
    protected static Rect _bounds   = new Rect();

    public class GameThread extends Thread  {
        protected SurfaceHolder mSurfaceHolder  = null;
        protected Handler mHandler              = null;
        protected Context mContext              = null;
        protected boolean mRun                  = false;
        protected final Object mRunLock         = new Object();
        protected int mCanvasWidth              = 0;
        protected int mCanvasHeight             = 0;
        protected boolean bStarted              = false;

        public GameThread(SurfaceHolder surfaceHolder, Context context,
                          Handler handler) {
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;
        }

        public void run() {
            long lastTimeMS = java.lang.System.currentTimeMillis();

            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        // Critical section. Do not allow mRun to be set false until
                        // we are sure all canvas draw operations are complete.
                        //
                        // If mRun has been toggled false, inhibit canvas operations.
                        long currentTimeMS = java.lang.System.currentTimeMillis();
                        int dtMS = (int)(currentTimeMS - lastTimeMS);
                        lastTimeMS = currentTimeMS;

                        synchronized (mRunLock) {
                            if (mRun) doDraw(c);
                        }
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        // SurfaceHolder.Callback Interface ////////////////////////////////////////////////////////
        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;

                if (!bStarted) {

                    bStarted = true;
                }

                /*
                // don't forget to resize the background image
                mBackgroundImage = Bitmap.createScaledBitmap(
                        mBackgroundImage, width, height, true);
                */
            }
        }

        public void setRunning(boolean b) {
            // Do not allow mRun to be modified while any canvas operations
            // are potentially in-flight. See doDraw().
            if (mRun != b) {
                synchronized (mRunLock) {
                    mRun = b;
                }
            }
        }

        /**
         * Starts the game, setting parameters for the current difficulty.
         */
        public void doStart() {
            synchronized (mSurfaceHolder) {
                mRun = true;
            }
        }

        /**
         * Stops the game.
         */
        public void doStop() {
            synchronized(mSurfaceHolder) {
                mRun = false;
            }
        }

        /**
         * Pauses the physics update & animation.
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
            }
        }

        /**
         * Resumes from a pause.
         */
        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
            }
        }

        public boolean onTouch(View v, MotionEvent e) {
            boolean bHandled = false;

            synchronized(mSurfaceHolder) {
            }

            return bHandled;
        }

        private void doDraw(Canvas c) {
            c.save();

            c.drawARGB(1, 0, 0, 0);

            _paint.setTypeface(Typeface.DEFAULT);
            _paint.setStyle(Paint.Style.FILL);
            _paint.setTextSize(25);
            _paint.setColor(Color.RED);
            DrawTextCentered(c, "Fred ate bread.");

            c.restore();
        }
    }
    protected Rect GetTextSize(String text) {
        text = text.replace(' ', 'w');
        RunnableAnalyzer._paint.getTextBounds(text, 0, text.length(), _bounds);
        return _bounds;
    }

    protected void DrawTextCentered(Canvas c, String text) {
        RunnableAnalyzer._paint.getTextBounds(text, 0, text.length(), _bounds);

        int x = (c.getWidth() / 2) - (_bounds.width() / 2);
        int y = (c.getHeight() / 2) - (_bounds.height() / 2) - 12;
        c.drawText(text, x, y + RunnableAnalyzer._paint.getTextSize() / 2, RunnableAnalyzer._paint);
    }

    protected void DrawTextCenteredAtY(Canvas c, String text, int y) {
        RunnableAnalyzer._paint.getTextBounds(text, 0, text.length(), _bounds);

        int x = (c.getWidth() / 2) - (_bounds.width() / 2);
        c.drawText(text, x, y + RunnableAnalyzer._paint.getTextSize() / 2, RunnableAnalyzer._paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean bHandled = false;

        if (thread != null) {
            bHandled = thread.onTouch(this, event);
        }

        return bHandled;
    }

    /** Pointer to the text view to display "Paused.." etc. */
    /** The thread that actually draws the animation */
    private GameThread thread   = null;

    public RunnableAnalyzer(Context context) {
        super(context);
        createThreadAndResources(context);
    }

    public RunnableAnalyzer(Context context, AttributeSet attrs) {
        super(context, attrs);
        createThreadAndResources(context);
    }

    public RunnableAnalyzer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createThreadAndResources(context);
    }

    protected void initUI(Context context) {
    }

    protected void createThreadAndResources(Context context) {
        Resources res = context.getResources();

        initUI(context);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        // create thread only; it's started in surfaceCreated()
        thread = new GameThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });
        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public GameThread getThread() {
        return thread;
    }

    public void onStop() {
        if (thread != null) {
            thread.doStop();
        }
    }

    public void onStart() {
        if (thread != null) {
            thread.doStart();
        }
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        thread.setSurfaceSize(width, height);
    }
    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }
    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void OnPause() {
        getThread().pause();
    }

    public void OnResume() {
        getThread().unpause();
    }
}
