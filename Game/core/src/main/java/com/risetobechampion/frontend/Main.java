package com.risetobechampion.frontend;

import com.badlogic.gdx.Game;
import com.risetobechampion.frontend.screens.LoginScreen;

public class Main extends Game {

    @Override
    public void create() {

        // ganti page
        this.setScreen(new LoginScreen());
    }

    @Override
    public void render() {

        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        com.risetobechampion.frontend.utils.AudioManager.getInstance().dispose();
    }
}
