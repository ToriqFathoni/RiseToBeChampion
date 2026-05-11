package com.champion.backend.dto;

public class CombatSetupResponse {
    private PlayerCombatData player;
    private EnemyCombatData enemy;

    public CombatSetupResponse() {
    }

    public CombatSetupResponse(PlayerCombatData player, EnemyCombatData enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public PlayerCombatData getPlayer() {
        return player;
    }

    public void setPlayer(PlayerCombatData player) {
        this.player = player;
    }

    public EnemyCombatData getEnemy() {
        return enemy;
    }

    public void setEnemy(EnemyCombatData enemy) {
        this.enemy = enemy;
    }

    // Inner classes for Player and Enemy combat data
    public static class PlayerCombatData {
        private Long id;
        private String name;
        private int maxHp;
        private int maxEnergy;
        private int baseDamage;

        public PlayerCombatData() {
        }

        public PlayerCombatData(Long id, String name, int maxHp, int maxEnergy, int baseDamage) {
            this.id = id;
            this.name = name;
            this.maxHp = maxHp;
            this.maxEnergy = maxEnergy;
            this.baseDamage = baseDamage;
        }

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

        public int getMaxHp() {
            return maxHp;
        }

        public void setMaxHp(int maxHp) {
            this.maxHp = maxHp;
        }

        public int getMaxEnergy() {
            return maxEnergy;
        }

        public void setMaxEnergy(int maxEnergy) {
            this.maxEnergy = maxEnergy;
        }

        public int getBaseDamage() {
            return baseDamage;
        }

        public void setBaseDamage(int baseDamage) {
            this.baseDamage = baseDamage;
        }
    }

    public static class EnemyCombatData {
        private Long id;
        private String name;
        private int stageId;
        private int maxHp;
        private int basicAttackDamage;
        private int heavyAttackDamage;
        private int skillDamage;

        public EnemyCombatData() {
        }

        public EnemyCombatData(Long id, String name, int stageId, int maxHp, int basicAttackDamage, int heavyAttackDamage, int skillDamage) {
            this.id = id;
            this.name = name;
            this.stageId = stageId;
            this.maxHp = maxHp;
            this.basicAttackDamage = basicAttackDamage;
            this.heavyAttackDamage = heavyAttackDamage;
            this.skillDamage = skillDamage;
        }

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
}
