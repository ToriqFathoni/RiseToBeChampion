package com.champion.backend.dto;

import java.util.UUID;

public class SaveProgressRequest {
    private UUID runId;
    private int currentStage;
    private int deathCount;
    private int timeElapsed;
    private String status; // "ONGOING", "COMPLETED", atau "GAME_OVER"

    // --- Data Buff Stat ---
    private int bonusMaxHp;
    private int bonusBasicDmg;
    private int bonusSkillDmg;
    private int bonusMaxEnergy;
    private boolean hasMidbossSkill;

    public UUID getRunId() {
        return runId;
    }

    public void setRunId(UUID runId) {
        this.runId = runId;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBonusMaxHp() {
        return bonusMaxHp;
    }

    public void setBonusMaxHp(int bonusMaxHp) {
        this.bonusMaxHp = bonusMaxHp;
    }

    public int getBonusBasicDmg() {
        return bonusBasicDmg;
    }

    public void setBonusBasicDmg(int bonusBasicDmg) {
        this.bonusBasicDmg = bonusBasicDmg;
    }

    public int getBonusSkillDmg() {
        return bonusSkillDmg;
    }

    public void setBonusSkillDmg(int bonusSkillDmg) {
        this.bonusSkillDmg = bonusSkillDmg;
    }

    public int getBonusMaxEnergy() {
        return bonusMaxEnergy;
    }

    public void setBonusMaxEnergy(int bonusMaxEnergy) {
        this.bonusMaxEnergy = bonusMaxEnergy;
    }

    public boolean isHasMidbossSkill() {
        return hasMidbossSkill;
    }

    public void setHasMidbossSkill(boolean hasMidbossSkill) {
        this.hasMidbossSkill = hasMidbossSkill;
    }

    // SANGAT PENTING: Tekan Alt+Insert untuk men-generate
    // Getter dan Setter untuk SEMUA variabel di atas!
}