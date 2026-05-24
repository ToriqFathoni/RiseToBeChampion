package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoginScreen implements Screen {

    private Stage stage; // Tempat kita menaruh semua elemen UI
    private Skin skin;   // Tampilan visual tombol dan teks
    private final Viewport viewport;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private TextureRegion backgroundRegion;
    private Texture usernameTex;
    private Texture passwordTex;
    private Texture loginBtnTex;
    private Texture registerBtnTex;

    public LoginScreen() {
        viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage); // Agar UI bisa diklik
        UiViewportScaler.syncNow(viewport, 1280f, 720f, 0.9f);

        // Initialize batch and load background texture
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("Looks/LandingPage.png"));
        backgroundRegion = new TextureRegion(backgroundTexture);

        // Memuat skin bawaan (uiskin.json) dari folder assets
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Membuat layout utama menggunakan Table
        Table rootTable = new Table();
        rootTable.setFillParent(true); // Table akan memenuhi seluruh layar
        rootTable.top();
        stage.addActor(rootTable);

        Table formTable = new Table();

        // --- MEMBUAT ELEMEN UI ---
        usernameTex = new Texture(Gdx.files.internal("Looks/username.png"));
        passwordTex = new Texture(Gdx.files.internal("Looks/password.png"));
        loginBtnTex = new Texture(Gdx.files.internal("Looks/login-button.png"));
        registerBtnTex = new Texture(Gdx.files.internal("Looks/register-button.png"));

        // Error Label
        com.badlogic.gdx.scenes.scene2d.ui.Label errorLabel = new com.badlogic.gdx.scenes.scene2d.ui.Label("", skin);
        errorLabel.setColor(1f, 0.3f, 0.3f, 1f);
        errorLabel.setFontScale(0.9f);

        // Custom field style to remove default background
        com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle customFieldStyle = new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle(skin.get(com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle.class));
        customFieldStyle.background = new com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable();
        customFieldStyle.focusedBackground = new com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable();

        // 2. Input Username
        com.badlogic.gdx.scenes.scene2d.ui.TextField usernameField = new com.badlogic.gdx.scenes.scene2d.ui.TextField("", customFieldStyle);
        usernameField.setMessageText("Masukkan Username");

        // 3. Input Password
        com.badlogic.gdx.scenes.scene2d.ui.TextField passwordField = new com.badlogic.gdx.scenes.scene2d.ui.TextField("", customFieldStyle);
        passwordField.setMessageText("Masukkan Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        // 4. Tombol Login & Register
        com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable loginDrawable = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new TextureRegion(loginBtnTex));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle loginStyle = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle();
        loginStyle.imageUp = loginDrawable;
        loginStyle.imageDown = loginDrawable.tint(new com.badlogic.gdx.graphics.Color(0.6f, 0.6f, 0.6f, 1f));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton loginBtn = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(loginStyle);

        com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable registerDrawable = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new TextureRegion(registerBtnTex));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle registerStyle = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle();
        registerStyle.imageUp = registerDrawable;
        registerStyle.imageDown = registerDrawable.tint(new com.badlogic.gdx.graphics.Color(0.6f, 0.6f, 0.6f, 1f));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton registerBtn = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(registerStyle);

        // --- MENYUSUN TATA LETAK (GRID/TABLE) ---
        // row() berfungsi seperti 'Enter' atau pindah ke baris bawahnya

        Table userContainer = new Table();
        userContainer.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new TextureRegion(usernameTex)));
        userContainer.add(usernameField).expand().fill().padLeft(110f).padRight(15f).padTop(8f);
        formTable.add(userContainer).width(320).height(48).padBottom(8).row();

        Table passContainer = new Table();
        passContainer.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new TextureRegion(passwordTex)));
        passContainer.add(passwordField).expand().fill().padLeft(110f).padRight(15f).padTop(8f);
        formTable.add(passContainer).width(320).height(48).padBottom(5).row();

        formTable.add(errorLabel).colspan(2).center().padBottom(5).row();

        Table buttonTable = new Table();
        buttonTable.add(loginBtn).width(120).height(40).padRight(12);
        buttonTable.add(registerBtn).width(120).height(40);

        formTable.add(buttonTable).colspan(2).center().row();

        rootTable.add().expand();
        rootTable.row();
        rootTable.add(formTable).padBottom(10f);
        // Nanti kita akan tambahkan kolom input dan tombol di dalam rootTable ini
        // --- MEMBERIKAN LOGIKA PADA TOMBOL (EVENT LISTENER) ---

        loginBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                String inputUser = usernameField.getText();
                String inputPass = passwordField.getText();

                System.out.println("Mengirim data login ke Backend...");

                // Memanggil fungsi login dari ApiClient
                com.risetobechampion.frontend.network.ApiClient.login(inputUser, inputPass, new com.badlogic.gdx.Net.HttpResponseListener() {

                    @Override
                    public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
                        int statusCode = httpResponse.getStatus().getStatusCode();
                        String responseBody = httpResponse.getResultAsString();

                        // WAJIB menggunakan postRunnable saat merespons jaringan agar tidak crash
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (statusCode == 200) {
                                    System.out.println("SUKSES (200): " + responseBody);

                                    // --- KODE BARU: Menyimpan UserID ke SessionManager ---
                                    try {
                                        // Mengambil teks tepat setelah kata "UserID: "
                                        String extractedUserId = responseBody.substring(responseBody.indexOf("UserID: ") + 8).trim();
                                        com.risetobechampion.frontend.utils.SessionManager.getInstance().setUserId(extractedUserId);
                                        System.out.println("UserID berhasil diamankan di Sesi Global: " + extractedUserId);
                                    } catch (Exception e) {
                                        System.out.println("Gagal mengekstrak UserID dari server!");
                                    }
                                    // -----------------------------------------------------

                                    String storedUserId = com.risetobechampion.frontend.utils.SessionManager.getInstance().getUserId();
                                    if (storedUserId != null && !storedUserId.isEmpty()) {
                                        com.risetobechampion.frontend.network.ApiClient.getActiveProgress(storedUserId, new com.badlogic.gdx.Net.HttpResponseListener() {
                                            @Override
                                            public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse progressResponse) {
                                                int progressStatus = progressResponse.getStatus().getStatusCode();
                                                String progressBody = progressResponse.getResultAsString();

                                                Gdx.app.postRunnable(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (progressStatus == 200) {
                                                            try {
                                                                JsonValue progressJson = new JsonReader().parse(progressBody);
                                                                com.risetobechampion.frontend.utils.SessionManager sessionManager = com.risetobechampion.frontend.utils.SessionManager.getInstance();
                                                                sessionManager.setRunId(progressJson.getString("runId"));
                                                                sessionManager.setCurrentStage(progressJson.getInt("currentStage", 1));
                                                                sessionManager.setPlayerHpBonus(progressJson.getInt("bonusMaxHp", 0));
                                                                sessionManager.setPlayerAttack1Bonus(progressJson.getInt("bonusBasicDmg", 0));
                                                                sessionManager.setPlayerAttack2Bonus(progressJson.getInt("bonusSkillDmg", 0));
                                                                sessionManager.setPlayerAttack3Bonus(progressJson.getInt("bonusMaxEnergy", 0));
                                                                sessionManager.setDeathCount(progressJson.getInt("deathCount", 0));
                                                                sessionManager.setTotalTimeElapsed(progressJson.getInt("timeElapsed", 0));
                                                                System.out.println("Progress aktif dimuat dari backend.");
                                                                    // Fetch combat setup to discover which character is associated with this run
                                                                    try {
                                                                        String runId = sessionManager.getRunId();
                                                                        int stage = sessionManager.getCurrentStage();
                                                                        if (runId != null && !runId.isEmpty()) {
                                                                            com.risetobechampion.frontend.network.ApiClient.getCombatSetup(stage, runId, new com.badlogic.gdx.Net.HttpResponseListener() {
                                                                                @Override
                                                                                public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse combatResponse) {
                                                                                    if (combatResponse.getStatus().getStatusCode() >= 200 && combatResponse.getStatus().getStatusCode() < 300) {
                                                                                        try {
                                                                                            JsonValue root = new JsonReader().parse(combatResponse.getResultAsString());
                                                                                            JsonValue player = root.get("player");
                                                                                            if (player != null) {
                                                                                                String playerName = player.getString("name", null);
                                                                                                if (playerName != null) {
                                                                                                    com.risetobechampion.frontend.utils.SessionManager.getInstance().setSelectedCharacterName(playerName);
                                                                                                    System.out.println("Selected character loaded from run: " + playerName);
                                                                                                }
                                                                                            }
                                                                                        } catch (Exception ignore) {}
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void failed(Throwable t) {}

                                                                                @Override
                                                                                public void cancelled() {}
                                                                            });
                                                                        }
                                                                    } catch (Exception e) {
                                                                        // ignore
                                                                    }
                                                            } catch (Exception progressError) {
                                                                progressError.printStackTrace();
                                                            }
                                                        }

                                                        ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void failed(Throwable t) {
                                                Gdx.app.postRunnable(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void cancelled() {
                                                Gdx.app.postRunnable(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
                                    }
                                } else if (statusCode == 404) {
                                    System.out.println("GAGAL (404): " + responseBody);
                                    errorLabel.setText("Akun tidak terdaftar");
                                    errorLabel.setColor(1f, 0.3f, 0.3f, 1f);
                                    usernameField.setText("");
                                    passwordField.setText("");
                                } else if (statusCode == 401) {
                                    System.out.println("GAGAL (401): " + responseBody);
                                    errorLabel.setText("Password salah");
                                    errorLabel.setColor(1f, 0.3f, 0.3f, 1f);
                                    passwordField.setText("");
                                } else {
                                    System.out.println("GAGAL (" + statusCode + "): " + responseBody);
                                    errorLabel.setText("Login gagal. Silakan coba lagi.");
                                    errorLabel.setColor(1f, 0.3f, 0.3f, 1f);
                                }
                            }
                        });
                    }

                    @Override
                    public void failed(Throwable t) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("ERROR: Tidak bisa terhubung ke server!");
                                t.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void cancelled() {
                        System.out.println("Request dibatalkan.");
                    }
                });
            }
        });

        registerBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                System.out.println("Pindah ke layar Register...");
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new RegisterScreen());
            }
        });
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Membersihkan layar dengan warna latar belakang (misal: abu-abu gelap)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background image across the full window area
        batch.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.begin();
        drawBackgroundFullscreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Menggambar UI ke layar
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

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        usernameTex.dispose();
        passwordTex.dispose();
        loginBtnTex.dispose();
        registerBtnTex.dispose();
    }
}
