package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.risetobechampion.frontend.Main;
import com.risetobechampion.frontend.network.ProgressManager;
import com.risetobechampion.frontend.utils.SessionManager;

public class UpgradeScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;

    private static final int BASE_PLAYER_ATTACK_1 = 10;
    private static final int BASE_PLAYER_ATTACK_2 = 25;
    private static final int BASE_PLAYER_ATTACK_3 = 45;

    private enum Phase {
        CHOICE,
        SUMMARY
    }

    private static final class UpgradeOption {
        private final String title;
        private final String description;
        private final int hpBonus;
        private final int attack1Bonus;
        private final int attack2Bonus;
        private final int attack3Bonus;

        private UpgradeOption(String title, String description, int hpBonus, int attack1Bonus, int attack2Bonus, int attack3Bonus) {
            this.title = title;
            this.description = description;
            this.hpBonus = hpBonus;
            this.attack1Bonus = attack1Bonus;
            this.attack2Bonus = attack2Bonus;
            this.attack3Bonus = attack3Bonus;
        }
    }

    private Phase phase = Phase.CHOICE;
    private final UpgradeOption[] upgradeOptions = new UpgradeOption[] {
        new UpgradeOption("Vitality Core", "+20 HP", 20, 0, 0, 0),
        new UpgradeOption("Blade Focus", "+5 Att 1", 0, 5, 0, 0),
        new UpgradeOption("Arc Mastery", "+4 Att 2, +7 Att 3", 0, 0, 4, 7)
    };
    private int selectedUpgradeIndex = 0;
    
    private final int deathCount;
    private final int battleTimeElapsed;

    private Texture backgroundTexture;
    private Texture summaryBackgroundTexture;
    private Texture confirmBtnTex;
    private Texture[] pilTextures = new Texture[3];
    private Image[] upgradeCardImages = new Image[3];

    private Label saveStatusLabel;
    private ImageButton nextStageButton;
    private Texture continueTex;
    private Texture backToMenuTex;

    private int displayStartHp;
    private int displayStartAttack1;
    private int displayStartAttack2;
    private int displayStartAttack3;
    private int displayTargetHp;
    private int displayTargetAttack1;
    private int displayTargetAttack2;
    private int displayTargetAttack3;

    private Label hpValueLabel;
    private Label attack1ValueLabel;
    private Label attack2ValueLabel;
    private Label attack3ValueLabel;
    private float summaryAnimationTime = 0f;
    private boolean victoryProgressSaved = false;
    private com.risetobechampion.frontend.game.input.UiControllerNavigator uiNavigator;
    private boolean prevDPadLeft, prevDPadRight, prevBtnA;

    public UpgradeScreen(Main game, int deathCount, int battleTimeElapsed, int playerMaxHp) {
        this.game = game;
        this.deathCount = deathCount;
        this.battleTimeElapsed = battleTimeElapsed;
        this.displayStartHp = playerMaxHp;

        FitViewport viewport = new FitViewport(1280f, 720f);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        createTextures();

        SessionManager sm = SessionManager.getInstance();
        displayStartAttack1 = BASE_PLAYER_ATTACK_1 + sm.getPlayerAttack1Bonus();
        displayStartAttack2 = BASE_PLAYER_ATTACK_2 + sm.getPlayerAttack2Bonus();
        displayStartAttack3 = BASE_PLAYER_ATTACK_3 + sm.getPlayerAttack3Bonus();

        buildChoiceScreen();
    }

    private void createTextures() {
        int stageNum = SessionManager.getInstance().getCurrentStage();
        if (stageNum < 1 || stageNum > 3) stageNum = 1;
        String bgPath = "CompleteStage/Stage" + stageNum + (stageNum == 1 ? ".png" : ".jpg");
        
        backgroundTexture = new Texture(Gdx.files.internal(bgPath));
        summaryBackgroundTexture = new Texture(Gdx.files.internal("CompleteStage/upgrade-applied.png"));
        confirmBtnTex = new Texture(Gdx.files.internal("CompleteStage/confirm-button.png"));
        pilTextures[0] = new Texture(Gdx.files.internal("CompleteStage/pil1.png"));
        pilTextures[1] = new Texture(Gdx.files.internal("CompleteStage/pil2.png"));
        pilTextures[2] = new Texture(Gdx.files.internal("CompleteStage/pil3.png"));
    }

    private void buildChoiceScreen() {
        stage.clear();

        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        stage.addActor(root);

        Table contentTable = new Table();
        root.add(contentTable).expand().bottom().padBottom(0f);

        int stageNum = SessionManager.getInstance().getCurrentStage();
        if (stageNum > 1) {
            Label chooseLabel = new Label("CHOOSE ONE UPGRADE TO CONTINUE", skin);
            chooseLabel.setFontScale(1.1f);
            chooseLabel.setAlignment(Align.center);
            chooseLabel.setColor(0.9f, 0.9f, 0.9f, 1f);
            contentTable.add(chooseLabel).padBottom(5f).row();
        }

        Table cardsRow = new Table();
        cardsRow.defaults().pad(15f);
        for (int i = 0; i < 3; i++) {
            cardsRow.add(createUpgradeCard(i)).width(260f).height(150f);
        }
        contentTable.add(cardsRow).row();

        Button.ButtonStyle confirmStyle = new Button.ButtonStyle();
        confirmStyle.up = new TextureRegionDrawable(new TextureRegion(confirmBtnTex));
        Button confirmBtn = new Button(confirmStyle);
        confirmBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                confirmSelectedUpgrade();
            }
        });
        contentTable.add(confirmBtn).width(180f).height(45f).padTop(5f).row();

        saveStatusLabel = new Label("", skin);
        saveStatusLabel.setAlignment(Align.center);
        saveStatusLabel.setColor(0.85f, 0.9f, 1f, 1f);
        contentTable.add(saveStatusLabel).padTop(10f).row();

        refreshCardStates();
    }

    private Image createUpgradeCard(final int index) {
        Image img = new Image(new TextureRegionDrawable(new TextureRegion(pilTextures[index])));
        img.setOrigin(Align.center);
        upgradeCardImages[index] = img;
        
        img.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (phase == Phase.CHOICE) {
                    selectedUpgradeIndex = index;
                    refreshCardStates();
                }
            }
        });
        return img;
    }

    private void refreshCardStates() {
        for (int i = 0; i < upgradeCardImages.length; i++) {
            if (upgradeCardImages[i] == null) continue;
            boolean selected = i == selectedUpgradeIndex;
            upgradeCardImages[i].setScale(selected ? 1.05f : 0.95f);
            upgradeCardImages[i].getColor().a = selected ? 1f : 0.5f;
        }
    }

    private void confirmSelectedUpgrade() {
        UpgradeOption selected = upgradeOptions[selectedUpgradeIndex];
        SessionManager sm = SessionManager.getInstance();

        sm.setPlayerHpBonus(sm.getPlayerHpBonus() + selected.hpBonus);
        sm.setPlayerAttack1Bonus(sm.getPlayerAttack1Bonus() + selected.attack1Bonus);
        sm.setPlayerAttack2Bonus(sm.getPlayerAttack2Bonus() + selected.attack2Bonus);
        sm.setPlayerAttack3Bonus(sm.getPlayerAttack3Bonus() + selected.attack3Bonus);

        displayTargetHp = displayStartHp + selected.hpBonus;
        displayTargetAttack1 = displayStartAttack1 + selected.attack1Bonus;
        displayTargetAttack2 = displayStartAttack2 + selected.attack2Bonus;
        displayTargetAttack3 = displayStartAttack3 + selected.attack3Bonus;

        phase = Phase.SUMMARY;
        summaryAnimationTime = 0f;
        
        int currentStage = sm.getCurrentStage();
        sm.setCurrentStage(currentStage + 1);
        
        buildSummaryScreen();

        if (saveStatusLabel != null) saveStatusLabel.setText("Menyimpan progress...");
        
        // simpan progress
        ProgressManager.saveCurrentProgress(currentStage + 1, deathCount, battleTimeElapsed, "ONGOING", new ProgressManager.SaveCallback() {
            @Override
            public void onSuccess() {
                victoryProgressSaved = true;
                if (saveStatusLabel != null) saveStatusLabel.setText("Progress tersimpan.");
                if (nextStageButton != null) nextStageButton.setDisabled(false);
            }

            @Override
            public void onFailure(String errorMsg) {
                victoryProgressSaved = false;
                if (saveStatusLabel != null) saveStatusLabel.setText("Gagal menyimpan progress: " + errorMsg);
                if (nextStageButton != null) nextStageButton.setDisabled(true);
            }
        });
    }

    private void buildSummaryScreen() {
        stage.clear();

        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new TextureRegion(summaryBackgroundTexture)));
        stage.addActor(root);

        Table contentTable = new Table();
        root.add(contentTable).expand().fill();

        Table statsTable = new Table();
        statsTable.defaults().height(54f);
        UpgradeOption selected = upgradeOptions[selectedUpgradeIndex];

        statsTable.add().width(350f);
        hpValueLabel = new Label(String.valueOf(displayTargetHp), skin);
        hpValueLabel.setFontScale(1.2f);
        statsTable.add(hpValueLabel).width(200f).center();
        
        Label hpChangeLabel = new Label("+" + selected.hpBonus, skin);
        hpChangeLabel.setFontScale(1.2f);
        if (selected.hpBonus > 0) hpChangeLabel.setColor(0.2f, 1f, 0.2f, 1f);
        statsTable.add(hpChangeLabel).width(200f).center().row();

        statsTable.add().width(350f);
        attack1ValueLabel = new Label(String.valueOf(displayTargetAttack1), skin);
        attack1ValueLabel.setFontScale(1.2f);
        statsTable.add(attack1ValueLabel).width(200f).center();

        Label attack1ChangeLabel = new Label("+" + selected.attack1Bonus, skin);
        attack1ChangeLabel.setFontScale(1.2f);
        if (selected.attack1Bonus > 0) attack1ChangeLabel.setColor(0.2f, 1f, 0.2f, 1f);
        statsTable.add(attack1ChangeLabel).width(200f).center().row();

        statsTable.add().width(350f);
        attack2ValueLabel = new Label(String.valueOf(displayTargetAttack2), skin);
        attack2ValueLabel.setFontScale(1.2f);
        statsTable.add(attack2ValueLabel).width(200f).center();

        Label attack2ChangeLabel = new Label("+" + selected.attack2Bonus, skin);
        attack2ChangeLabel.setFontScale(1.2f);
        if (selected.attack2Bonus > 0) attack2ChangeLabel.setColor(0.2f, 1f, 0.2f, 1f);
        statsTable.add(attack2ChangeLabel).width(200f).center().row();

        statsTable.add().width(350f);
        attack3ValueLabel = new Label(String.valueOf(displayTargetAttack3), skin);
        attack3ValueLabel.setFontScale(1.2f);
        statsTable.add(attack3ValueLabel).width(200f).center();

        Label attack3ChangeLabel = new Label("+" + selected.attack3Bonus, skin);
        attack3ChangeLabel.setFontScale(1.2f);
        if (selected.attack3Bonus > 0) attack3ChangeLabel.setColor(0.2f, 1f, 0.2f, 1f);
        statsTable.add(attack3ChangeLabel).width(200f).center().row();

        contentTable.add(statsTable).expand().center().padTop(225f).row();

        Table buttonsTable = new Table();

        continueTex = new Texture(Gdx.files.internal("button/continue.png"));
        nextStageButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(continueTex)));
        nextStageButton.setDisabled(true);
        nextStageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!nextStageButton.isDisabled()) {
                    // ganti page
                    game.setScreen(new LevelMapScreen());
                }
            }
        });
        buttonsTable.add(nextStageButton).width(240f).height(55f).padBottom(15f).row();

        backToMenuTex = new Texture(Gdx.files.internal("button/back-to-main-menu.png"));
        ImageButton backMenuBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(backToMenuTex)));
        backMenuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // ganti page
                game.setScreen(new MainMenuScreen());
            }
        });
        buttonsTable.add(backMenuBtn).width(240f).height(55f).padBottom(10f).row();

        contentTable.add(buttonsTable).bottom().padBottom(30f).row();

        uiNavigator = new com.risetobechampion.frontend.game.input.UiControllerNavigator();
        uiNavigator.addButton(nextStageButton);
        uiNavigator.addButton(backMenuBtn);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (phase == Phase.CHOICE) {
            boolean ctrlLeft = false;
            boolean ctrlRight = false;
            boolean ctrlA = false;
            
            if (com.badlogic.gdx.controllers.Controllers.getControllers().size > 0) {
                com.badlogic.gdx.controllers.Controller c = com.badlogic.gdx.controllers.Controllers.getControllers().get(0);
                com.badlogic.gdx.controllers.ControllerMapping m = c.getMapping();
                
                boolean currentLeft = c.getButton(m.buttonDpadLeft) || c.getAxis(m.axisLeftX) < -0.5f;
                boolean currentRight = c.getButton(m.buttonDpadRight) || c.getAxis(m.axisLeftX) > 0.5f;
                boolean currentA = c.getButton(m.buttonA);
                
                ctrlLeft = currentLeft && !prevDPadLeft;
                ctrlRight = currentRight && !prevDPadRight;
                ctrlA = currentA && !prevBtnA;
                
                prevDPadLeft = currentLeft;
                prevDPadRight = currentRight;
                prevBtnA = currentA;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A) || ctrlLeft) {
                selectedUpgradeIndex = (selectedUpgradeIndex + upgradeOptions.length - 1) % upgradeOptions.length;
                refreshCardStates();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D) || ctrlRight) {
                selectedUpgradeIndex = (selectedUpgradeIndex + 1) % upgradeOptions.length;
                refreshCardStates();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || ctrlA) {
                confirmSelectedUpgrade();
            }

            float pulse = 1f + 0.03f * (float) Math.sin((summaryAnimationTime + delta) * 18f + selectedUpgradeIndex);
            for (int i = 0; i < upgradeCardImages.length; i++) {
                if (upgradeCardImages[i] == null) continue;
                boolean selected = i == selectedUpgradeIndex;
                float targetScale = selected ? 1.05f + 0.02f * pulse : 0.95f;
                upgradeCardImages[i].setScale(targetScale);
                upgradeCardImages[i].getColor().a = selected ? 1f : 0.5f;
            }
        } else if (phase == Phase.SUMMARY) {
            summaryAnimationTime += delta;
            if (uiNavigator != null) {
                // update status secara berkala
                uiNavigator.update();
            }
        }

        stage.act(delta);
        // render karakter/gambar
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // update status secara berkala
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        // putar lagu menu
        com.risetobechampion.frontend.utils.AudioManager.getInstance().playMainMusic();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (summaryBackgroundTexture != null) summaryBackgroundTexture.dispose();
        if (confirmBtnTex != null) confirmBtnTex.dispose();
        for (Texture tex : pilTextures) {
            if (tex != null) tex.dispose();
        }
        if (continueTex != null) continueTex.dispose();
        if (backToMenuTex != null) backToMenuTex.dispose();
    }
}
