package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.risetobechampion.frontend.Main;

public class VictoryScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;

    private Texture backgroundTexture;
    private Texture backToMenuTex;

    public VictoryScreen(Main game, int deathCount, int battleTimeElapsed) {
        this.game = game;

        FitViewport viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        createTextures();
        buildUI(deathCount, battleTimeElapsed);
    }

    private void createTextures() {
        backgroundTexture = new Texture(Gdx.files.internal("CompleteStage/champion.png"));
        backToMenuTex = new Texture(Gdx.files.internal("button/back-to-main-menu.png"));
    }

    private void buildUI(int deathCount, int battleTimeElapsed) {
        Table root = new Table();
        root.setFillParent(true);
        // Draw background image across the full window area (preserve aspect ratio or stretch? The previous code used stretch by TextureRegionDrawable)
        root.setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        stage.addActor(root);

        Table contentTable = new Table();
        contentTable.setFillParent(true);
        stage.addActor(contentTable);

        contentTable.bottom();

        Table statsTable = new Table();
        
        Label deathLabel = new Label(String.valueOf(deathCount), skin);
        deathLabel.setFontScale(1.3f);
        deathLabel.setAlignment(Align.left);
        
        Label timeLabel = new Label(battleTimeElapsed + " Detik", skin);
        timeLabel.setFontScale(1.3f);
        timeLabel.setAlignment(Align.left);

        // Push labels to the right so they appear next to the baked text
        statsTable.add(deathLabel).padLeft(280f).padBottom(14f).row();
        statsTable.add(timeLabel).padLeft(280f).row();

        // Add statsTable to contentTable, pushing it up into the User Statistics box
        contentTable.add(statsTable).padBottom(85f).row();

        ImageButton backMenuBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(backToMenuTex)));
        backMenuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen());
            }
        });

        // Add button at the bottom
        contentTable.add(backMenuBtn).width(300f).height(60f).padBottom(45f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (backToMenuTex != null) backToMenuTex.dispose();
    }
}
