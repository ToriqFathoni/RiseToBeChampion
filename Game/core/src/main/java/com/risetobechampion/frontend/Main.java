package com.risetobechampion.frontend;

import com.badlogic.gdx.Game;
import com.risetobechampion.frontend.screens.LoginScreen;

// Mengubah dari ApplicationAdapter menjadi Game agar mendukung fitur "Screen" (pindah layar)
public class Main extends Game {

    @Override
    public void create() {
        // Langsung arahkan game untuk membuka LoginScreen saat pertama kali menyala
        this.setScreen(new LoginScreen());
    }

    @Override
    public void render() {
        // INI SANGAT PENTING! Perintah ini menyuruh game untuk merender layar yang sedang aktif
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
