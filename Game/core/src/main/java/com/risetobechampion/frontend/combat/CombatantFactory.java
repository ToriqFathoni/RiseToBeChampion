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
        player.setRenderOffsetY(0f);
        register(player,
            AnimationSpec.of(EntityState.IDLE, "characters/Kael/idle.png", 100, 4, 0.15f, true, 3.0f),
            AnimationSpec.of(EntityState.WALK, "characters/Kael/walk.png", 100, 9, 0.1f, true, 3.0f),
            AnimationSpec.of(EntityState.JUMP, "characters/Kael/jump.png", 100, 5, 0.1f, false, 3.1f),
            AnimationSpec.of(EntityState.ATTACK_BASIC, "characters/Kael/attack1.png", 100, 6, 0.08f, false, 3.15f),
            AnimationSpec.of(EntityState.ATTACK_HEAVY, "characters/Kael/attack2.png", 100, 4, 0.1f, false, 3.15f),
            AnimationSpec.of(EntityState.SKILL, "characters/Kael/attack3.png", 100, 6, 0.08f, false, 3.2f),
            AnimationSpec.of(EntityState.HIT, "characters/Kael/hit_by_punch.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEATED, "characters/Kael/dead.png", 100, 7, 0.15f, false, 3.0f),
            AnimationSpec.of(EntityState.TAUNT, "characters/Kael/taunt.png", 100, 3, 0.2f, false, 3.0f)
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
    public static Combatant createMrVan(int hp, int basicDamage, int heavyDamage, float x, float y) {
        Combatant enemy = new Combatant("Mr. Van", hp, x, y, false);
        enemy.setRenderSize(270f, 350f);
        enemy.setRenderOffsetY(0f);
        register(enemy,
            AnimationSpec.of(EntityState.IDLE, "characters/Mr.Van/idle.png", 100, 4, 0.15f, true, 3.0f),
            AnimationSpec.of(EntityState.WALK, "characters/Mr.Van/walk.png", 100, 5, 0.1f, true, 3.0f),
            AnimationSpec.of(EntityState.JUMP, "characters/Mr.Van/jump.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.ATTACK_BASIC, "characters/Mr.Van/attack1.png", 100, 3, 0.1f, false, 3.2f),
            AnimationSpec.of(EntityState.ATTACK_HEAVY, "characters/Mr.Van/attack2.png", 100, 4, 0.1f, false, 3.15f),
            AnimationSpec.of(EntityState.SKILL, "characters/Mr.Van/attack3.png", 100, 8, 0.08f, false, 3.2f),
            AnimationSpec.of(EntityState.HIT, "characters/Mr.Van/hit_by_enemy.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of(EntityState.DEFEATED, "characters/Mr.Van/dead.png", 100, 8, 0.15f, false, 3.0f),
            AnimationSpec.of(EntityState.TAUNT, "characters/Mr.Van/taunt.png", 100, 3, 0.2f, false, 3.0f)
        );
        // Tune hit frames and ranges for Mr. Van
        enemy.setAttackHitFrame(EntityState.ATTACK_BASIC, 1);
        enemy.setAttackHitFrame(EntityState.ATTACK_HEAVY, 2);
        enemy.setAttackHitFrame(EntityState.SKILL, 4);
        enemy.setAttackRange(EntityState.ATTACK_BASIC, 200f);
        enemy.setAttackRange(EntityState.ATTACK_HEAVY, 240f);
        enemy.setAttackRange(EntityState.SKILL, 280f);
        enemy.setAi(new AggressiveAi());
        return enemy;
    }

    /**
     * Legacy method for backward compatibility. Creates Kael with default stats (90 HP, 100 Energy).
     */
    public static Combatant createKael(float x, float y) {
        return createKael(90, 100, x, y);
    }

    /**
     * Legacy method for backward compatibility. Creates Mr. Van with default stats (120 HP, 25/35 Damage).
     */
    public static Combatant createMrVan(float x, float y) {
        return createMrVan(120, 25, 35, x, y);
    }

    private static void register(Combatant combatant, AnimationSpec... specs) {
        for (AnimationSpec spec : specs) {
            combatant.loadAnimation(spec.getState(), spec.getPath(), spec.getFrameWidth(), spec.getColumns(), spec.getFrameDuration(), spec.isLoop(), true, spec.getScale());
        }
    }

    private static void tuneCombatant(Combatant c) {
        // sensible defaults; can be tweaked per-character below
        c.setAttackRange(EntityState.ATTACK_BASIC, 200f);
        c.setAttackRange(EntityState.ATTACK_HEAVY, 220f);
        c.setAttackRange(EntityState.SKILL, 260f);
    }
}