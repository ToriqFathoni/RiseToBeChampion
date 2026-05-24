package com.risetobechampion.frontend.utils; // Sesuaikan jika berbeda

public final class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private String userId;
    private String runId;
    private int currentStage = 1;
    private int playerHpBonus;
    private int playerAttack1Bonus;
    private int playerAttack2Bonus;
    private int playerAttack3Bonus;
    private String selectedCharacterName;
    private String player2CharacterName;
    private boolean isLocalMultiplayer;
    private int deathCount;
    private int totalTimeElapsed;

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }
    public int getCurrentStage() {
        return currentStage;
    }
    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public int getPlayerHpBonus() {
        return playerHpBonus;
    }

    public void setPlayerHpBonus(int playerHpBonus) {
        this.playerHpBonus = Math.max(0, playerHpBonus);
    }

    public int getPlayerAttack1Bonus() {
        return playerAttack1Bonus;
    }

    public void setPlayerAttack1Bonus(int playerAttack1Bonus) {
        this.playerAttack1Bonus = Math.max(0, playerAttack1Bonus);
    }

    public int getPlayerAttack2Bonus() {
        return playerAttack2Bonus;
    }

    public void setPlayerAttack2Bonus(int playerAttack2Bonus) {
        this.playerAttack2Bonus = Math.max(0, playerAttack2Bonus);
    }

    public int getPlayerAttack3Bonus() {
        return playerAttack3Bonus;
    }

    public void setPlayerAttack3Bonus(int playerAttack3Bonus) {
        this.playerAttack3Bonus = Math.max(0, playerAttack3Bonus);
    }

    public String getSelectedCharacterName() {
        return selectedCharacterName;
    }

    public void setSelectedCharacterName(String selectedCharacterName) {
        this.selectedCharacterName = selectedCharacterName;
    }

    public String getPlayer2CharacterName() {
        return player2CharacterName;
    }

    public void setPlayer2CharacterName(String player2CharacterName) {
        this.player2CharacterName = player2CharacterName;
    }

    public boolean isLocalMultiplayer() {
        return isLocalMultiplayer;
    }

    public void setLocalMultiplayer(boolean isLocalMultiplayer) {
        this.isLocalMultiplayer = isLocalMultiplayer;
    }

    public void resetRunProgress() {
        runId = null;
        currentStage = 1;
        playerHpBonus = 0;
        playerAttack1Bonus = 0;
        playerAttack2Bonus = 0;
        playerAttack3Bonus = 0;
        totalTimeElapsed = 0;
    }

    public void reset() {
        userId = null;
        selectedCharacterName = null;
        player2CharacterName = null;
        isLocalMultiplayer = false;
        resetRunProgress();
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public int getTotalTimeElapsed() {
        return totalTimeElapsed;
    }

    public void setTotalTimeElapsed(int totalTimeElapsed) {
        this.totalTimeElapsed = totalTimeElapsed;
    }
}
