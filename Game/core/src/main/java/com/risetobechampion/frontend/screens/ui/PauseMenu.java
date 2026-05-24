package com.risetobechampion.frontend.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.risetobechampion.frontend.Main;
import com.risetobechampion.frontend.screens.MainMenuScreen;

/**
 * Reusable Pause Menu component for Battle Screens.
 */
public class PauseMenu {
    private final Table pauseRoot;
    private com.badlogic.gdx.graphics.Texture resumeTex;
    private com.badlogic.gdx.graphics.Texture quitTex;

    public PauseMenu(Stage stage, Skin skin, Main game, Runnable onResume) {
        pauseRoot = new Table();
        pauseRoot.setFillParent(true);
        pauseRoot.setVisible(false);

        // Semi-transparent background using Pixmap
        com.badlogic.gdx.graphics.Pixmap dimPixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        dimPixmap.setColor(0f, 0f, 0f, 0.6f);
        dimPixmap.fill();
        com.badlogic.gdx.graphics.Texture dimTexture = new com.badlogic.gdx.graphics.Texture(dimPixmap);
        dimPixmap.dispose();
        com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable dimDrawable = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(dimTexture));
        
        Table dimBg = new Table();
        dimBg.setFillParent(true);
        dimBg.setBackground(dimDrawable);
        pauseRoot.addActor(dimBg);

        com.badlogic.gdx.graphics.Texture popupTexture = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("Looks/popup-pause.png"));
        com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable popupDrawable = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(popupTexture));

        Table contentTable = new Table();
        contentTable.setBackground(popupDrawable);
        contentTable.pad(40f);
        pauseRoot.add(contentTable).width(450f).height(450f);

        Label pauseTitle = new Label("PAUSED", skin);
        pauseTitle.setFontScale(2f);
        pauseTitle.setColor(Color.YELLOW);
        contentTable.add(pauseTitle).padBottom(40f).row();

        resumeTex = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("button/continue.png"));
        ImageButton resumeBtn = new ImageButton(new TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(resumeTex)));
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onResume != null) {
                    onResume.run();
                }
                setVisible(false);
            }
        });
        contentTable.add(resumeBtn).width(200f).height(60f).padBottom(20f).row();

        quitTex = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("button/back-to-main-menu.png"));
        ImageButton quitBtn = new ImageButton(new TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(quitTex)));
        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen());
            }
        });
        contentTable.add(quitBtn).width(200f).height(60f).row();

        stage.addActor(pauseRoot);
    }

    public void setVisible(boolean visible) {
        pauseRoot.setVisible(visible);
    }

    public boolean isVisible() {
        return pauseRoot.isVisible();
    }

    public void dispose() {
        if (resumeTex != null) resumeTex.dispose();
        if (quitTex != null) quitTex.dispose();
    }
}
