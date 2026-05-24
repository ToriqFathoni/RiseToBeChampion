package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.risetobechampion.frontend.Main;
import com.risetobechampion.frontend.combat.CombatLogger;
import com.risetobechampion.frontend.combat.Combatant;
import com.risetobechampion.frontend.combat.CombatantFactory;
import com.risetobechampion.frontend.combat.CombatantObserver;
import com.risetobechampion.frontend.game.PhysicsSystem;
import com.risetobechampion.frontend.game.input.PlayerInputController;
import com.risetobechampion.frontend.network.ApiClient;
import com.risetobechampion.frontend.network.ProgressManager;
import com.risetobechampion.frontend.screens.ui.BattleHUD;
import com.risetobechampion.frontend.screens.ui.PauseMenu;
import com.risetobechampion.frontend.utils.SessionManager;
import com.badlogic.gdx.Net;

public class BattleScreen implements Screen, CombatLogger, CombatantObserver {
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;
    private static final float FLOOR_Y = 50f;
    private static final float GRAVITY = -1500f;
    private static final float JUMP_VELOCITY = 650f;
    private static final float SPEED = 350f;

    private final Main game;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport worldViewport;
    private final Viewport uiViewport;
    private final Stage stage;
    private final Skin skin;

    private Combatant player;
    private Combatant enemy;
    private Texture stageBackgroundTexture;

    private boolean isLoading = true;
    private boolean isGameOver = false;
    private boolean isPaused = false;

    private PhysicsSystem physicsSystem;
    private PlayerInputController playerInputController;
    private BattleHUD hud;
    private PauseMenu pauseMenu;
    private Texture tryAgainTex;
    private Texture backMenuTex;
    private Texture losePopupTex;

    private int currentStage;
    private int deathCount;
    private float battleTimeElapsed = 0f;

    private int characterBaseAttack1 = 15;
    private int characterBaseAttack2 = 30;
    private int characterBaseAttack3 = 50;

    public BattleScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        worldViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);

        stage = new Stage(uiViewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(uiViewport, WORLD_WIDTH, WORLD_HEIGHT, 0.9f);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        currentStage = SessionManager.getInstance().getCurrentStage();
        if (currentStage < 1 || currentStage > 4) {
            currentStage = 1;
            SessionManager.getInstance().setCurrentStage(currentStage);
        }

        deathCount = SessionManager.getInstance().getDeathCount();

        loadBackground();
        fetchCombatSetup();
    }

    private void loadBackground() {
        String backgroundPath = currentStage == 1 ? "Looks/stage1.jpeg" : "Looks/stage" + currentStage + ".png";
        try {
            stageBackgroundTexture = new Texture(Gdx.files.internal(backgroundPath));
        } catch (Exception e) {
            System.err.println("Stage background not found: " + backgroundPath);
        }
    }

    private void fetchCombatSetup() {
        stage.clear();
        isLoading = true;
        Table loadingTable = new Table();
        loadingTable.setFillParent(true);
        loadingTable.add(new Label("Loading Stage " + currentStage + "...", skin)).center();
        stage.addActor(loadingTable);

        String runId = SessionManager.getInstance().getRunId();
        ApiClient.getCombatSetup(currentStage, runId, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                Gdx.app.postRunnable(() -> {
                    loadingTable.remove();
                    try {
                        JsonValue base = new JsonReader().parse(response);
                        JsonValue playerData = base.get("player");
                        JsonValue enemyData = base.get("enemy");
                        
                        setupCombatants(playerData, enemyData);
                        finishSetup();
                    } catch (Exception e) {
                        e.printStackTrace();
                        fallbackSetup();
                        finishSetup();
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    loadingTable.remove();
                    fallbackSetup();
                    finishSetup();
                });
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> {
                    loadingTable.remove();
                    fallbackSetup();
                    finishSetup();
                });
            }
        });
    }

    private void setupCombatants(JsonValue playerData, JsonValue enemyData) {
        String pName = SessionManager.getInstance().getSelectedCharacterName();
        if (pName == null) pName = playerData != null ? playerData.getString("name", "Kael The Phantom") : "Kael The Phantom";

        int pHp = playerData != null ? playerData.getInt("maxHp", 100) : 100;
        characterBaseAttack1 = playerData != null ? playerData.getInt("basicAttackDamage", 15) : 15;
        characterBaseAttack2 = playerData != null ? playerData.getInt("heavyAttackDamage", 30) : 30;
        characterBaseAttack3 = playerData != null ? playerData.getInt("skillDamage", 50) : 50;
        int maxEnergy = playerData != null ? playerData.getInt("maxEnergy", 100) : 100;

        SessionManager sm = SessionManager.getInstance();
        int finalHp = pHp + sm.getPlayerHpBonus();
        int att1 = characterBaseAttack1 + sm.getPlayerAttack1Bonus();
        int att2 = characterBaseAttack2 + sm.getPlayerAttack2Bonus();
        int att3 = characterBaseAttack3 + sm.getPlayerAttack3Bonus();

        player = createCombatantFromStats(pName, finalHp, maxEnergy, att1, att2, att3, 200, true);
        playerInputController = new PlayerInputController(PlayerInputController.InputProfile.PLAYER_1, att1, att2, att3, att3 + 80, JUMP_VELOCITY, SPEED);

        int eAtt1 = 10, eAtt2 = 20, eAtt3 = 40;
        if (enemyData != null) {
            String eName = enemyData.getString("name", "Enemy");
            int eHp = enemyData.getInt("maxHp", 100);
            eAtt1 = enemyData.getInt("basicAttackDamage", 10);
            eAtt2 = enemyData.getInt("heavyAttackDamage", 20);
            eAtt3 = enemyData.getInt("skillDamage", 40);
            enemy = createCombatantFromStats(eName, eHp, 100, eAtt1, eAtt2, eAtt3, 900, false);
        } else {
            enemy = createCombatantFromStats("Mr. Van", 100, 100, eAtt1, eAtt2, eAtt3, 900, false);
        }
        
        enemy.setAi(new com.risetobechampion.frontend.combat.AggressiveAi(eAtt1, eAtt2, eAtt3));
    }

    private void fallbackSetup() {
        SessionManager sm = SessionManager.getInstance();
        String pName = sm.getSelectedCharacterName();
        if (pName == null) pName = "Kael The Phantom";

        int finalHp = 100 + sm.getPlayerHpBonus();
        int att1 = 15 + sm.getPlayerAttack1Bonus();
        int att2 = 30 + sm.getPlayerAttack2Bonus();
        int att3 = 50 + sm.getPlayerAttack3Bonus();

        player = createCombatantFromStats(pName, finalHp, 100, att1, att2, att3, 200, true);
        playerInputController = new PlayerInputController(PlayerInputController.InputProfile.PLAYER_1, att1, att2, att3, att3 + 80, JUMP_VELOCITY, SPEED);

        enemy = createCombatantFromStats("Mr. Van", 100, 100, 10, 20, 40, 900, false);
        enemy.setAi(new com.risetobechampion.frontend.combat.AggressiveAi(10, 20, 40));
    }

    private Combatant createCombatantFromStats(String name, int hp, int maxEnergy, int att1, int att2, int att3, float x, boolean facingRight) {
        Combatant c;
        if ("Ryu".equals(name)) {
            c = CombatantFactory.createRyu(hp, maxEnergy, x, FLOOR_Y);
        } else if ("Mr. Van".equals(name)) {
            c = CombatantFactory.createMrVan(hp, att1, att2, att3, x, FLOOR_Y);
        } else if ("Chen Long".equals(name)) {
            c = CombatantFactory.createChenLong(hp, att1, att2, att3, x, FLOOR_Y);
        } else if ("Kagetsu".equals(name)) {
            c = CombatantFactory.createKagetsu(hp, att1, att2, att3, x, FLOOR_Y);
        } else if ("Joe".equals(name)) {
            c = CombatantFactory.createJoe(hp, att1, att2, att3, att3 + 80, x, FLOOR_Y);
        } else {
            c = CombatantFactory.createKael(hp, maxEnergy, x, FLOOR_Y);
        }
        c.setFacingRight(facingRight);
        return c;
    }

    private void finishSetup() {
        physicsSystem = new PhysicsSystem(FLOOR_Y, GRAVITY, WORLD_WIDTH, 10f, 0.5f, 0.85f);
        
        hud = new BattleHUD(stage, skin);
        hud.initialize(player, enemy, true);

        pauseMenu = new PauseMenu(stage, skin, game, () -> isPaused = false);

        player.addObserver(this);
        enemy.addObserver(this);
        isLoading = false;
        isGameOver = false;
        battleTimeElapsed = 0f;
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
        hud.refreshPlayer(combatant, combatant == player);
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
            stage.draw();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
        }

        if (!isPaused && !isGameOver) {
            battleTimeElapsed += delta;

            playerInputController.handleInput(player, enemy, this);
            
            player.update(delta, enemy, this);
            enemy.update(delta, player, this);
            
            physicsSystem.updatePhysics(player, enemy, delta);
            hud.update(delta);

            checkGameOver();
        }

        worldViewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (stageBackgroundTexture != null) {
            batch.draw(stageBackgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
        player.draw(batch);
        enemy.draw(batch);
        batch.end();

        uiViewport.apply();
        stage.act(isPaused ? 0f : delta);
        stage.draw();
    }

    private void checkGameOver() {
        SessionManager sm = SessionManager.getInstance();
        int newTotalTime = sm.getTotalTimeElapsed() + (int) battleTimeElapsed;
        sm.setTotalTimeElapsed(newTotalTime);

        if (enemy.getHp() <= 0) {
            isGameOver = true;
            enemy.setState(com.risetobechampion.frontend.combat.EntityState.DEFEATED);
            if (currentStage >= 4) {
                showFinalVictory(newTotalTime);
            } else {
                game.setScreen(new UpgradeScreen(game, deathCount, newTotalTime, player.getMaxHp()));
            }
        } else if (player.getHp() <= 0) {
            isGameOver = true;
            player.setState(com.risetobechampion.frontend.combat.EntityState.DEFEATED);
            deathCount++;
            sm.setDeathCount(deathCount);
            showGameOverDialog(newTotalTime);
        }
    }

    private void showFinalVictory(int totalTime) {
        hud.setCombatLog("STORY MODE CLEARED!");
        ProgressManager.saveCurrentProgress(4, deathCount, totalTime, "COMPLETED", new ProgressManager.SaveCallback() {
            @Override
            public void onSuccess() {
                SessionManager.getInstance().resetRunProgress();
                Gdx.app.postRunnable(() -> game.setScreen(new VictoryScreen(game, deathCount, totalTime)));
            }

            @Override
            public void onFailure(String errorMsg) {
                SessionManager.getInstance().resetRunProgress();
                Gdx.app.postRunnable(() -> game.setScreen(new MainMenuScreen()));
            }
        });
    }

    private void showGameOverDialog(int totalTime) {
        hud.setCombatLog("YOU DIED");
        ProgressManager.saveCurrentProgress(currentStage, deathCount, totalTime, "ONGOING", null);
        com.badlogic.gdx.scenes.scene2d.ui.Dialog dialog = new com.badlogic.gdx.scenes.scene2d.ui.Dialog("", skin) {
            @Override
            protected void result(Object object) {
                if (object.equals(true)) {
                    fetchCombatSetup();
                } else {
                    game.setScreen(new MainMenuScreen());
                }
            }
        };
        
        losePopupTex = new Texture(Gdx.files.internal("lose/Lose.png"));
        dialog.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(losePopupTex)));
        
        tryAgainTex = new Texture(Gdx.files.internal("button/try-again.png"));
        backMenuTex = new Texture(Gdx.files.internal("button/back-to-main-menu.png"));
        
        com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle tryAgainStyle = new com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle();
        tryAgainStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(tryAgainTex));
        com.badlogic.gdx.scenes.scene2d.ui.Button tryAgainBtn = new com.badlogic.gdx.scenes.scene2d.ui.Button(tryAgainStyle);
        
        com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle backMenuStyle = new com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle();
        backMenuStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(backMenuTex));
        com.badlogic.gdx.scenes.scene2d.ui.Button backMenuBtn = new com.badlogic.gdx.scenes.scene2d.ui.Button(backMenuStyle);
        
        dialog.getButtonTable().add(tryAgainBtn).width(250).height(60).padBottom(5).row();
        dialog.getButtonTable().add(backMenuBtn).width(250).height(60).padBottom(40);
        
        dialog.setObject(tryAgainBtn, true);
        dialog.setObject(backMenuBtn, false);
        
        dialog.show(stage);
        
        // Membatasi ukuran popup agar tidak terlalu besar
        dialog.setSize(600f, 350f);
        dialog.setPosition((stage.getWidth() - dialog.getWidth()) / 2f, (stage.getHeight() - dialog.getHeight()) / 2f);
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        uiViewport.update(width, height, true);
        UiViewportScaler.syncNow(uiViewport, WORLD_WIDTH, WORLD_HEIGHT, 0.9f);
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        skin.dispose();
        if (stageBackgroundTexture != null) stageBackgroundTexture.dispose();
        if (pauseMenu != null) pauseMenu.dispose();
        if (tryAgainTex != null) tryAgainTex.dispose();
        if (backMenuTex != null) backMenuTex.dispose();
        if (losePopupTex != null) losePopupTex.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
