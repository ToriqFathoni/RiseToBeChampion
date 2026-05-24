package com.risetobechampion.frontend.screens; // Sesuaikan jika berbeda

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RegisterScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private final Viewport viewport;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private TextureRegion backgroundRegion;
    private Texture newUsernameTex;
    private Texture newPasswordTex;
    private Texture loginBtnTex;
    private Texture registerBtnTex;

    public RegisterScreen() {
        viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(viewport, 1280f, 720f, 0.9f);

        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("Looks/LandingPage.png"));
        backgroundRegion = new TextureRegion(backgroundTexture);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top();
        stage.addActor(rootTable);

        Table formTable = new Table();

        newUsernameTex = new Texture(Gdx.files.internal("Looks/new-username.png"));
        newPasswordTex = new Texture(Gdx.files.internal("Looks/new-password.png"));
        loginBtnTex = new Texture(Gdx.files.internal("Looks/login-button.png"));
        registerBtnTex = new Texture(Gdx.files.internal("Looks/register-button.png"));

        com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle customFieldStyle = new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle(skin.get(com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle.class));
        customFieldStyle.background = new com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable();
        customFieldStyle.focusedBackground = new com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable();

        final TextField usernameField = new TextField("", customFieldStyle);
        usernameField.setMessageText("Buat Username");

        final TextField passwordField = new TextField("", customFieldStyle);
        passwordField.setMessageText("Buat Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable registerDrawable = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new TextureRegion(registerBtnTex));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle registerStyle = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle();
        registerStyle.imageUp = registerDrawable;
        registerStyle.imageDown = registerDrawable.tint(new com.badlogic.gdx.graphics.Color(0.6f, 0.6f, 0.6f, 1f));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton submitRegisterBtn = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(registerStyle);

        com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable loginDrawable = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new TextureRegion(loginBtnTex));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle loginStyle = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle();
        loginStyle.imageUp = loginDrawable;
        loginStyle.imageDown = loginDrawable.tint(new com.badlogic.gdx.graphics.Color(0.6f, 0.6f, 0.6f, 1f));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton backToLoginBtn = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(loginStyle);

        final Label statusLabel = new Label("", skin); // Untuk menampilkan pesan sukses/gagal
        statusLabel.setColor(1f, 0.3f, 0.3f, 1f);

        Table userContainer = new Table();
        userContainer.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new TextureRegion(newUsernameTex)));
        userContainer.add(usernameField).expand().fill().padLeft(110f).padRight(15f).padTop(8f);
        formTable.add(userContainer).width(320).height(48).padBottom(8).row();

        Table passContainer = new Table();
        passContainer.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new TextureRegion(newPasswordTex)));
        passContainer.add(passwordField).expand().fill().padLeft(110f).padRight(15f).padTop(8f);
        formTable.add(passContainer).width(320).height(48).padBottom(5).row();

        formTable.add(statusLabel).colspan(2).center().padBottom(5).row();

        Table buttonTable = new Table();
        buttonTable.add(backToLoginBtn).width(120).height(40).padRight(12);
        buttonTable.add(submitRegisterBtn).width(120).height(40);

        formTable.add(buttonTable).colspan(2).center().row();

        rootTable.add().expand();
        rootTable.row();
        rootTable.add(formTable).padBottom(10f);

        backToLoginBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // ganti page
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new LoginScreen());
            }
        });

        submitRegisterBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                String inputUser = usernameField.getText();
                String inputPass = passwordField.getText();

                if(inputUser.isEmpty() || inputPass.isEmpty()) {
                    statusLabel.setText("Username & Password tidak boleh kosong!");
                    return;
                }

                statusLabel.setText("Memproses pendaftaran...");

                // tembak api backend
                com.risetobechampion.frontend.network.ApiClient.register(inputUser, inputPass, new com.badlogic.gdx.Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
                        int statusCode = httpResponse.getStatus().getStatusCode();
                        final String responseBody = httpResponse.getResultAsString();

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (statusCode == 200) {
                                    System.out.println("Register Sukses: " + responseBody);

                                    // ganti page
                                    ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new LoginScreen());
                                } else {
                                    System.out.println("Register Gagal: " + responseBody);
                                    statusLabel.setText("Gagal: Username mungkin sudah dipakai.");
                                }
                            }
                        });
                    }

                    @Override
                    public void failed(Throwable t) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                statusLabel.setText("Error: Tidak bisa terhubung ke server.");
                            }
                        });
                    }

                    @Override
                    public void cancelled() {}
                });
            }
        });
    }

    @Override
    public void show() {
        // putar lagu menu
        com.risetobechampion.frontend.utils.AudioManager.getInstance().playMainMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.begin();
        drawBackgroundFullscreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        // render karakter/gambar
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

        // render karakter/gambar
        batch.draw(backgroundRegion, drawX, drawY, drawWidth, drawHeight);
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
        batch.dispose();
        backgroundTexture.dispose();
        newUsernameTex.dispose();
        newPasswordTex.dispose();
        loginBtnTex.dispose();
        registerBtnTex.dispose();
    }
}
