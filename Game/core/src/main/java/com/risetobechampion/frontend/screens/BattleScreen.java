package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.risetobechampion.frontend.combat.CombatLogger;
import com.risetobechampion.frontend.combat.Combatant;
import com.risetobechampion.frontend.combat.CombatantFactory;
import com.risetobechampion.frontend.command.BasicAttackCommand;
import com.risetobechampion.frontend.command.HeavyAttackCommand;
import com.risetobechampion.frontend.command.SkillCommand;
import com.risetobechampion.frontend.command.TauntCommand;
import com.risetobechampion.frontend.command.CommandInvoker;
import com.risetobechampion.frontend.game.GameState;
import com.risetobechampion.frontend.combat.CombatantObserver;
import com.risetobechampion.frontend.combat.EntityState;
import com.risetobechampion.frontend.network.ApiClient;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class BattleScreen implements Screen, CombatLogger, CombatantObserver {
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;
    private static final float FLOOR_Y = 150f;
    private static final float GRAVITY = -1500f;
    private static final float JUMP_VELOCITY = 800f;

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport worldViewport;
    private final Viewport uiViewport;

    private final Stage stage;
    private final Skin skin;

    private Combatant player;
    private Combatant enemy;

    private final Label combatLog;
    private Label energyWarningLabel;
    private Label playerHpLabel;
    private Label playerEnergyLabel;
    private Label enemyHpLabel;
    private ProgressBar playerHpBar;
    private ProgressBar playerEnergyBar;
    private ProgressBar enemyHpBar;
    private float energyWarningTimer;
    
    private Texture greenBarTexture;
    private Texture yellowBarTexture;

    private boolean isGameOver;
    private boolean isLoading;
    private int currentStage = 1;

    public BattleScreen() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        worldViewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);

        stage = new Stage(uiViewport);
        Gdx.input.setInputProcessor(stage);
        UiViewportScaler.syncNow(uiViewport, WORLD_WIDTH, WORLD_HEIGHT, 0.9f);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        
        initializeColoredBars();

        isLoading = true;
        isGameOver = false;

        // Create a temporary loading label
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();
        stage.addActor(rootTable);

        combatLog = new Label("Loading combat data...", skin);
        combatLog.setAlignment(Align.center);
        rootTable.add(combatLog).center();

        // Fetch combat data from backend
        fetchCombatSetup(currentStage);
    }

    /**
     * Fetch combat setup data from the backend API.
     * The API returns player and enemy stats, which are then used to create Combatants.
     */
    private void fetchCombatSetup(int stageId) {
        ApiClient.getCombatSetup(stageId, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final String response = httpResponse.getResultAsString();
                Gdx.app.postRunnable(() -> {
                    try {
                        if (httpResponse.getStatus().getStatusCode() < 200 || httpResponse.getStatus().getStatusCode() >= 300) {
                            throw new IllegalStateException("HTTP " + httpResponse.getStatus().getStatusCode() + ": " + response);
                        }

                        JsonValue json = new JsonReader().parse(response);

                        // Parse player data
                        JsonValue playerData = json.get("player");
                        int playerMaxHp = playerData.getInt("maxHp");
                        int playerMaxEnergy = playerData.getInt("maxEnergy");

                        // Parse enemy data
                        JsonValue enemyData = json.get("enemy");
                        int enemyMaxHp = enemyData.getInt("maxHp");
                        int enemyBasicDamage = enemyData.getInt("basicAttackDamage");
                        int enemyHeavyDamage = enemyData.getInt("heavyAttackDamage");

                        // Create combatants with fetched data
                        player = CombatantFactory.createKael(playerMaxHp, playerMaxEnergy, 200, FLOOR_Y);
                        enemy = CombatantFactory.createMrVan(enemyMaxHp, enemyBasicDamage, enemyHeavyDamage, 900, FLOOR_Y);

                        // publish to global game state (Singleton)
                        GameState.getInstance().setPlayer(player);
                        GameState.getInstance().setEnemy(enemy);

                        // Set up observers and UI
                        player.addObserver(BattleScreen.this);
                        enemy.addObserver(BattleScreen.this);

                        // Rebuild the UI with player and enemy data
                        rebuildUI();

                        isLoading = false;
                        combatLog.setText("Fight! Gunakan J, K, L, T");
                    } catch (Exception e) {
                        e.printStackTrace();
                        combatLog.setText("Gagal memuat data combat!");
                        isLoading = false;
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                combatLog.setText("Network error: " + t.getMessage());
                isLoading = false;
            }

            @Override
            public void cancelled() {
                combatLog.setText("Request cancelled");
                isLoading = false;
            }
        });
    }

    /**
     * Rebuild the UI after successful combat setup fetch.
     */
    private void rebuildUI() {
        // Clear existing actors
        stage.clear();

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(24f);
        stage.addActor(rootTable);

        // Enemy HUD
        enemyHpLabel = new Label("", skin);
        enemyHpLabel.setColor(1f, 0.3f, 0.3f, 1f);
        enemyHpLabel.setFontScale(1.3f);

        enemyHpBar = new ProgressBar(0f, enemy.getMaxHp(), 1f, false, skin);
        enemyHpBar.setValue(enemy.getHp());

        // Combat Log
        combatLog.setAlignment(Align.center);
        combatLog.setWrap(true);

        energyWarningLabel = new Label("", skin);
        energyWarningLabel.setAlignment(Align.center);
        energyWarningLabel.setFontScale(1.15f);
        energyWarningLabel.setColor(1f, 0.35f, 0.35f, 1f);

        // Player HUD
        playerHpLabel = new Label("", skin);
        playerHpLabel.setColor(0.3f, 1f, 0.3f, 1f);
        playerHpLabel.setFontScale(1.3f);

        playerHpBar = new ProgressBar(0f, player.getMaxHp(), 1f, false, skin);
        playerHpBar.setValue(player.getHp());

        // Player Energy Bar (new)
        playerEnergyLabel = new Label("", skin);
        playerEnergyLabel.setColor(1f, 1f, 0f, 1f);
        playerEnergyLabel.setFontScale(0.95f);

        playerEnergyBar = new ProgressBar(0f, player.getMaxEnergy(), 1f, false, skin);
        playerEnergyBar.setValue(player.getEnergy());
        applyColoredBars();

        // Top HUD layout
        Table topHudTable = new Table();
        topHudTable.setFillParent(false);

        Table enemyHudTable = new Table();
        enemyHudTable.right();
        enemyHudTable.add(enemyHpLabel).right().expandX().row();
        enemyHudTable.add(enemyHpBar).width(420f).height(18f).right();

        Table playerHudTable = new Table();
        playerHudTable.left();
        playerHudTable.defaults().left();
        playerHudTable.add(playerHpLabel).left().expandX().padBottom(4f).row();
        playerHudTable.add(playerHpBar).width(420f).height(18f).left().padBottom(10f).row();
        playerHudTable.add(playerEnergyLabel).left().expandX().padTop(2f).padBottom(4f).row();
        playerHudTable.add(playerEnergyBar).width(420f).height(14f).left();

        topHudTable.add(playerHudTable).expandX().left().padRight(24f);
        topHudTable.add(enemyHudTable).expandX().right();

        rootTable.top();
        rootTable.padTop(10f);
        rootTable.add(topHudTable).expandX().fillX().top().row();
        rootTable.add(energyWarningLabel).expandX().center().padTop(10f).row();
        rootTable.add(combatLog).expand().center().width(760f).padTop(120f).padBottom(120f).row();

        refreshHud(player);
        refreshHud(enemy);
        energyWarningTimer = 0f;
        energyWarningLabel.setText("");
    }

    @Override
    public void log(String message) {
        combatLog.setText(message);
        if (message != null && message.toLowerCase().contains("energi tidak cukup")) {
            energyWarningTimer = 1.4f;
            energyWarningLabel.setText("ENERGI TIDAK CUKUP");
            energyWarningLabel.setColor(1f, 0.25f, 0.25f, 1f);
        }
    }

    @Override
    public void onCombatantChanged(Combatant combatant) {
        refreshHud(combatant);
    }

    private void refreshHud(Combatant combatant) {
        if (combatant == player) {
            playerHpLabel.setText(player.getName() + " HP: " + player.getHp() + " / " + player.getMaxHp());
            playerHpBar.setValue(player.getHp());
            playerEnergyLabel.setText("Energy:");
            playerEnergyBar.setValue(player.getEnergy());
        } else if (combatant == enemy) {
            enemyHpLabel.setText(enemy.getName() + " HP: " + enemy.getHp() + " / " + enemy.getMaxHp());
            enemyHpBar.setValue(enemy.getHp());
        }
    }
    
    private void initializeColoredBars() {
        // Create green texture for HP bar (larger for proper stretching)
        Pixmap greenPixmap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        greenPixmap.setColor(0.2f, 0.9f, 0.2f, 1f);
        greenPixmap.fill();
        greenBarTexture = new Texture(greenPixmap);
        greenBarTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        greenPixmap.dispose();
        
        // Create yellow texture for energy bar (larger for proper stretching)
        Pixmap yellowPixmap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        yellowPixmap.setColor(1f, 1f, 0f, 1f);
        yellowPixmap.fill();
        yellowBarTexture = new Texture(yellowPixmap);
        yellowBarTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        yellowPixmap.dispose();
    }
    
    private void applyColoredBars() {
        if (playerHpBar != null && greenBarTexture != null) {
            ProgressBar.ProgressBarStyle playerHpStyle = new ProgressBar.ProgressBarStyle(playerHpBar.getStyle());
            playerHpStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(greenBarTexture));
            playerHpBar.setStyle(playerHpStyle);
        }
        
        if (playerEnergyBar != null && yellowBarTexture != null) {
            ProgressBar.ProgressBarStyle playerEnergyStyle = new ProgressBar.ProgressBarStyle(playerEnergyBar.getStyle());
            playerEnergyStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(yellowBarTexture));
            playerEnergyBar.setStyle(playerEnergyStyle);
        }
        
        if (enemyHpBar != null && greenBarTexture != null) {
            ProgressBar.ProgressBarStyle enemyHpStyle = new ProgressBar.ProgressBarStyle(enemyHpBar.getStyle());
            enemyHpStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(greenBarTexture));
            enemyHpBar.setStyle(enemyHpStyle);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (energyWarningTimer > 0f) {
            energyWarningTimer = Math.max(0f, energyWarningTimer - delta);
            float pulse = 0.55f + 0.45f * (float) Math.sin((1.4f - energyWarningTimer) * 18f);
            energyWarningLabel.setColor(1f, 0.25f + 0.35f * pulse, 0.25f + 0.15f * pulse, 1f);
            if (energyWarningTimer <= 0f) {
                energyWarningLabel.setText("");
            }
        }

        if (!isLoading && player != null && enemy != null) {
            updateGameLogic(delta);

            worldViewport.apply();
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            player.draw(batch);
            enemy.draw(batch);
            batch.end();
        }

        stage.act(delta);
        stage.draw();
    }

    private void updateGameLogic(float delta) {
        if (player == null || enemy == null) {
            return;
        }

        if (!isGameOver) {
            handlePlayerInput(delta);
        }

        player.update(delta, enemy, this);
        enemy.update(delta, player, this);
        player.applyPhysics(delta, GRAVITY, FLOOR_Y);
        enemy.applyPhysics(delta, GRAVITY, FLOOR_Y);

        if (!isGameOver && enemy.getHp() <= 0) {
            isGameOver = true;
            enemy.setState(EntityState.DEFEATED);
            log("Kemenangan!");
        } else if (!isGameOver && player.getHp() <= 0) {
            isGameOver = true;
            player.setState(EntityState.DEFEATED);
            log("Kekalahan!");
        }
    }

    private void handlePlayerInput(float delta) {
        if (player.getHp() <= 0 || player.isLocked()) {
            return;
        }

        player.getVelocity().x = 0f;
        float speed = 350f;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.getVelocity().x = -speed;
            player.setFacingRight(false);
            if (player.isGrounded()) {
                player.setState(EntityState.WALK);
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.getVelocity().x = speed;
            player.setFacingRight(true);
            if (player.isGrounded()) {
                player.setState(EntityState.WALK);
            }
        } else if (player.isGrounded()) {
            player.setState(EntityState.IDLE);
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) && player.isGrounded()) {
            player.jump(JUMP_VELOCITY);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            CommandInvoker.getInstance().execute(new BasicAttackCommand(player, enemy, 10, this));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            CommandInvoker.getInstance().execute(new HeavyAttackCommand(player, enemy, 25, this));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            CommandInvoker.getInstance().execute(new SkillCommand(player, enemy, 45, this));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            CommandInvoker.getInstance().execute(new TauntCommand(player, this));
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        UiViewportScaler.update(uiViewport, width, height, WORLD_WIDTH, WORLD_HEIGHT, 0.9f);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (player != null) player.dispose();
        if (enemy != null) enemy.dispose();
        if (greenBarTexture != null) greenBarTexture.dispose();
        if (yellowBarTexture != null) yellowBarTexture.dispose();
        stage.dispose();
        skin.dispose();
        batch.dispose();
    }
}
