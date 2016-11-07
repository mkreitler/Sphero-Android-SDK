package com.lbt.demos.kreitler.mark.states;

import android.graphics.Canvas;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark on 11/6/2016.
 */
public class GameState {

// Interface ///////////////////////////////////////////////////////////////////////////////////////
    // Static --------------------------------------------------------------------------------------
    private static HashMap<String, GameState> _states = new HashMap<String, GameState>();
    private static GameState _currentState      = null;
    private static GameState _nextState         = null;

    public static void Update(float dt) {
        if (_currentState != null) {
            _currentState.update(dt);
        }

        if (_nextState != null) {

            if (_currentState != null) {
                _currentState.exit();
            }

            _nextState.enter();

            _currentState = _nextState;
            _nextState = null;

        }
    }

    public static void Draw(Canvas canvas) {
        if (_currentState != null) {
            _currentState.draw(canvas);
        }
    }

    public static void SetState(String nextStateName) {
        GameState nextState = _states.get(nextStateName);

        if (nextState != _currentState) {
            _nextState = nextState;
        }
    }

    // Instance ------------------------------------------------------------------------------------
    public GameState(String name) {
        _states.put(name, this);
    }

    public void destroy() {
        _states.remove(this);
    }

    protected void update(float dt) {}

    protected void enter() {}

    protected void exit() {}

    protected void draw(Canvas canvas) {}
}
