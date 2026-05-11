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

public class LevelMapScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private final Viewport viewport;

    public LevelMapScreen() {
        viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(viewport, 1280f, 720f, 0.9f);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Membaca level saat ini dari SessionManager
        int stageSaatIni = com.risetobechampion.frontend.utils.SessionManager.getInstance().getCurrentStage();

        // --- ELEMEN UI ---
        Label titleLabel = new Label("PERJALANAN DIMULAI", skin);

        Label stageLabel = new Label("STAGE " + stageSaatIni, skin);
        stageLabel.setFontScale(2.0f); // Dibuat sangat besar agar epik

        // Kita bisa membuat sub-judul dinamis tergantung stagenya
        String namaTempat = (stageSaatIni == 10) ? "Ruang Takhta Boss" : "Jalanan Gelap Zona " + stageSaatIni;
        Label subtitleLabel = new Label(namaTempat, skin);

        TextButton enterBattleBtn = new TextButton("Masuk Arena Pertarungan", skin);

        // --- TATA LETAK ---
        rootTable.add(titleLabel).padBottom(10).row();
        rootTable.add(stageLabel).padBottom(10).row();
        rootTable.add(subtitleLabel).padBottom(60).row();
        rootTable.add(enterBattleBtn).width(300).height(60).row();

        // --- LOGIKA TOMBOL ---
        // --- LOGIKA TOMBOL ---
        enterBattleBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                System.out.println("Mempersiapkan musuh untuk Stage " + stageSaatIni + "...");

                // INI ADALAH KODE KUNCI UNTUK PINDAH LAYAR:
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new BattleScreen());
            }
        });
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Latar belakang sedikit kebiruan gelap / navy
        Gdx.gl.glClearColor(0.05f, 0.1f, 0.15f, 1);
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
