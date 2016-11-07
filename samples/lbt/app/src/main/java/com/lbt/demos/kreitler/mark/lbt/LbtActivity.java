package com.lbt.demos.kreitler.mark.lbt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

public class LbtActivity extends Activity {
    private static final int BLUETOOTH_ENABLE_INTENT_CODE = 0;

    private GameThread gameThread               = null;
    private BluetoothAdapter BA                 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBluetooth();
    }

    @Override
    public void onDestroy() {
        if (gameThread != null) {
            gameThread.setRunning(false);

            boolean bRetry = true;

            while (bRetry) {
                try {
                    gameThread.join();
                    bRetry = false;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        super.onDestroy();
    }

    // Implementation //////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLUETOOTH_ENABLE_INTENT_CODE) {
            gameThread = new GameThread();
            CanvasView canvasView = new CanvasView(getApplicationContext());

            assert(canvasView != null);
            assert(gameThread != null);

            setContentView(canvasView);
            gameThread.setViewAndStart(canvasView);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void initBluetooth() {
        BA = BluetoothAdapter.getDefaultAdapter();
        assert(BA != null);
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(turnOn, BLUETOOTH_ENABLE_INTENT_CODE);
    }
}

