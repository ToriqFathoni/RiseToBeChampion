package com.risetobechampion.frontend.screens; // Sesuaikan jika berbeda

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
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

    public RegisterScreen() {
        viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(viewport, 1280f, 720f, 0.9f);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // --- MEMBUAT ELEMEN UI ---
        Label titleLabel = new Label("DAFTAR AKUN BARU", skin);
        titleLabel.setFontScale(1.2f);

        final Label statusLabel = new Label("", skin); // Untuk menampilkan pesan sukses/gagal

        Label userLabel = new Label("Username Baru:", skin);
        final TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Buat Username");

        Label passLabel = new Label("Password Baru:", skin);
        final TextField passwordField = new TextField("", skin);
        passwordField.setMessageText("Buat Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton submitRegisterBtn = new TextButton("Daftar Sekarang", skin);
        TextButton backToLoginBtn = new TextButton("Kembali ke Login", skin);

        // --- MENYUSUN TATA LETAK ---
        rootTable.add(titleLabel).colspan(2).padBottom(20).row();
        rootTable.add(statusLabel).colspan(2).padBottom(20).row();

        rootTable.add(userLabel).right().padRight(10).padBottom(10);
        rootTable.add(usernameField).width(200).padBottom(10).row();

        rootTable.add(passLabel).right().padRight(10).padBottom(20);
        rootTable.add(passwordField).width(200).padBottom(20).row();

        rootTable.add(backToLoginBtn).width(140).right().padRight(10);
        rootTable.add(submitRegisterBtn).width(140).left().row();

        // --- LOGIKA TOMBOL ---
        backToLoginBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
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

                // Menembak API Register
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
                                    // Langsung lempar kembali ke layar Login jika sukses
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
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
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
