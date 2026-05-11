package com.risetobechampion.frontend.screens; // Sesuaikan jika berbeda

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private final Viewport viewport;

    public MainMenuScreen() {
        viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(viewport, 1280f, 720f, 0.9f);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Membuat layout utama
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        // Menggeser seluruh isi tabel ke kiri, dengan jarak 50 pixel dari tepi layar
        mainTable.left().padLeft(50);

        // --- MEMBUAT ELEMEN UI ---
        Label titleLabel = new Label("RISE TO CHAMPION", skin);
        titleLabel.setFontScale(1.5f); // Membesarkan teks judul

        TextButton storyBtn = new TextButton("Story Mode", skin);
        TextButton localMatchBtn = new TextButton("Local Multiplayer Match", skin);
        TextButton optionsBtn = new TextButton("Options", skin);
        TextButton exitBtn = new TextButton("Exit", skin);

        // --- MENYUSUN TATA LETAK ---
        // .width() dan .height() untuk menyeragamkan ukuran tombol
        mainTable.add(titleLabel).padBottom(60).row();
        mainTable.add(storyBtn).width(250).height(40).padBottom(15).row();
        mainTable.add(localMatchBtn).width(250).height(40).padBottom(15).row();
        mainTable.add(optionsBtn).width(250).height(40).padBottom(15).row();
        mainTable.add(exitBtn).width(250).height(40).row();

        // Memberikan aksi pada tombol Exit agar bisa menutup game
        exitBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Gdx.app.exit();
            }
        });
        // Memberikan aksi pada tombol Story Mode untuk pindah layar
        storyBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new CharacterSelectScreen());
            }
        });

        stage.addActor(mainTable);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Untuk sementara latar belakang abu-abu.
        // Nanti kita bisa ganti dengan gambar pemandangan di sini.
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        UiViewportScaler.update(viewport, width, height, 1280f, 720f, 0.9f);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
