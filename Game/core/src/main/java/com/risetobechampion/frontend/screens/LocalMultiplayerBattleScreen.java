package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.risetobechampion.frontend.combat.CombatLogger;
import com.risetobechampion.frontend.combat.Combatant;
import com.risetobechampion.frontend.combat.CombatantFactory;
import com.risetobechampion.frontend.combat.CombatantObserver;
import com.risetobechampion.frontend.game.PhysicsSystem;
import com.risetobechampion.frontend.Main;
import com.risetobechampion.frontend.game.input.PlayerInputController;
import com.risetobechampion.frontend.game.input.PlayerInputController.InputProfile;
import com.risetobechampion.frontend.network.ApiClient;
import com.risetobechampion.frontend.utils.SessionManager;
import com.risetobechampion.frontend.screens.ui.BattleHUD;
import com.risetobechampion.frontend.screens.ui.PauseMenu;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpResponseHeader;

public class LocalMultiplayerBattleScreen implements Screen, CombatLogger, CombatantObserver {
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;
    private static final float FLOOR_Y = 50f;
    private static final float GRAVITY = -1500f;
    private static final float JUMP_VELOCITY = 650f;
    private static final float SPEED = 350f;

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport worldViewport;
    private final Viewport uiViewport;
    private final Stage stage;
    private final Skin skin;

    private Combatant player1;
    private Combatant player2;
    private Texture stageBackgroundTexture;

    private boolean isLoading = true;
    private boolean isGameOver = false;
    private boolean isPaused = false;

    private PhysicsSystem physicsSystem;
    private PlayerInputController p1InputController;
    private PlayerInputController p2InputController;
    private BattleHUD hud;
    private PauseMenu pauseMenu;
    private Table victoryOverlay;
    private com.risetobechampion.frontend.game.input.UiControllerNavigator uiNavigator;
    private boolean prevStartBtn;

    public LocalMultiplayerBattleScreen() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        worldViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);

        stage = new Stage(uiViewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(uiViewport, WORLD_WIDTH, WORLD_HEIGHT, 0.9f);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        loadBackground();
        fetchCharactersAndSetup();
    }

    private void loadBackground() {
        int randomStage = MathUtils.random(1, 4);
        String backgroundPath = randomStage == 1 ? "Looks/stage1.jpeg" : "Looks/stage" + randomStage + ".png";
        try {
            stageBackgroundTexture = new Texture(Gdx.files.internal(backgroundPath));
        } catch (Exception e) {
            System.err.println("Stage background not found: " + backgroundPath);
        }
    }

    private void fetchCharactersAndSetup() {
        isLoading = true;
        Table loadingTable = new Table();
        loadingTable.setFillParent(true);
        loadingTable.add(new Label("Loading characters...", skin)).center();
        stage.addActor(loadingTable);

        // tembak api backend
        ApiClient.getCharacters(new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                Gdx.app.postRunnable(() -> {
                    loadingTable.remove();
                    try {
                        JsonValue base = new JsonReader().parse(response);
                        parseAndBuildPlayers(base);
                        finishSetup();
                    } catch (Exception e) {
                        fallbackToDefaultStats();
                        finishSetup();
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    loadingTable.remove();
                    fallbackToDefaultStats();
                    finishSetup();
                });
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> {
                    loadingTable.remove();
                    fallbackToDefaultStats();
                    finishSetup();
                });
            }
        });
    }

    private void parseAndBuildPlayers(JsonValue base) {
        String p1Name = SessionManager.getInstance().getSelectedCharacterName();
        String p2Name = SessionManager.getInstance().getPlayer2CharacterName();
        if (p1Name == null) p1Name = "Kael The Phantom";
        if (p2Name == null) p2Name = "Mr. Van";

        for (JsonValue charNode : base) {
            String name = charNode.getString("name", "Unknown");
            int hp = charNode.getInt("maxHp", 100);
            int att1 = charNode.getInt("basicAttackDamage", 15);
            int att2 = charNode.getInt("heavyAttackDamage", 30);
            int att3 = charNode.getInt("skillDamage", 50);

            if (name.equals(p1Name)) {
                player1 = createCombatantFromStats(name, hp, att1, att2, att3, 200, true);
                p1InputController = new PlayerInputController(InputProfile.PLAYER_1, att1, att2, att3, att3 + 80, JUMP_VELOCITY, SPEED);
            }
            if (name.equals(p2Name)) {
                player2 = createCombatantFromStats(name, hp, att1, att2, att3, 900, false);
                p2InputController = new PlayerInputController(InputProfile.PLAYER_2, att1, att2, att3, att3 + 80, JUMP_VELOCITY, SPEED);
            }
        }
        applyDefaultStatsIfNull(p1Name, p2Name);
    }

    private void fallbackToDefaultStats() {
        String p1Name = SessionManager.getInstance().getSelectedCharacterName();
        String p2Name = SessionManager.getInstance().getPlayer2CharacterName();
        if (p1Name == null) p1Name = "Kael The Phantom";
        if (p2Name == null) p2Name = "Mr. Van";
        applyDefaultStatsIfNull(p1Name, p2Name);
    }

    private void applyDefaultStatsIfNull(String p1Name, String p2Name) {
        if (player1 == null) {
            int hp = getDefaultHp(p1Name);
            int att1 = getDefaultAtt1(p1Name);
            int att2 = getDefaultAtt2(p1Name);
            int att3 = getDefaultAtt3(p1Name);
            player1 = createCombatantFromStats(p1Name, hp, att1, att2, att3, 200, true);
            p1InputController = new PlayerInputController(InputProfile.PLAYER_1, att1, att2, att3, att3 + 80, JUMP_VELOCITY, SPEED);
        }
        if (player2 == null) {
            int hp = getDefaultHp(p2Name);
            int att1 = getDefaultAtt1(p2Name);
            int att2 = getDefaultAtt2(p2Name);
            int att3 = getDefaultAtt3(p2Name);
            player2 = createCombatantFromStats(p2Name, hp, att1, att2, att3, 900, false);
            p2InputController = new PlayerInputController(InputProfile.PLAYER_2, att1, att2, att3, att3 + 80, JUMP_VELOCITY, SPEED);
        }
    }

    private Combatant createCombatantFromStats(String name, int hp, int att1, int att2, int att3, float x, boolean facingRight) {
        Combatant c;
        if ("Ryu".equals(name)) {
            c = CombatantFactory.createRyu(hp, 100, x, FLOOR_Y);
        } else if ("Mr. Van".equals(name)) {
            c = CombatantFactory.createMrVan(hp, att1, att2, att3, x, FLOOR_Y);
        } else if ("Chen Long".equals(name)) {
            c = CombatantFactory.createChenLong(hp, att1, att2, att3, x, FLOOR_Y);
        } else if ("Kagetsu".equals(name)) {
            c = CombatantFactory.createKagetsu(hp, att1, att2, att3, x, FLOOR_Y);
        } else if ("Joe".equals(name)) {
            c = CombatantFactory.createJoe(hp, att1, att2, att3, att3 + 80, x, FLOOR_Y);
        } else {
            c = CombatantFactory.createKael(hp, 100, x, FLOOR_Y);
        }
        c.setAi(null);
        c.setFacingRight(facingRight);
        return c;
    }

    private void finishSetup() {
        physicsSystem = new PhysicsSystem(FLOOR_Y, GRAVITY, WORLD_WIDTH, 10f, 0.5f, 0.85f);
        
        hud = new BattleHUD(stage, skin);
        hud.initialize(player1, player2, false);

        Main game = (Main) Gdx.app.getApplicationListener();
        pauseMenu = new PauseMenu(stage, skin, game, () -> isPaused = false);

        player1.addObserver(this);
        player2.addObserver(this);
        isLoading = false;
    }

    @Override
    public void log(String message) {
        if (message != null && message.toLowerCase().contains("energi tidak cukup")) {
            hud.showEnergyWarning();
        } else {
            hud.setCombatLog(message);
        }
    }

    @Override
    public void onCombatantChanged(Combatant combatant) {
        hud.refreshPlayer(combatant, combatant == player1);
    }

    private void togglePause() {
        if (isGameOver || isLoading) return;
        isPaused = !isPaused;
        pauseMenu.setVisible(isPaused);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (isLoading) {
            uiViewport.apply();
            stage.act(delta);
            // render karakter/gambar
            stage.draw();
            return;
        }

        boolean startBtn = com.badlogic.gdx.controllers.Controllers.getControllers().size > 0 && com.badlogic.gdx.controllers.Controllers.getControllers().get(0).getButton(com.badlogic.gdx.controllers.Controllers.getControllers().get(0).getMapping().buttonStart);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || (startBtn && !prevStartBtn)) {
            togglePause();
        }
        prevStartBtn = startBtn;

        if (isPaused) {
            // update status secara berkala
            pauseMenu.update();
        } else if (!isGameOver) {
            // baca input tombol
            p1InputController.handleInput(player1, player2, this);
            // baca input tombol
            p2InputController.handleInput(player2, player1, this);

            // update status secara berkala
            player1.update(delta, player2, this);
            // update status secara berkala
            player2.update(delta, player1, this);
            
            physicsSystem.updatePhysics(player1, player2, delta);
            // update status secara berkala
            hud.update(delta);

            if (player1.getHp() <= 0 && player2.getHp() <= 0) {
                showVictory("DRAW!");
            } else if (player1.getHp() <= 0) {
                showVictory("PLAYER 2 WINS!");
            } else if (player2.getHp() <= 0) {
                showVictory("PLAYER 1 WINS!");
            }
        }

        worldViewport.apply();
        // update status secara berkala
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (stageBackgroundTexture != null) {
            // render karakter/gambar
            batch.draw(stageBackgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
        // render karakter/gambar
        player1.draw(batch);
        // render karakter/gambar
        player2.draw(batch);
        batch.end();

        uiViewport.apply();
        stage.act(isPaused ? 0f : delta);
        // render karakter/gambar
        stage.draw();
        
        if (isGameOver && uiNavigator != null) {
            // update status secara berkala
            uiNavigator.update();
        }
    }

    private void showVictory(String message) {
        isGameOver = true;
        hud.setCombatLog(message);

        victoryOverlay = new Table();
        victoryOverlay.setFillParent(true);
        Table bg = new Table();
        bg.setFillParent(true);
        bg.setColor(0f, 0f, 0f, 0.7f);
        victoryOverlay.addActor(bg);

        Table content = new Table();
        content.setFillParent(true);
        victoryOverlay.addActor(content);

        Label winLabel = new Label(message, skin);
        winLabel.setFontScale(2.5f);
        winLabel.setColor(message.contains("PLAYER 1") ? com.badlogic.gdx.graphics.Color.GREEN : com.badlogic.gdx.graphics.Color.RED);
        if (message.contains("DRAW")) winLabel.setColor(com.badlogic.gdx.graphics.Color.YELLOW);
        
        content.add(winLabel).padBottom(40f).row();

        TextButton backBtn = new TextButton("Back to Menu", skin);
        backBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // ganti page
                ((Main) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            }
        });
        content.add(backBtn).width(250f).height(60f);

        uiNavigator = new com.risetobechampion.frontend.game.input.UiControllerNavigator();
        uiNavigator.addButton(backBtn);

        stage.addActor(victoryOverlay);
    }

    private int getDefaultHp(String name) {
        if ("Joe".equals(name)) return 300;
        if ("Kagetsu".equals(name)) return 220;
        if ("Chen Long".equals(name)) return 150;
        if ("Ryu".equals(name)) return 105;
        if ("Kael The Phantom".equals(name) || "Kael".equals(name)) return 90;
        return 100;
    }
    
    private int getDefaultAtt1(String name) {
        if ("Joe".equals(name)) return 35;
        if ("Kagetsu".equals(name)) return 28;
        if ("Chen Long".equals(name)) return 20;
        if ("Ryu".equals(name)) return 9;
        if ("Kael The Phantom".equals(name) || "Kael".equals(name)) return 12;
        return 15;
    }
    
    private int getDefaultAtt2(String name) {
        if ("Joe".equals(name)) return 70;
        if ("Kagetsu".equals(name)) return 55;
        if ("Chen Long".equals(name)) return 40;
        if ("Ryu".equals(name)) return 22;
        if ("Kael The Phantom".equals(name) || "Kael".equals(name)) return 28;
        return 30;
    }
    
    private int getDefaultAtt3(String name) {
        if ("Joe".equals(name)) return 120;
        if ("Kagetsu".equals(name)) return 90;
        if ("Chen Long".equals(name)) return 65;
        if ("Ryu".equals(name)) return 40;
        if ("Kael The Phantom".equals(name) || "Kael".equals(name)) return 50;
        return 50;
    }

    @Override
    public void resize(int width, int height) {
        // update status secara berkala
        worldViewport.update(width, height, true);
        // update status secara berkala
        uiViewport.update(width, height, true);
        UiViewportScaler.syncNow(uiViewport, WORLD_WIDTH, WORLD_HEIGHT, 0.9f);
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        skin.dispose();
        if (stageBackgroundTexture != null) stageBackgroundTexture.dispose();
    }

    @Override
    public void show() {
        // putar lagu berantem
        com.risetobechampion.frontend.utils.AudioManager.getInstance().playFightMusic();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
