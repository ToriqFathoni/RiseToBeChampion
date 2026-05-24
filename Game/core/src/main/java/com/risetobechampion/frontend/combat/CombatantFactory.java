package com.risetobechampion.frontend.combat;

public final class CombatantFactory {
    private CombatantFactory() {
    }

    /**
     * Create Kael (Player character) with dynamic stats from backend.
     * @param hp Maximum HP for Kael
     * @param maxEnergy Maximum energy for Kael
     * @param x X position
     * @param y Y position
     * @return A configured Combatant instance for Kael
     */
    public static Combatant createKael(int hp, int maxEnergy, float x, float y) {
        Combatant player = new Combatant("Kael The Phantom", hp, maxEnergy, x, y, true);
        player.setRenderSize(260f, 340f);
        player.setRenderOffsetY(-20f);
        register(player,
            AnimationSpec.of(EntityState.IDLE, "characters/Kael/idle.png", 100, 4, 0.15f, true, 3.0f),
            AnimationSpec.of(EntityState.WALK, "characters/Kael/walk.png", 100, 9, 0.1f, true, 3.0f),
            AnimationSpec.of(EntityState.JUMP, "characters/Kael/jump.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEND, "characters/Kael/defence.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_BASIC, "characters/Kael/attack1.png", 100, 6, 0.08f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_HEAVY, "characters/Kael/attack2.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.SKILL, "characters/Kael/attack3.png", 100, 6, 0.08f, false, 3.0f),
            AnimationSpec.of(EntityState.HIT, "characters/Kael/hit_by_punch.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEATED, "characters/Kael/dead.png", 100, 7, 0.15f, false, 3.0f),
            AnimationSpec.of(EntityState.TAUNT, "characters/Kael/taunt.png", 100, 3, 0.1f, false, 3.0f)
        );
        // Tune hit frames and ranges for Kael
        player.setAttackHitFrame(EntityState.ATTACK_BASIC, 2);
        player.setAttackHitFrame(EntityState.ATTACK_HEAVY, 3);
        player.setAttackHitFrame(EntityState.SKILL, 4);
        player.setAttackRange(EntityState.ATTACK_BASIC, 200f);
        player.setAttackRange(EntityState.ATTACK_HEAVY, 220f);
        player.setAttackRange(EntityState.SKILL, 260f);
        return player;
    }

    /**
     * Create Mr. Van (Enemy character) with dynamic stats from backend.
     * @param hp Maximum HP for Mr. Van
     * @param basicDamage Damage for basic attack
     * @param heavyDamage Damage for heavy attack
     * @param x X position
     * @param y Y position
     * @return A configured Combatant instance for Mr. Van
     */
    public static Combatant createMrVan(int hp, int basicDamage, int heavyDamage, int skillDamage, float x, float y) {
        Combatant enemy = new Combatant("Mr. Van", hp, x, y, false);
        enemy.setRenderSize(270f, 350f);
        enemy.setRenderOffsetY(-20f);
        register(enemy,
            AnimationSpec.of(EntityState.IDLE, "characters/Mr.Van/idle.png", 100, 4, 0.15f, true, 3.0f),
            AnimationSpec.of(EntityState.WALK, "characters/Mr.Van/walk.png", 100, 5, 0.1f, true, 3.0f),
            AnimationSpec.of(EntityState.JUMP, "characters/Mr.Van/jump.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEND, "characters/Mr.Van/defence.png", 100, 2, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_BASIC, "characters/Mr.Van/attack1.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_HEAVY, "characters/Mr.Van/attack2.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.SKILL, "characters/Mr.Van/attack3.png", 100, 8, 0.08f, false, 3.0f),
            AnimationSpec.of(EntityState.HIT, "characters/Mr.Van/hit_by_enemy.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEATED, "characters/Mr.Van/dead.png", 100, 8, 0.15f, false, 3.0f),
            AnimationSpec.of(EntityState.TAUNT, "characters/Mr.Van/taunt.png", 100, 3, 0.1f, false, 3.0f)
        );
        // Tune hit frames and ranges for Mr. Van
        enemy.setAttackHitFrame(EntityState.ATTACK_BASIC, 1);
        enemy.setAttackHitFrame(EntityState.ATTACK_HEAVY, 2);
        enemy.setAttackHitFrame(EntityState.SKILL, 4);
        enemy.setAttackRange(EntityState.ATTACK_BASIC, 200f);
        enemy.setAttackRange(EntityState.ATTACK_HEAVY, 240f);
        enemy.setAttackRange(EntityState.SKILL, 280f);
        enemy.setAi(new AggressiveAi(basicDamage, heavyDamage, skillDamage));
        return enemy;
    }

    /**
     * Legacy method for backward compatibility. Creates Kael with default stats (90 HP, 100 Energy).
     */
    public static Combatant createKael(float x, float y) {
        return createKael(90, 100, x, y);
    }

    /**
     * Create Ryu (Player character) with dynamic stats from backend.
     * @param hp Maximum HP for Ryu
     * @param maxEnergy Maximum energy for Ryu
     * @param x X position
     * @param y Y position
     * @return A configured Combatant instance for Ryu
     */
    public static Combatant createRyu(int hp, int maxEnergy, float x, float y) {
        Combatant player = new Combatant("Ryu", hp, maxEnergy, x, y, true);
        player.setRenderSize(260f, 340f);
        player.setRenderOffsetY(-20f);
        register(player,
            AnimationSpec.of(EntityState.IDLE, "characters/ryu/idle.png", 100, 4, 0.15f, true, 3.0f),
            AnimationSpec.of(EntityState.WALK, "characters/ryu/walk.png", 100, 9, 0.1f, true, 3.0f),
            AnimationSpec.of(EntityState.JUMP, "characters/ryu/jump.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEND, "characters/ryu/defence.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_BASIC, "characters/ryu/attack1.png", 100, 6, 0.08f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_HEAVY, "characters/ryu/attack2.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.SKILL, "characters/ryu/attack3.png", 100, 6, 0.08f, false, 3.0f),
            AnimationSpec.of(EntityState.HIT, "characters/ryu/hit.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEATED, "characters/ryu/dead.png", 100, 7, 0.15f, false, 3.0f),
            AnimationSpec.of(EntityState.TAUNT, "characters/ryu/tunt.png", 100, 3, 0.1f, false, 3.0f)
        );
        // Tune hit frames and ranges for Ryu
        player.setAttackHitFrame(EntityState.ATTACK_BASIC, 2);
        player.setAttackHitFrame(EntityState.ATTACK_HEAVY, 3);
        player.setAttackHitFrame(EntityState.SKILL, 4);
        player.setAttackRange(EntityState.ATTACK_BASIC, 200f);
        player.setAttackRange(EntityState.ATTACK_HEAVY, 220f);
        player.setAttackRange(EntityState.SKILL, 260f);
        return player;
    }

    /**
     * Legacy method for backward compatibility. Creates Ryu with default stats (90 HP, 100 Energy).
     */
    public static Combatant createRyu(float x, float y) {
        return createRyu(90, 100, x, y);
    }

    /**
     * Legacy method for backward compatibility. Creates Mr. Van with default stats (120 HP, 25/35 Damage).
     */
    public static Combatant createMrVan(float x, float y) {
        return createMrVan(100, 15, 30, 50, x, y);
    }

    /**
     * Create Chen Long (Stage 2 Enemy character) with dynamic stats from backend.
     * @param hp Maximum HP for Chen Long
     * @param basicDamage Damage for basic attack
     * @param heavyDamage Damage for heavy attack
     * @param skillDamage Damage for skill attack
     * @param x X position
     * @param y Y position
     * @return A configured Combatant instance for Chen Long
     */
    public static Combatant createChenLong(int hp, int basicDamage, int heavyDamage, int skillDamage, float x, float y) {
        Combatant enemy = new Combatant("Chen Long", hp, x, y, false);
        enemy.setRenderSize(270f, 350f);
        enemy.setRenderOffsetY(-20f);
        register(enemy,
            AnimationSpec.of(EntityState.IDLE, "characters/chen long/idle.png", 100, 5, 0.15f, true, 3.0f),
            AnimationSpec.of(EntityState.WALK, "characters/chen long/walk.png", 100, 12, 0.1f, true, 3.0f),
            AnimationSpec.of(EntityState.JUMP, "characters/chen long/jump.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEND, "characters/chen long/defence.png", 100, 2, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_BASIC, "characters/chen long/attack1.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_HEAVY, "characters/chen long/attack2.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.SKILL, "characters/chen long/attack3.png", 100, 9, 0.08f, false, 3.0f),
            AnimationSpec.of(EntityState.HIT, "characters/chen long/hit.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEATED, "characters/chen long/dead.png", 100, 11, 0.15f, false, 3.0f),
            AnimationSpec.of(EntityState.TAUNT, "characters/chen long/taunt.png", 100, 5, 0.1f, false, 3.0f)
        );
        // Tune hit frames and ranges for Chen Long
        enemy.setAttackHitFrame(EntityState.ATTACK_BASIC, 2);
        enemy.setAttackHitFrame(EntityState.ATTACK_HEAVY, 2);
        enemy.setAttackHitFrame(EntityState.SKILL, 4);
        enemy.setAttackRange(EntityState.ATTACK_BASIC, 200f);
        enemy.setAttackRange(EntityState.ATTACK_HEAVY, 240f);
        enemy.setAttackRange(EntityState.SKILL, 280f);
        enemy.setAi(new AggressiveAi(basicDamage, heavyDamage, skillDamage));
        return enemy;
    }

    /**
     * Legacy method for backward compatibility. Creates Chen Long with default stats.
     */
    public static Combatant createChenLong(float x, float y) {
        return createChenLong(150, 20, 40, 65, x, y);
    }

    /**
     * Create Kagetsu (Stage 3 Enemy character) with dynamic stats from backend.
     * @param hp Maximum HP for Kagetsu
     * @param basicDamage Damage for basic attack
     * @param heavyDamage Damage for heavy attack
     * @param skillDamage Damage for skill attack
     * @param x X position
     * @param y Y position
     * @return A configured Combatant instance for Kagetsu
     */
    public static Combatant createKagetsu(int hp, int basicDamage, int heavyDamage, int skillDamage, float x, float y) {
        Combatant enemy = new Combatant("Kagetsu", hp, x, y, false);
        enemy.setRenderSize(270f, 350f);
        enemy.setRenderOffsetY(-20f);
        register(enemy,
            AnimationSpec.of(EntityState.IDLE, "characters/kagetsu/idle.png", 100, 5, 0.15f, true, 3.0f),
            AnimationSpec.of(EntityState.WALK, "characters/kagetsu/walk.png", 100, 9, 0.1f, true, 3.0f),
            AnimationSpec.of(EntityState.JUMP, "characters/kagetsu/jump.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEND, "characters/kagetsu/defence.png", 100, 2, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_BASIC, "characters/kagetsu/attack1.png", 100, 6, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_HEAVY, "characters/kagetsu/attack2.png", 100, 6, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.SKILL, "characters/kagetsu/attack3.png", 100, 10, 0.08f, false, 3.0f),
            AnimationSpec.of(EntityState.HIT, "characters/kagetsu/hit.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEATED, "characters/kagetsu/dead.png", 100, 6, 0.15f, false, 3.0f),
            AnimationSpec.of(EntityState.TAUNT, "characters/kagetsu/taunt.png", 100, 3, 0.1f, false, 3.0f)
        );
        enemy.setAttackHitFrame(EntityState.ATTACK_BASIC, 2);
        enemy.setAttackHitFrame(EntityState.ATTACK_HEAVY, 2);
        enemy.setAttackHitFrame(EntityState.SKILL, 4);
        enemy.setAttackRange(EntityState.ATTACK_BASIC, 210f);
        enemy.setAttackRange(EntityState.ATTACK_HEAVY, 245f);
        enemy.setAttackRange(EntityState.SKILL, 290f);
        enemy.setAi(new AggressiveAi(basicDamage, heavyDamage, skillDamage));
        return enemy;
    }

    /**
     * Legacy method for backward compatibility. Creates Kagetsu with default stats.
     */
    public static Combatant createKagetsu(float x, float y) {
        return createKagetsu(220, 28, 55, 90, x, y);
    }

    /**
     * Create Joe (Stage 4 Final Boss) with dynamic stats from backend.
     * Joe is the strongest boss with 4 different attacks.
     * @param hp Maximum HP for Joe
     * @param basicDamage Damage for basic attack (attack1)
     * @param heavyDamage Damage for heavy attack (attack2)
     * @param skillDamage Damage for skill attack (attack3 and attack4)
     * @param x X position
     * @param y Y position
     * @return A configured Combatant instance for Joe
     */
    public static Combatant createJoe(int hp, int basicDamage, int heavyDamage, int skillDamage, int ultimateDamage, float x, float y) {
        Combatant enemy = new Combatant("Joe", hp, x, y, false);
        enemy.setRenderSize(280f, 360f);
        enemy.setRenderOffsetY(-20f);
        register(enemy,
            AnimationSpec.of(EntityState.IDLE, "characters/Joe/idle.png", 100, 5, 0.15f, true, 3.0f),
            AnimationSpec.of(EntityState.WALK, "characters/Joe/walk.png", 100, 5, 0.1f, true, 3.0f),
            AnimationSpec.of(EntityState.JUMP, "characters/Joe/jump.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEND, "characters/Joe/defence.png", 100, 2, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_BASIC, "characters/Joe/attack1.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_HEAVY, "characters/Joe/attack2.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.SKILL, "characters/Joe/attack3.png", 100, 7, 0.08f, false, 3.0f),
            AnimationSpec.of(EntityState.ULTIMATE, "characters/Joe/attack4.png", 100, 7, 0.08f, false, 3.0f),
            AnimationSpec.of(EntityState.HIT, "characters/Joe/hit.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEATED, "characters/Joe/dead.png", 100, 7, 0.15f, false, 3.0f),
            AnimationSpec.of(EntityState.TAUNT, "characters/Joe/taunt.png", 100, 3, 0.1f, false, 3.0f)
        );
        enemy.setAttackHitFrame(EntityState.ATTACK_BASIC, 2);
        enemy.setAttackHitFrame(EntityState.ATTACK_HEAVY, 2);
        enemy.setAttackHitFrame(EntityState.SKILL, 4);
        enemy.setAttackHitFrame(EntityState.ULTIMATE, 4);
        enemy.setAttackRange(EntityState.ATTACK_BASIC, 220f);
        enemy.setAttackRange(EntityState.ATTACK_HEAVY, 250f);
        enemy.setAttackRange(EntityState.SKILL, 300f);
        enemy.setAttackRange(EntityState.ULTIMATE, 350f);
        enemy.setAi(new AggressiveAi(basicDamage, heavyDamage, skillDamage));
        return enemy;
    }

    /**
     * Legacy method for backward compatibility. Creates Joe with default stats (final boss).
     */
    public static Combatant createJoe(float x, float y) {
        return createJoe(300, 35, 70, 120, 200, x, y);
    }

    private static void register(Combatant combatant, AnimationSpec... specs) {
        for (AnimationSpec spec : specs) {
            combatant.loadAnimation(spec.getState(), spec.getPath(), spec.getFrameWidth(), spec.getColumns(), spec.getFrameDuration(), spec.isLoop(), true, spec.getScale());
        }
    }
}