package com.risetobechampion.frontend.command;

import com.risetobechampion.frontend.combat.CombatLogger;
import com.risetobechampion.frontend.combat.Combatant;
import com.risetobechampion.frontend.combat.ActionState;

public class BasicAttackCommand implements Command {
    private final Combatant actor;
    private final Combatant target;
    private final int damage;
    private final CombatLogger logger;

    public BasicAttackCommand(Combatant actor, Combatant target, int damage, CombatLogger logger) {
        this.actor = actor;
        this.target = target;
        this.damage = damage;
        this.logger = logger;
    }

    @Override
    public void execute() {
        actor.performAction(target, ActionState.ATTACK_BASIC, damage, logger);
    }
}
