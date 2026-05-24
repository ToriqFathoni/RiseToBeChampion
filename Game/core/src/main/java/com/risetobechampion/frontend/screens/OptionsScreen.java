package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class OptionsScreen implements Screen {

    private final Game game;
    private final Stage stage;
    private final Skin skin;

    private Texture backgroundTexture;
    private Texture panelTexture;
    private Texture backBtnTex;

    public OptionsScreen(Game game) {
        this.game = game;
        stage = new Stage(new FitViewport(1280f, 720f));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        createTextures();
        buildUI();
    }

    private void createTextures() {
        backgroundTexture = new Texture(Gdx.files.internal("Looks/main-menu.png"));
        backBtnTex = new Texture(Gdx.files.internal("button/back-to-main-menu.png"));

        Pixmap panelPixmap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        panelPixmap.setColor(0.12f, 0.16f, 0.22f, 0.90f);
        panelPixmap.fill();
        panelTexture = new Texture(panelPixmap);
        panelPixmap.dispose();
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        stage.addActor(root);

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(40f);
        root.add(panel).width(900f).height(550f).center();

        Label title = new Label("GAME CONTROLS", skin);
        title.setFontScale(2.5f);
        title.setColor(0.95f, 0.92f, 0.2f, 1f);
        title.setAlignment(Align.center);
        panel.add(title).colspan(2).padBottom(30f).row();

        // Columns for Player 1 and Player 2
        Table player1Table = createControlsTable("PLAYER 1", 
            new String[]{"Movement", "Jump", "Basic Attack", "Heavy Attack", "Skill", "Ultimate", "Defend", "Taunt", "Pause"},
            new String[]{"W, A, S, D", "W", "J", "K", "L", "I", "P", "T", "ESC"}
        );

        Table player2Table = createControlsTable("PLAYER 2 (Local Multiplayer)", 
            new String[]{"Movement", "Jump", "Basic Attack", "Heavy Attack", "Skill", "Ultimate", "Defend", "Taunt", "Pause"},
            new String[]{"Arrow Keys", "Up Arrow", "Num 1", "Num 2", "Num 3", "Num 4", "Num 5", "Num 0", "N/A"}
        );

        panel.add(player1Table).width(400f).padRight(20f).top();
        panel.add(player2Table).width(400f).padLeft(20f).top().row();

        ImageButton backBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(backBtnTex)));
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen());
            }
        });
        panel.add(backBtn).colspan(2).padTop(40f).width(250f).height(50f).center();
    }

    private Table createControlsTable(String playerName, String[] actions, String[] keys) {
        Table table = new Table();
        
        Label pName = new Label(playerName, skin);
        pName.setFontScale(1.5f);
        pName.setColor(0.5f, 0.8f, 1f, 1f);
        pName.setAlignment(Align.center);
        table.add(pName).colspan(2).padBottom(20f).row();

        for (int i = 0; i < actions.length; i++) {
            Label actionLabel = new Label(actions[i], skin);
            actionLabel.setFontScale(1.2f);
            
            Label keyLabel = new Label(keys[i], skin);
            keyLabel.setFontScale(1.2f);
            keyLabel.setColor(1f, 0.8f, 0.2f, 1f);
            keyLabel.setAlignment(Align.right);

            table.add(actionLabel).left().padBottom(10f);
            table.add(keyLabel).right().expandX().padBottom(10f).row();
        }

        return table;
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
        if (panelTexture != null) panelTexture.dispose();
        if (backBtnTex != null) backBtnTex.dispose();
    }
}
