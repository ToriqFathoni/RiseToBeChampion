package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StoryModeScreen implements Screen {
    private static final float MENU_LEFT_PADDING = 50f;
    private static final float MENU_BUTTON_WIDTH = 250f;
    private static final float MENU_BUTTON_HEIGHT = 60f;
    private static final float DIALOG_WIDTH = 560f;

    private final Stage stage;
    private final Skin skin;
    private final Viewport viewport;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private TextureRegion backgroundRegion;
    private Texture newGameTex;
    private Texture continueTex;
    private Texture backTex;

    public StoryModeScreen() {
        viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(viewport, 1280f, 720f, 0.9f);

        // Initialize batch and load background texture
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("Looks/main-menu.png"));
        backgroundRegion = new TextureRegion(backgroundTexture);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.left().padLeft(MENU_LEFT_PADDING).top().padTop(380f);

        Label subtitleLabel = new Label("Story Mode", skin);

        com.risetobechampion.frontend.utils.SessionManager sessionManager = com.risetobechampion.frontend.utils.SessionManager.getInstance();
        boolean hasSavedProgress = hasSavedProgress();
        String progressText = hasSavedProgress
            ? "Progress tersimpan: STAGE " + sessionManager.getCurrentStage()
            : "Belum ada progress tersimpan";
        Label progressLabel = new Label(progressText, skin);
        newGameTex = new Texture(Gdx.files.internal("button/new-game.png"));
        continueTex = new Texture(Gdx.files.internal("button/continue.png"));
        backTex = new Texture(Gdx.files.internal("button/back.png"));

        Button.ButtonStyle newGameStyle = new Button.ButtonStyle();
        newGameStyle.up = new TextureRegionDrawable(new TextureRegion(newGameTex));
        Button newGameBtn = new Button(newGameStyle);

        Button.ButtonStyle continueStyle = new Button.ButtonStyle();
        continueStyle.up = new TextureRegionDrawable(new TextureRegion(continueTex));
        Button continueBtn = new Button(continueStyle);

        Button.ButtonStyle backStyle = new Button.ButtonStyle();
        backStyle.up = new TextureRegionDrawable(new TextureRegion(backTex));
        Button backBtn = new Button(backStyle);

        if (!hasSavedProgress) {
            continueBtn.setDisabled(true);
            continueBtn.getColor().a = 0.35f;
        }

        mainTable.add(subtitleLabel).left().padBottom(10f).row();
        mainTable.add(progressLabel).left().padBottom(30f).row();
        mainTable.add(newGameBtn).left().width(MENU_BUTTON_WIDTH).height(MENU_BUTTON_HEIGHT).padBottom(5f).row();
        mainTable.add(continueBtn).left().width(MENU_BUTTON_WIDTH).height(MENU_BUTTON_HEIGHT).padBottom(5f).row();
        mainTable.add(backBtn).left().width(MENU_BUTTON_WIDTH).height(MENU_BUTTON_HEIGHT).row();

        newGameBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (hasSavedProgress()) {
                    showNewGameWarning();
                } else {
                    com.risetobechampion.frontend.utils.SessionManager.getInstance().resetRunProgress();
                    ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new CharacterSelectScreen());
                }
            }
        });

        continueBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (sessionManager.getRunId() != null && !sessionManager.getRunId().isEmpty()) {
                    ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new LevelMapScreen());
                } else {
                    showNoProgressDialog();
                }
            }
        });

        backBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            }
        });

        stage.addActor(mainTable);
    }

    private void showNewGameWarning() {
        Dialog dialog = new Dialog("WARNING", skin) {
            @Override
            protected void result(Object object) {
                if ("continue".equals(object)) {
                    com.risetobechampion.frontend.utils.SessionManager.getInstance().resetRunProgress();
                    ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new CharacterSelectScreen());
                }
            }
        };
        dialog.setMovable(false);
        dialog.setResizable(false);
        dialog.pad(20f);
        dialog.getContentTable().pad(8f);
        dialog.getButtonTable().padTop(18f).padBottom(8f);
        dialog.setWidth(DIALOG_WIDTH);

        Label warningLabel = new Label("Memulai New Game akan menghapus progress lama.\nProgress yang sudah ada tidak bisa dikembalikan.", skin);
        warningLabel.setWrap(true);
        warningLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        dialog.getContentTable().add(warningLabel).width(DIALOG_WIDTH - 80f).padBottom(6f).row();

        dialog.button("Continue", "continue");
        dialog.button("Cancel", "cancel");

        dialog.show(stage);
        dialog.setSize(DIALOG_WIDTH, 220f);
        dialog.setPosition((1280f - DIALOG_WIDTH) * 0.5f, (720f - 220f) * 0.5f);
        dialog.key(com.badlogic.gdx.Input.Keys.ESCAPE, false);
    }

    private void showNoProgressDialog() {
        Dialog dialog = new Dialog("INFO", skin);
        dialog.setMovable(false);
        dialog.setResizable(false);
        dialog.pad(20f);
        dialog.getContentTable().pad(8f);
        dialog.setWidth(DIALOG_WIDTH);

        Label infoLabel = new Label("Belum ada progress yang bisa dilanjutkan. Pilih New Game dulu.", skin);
        infoLabel.setWrap(true);
        infoLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        dialog.getContentTable().add(infoLabel).width(DIALOG_WIDTH - 80f).padBottom(8f).row();

        TextButton okButton = new TextButton("OK", skin);
        okButton.getLabel().setFontScale(0.95f);
        dialog.getButtonTable().add(okButton).width(160f).height(44f);

        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                dialog.hide();
            }
        });

        dialog.show(stage);
        dialog.setSize(DIALOG_WIDTH, 180f);
        dialog.setPosition((1280f - DIALOG_WIDTH) * 0.5f, (720f - 180f) * 0.5f);
    }

    private boolean hasSavedProgress() {
        String runId = com.risetobechampion.frontend.utils.SessionManager.getInstance().getRunId();
        return runId != null && !runId.isEmpty();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background image across the full window area
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.begin();
        drawBackgroundFullscreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void drawBackgroundFullscreen(float screenWidth, float screenHeight) {
        if (backgroundTexture == null) return;

        float imageWidth = backgroundTexture.getWidth();
        float imageHeight = backgroundTexture.getHeight();
        float screenAspect = screenWidth / screenHeight;
        float imageAspect = imageWidth / imageHeight;

        float drawWidth;
        float drawHeight;
        float drawX;
        float drawY;

        if (imageAspect > screenAspect) {
            drawHeight = screenHeight;
            drawWidth = screenHeight * imageAspect;
            drawX = (screenWidth - drawWidth) / 2f;
            drawY = 0f;
        } else {
            drawWidth = screenWidth;
            drawHeight = screenWidth / imageAspect;
            drawX = 0f;
            drawY = (screenHeight - drawHeight) / 2f;
        }

        batch.draw(backgroundRegion, drawX, drawY, drawWidth, drawHeight);
    }

    @Override
    public void resize(int width, int height) {
        UiViewportScaler.update(viewport, width, height, 1280f, 720f, 0.9f);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (batch != null) batch.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (newGameTex != null) newGameTex.dispose();
        if (continueTex != null) continueTex.dispose();
        if (backTex != null) backTex.dispose();
    }
}