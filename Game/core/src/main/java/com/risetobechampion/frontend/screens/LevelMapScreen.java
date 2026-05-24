package com.risetobechampion.frontend.screens; // Sesuaikan jika berbeda

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LevelMapScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private final Viewport viewport;
    private Texture backgroundTexture;
    private Texture enterBattleTex;

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

        int bgStage = Math.max(1, Math.min(4, stageSaatIni));
        backgroundTexture = new Texture(Gdx.files.internal("mulaistage/stage" + bgStage + ".png"));
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));

        enterBattleTex = new Texture(Gdx.files.internal("button/enter-battle-arena.png"));
        ImageButton enterBattleBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(enterBattleTex)));

        // --- TATA LETAK ---
        rootTable.bottom();
        rootTable.add(enterBattleBtn).width(360).height(50).padBottom(115f).row();

        // --- LOGIKA TOMBOL ---
        // --- LOGIKA TOMBOL ---
        enterBattleBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                System.out.println("Mempersiapkan musuh untuk Stage " + stageSaatIni + "...");

                // INI ADALAH KODE KUNCI UNTUK PINDAH LAYAR:
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new BattleScreen((com.risetobechampion.frontend.Main) Gdx.app.getApplicationListener()));
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
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (enterBattleTex != null) enterBattleTex.dispose();
    }
}
