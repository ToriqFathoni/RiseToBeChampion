package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoginScreen implements Screen {

    private Stage stage; // Tempat kita menaruh semua elemen UI
    private Skin skin;   // Tampilan visual tombol dan teks
    private final Viewport viewport;

    public LoginScreen() {
        viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage); // Agar UI bisa diklik
        UiViewportScaler.syncNow(viewport, 1280f, 720f, 0.9f);

        // Memuat skin bawaan (uiskin.json) dari folder assets
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Membuat layout utama menggunakan Table
        Table rootTable = new Table();
        rootTable.setFillParent(true); // Table akan memenuhi seluruh layar
        stage.addActor(rootTable);

        // --- MEMBUAT ELEMEN UI ---
        // 1. Label Judul
        com.badlogic.gdx.scenes.scene2d.ui.Label titleLabel = new com.badlogic.gdx.scenes.scene2d.ui.Label("GAME OPREC - LOGIN", skin);

        // 2. Input Username
        com.badlogic.gdx.scenes.scene2d.ui.TextField usernameField = new com.badlogic.gdx.scenes.scene2d.ui.TextField("", skin);
        usernameField.setMessageText("Masukkan Username");

        // 3. Input Password
        com.badlogic.gdx.scenes.scene2d.ui.TextField passwordField = new com.badlogic.gdx.scenes.scene2d.ui.TextField("", skin);
        passwordField.setMessageText("Masukkan Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        // 4. Tombol Login & Register
        com.badlogic.gdx.scenes.scene2d.ui.TextButton loginBtn = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("Login", skin);
        com.badlogic.gdx.scenes.scene2d.ui.TextButton registerBtn = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("Register", skin);

        // --- MENYUSUN TATA LETAK (GRID/TABLE) ---
        // row() berfungsi seperti 'Enter' atau pindah ke baris bawahnya

        rootTable.add(titleLabel).colspan(2).padBottom(40).row(); // Judul di tengah atas

        rootTable.add(new com.badlogic.gdx.scenes.scene2d.ui.Label("Username:", skin)).right().padRight(10);
        rootTable.add(usernameField).width(200).padBottom(15).row();

        rootTable.add(new com.badlogic.gdx.scenes.scene2d.ui.Label("Password:", skin)).right().padRight(10);
        rootTable.add(passwordField).width(200).padBottom(30).row();

        rootTable.add(loginBtn).width(100).padRight(10).right();
        rootTable.add(registerBtn).width(100).left();
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

                                    ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
                                } else {
                                    System.out.println("GAGAL (" + statusCode + "): " + responseBody);
                                    // TODO: Di sini nanti kita tampilkan pop-up error ke pemain
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
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Menggambar UI ke layar
        stage.act(delta);
        stage.draw();
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
    }
}
