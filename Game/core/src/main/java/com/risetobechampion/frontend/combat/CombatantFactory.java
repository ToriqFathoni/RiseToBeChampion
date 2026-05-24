package com.risetobechampion.frontend.combat;

public final class CombatantFactory {
    private CombatantFactory() {
    }

    
    public static Combatant createKael(int hp, int maxEnergy, float x, float y) {
        Combatant player = new Combatant("Kael The Phantom", hp, maxEnergy, x, y, true);
        player.setRenderSize(260f, 340f);
        player.setRenderOffsetY(-20f);
        register(player,
            AnimationSpec.of("IDLE", "characters/Kael/idle.png", 100, 4, 0.15f, true, 3.0f),
            AnimationSpec.of("WALK", "characters/Kael/walk.png", 100, 9, 0.1f, true, 3.0f),
            AnimationSpec.of("JUMP", "characters/Kael/jump.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEND", "characters/Kael/defence.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of("ATTACK_BASIC", "characters/Kael/attack1.png", 100, 6, 0.08f, false, 3.0f),
            AnimationSpec.of("ATTACK_HEAVY", "characters/Kael/attack2.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of("SKILL", "characters/Kael/attack3.png", 100, 6, 0.08f, false, 3.0f),
            AnimationSpec.of("HIT", "characters/Kael/hit_by_punch.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEATED", "characters/Kael/dead.png", 100, 7, 0.15f, false, 3.0f),
            AnimationSpec.of("TAUNT", "characters/Kael/taunt.png", 100, 3, 0.1f, false, 3.0f)
        );

        player.setAttackHitFrame(ActionState.ATTACK_BASIC, 2);
        player.setAttackHitFrame(ActionState.ATTACK_HEAVY, 3);
        player.setAttackHitFrame(ActionState.SKILL, 4);
        player.setAttackRange(ActionState.ATTACK_BASIC, 200f);
        player.setAttackRange(ActionState.ATTACK_HEAVY, 220f);
        player.setAttackRange(ActionState.SKILL, 260f);
        return player;
    }

    
    public static Combatant createMrVan(int hp, int basicDamage, int heavyDamage, int skillDamage, float x, float y) {
        Combatant enemy = new Combatant("Mr. Van", hp, x, y, false);
        enemy.setRenderSize(270f, 350f);
        enemy.setRenderOffsetY(-20f);
        register(enemy,
            AnimationSpec.of("IDLE", "characters/Mr.Van/idle.png", 100, 4, 0.15f, true, 3.0f),
            AnimationSpec.of("WALK", "characters/Mr.Van/walk.png", 100, 5, 0.1f, true, 3.0f),
            AnimationSpec.of("JUMP", "characters/Mr.Van/jump.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEND", "characters/Mr.Van/defence.png", 100, 2, 0.1f, false, 3.0f),
            AnimationSpec.of("ATTACK_BASIC", "characters/Mr.Van/attack1.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of("ATTACK_HEAVY", "characters/Mr.Van/attack2.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of("SKILL", "characters/Mr.Van/attack3.png", 100, 8, 0.08f, false, 3.0f),
            AnimationSpec.of("HIT", "characters/Mr.Van/hit_by_enemy.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEATED", "characters/Mr.Van/dead.png", 100, 8, 0.15f, false, 3.0f),
            AnimationSpec.of("TAUNT", "characters/Mr.Van/taunt.png", 100, 3, 0.1f, false, 3.0f)
        );

        enemy.setAttackHitFrame(ActionState.ATTACK_BASIC, 1);
        enemy.setAttackHitFrame(ActionState.ATTACK_HEAVY, 2);
        enemy.setAttackHitFrame(ActionState.SKILL, 4);
        enemy.setAttackRange(ActionState.ATTACK_BASIC, 200f);
        enemy.setAttackRange(ActionState.ATTACK_HEAVY, 240f);
        enemy.setAttackRange(ActionState.SKILL, 280f);
        enemy.setAi(new AggressiveAi(basicDamage, heavyDamage, skillDamage));
        return enemy;
    }

    
    public static Combatant createKael(float x, float y) {
        return createKael(90, 100, x, y);
    }

    
    public static Combatant createRyu(int hp, int maxEnergy, float x, float y) {
        Combatant player = new Combatant("Ryu", hp, maxEnergy, x, y, true);
        player.setRenderSize(260f, 340f);
        player.setRenderOffsetY(-20f);
        register(player,
            AnimationSpec.of("IDLE", "characters/ryu/idle.png", 100, 4, 0.15f, true, 3.0f),
            AnimationSpec.of("WALK", "characters/ryu/walk.png", 100, 9, 0.1f, true, 3.0f),
            AnimationSpec.of("JUMP", "characters/ryu/jump.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEND", "characters/ryu/defence.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of("ATTACK_BASIC", "characters/ryu/attack1.png", 100, 6, 0.08f, false, 3.0f),
            AnimationSpec.of("ATTACK_HEAVY", "characters/ryu/attack2.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of("SKILL", "characters/ryu/attack3.png", 100, 6, 0.08f, false, 3.0f),
            AnimationSpec.of("HIT", "characters/ryu/hit.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEATED", "characters/ryu/dead.png", 100, 7, 0.15f, false, 3.0f),
            AnimationSpec.of("TAUNT", "characters/ryu/tunt.png", 100, 3, 0.1f, false, 3.0f)
        );

        player.setAttackHitFrame(ActionState.ATTACK_BASIC, 2);
        player.setAttackHitFrame(ActionState.ATTACK_HEAVY, 3);
        player.setAttackHitFrame(ActionState.SKILL, 4);
        player.setAttackRange(ActionState.ATTACK_BASIC, 200f);
        player.setAttackRange(ActionState.ATTACK_HEAVY, 220f);
        player.setAttackRange(ActionState.SKILL, 260f);
        return player;
    }

    
    public static Combatant createRyu(float x, float y) {
        return createRyu(90, 100, x, y);
    }

    
    public static Combatant createMrVan(float x, float y) {
        return createMrVan(100, 15, 30, 50, x, y);
    }

    
    public static Combatant createChenLong(int hp, int basicDamage, int heavyDamage, int skillDamage, float x, float y) {
        Combatant enemy = new Combatant("Chen Long", hp, x, y, false);
        enemy.setRenderSize(270f, 350f);
        enemy.setRenderOffsetY(-20f);
        register(enemy,
            AnimationSpec.of("IDLE", "characters/chen long/idle.png", 100, 5, 0.15f, true, 3.0f),
            AnimationSpec.of("WALK", "characters/chen long/walk.png", 100, 12, 0.1f, true, 3.0f),
            AnimationSpec.of("JUMP", "characters/chen long/jump.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEND", "characters/chen long/defence.png", 100, 2, 0.1f, false, 3.0f),
            AnimationSpec.of("ATTACK_BASIC", "characters/chen long/attack1.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of("ATTACK_HEAVY", "characters/chen long/attack2.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of("SKILL", "characters/chen long/attack3.png", 100, 9, 0.08f, false, 3.0f),
            AnimationSpec.of("HIT", "characters/chen long/hit.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEATED", "characters/chen long/dead.png", 100, 11, 0.15f, false, 3.0f),
            AnimationSpec.of("TAUNT", "characters/chen long/taunt.png", 100, 5, 0.1f, false, 3.0f)
        );

        enemy.setAttackHitFrame(ActionState.ATTACK_BASIC, 2);
        enemy.setAttackHitFrame(ActionState.ATTACK_HEAVY, 2);
        enemy.setAttackHitFrame(ActionState.SKILL, 4);
        enemy.setAttackRange(ActionState.ATTACK_BASIC, 200f);
        enemy.setAttackRange(ActionState.ATTACK_HEAVY, 240f);
        enemy.setAttackRange(ActionState.SKILL, 280f);
        enemy.setAi(new AggressiveAi(basicDamage, heavyDamage, skillDamage));
        return enemy;
    }

    
    public static Combatant createChenLong(float x, float y) {
        return createChenLong(150, 20, 40, 65, x, y);
    }

    
    public static Combatant createKagetsu(int hp, int basicDamage, int heavyDamage, int skillDamage, float x, float y) {
        Combatant enemy = new Combatant("Kagetsu", hp, x, y, false);
        enemy.setRenderSize(270f, 350f);
        enemy.setRenderOffsetY(-20f);
        register(enemy,
            AnimationSpec.of("IDLE", "characters/kagetsu/idle.png", 100, 5, 0.15f, true, 3.0f),
            AnimationSpec.of("WALK", "characters/kagetsu/walk.png", 100, 9, 0.1f, true, 3.0f),
            AnimationSpec.of("JUMP", "characters/kagetsu/jump.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEND", "characters/kagetsu/defence.png", 100, 2, 0.1f, false, 3.0f),
            AnimationSpec.of("ATTACK_BASIC", "characters/kagetsu/attack1.png", 100, 6, 0.1f, false, 3.0f),
            AnimationSpec.of("ATTACK_HEAVY", "characters/kagetsu/attack2.png", 100, 6, 0.1f, false, 3.0f),
            AnimationSpec.of("SKILL", "characters/kagetsu/attack3.png", 100, 10, 0.08f, false, 3.0f),
            AnimationSpec.of("HIT", "characters/kagetsu/hit.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEATED", "characters/kagetsu/dead.png", 100, 6, 0.15f, false, 3.0f),
            AnimationSpec.of("TAUNT", "characters/kagetsu/taunt.png", 100, 3, 0.1f, false, 3.0f)
        );
        enemy.setAttackHitFrame(ActionState.ATTACK_BASIC, 2);
        enemy.setAttackHitFrame(ActionState.ATTACK_HEAVY, 2);
        enemy.setAttackHitFrame(ActionState.SKILL, 4);
        enemy.setAttackRange(ActionState.ATTACK_BASIC, 210f);
        enemy.setAttackRange(ActionState.ATTACK_HEAVY, 245f);
        enemy.setAttackRange(ActionState.SKILL, 290f);
        enemy.setAi(new AggressiveAi(basicDamage, heavyDamage, skillDamage));
        return enemy;
    }

    
    public static Combatant createKagetsu(float x, float y) {
        return createKagetsu(220, 28, 55, 90, x, y);
    }

    
    public static Combatant createJoe(int hp, int basicDamage, int heavyDamage, int skillDamage, int ultimateDamage, float x, float y) {
        Combatant enemy = new Combatant("Joe", hp, x, y, false);
        enemy.setRenderSize(280f, 360f);
        enemy.setRenderOffsetY(-20f);
        register(enemy,
            AnimationSpec.of("IDLE", "characters/Joe/idle.png", 100, 5, 0.15f, true, 3.0f),
            AnimationSpec.of("WALK", "characters/Joe/walk.png", 100, 5, 0.1f, true, 3.0f),
            AnimationSpec.of("JUMP", "characters/Joe/jump.png", 100, 3, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEND", "characters/Joe/defence.png", 100, 2, 0.1f, false, 3.0f),
            AnimationSpec.of("ATTACK_BASIC", "characters/Joe/attack1.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of("ATTACK_HEAVY", "characters/Joe/attack2.png", 100, 5, 0.1f, false, 3.0f),
            AnimationSpec.of("SKILL", "characters/Joe/attack3.png", 100, 7, 0.08f, false, 3.0f),
            AnimationSpec.of("ULTIMATE", "characters/Joe/attack4.png", 100, 7, 0.08f, false, 3.0f),
            AnimationSpec.of("HIT", "characters/Joe/hit.png", 100, 4, 0.1f, false, 3.0f),
            AnimationSpec.of("DEFEATED", "characters/Joe/dead.png", 100, 7, 0.15f, false, 3.0f),
            AnimationSpec.of("TAUNT", "characters/Joe/taunt.png", 100, 3, 0.1f, false, 3.0f)
        );
        enemy.setAttackHitFrame(ActionState.ATTACK_BASIC, 2);
        enemy.setAttackHitFrame(ActionState.ATTACK_HEAVY, 2);
        enemy.setAttackHitFrame(ActionState.SKILL, 4);
        enemy.setAttackHitFrame(ActionState.ULTIMATE, 4);
        enemy.setAttackRange(ActionState.ATTACK_BASIC, 220f);
        enemy.setAttackRange(ActionState.ATTACK_HEAVY, 250f);
        enemy.setAttackRange(ActionState.SKILL, 300f);
        enemy.setAttackRange(ActionState.ULTIMATE, 350f);
        enemy.setAi(new AggressiveAi(basicDamage, heavyDamage, skillDamage));
        return enemy;
    }

    
    public static Combatant createJoe(float x, float y) {
        return createJoe(300, 35, 70, 120, 200, x, y);
    }

    private static void register(Combatant combatant, AnimationSpec... specs) {
        for (AnimationSpec spec : specs) {
            combatant.loadAnimation(spec.getState(), spec.getPath(), spec.getFrameWidth(), spec.getColumns(), spec.getFrameDuration(), spec.isLoop(), true, spec.getScale());
        }
    }
}