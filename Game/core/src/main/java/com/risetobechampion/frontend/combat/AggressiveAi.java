package com.risetobechampion.frontend.combat;

public class AggressiveAi implements AiStrategy {
    private static final float APPROACH_DISTANCE = 170f;
    private static final float TAUNT_DISTANCE = 420f;
    private static final float JUMP_DISTANCE = 260f;
    private static final float DEFEND_DISTANCE = 140f;
    private static final float DEFEND_COOLDOWN = 3.5f;

    private final int basicDamage;
    private final int heavyDamage;
    private final int skillDamage;

    private float attackCooldown = 0f;
    private float tauntCooldown = 0f;
    private float defendCooldown = 0f;

    public AggressiveAi(int basicDamage, int heavyDamage, int skillDamage) {
        this.basicDamage = basicDamage;
        this.heavyDamage = heavyDamage;
        this.skillDamage = skillDamage;
    }

    public AggressiveAi() {
        this(15, 30, 50);
    }

    @Override
    public void execute(Combatant self, Combatant target, float delta, CombatLogger logger) {
        if (self.getHp() <= 0 || self.isLocked()) {
            return;
        }

        attackCooldown = Math.max(0f, attackCooldown - delta);
        tauntCooldown = Math.max(0f, tauntCooldown - delta);
        defendCooldown = Math.max(0f, defendCooldown - delta);

        self.getVelocity().x = 0f;
        float distance = target.getPosition().x - self.getPosition().x;
        float absDistance = Math.abs(distance);
        self.setFacingRight(distance > 0f);

        if (absDistance > APPROACH_DISTANCE) {
            if (absDistance >= TAUNT_DISTANCE && tauntCooldown <= 0f && Math.random() > 0.65d) {
                self.setActionState(ActionState.TAUNT);
                // tulis log pertarungan
                logger.log(self.getName() + " menantang dari jarak jauh!");
                tauntCooldown = 4.0f;
                return;
            }

            self.getVelocity().x = distance > 0f ? 200f : -200f;
            if (self.isGrounded() && absDistance < JUMP_DISTANCE && Math.random() > 0.985d) {
                self.jump(720f);
                // tulis log pertarungan
                logger.log(self.getName() + " melompat!");
                return;
            }
            return;
        }

        if (defendCooldown <= 0f && (self.getHp() <= self.getMaxHp() * 0.35f || (absDistance <= DEFEND_DISTANCE && Math.random() > 0.78d))) {
            self.performAction(null, ActionState.DEFEND, 0, logger);
            defendCooldown = DEFEND_COOLDOWN;
            return;
        }

        if (attackCooldown > 0f) {
            return;
        }

        double targetHpRatio = target.getMaxHp() <= 0 ? 1.0d : (double) target.getHp() / (double) target.getMaxHp();
        double random = Math.random();
        if (self.getEnergyCost(ActionState.SKILL) <= self.getEnergy() && targetHpRatio <= 0.45d && random > 0.6d) {
            self.performAction(target, ActionState.SKILL, skillDamage, logger);
            attackCooldown = 1.9f;
        } else if (self.getEnergyCost(ActionState.ATTACK_HEAVY) <= self.getEnergy() && (absDistance <= 120f || targetHpRatio <= 0.65d || random > 0.55d)) {
            self.performAction(target, ActionState.ATTACK_HEAVY, heavyDamage, logger);
            attackCooldown = 1.6f;
        } else if (self.getEnergyCost(ActionState.ATTACK_BASIC) <= self.getEnergy()) {
            self.performAction(target, ActionState.ATTACK_BASIC, basicDamage, logger);
            attackCooldown = 1.1f;
        }
    }
}