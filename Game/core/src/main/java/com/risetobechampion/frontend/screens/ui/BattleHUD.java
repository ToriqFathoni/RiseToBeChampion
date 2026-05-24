package com.risetobechampion.frontend.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.risetobechampion.frontend.combat.Combatant;

/**
 * Handles the HUD for the battle screen, enforcing MVC architecture.
 */
public class BattleHUD {
    private final Skin skin;
    private final Stage stage;
    
    private Label p1HpLabel;
    private Label p1EnergyLabel;
    private ProgressBar p1HpBar;
    private ProgressBar p1EnergyBar;

    private Label p2HpLabel;
    private Label p2EnergyLabel;
    private ProgressBar p2HpBar;
    private ProgressBar p2EnergyBar;

    private Label energyWarningLabel;

    private float energyWarningTimer;
    private boolean isStoryMode;

    public BattleHUD(Stage stage, Skin skin) {
        this.stage = stage;
        this.skin = skin;
    }

    public void initialize(Combatant player1, Combatant player2, boolean isStoryMode) {
        this.isStoryMode = isStoryMode;
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(24f);
        stage.addActor(rootTable);

        p1HpLabel = new Label("", skin);
        p1HpLabel.setColor(0.3f, 1f, 0.3f, 1f);
        p1HpLabel.setFontScale(1.3f);
        p1HpBar = new ProgressBar(0f, player1.getMaxHp(), 1f, false, skin);
        
        p1EnergyLabel = new Label("", skin);
        p1EnergyLabel.setColor(1f, 1f, 0f, 1f);
        p1EnergyLabel.setFontScale(0.95f);
        p1EnergyBar = new ProgressBar(0f, player1.getMaxEnergy(), 1f, false, skin);

        p2HpLabel = new Label("", skin);
        p2HpLabel.setColor(1f, 0.3f, 0.3f, 1f);
        p2HpLabel.setFontScale(1.3f);
        p2HpBar = new ProgressBar(0f, player2.getMaxHp(), 1f, false, skin);
        
        p2EnergyLabel = new Label("", skin);
        p2EnergyLabel.setColor(1f, 1f, 0f, 1f);
        p2EnergyLabel.setFontScale(0.95f);
        p2EnergyBar = new ProgressBar(0f, player2.getMaxEnergy(), 1f, false, skin);

        applyColoredBars();

        Table topHudTable = new Table();
        Table p1HudTable = new Table();
        p1HudTable.defaults().left();
        p1HudTable.add(p1HpLabel).expandX().padBottom(4f).row();
        p1HudTable.add(p1HpBar).width(420f).height(18f).padBottom(10f).row();
        p1HudTable.add(p1EnergyLabel).expandX().padBottom(4f).row();
        p1HudTable.add(p1EnergyBar).width(420f).height(14f);

        Table p2HudTable = new Table();
        p2HudTable.defaults().right();
        p2HudTable.add(p2HpLabel).expandX().padBottom(4f).row();
        p2HudTable.add(p2HpBar).width(420f).height(18f).padBottom(10f).row();
        if (!isStoryMode) {
            p2HudTable.add(p2EnergyLabel).expandX().padBottom(4f).row();
            p2HudTable.add(p2EnergyBar).width(420f).height(14f);
        }

        topHudTable.add(p1HudTable).expandX().left().top();
        topHudTable.add(p2HudTable).expandX().right().top();

        rootTable.add(topHudTable).expandX().fillX().top().row();

        energyWarningLabel = new Label("", skin);
        energyWarningLabel.setFontScale(1.2f);
        rootTable.add(energyWarningLabel).expand().center().top().padTop(10f);

        refreshAll(player1, player2);
    }

    private void applyColoredBars() {
        com.badlogic.gdx.graphics.Pixmap greenPixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        greenPixmap.setColor(0.2f, 0.9f, 0.2f, 1f);
        greenPixmap.fill();
        Texture greenBarTexture = new Texture(greenPixmap);
        greenPixmap.dispose();

        com.badlogic.gdx.graphics.Pixmap yellowPixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        yellowPixmap.setColor(0.9f, 0.9f, 0.2f, 1f);
        yellowPixmap.fill();
        Texture yellowBarTexture = new Texture(yellowPixmap);
        yellowPixmap.dispose();

        ProgressBar.ProgressBarStyle p1HpStyle = new ProgressBar.ProgressBarStyle(p1HpBar.getStyle());
        p1HpStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(greenBarTexture));
        p1HpBar.setStyle(p1HpStyle);

        ProgressBar.ProgressBarStyle p1EStyle = new ProgressBar.ProgressBarStyle(p1EnergyBar.getStyle());
        p1EStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(yellowBarTexture));
        p1EnergyBar.setStyle(p1EStyle);

        ProgressBar.ProgressBarStyle p2HpStyle = new ProgressBar.ProgressBarStyle(p2HpBar.getStyle());
        p2HpStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(greenBarTexture));
        p2HpBar.setStyle(p2HpStyle);
        
        ProgressBar.ProgressBarStyle p2EStyle = new ProgressBar.ProgressBarStyle(p2EnergyBar.getStyle());
        p2EStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(yellowBarTexture));
        p2EnergyBar.setStyle(p2EStyle);
    }

    public void update(float delta) {
        if (energyWarningTimer > 0f) {
            energyWarningTimer -= delta;
            if (energyWarningTimer <= 0f) {
                energyWarningLabel.setText("");
            }
        }
    }

    public void refreshAll(Combatant player1, Combatant player2) {
        refreshPlayer(player1, true);
        refreshPlayer(player2, false);
    }

    public void refreshPlayer(Combatant combatant, boolean isPlayer1) {
        if (isPlayer1) {
            p1HpLabel.setText((isStoryMode ? "" : "P1: ") + combatant.getName() + " HP: " + combatant.getHp() + " / " + combatant.getMaxHp());
            p1HpBar.setValue(combatant.getHp());
            p1EnergyLabel.setText("Energy:");
            p1EnergyBar.setValue(combatant.getEnergy());
        } else {
            p2HpLabel.setText((isStoryMode ? "" : "P2: ") + combatant.getName() + " HP: " + combatant.getHp() + " / " + combatant.getMaxHp());
            p2HpBar.setValue(combatant.getHp());
            if (!isStoryMode) {
                p2EnergyLabel.setText("Energy:");
                p2EnergyBar.setValue(combatant.getEnergy());
            }
        }
    }

    public void setCombatLog(String text) {
        // Combat log is disabled
    }

    public void showEnergyWarning() {
        energyWarningTimer = 1.4f;
        if (energyWarningLabel != null) {
            energyWarningLabel.setText("ENERGI TIDAK CUKUP");
            energyWarningLabel.setColor(1f, 0.25f, 0.25f, 1f);
        }
    }
}
