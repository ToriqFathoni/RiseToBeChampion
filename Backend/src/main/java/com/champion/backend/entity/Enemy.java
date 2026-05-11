package com.champion.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "enemies")
public class Enemy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int stageId;

    @Column(nullable = false)
    private int maxHp;

    @Column(nullable = false)
    private int basicAttackDamage;

    @Column(nullable = false)
    private int heavyAttackDamage;
    
    @Column(nullable = false)
    private int skillDamage;

    // Constructors
    public Enemy() {
    }

    public Enemy(String name, int stageId, int maxHp, int basicAttackDamage, int heavyAttackDamage, int skillDamage) {
        this.name = name;
        this.stageId = stageId;
        this.maxHp = maxHp;
        this.basicAttackDamage = basicAttackDamage;
        this.heavyAttackDamage = heavyAttackDamage;
        this.skillDamage = skillDamage;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getBasicAttackDamage() {
        return basicAttackDamage;
    }

    public void setBasicAttackDamage(int basicAttackDamage) {
        this.basicAttackDamage = basicAttackDamage;
    }

    public int getHeavyAttackDamage() {
        return heavyAttackDamage;
    }

    public void setHeavyAttackDamage(int heavyAttackDamage) {
        this.heavyAttackDamage = heavyAttackDamage;
    }

    public int getSkillDamage() {
        return skillDamage;
    }

    public void setSkillDamage(int skillDamage) {
        this.skillDamage = skillDamage;
    }
}
