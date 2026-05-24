package com.risetobechampion.frontend.game;

import com.risetobechampion.frontend.combat.Combatant;

public class GameState {
    private static final GameState INSTANCE = new GameState();

    private Combatant player;
    private Combatant enemy;
    private int currentStage = 1;

    private GameState() {}

    public static GameState getInstance() {
        return INSTANCE;
    }

    public Combatant getPlayer() { return player; }
    public void setPlayer(Combatant p) { this.player = p; }

    public Combatant getEnemy() { return enemy; }
    public void setEnemy(Combatant e) { this.enemy = e; }

    public int getCurrentStage() { return currentStage; }
    public void setCurrentStage(int s) { this.currentStage = s; }
}
