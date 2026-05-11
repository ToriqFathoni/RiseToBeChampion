package com.champion.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "characters")
public class GameCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long charId;

    @Column(nullable = false)
    private String name;

    private int baseHp;

    @Column(nullable = false)
    private String type; // "PLAYER" atau "ENEMY"

    // --- Variasi Serangan ---
    private String lightAttackName;
    private int lightAttackDmg;

    private String heavyAttackName;
    private int heavyAttackDmg;

    private String specialAttackName;
    private int specialAttackDmg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCharId() {
        return charId;
    }

    public void setCharId(Long charId) {
        this.charId = charId;
    }

    public int getBaseHp() {
        return baseHp;
    }

    public void setBaseHp(int baseHp) {
        this.baseHp = baseHp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLightAttackName() {
        return lightAttackName;
    }

    public void setLightAttackName(String lightAttackName) {
        this.lightAttackName = lightAttackName;
    }

    public int getLightAttackDmg() {
        return lightAttackDmg;
    }

    public void setLightAttackDmg(int lightAttackDmg) {
        this.lightAttackDmg = lightAttackDmg;
    }

    public String getHeavyAttackName() {
        return heavyAttackName;
    }

    public void setHeavyAttackName(String heavyAttackName) {
        this.heavyAttackName = heavyAttackName;
    }

    public int getHeavyAttackDmg() {
        return heavyAttackDmg;
    }

    public void setHeavyAttackDmg(int heavyAttackDmg) {
        this.heavyAttackDmg = heavyAttackDmg;
    }

    public String getSpecialAttackName() {
        return specialAttackName;
    }

    public void setSpecialAttackName(String specialAttackName) {
        this.specialAttackName = specialAttackName;
    }

    public int getSpecialAttackDmg() {
        return specialAttackDmg;
    }

    public void setSpecialAttackDmg(int specialAttackDmg) {
        this.specialAttackDmg = specialAttackDmg;
    }
// PENTING: Karena kita mengubah variabel, hapus Getter/Setter yang lama di bagian bawah class ini,
    // lalu tekan Alt+Insert untuk men-generate ulang Getter dan Setter yang baru!
}