package com.risetobechampion.frontend.combat;

public class AggressiveAi implements AiStrategy {
    private float cooldown = 2.0f;

    @Override
    public void execute(Combatant self, Combatant target, float delta, CombatLogger logger) {
        if (self.getHp() <= 0 || self.isLocked()) {
            return;
        }

        self.getVelocity().x = 0f;
        float distance = target.getPosition().x - self.getPosition().x;
        self.setFacingRight(distance > 0f);

        if (Math.abs(distance) > 160f) {
            self.getVelocity().x = distance > 0f ? 200f : -200f;
            if (self.isGrounded() && Math.abs(distance) < 260f && Math.random() > 0.985d) {
                self.jump(720f);
                logger.log(self.getName() + " melompat!");
                return;
            }
            if (self.isGrounded()) {
                self.setState(EntityState.WALK);
            }
            return;
        }

        self.setState(EntityState.IDLE);
        cooldown -= delta;
        if (cooldown <= 0f) {
            cooldown = 2.0f;
            double random = Math.random();
            if (random > 0.85d) {
                self.performAction(target, EntityState.TAUNT, 0, logger);
            } else if (random > 0.5d) {
                self.performAction(target, EntityState.ATTACK_HEAVY, 20, logger);
            } else {
                self.performAction(target, EntityState.ATTACK_BASIC, 10, logger);
            }
        }
    }
}