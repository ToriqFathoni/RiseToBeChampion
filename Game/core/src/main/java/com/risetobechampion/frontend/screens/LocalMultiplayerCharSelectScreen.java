package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.risetobechampion.frontend.utils.SessionManager;

public class LocalMultiplayerCharSelectScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private final Viewport viewport;

    private String p1Selection = null;
    private String p2Selection = null;
    private Texture backgroundTexture;

    private Label p1StatusLabel;
    private Label p2StatusLabel;
    private TextButton fightButton;
    private com.risetobechampion.frontend.game.input.UiControllerNavigator uiNavigatorP1;
    private com.risetobechampion.frontend.game.input.UiControllerNavigator uiNavigatorP2;

    private final String[] CHARACTERS = {
        "Kael The Phantom", "Mr. Van", "Ryu", "Chen Long", "Kagetsu", "Joe"
    };

    public LocalMultiplayerCharSelectScreen() {
        viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(viewport, 1280f, 720f, 0.9f);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("Looks/bg-choose-multiplayer.png"));

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        stage.addActor(rootTable);

        Label titleLabel = new Label("LOCAL MULTIPLAYER - SELECT CHARACTER", skin);
        titleLabel.setFontScale(1.8f);
        titleLabel.setAlignment(Align.center);
        rootTable.add(titleLabel).colspan(2).padBottom(30).row();

        Table p1Table = new Table();
        Table p2Table = new Table();

        Label p1Title = new Label("PLAYER 1 (LEFT)", skin);
        p1Title.setFontScale(1.3f);
        p1Title.setColor(0.3f, 0.8f, 1f, 1f);
        p1Table.add(p1Title).padBottom(15).row();

        Label p2Title = new Label("PLAYER 2 (RIGHT)", skin);
        p2Title.setFontScale(1.3f);
        p2Title.setColor(1f, 0.3f, 0.3f, 1f);
        p2Table.add(p2Title).padBottom(15).row();

        for (String charName : CHARACTERS) {
            TextButton btn = new TextButton(charName, skin);
            p1Table.add(btn).width(300).height(50).padBottom(10).row();
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    p1Selection = charName;
                    p1StatusLabel.setText("P1 Selected: " + charName);
                    checkFightReady();
                }
            });
        }

        for (String charName : CHARACTERS) {
            TextButton btn = new TextButton(charName, skin);
            p2Table.add(btn).width(300).height(50).padBottom(10).row();
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    p2Selection = charName;
                    p2StatusLabel.setText("P2 Selected: " + charName);
                    checkFightReady();
                }
            });
        }

        p1StatusLabel = new Label("Waiting for P1...", skin);
        p2StatusLabel = new Label("Waiting for P2...", skin);

        p1Table.add(p1StatusLabel).padTop(20).row();
        p2Table.add(p2StatusLabel).padTop(20).row();

        rootTable.add(p1Table).expandX().top();
        rootTable.add(p2Table).expandX().top().row();

        Table bottomTable = new Table();
        
        TextButton backBtn = new TextButton("Back to Main Menu", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // ganti page
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            }
        });

        fightButton = new TextButton("FIGHT!", skin);
        fightButton.setDisabled(true);
        fightButton.getLabel().setFontScale(1.5f);
        fightButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (p1Selection != null && p2Selection != null) {
                    SessionManager.getInstance().setSelectedCharacterName(p1Selection);
                    SessionManager.getInstance().setPlayer2CharacterName(p2Selection);
                    SessionManager.getInstance().setLocalMultiplayer(true);
                    // ganti page
                    ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new LocalMultiplayerBattleScreen());
                }
            }
        });

        bottomTable.add(backBtn).width(200).height(50).padRight(50);
        bottomTable.add(fightButton).width(300).height(70);

        rootTable.add(bottomTable).colspan(2).padTop(40).row();

        com.badlogic.gdx.controllers.Controller c1 = null;
        com.badlogic.gdx.controllers.Controller c2 = null;
        if (com.badlogic.gdx.controllers.Controllers.getControllers().size > 0) {
            c1 = com.badlogic.gdx.controllers.Controllers.getControllers().get(0);
        }
        if (com.badlogic.gdx.controllers.Controllers.getControllers().size > 1) {
            c2 = com.badlogic.gdx.controllers.Controllers.getControllers().get(1);
        }

        uiNavigatorP1 = new com.risetobechampion.frontend.game.input.UiControllerNavigator(c1);
        for (com.badlogic.gdx.scenes.scene2d.Actor a : p1Table.getChildren()) {
            if (a instanceof com.badlogic.gdx.scenes.scene2d.ui.Button) {
                uiNavigatorP1.addButton((com.badlogic.gdx.scenes.scene2d.ui.Button) a);
            }
        }
        uiNavigatorP1.addButton(backBtn);
        uiNavigatorP1.addButton(fightButton);

        uiNavigatorP2 = new com.risetobechampion.frontend.game.input.UiControllerNavigator(c2);
        for (com.badlogic.gdx.scenes.scene2d.Actor a : p2Table.getChildren()) {
            if (a instanceof com.badlogic.gdx.scenes.scene2d.ui.Button) {
                uiNavigatorP2.addButton((com.badlogic.gdx.scenes.scene2d.ui.Button) a);
            }
        }
        uiNavigatorP2.addButton(backBtn);
        uiNavigatorP2.addButton(fightButton);
    }

    private void checkFightReady() {
        if (p1Selection != null && p2Selection != null) {
            fightButton.setDisabled(false);
            fightButton.setColor(1f, 0.8f, 0.2f, 1f); // Highlight button
        }
    }

    @Override
    public void show() {
        // putar lagu menu
        com.risetobechampion.frontend.utils.AudioManager.getInstance().playMainMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        // render karakter/gambar
        stage.draw();

        if (uiNavigatorP1 != null) {
            // update status secara berkala
            uiNavigatorP1.update();
        }
        if (uiNavigatorP2 != null) {
            // update status secara berkala
            uiNavigatorP2.update();
        }
    }

    @Override
    public void resize(int width, int height) {
        // update status secara berkala
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
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
