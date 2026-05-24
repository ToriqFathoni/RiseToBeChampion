package com.risetobechampion.frontend.screens; // Sesuaikan jika berbeda

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private final Viewport viewport;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private TextureRegion backgroundRegion;
    private Texture storyTex;
    private Texture localTex;
    private Texture optionsTex;
    private Texture exitTex;

    public MainMenuScreen() {
        viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(viewport, 1280f, 720f, 0.9f);

        // Initialize batch and load background texture
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("Looks/main-menu.png"));
        backgroundRegion = new TextureRegion(backgroundTexture);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Membuat layout utama
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        // Menggeser isi menu lebih ke bawah agar tidak menutupi teks pada background
        mainTable.left().padLeft(50).top().padTop(380f);

        // --- MEMBUAT ELEMEN UI ---
        storyTex = new Texture(Gdx.files.internal("button/story-mode.png"));
        localTex = new Texture(Gdx.files.internal("button/local-multiplayer.png"));
        optionsTex = new Texture(Gdx.files.internal("button/Options.png"));
        exitTex = new Texture(Gdx.files.internal("button/Exit.png"));

        ImageButton storyBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(storyTex)));
        ImageButton localMatchBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(localTex)));
        ImageButton optionsBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(optionsTex)));
        ImageButton exitBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(exitTex)));

        // --- MENYUSUN TATA LETAK ---
        // .width() dan .height() disesuaikan dengan asset
        mainTable.add(storyBtn).width(250).height(60).padBottom(5).row();
        mainTable.add(localMatchBtn).width(250).height(60).padBottom(5).row();
        mainTable.add(optionsBtn).width(250).height(60).padBottom(5).row();
        mainTable.add(exitBtn).width(250).height(60).row();

        // Memberikan aksi pada tombol Options untuk membuka OptionsScreen
        optionsBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new OptionsScreen((com.risetobechampion.frontend.Main) Gdx.app.getApplicationListener()));
            }
        });

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
                com.risetobechampion.frontend.utils.SessionManager.getInstance().setLocalMultiplayer(false);
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new StoryModeScreen());
            }
        });

        // Memberikan aksi pada tombol Local Multiplayer
        localMatchBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                com.risetobechampion.frontend.utils.SessionManager.getInstance().setLocalMultiplayer(true);
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new LocalMultiplayerCharSelectScreen());
            }
        });

        stage.addActor(mainTable);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background image across the full window area
        batch.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.begin();
        drawBackgroundScaled(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void drawBackgroundScaled(float screenWidth, float screenHeight) {
        if (backgroundTexture == null) return;

        float imageWidth = backgroundTexture.getWidth();
        float imageHeight = backgroundTexture.getHeight();

        // Calculate aspect ratios
        float viewportAspect = screenWidth / screenHeight;
        float imageAspect = imageWidth / imageHeight;

        float drawWidth, drawHeight, drawX, drawY;

        // Scale to cover viewport while maintaining aspect ratio
        if (imageAspect > viewportAspect) {
            // Image is wider - scale by height
            drawHeight = screenHeight;
            drawWidth = screenHeight * imageAspect;
            drawX = (screenWidth - drawWidth) / 2f;
            drawY = 0;
        } else {
            // Image is taller - scale by width
            drawWidth = screenWidth;
            drawHeight = screenWidth / imageAspect;
            drawX = 0;
            drawY = (screenHeight - drawHeight) / 2f;
        }

        batch.draw(backgroundRegion, drawX, drawY, drawWidth, drawHeight);
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
        if (batch != null) batch.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (storyTex != null) storyTex.dispose();
        if (localTex != null) localTex.dispose();
        if (optionsTex != null) optionsTex.dispose();
        if (exitTex != null) exitTex.dispose();
    }
}
