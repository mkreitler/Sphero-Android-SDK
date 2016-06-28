package com.demos.kreitler.mark.simplespherogames;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.orbotix.DualStackDiscoveryAgent;

public class MainActivity extends AppCompatActivity {
    private GameView gameView = null;

    // Interface ///////////////////////////////////////////////////////////////////////////////////
    private static final int REQUEST_ENABLE_BT  = 1;

    private static MainActivity _this = null;

    public static void _finish() {
        if (_this != null) {
            _this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _this = this;

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        gameView = (GameView) findViewById(R.id.gameview);
    }

    @Override
    public void onStart() {
        gameView.onStart();

        super.onStart();
    }

    @Override
    protected void onStop() {
        gameView.onStop();

        // Last-ditch attempt to free resources assigned to robot discovery.
        // Hopefully, the game state machine already did this.
        if( DualStackDiscoveryAgent.getInstance().isDiscovering() ) {
            DualStackDiscoveryAgent.getInstance().stopDiscovery();
        }

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        gameView.OnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameView.OnResume();
    }

    // Implementation //////////////////////////////////////////////////////////////////////////////
}
