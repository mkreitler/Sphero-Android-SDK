package com.demos.kreitler.mark.simplespherogames.HotPotato;

import android.content.res.Resources;

import com.demos.kreitler.mark.demouilib.TransitionTrig;
import com.demos.kreitler.mark.lib_demo_sprites.Sprite;
import com.demos.kreitler.mark.simplespherogames.GameStateBase;
import com.demos.kreitler.mark.simplespherogames.GameView;
import com.demos.kreitler.mark.simplespherogames.R;

/**
 * Created by Mark on 6/28/2016.
 */
public class GameStateHotPotatoIntro extends GameStateBase {
    // Interface ///////////////////////////////////////////////////////////////////////////////////
    // Static --------------------------------------------------------------------------------------
    // Instance ------------------------------------------------------------------------------------
    public GameStateHotPotatoIntro(GameView.GameThread game) {
        super(game);

        Resources res = game.getContext().getResources();
        infoSprite = new Sprite(res.getDrawable(R.drawable.instructions_hotPotato, null), 0, 0, 0.0f, 0.5f);
        transition = new TransitionTrig(TRANSITION_DURATION);
    }

    // Implementation //////////////////////////////////////////////////////////////////////////////
    // Static --------------------------------------------------------------------------------------
    // Instance ------------------------------------------------------------------------------------
    private final float TRANSITION_DURATION     = 0.5f;

    private Sprite infoSprite           = null;
    private TransitionTrig transition   = null;
}
