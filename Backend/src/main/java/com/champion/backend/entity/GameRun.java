package com.champion.backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "game_runs")
public class GameRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID runId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "char_id", nullable = false)
    private GameCharacter character;

    private int currentStage = 1;
    private int deathCount = 0;
    private int timeElapsed = 0;
    private String status = "ONGOING"; // Pilihannya: ONGOING, COMPLETED, GAME_OVER

    @Column(nullable = true)
    private Integer finalRating;

    private int bonusMaxHp = 0;
    private int bonusBasicDmg = 0;
    private int bonusSkillDmg = 0;
    private int bonusMaxEnergy = 0;
    private boolean hasMidbossSkill = false;

    public UUID getRunId() {
        return runId;
    }

    public void setRunId(UUID runId) {
        this.runId = runId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public void setCharacter(GameCharacter character) {
        this.character = character;
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

    public Integer getFinalRating() {
        return finalRating;
    }

    public void setFinalRating(Integer finalRating) {
        this.finalRating = finalRating;
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

}