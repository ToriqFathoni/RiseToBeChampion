package com.risetobechampion.frontend.command;

import com.risetobechampion.frontend.combat.CombatLogger;
import com.risetobechampion.frontend.combat.Combatant;
import com.risetobechampion.frontend.combat.EntityState;

public class SkillCommand implements Command {
    private final Combatant actor;
    private final Combatant target;
    private final int damage;
    private final CombatLogger logger;

    public SkillCommand(Combatant actor, Combatant target, int damage, CombatLogger logger) {
        this.actor = actor;
        this.target = target;
        this.damage = damage;
        this.logger = logger;
    }

    @Override
    public void execute() {
        actor.performAction(target, EntityState.SKILL, damage, logger);
    }
}
