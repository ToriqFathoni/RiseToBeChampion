package com.risetobechampion.frontend.screens; // Sesuaikan jika berbeda

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CharacterSelectScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private final Viewport viewport;

    public CharacterSelectScreen() {
        viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(viewport, 1280f, 720f, 0.9f);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        final Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Label Judul (Bisa langsung ditambahkan)
        Label titleLabel = new Label("PILIH KARAKTER ANDA", skin);
        titleLabel.setFontScale(1.5f);
        rootTable.add(titleLabel).padBottom(40).row();

        Label loadingLabel = new Label("Memuat data karakter dari server...", skin);
        rootTable.add(loadingLabel).padBottom(20).row();

        System.out.println("Meminta data karakter ke Backend...");

        // Memanggil API getCharacters
        com.risetobechampion.frontend.network.ApiClient.getCharacters(new com.badlogic.gdx.Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                final String responseBody = httpResponse.getResultAsString();

                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        // Hapus tulisan "Memuat data..."
                        rootTable.removeActor(loadingLabel);
                        rootTable.clearChildren(); // Bersihkan isi tabel sementara

                        rootTable.add(titleLabel).padBottom(40).row(); // Masukkan ulang judul

                        if (statusCode == 200) {
                            System.out.println("Data Karakter Diterima: " + responseBody);

                            // Membaca JSON balasan dari Spring Boot
                            com.badlogic.gdx.utils.JsonReader json = new com.badlogic.gdx.utils.JsonReader();
                            com.badlogic.gdx.utils.JsonValue base = json.parse(responseBody);

                            // Looping untuk membuat tombol sebanyak jumlah karakter
                            for (com.badlogic.gdx.utils.JsonValue charNode : base) {
                                // CATATAN: Ubah "name" sesuai dengan nama kolom/properti di entitas GameCharacter Anda
                                String charName = charNode.getString("name", "Unknown");
                                int charId = charNode.getInt("charId", 0); // Ambil ID karakternya

                                TextButton charBtn = new TextButton(charName, skin);
                                rootTable.add(charBtn).width(300).height(50).padBottom(15).row();

                                // Aksi saat tombol karakter dipilih
                                charBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
                                    @Override
                                    public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                                        System.out.println("Karakter " + charName + " (ID: " + charId + ") dipilih!");

                                        // Mengambil UserID dari sesi global
                                        String loggedInUserId = com.risetobechampion.frontend.utils.SessionManager.getInstance().getUserId();

                                        System.out.println("Memulai game untuk User: " + loggedInUserId + " dengan Karakter ID: " + charId);

                                        // Menembak API /start
                                        com.risetobechampion.frontend.network.ApiClient.startGame(loggedInUserId, charId, new com.badlogic.gdx.Net.HttpResponseListener() {
                                            @Override
                                            public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
                                                int startStatus = httpResponse.getStatus().getStatusCode();
                                                final String startBody = httpResponse.getResultAsString();

                                                Gdx.app.postRunnable(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (startStatus == 200) {
                                                            System.out.println("BERHASIL MEMULAI GAME! " + startBody);

                                                            // --- KODE BARU: Menyimpan RunID ke SessionManager ---
                                                            try {
                                                                // Mengambil teks tepat setelah kata "RunID: "
                                                                String extractedRunId = startBody.substring(startBody.indexOf("RunID: ") + 7).trim();
                                                                com.risetobechampion.frontend.utils.SessionManager.getInstance().setRunId(extractedRunId);
                                                                System.out.println("RunID berhasil diamankan di Sesi Global: " + extractedRunId);
                                                            } catch (Exception e) {
                                                                System.out.println("Gagal mengekstrak RunID dari server!");
                                                            }
                                                            // -----------------------------------------------------
                                                            com.risetobechampion.frontend.utils.SessionManager.getInstance().setCurrentStage(1);
                                                            ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new LevelMapScreen());
                                                        } else {
                                                            System.out.println("GAGAL MEMULAI GAME: " + startBody);
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void failed(Throwable t) {
                                                System.out.println("Error koneksi saat mencoba memulai game!");
                                            }

                                            @Override
                                            public void cancelled() {}
                                        });
                                    }
                                });
                            }
                        } else {
                            rootTable.add(new Label("Gagal memuat karakter. Error: " + statusCode, skin)).row();
                        }

                        // Selalu tambahkan tombol kembali di paling bawah
                        TextButton backBtn = new TextButton("Kembali ke Main Menu", skin);
                        rootTable.add(backBtn).width(200).height(40).padTop(30).row();

                        backBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
                            }
                        });
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        rootTable.clearChildren();
                        rootTable.add(titleLabel).padBottom(40).row();
                        rootTable.add(new Label("Koneksi ke server terputus!", skin)).row();

                        TextButton backBtn = new TextButton("Kembali ke Main Menu", skin);
                        rootTable.add(backBtn).width(200).height(40).padTop(30).row();
                        backBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
                            }
                        });
                    }
                });
            }

            @Override
            public void cancelled() {}
        });
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Warna latar agak kemerahan untuk membedakan dengan Main Menu
        Gdx.gl.glClearColor(0.2f, 0.1f, 0.1f, 1);
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
