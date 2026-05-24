package com.champion.backend.dto;

import java.util.UUID;

public class GameProgressResponse {
    private UUID runId;
    private int currentStage;
    private String status;
    private int bonusMaxHp;
    private int bonusBasicDmg;
    private int bonusSkillDmg;
    private int bonusMaxEnergy;
    private boolean hasMidbossSkill;
    private int deathCount;
    private int timeElapsed;

    public GameProgressResponse(UUID runId, int currentStage, String status, int bonusMaxHp, int bonusBasicDmg, int bonusSkillDmg, int bonusMaxEnergy, boolean hasMidbossSkill, int deathCount, int timeElapsed) {
        this.runId = runId;
        this.currentStage = currentStage;
        this.status = status;
        this.bonusMaxHp = bonusMaxHp;
        this.bonusBasicDmg = bonusBasicDmg;
        this.bonusSkillDmg = bonusSkillDmg;
        this.bonusMaxEnergy = bonusMaxEnergy;
        this.hasMidbossSkill = hasMidbossSkill;
        this.deathCount = deathCount;
        this.timeElapsed = timeElapsed;
    }

    public UUID getRunId() {
        return runId;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public String getStatus() {
        return status;
    }

    public int getBonusMaxHp() {
        return bonusMaxHp;
    }

    public int getBonusBasicDmg() {
        return bonusBasicDmg;
    }

    public int getBonusSkillDmg() {
        return bonusSkillDmg;
    }

    public int getBonusMaxEnergy() {
        return bonusMaxEnergy;
    }

    public boolean isHasMidbossSkill() {
        return hasMidbossSkill;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }
}