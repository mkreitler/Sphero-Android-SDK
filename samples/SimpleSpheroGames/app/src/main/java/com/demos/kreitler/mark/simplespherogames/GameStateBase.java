package com.demos.kreitler.mark.simplespherogames;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.orbotix.async.CollisionDetectedAsyncData;
import com.orbotix.common.sensor.DeviceSensorsData;
import com.orbotix.common.sensor.LocatorData;

/**
 * Created by Mark on 6/15/2016.
 */
public class GameStateBase {
    // Interface ///////////////////////////////////////////////////////////////////////////////////
    // Static --------------------------------------------------------------------------------------

    // Instance ------------------------------------------------------------------------------------
    public GameStateBase(GameView.GameThread gameThread) {
        game = gameThread;
    }

    public void Enter(GameView.GameThread game) {}
    public boolean Update(int dtMS) {return true;}
    public void Exit() {}
    public void Draw(Canvas c) {}
    public boolean OnTouch(View v, MotionEvent e) {return false;}
    public void OnPause() {}
    public void OnResume() {}

    public void ProcessCollision(CollisionDetectedAsyncData colData) {}
    public void ProcessLocatorData(LocatorData locDat) {}
    public void ProcessSensorData(DeviceSensorsData sensorData) {}

    // Implementation //////////////////////////////////////////////////////////////////////////////
    // Static --------------------------------------------------------------------------------------
    protected static Rect _bounds = new Rect();

    // Instance ------------------------------------------------------------------------------------
    protected GameView.GameThread game = null;
    protected Rect GetTextSize(String text) {
        text = text.replace(' ', 'w');
        GameView._paint.getTextBounds(text, 0, text.length(), _bounds);
        return _bounds;
    }

    protected void DrawTextCentered(Canvas c, String text) {
        GameView._paint.getTextBounds(text, 0, text.length(), _bounds);

        int x = (c.getWidth() / 2) - (_bounds.width() / 2);
        int y = (c.getHeight() / 2) - (_bounds.height() / 2) - 12;
        c.drawText(text, x, y + GameView._paint.getTextSize() / 2, GameView._paint);
    }

    protected void DrawTextCenteredAtY(Canvas c, String text, int y) {
        GameView._paint.getTextBounds(text, 0, text.length(), _bounds);

        int x = (c.getWidth() / 2) - (_bounds.width() / 2);
        c.drawText(text, x, y + GameView._paint.getTextSize() / 2, GameView._paint);
    }
}
