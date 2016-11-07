package com.lbt.demos.kreitler.mark.lbt;

import android.graphics.Canvas;

/**
 * Created by mark on 11/3/16.
 */
public class GameThread extends Thread {
    private boolean running             = false;
    private CanvasView gameView         = null;

    // Interface ///////////////////////////////////////////////////////////////////////////////////
    public void setViewAndStart(CanvasView view) {
        gameView = view;
        setRunning(true);
    }

    public void setRunning(boolean running) {
        boolean bDoStart = false;

        if (!this.running && running) {
            bDoStart = true;
        }

        this.running = running;

        if (bDoStart) {
            this.start();
        }
    }

    @Override
    public void run() {
        while (running) {
            // update game state
            // render state to the screen

            if (gameView != null) {
                Canvas canvas = gameView.lock();

                if (canvas != null) {
                    synchronized(gameView.getSurfaceHolder()) {
                        gameView.draw(canvas);
                    }

                    gameView.unlock(canvas);
                }
            }
        }
    }

    // Implementation //////////////////////////////////////////////////////////////////////////////
}
