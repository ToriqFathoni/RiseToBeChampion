package com.risetobechampion.frontend.command;

import com.risetobechampion.frontend.combat.CombatLogger;
import com.risetobechampion.frontend.combat.Combatant;
import com.risetobechampion.frontend.combat.EntityState;

public class DefendCommand implements Command {
    private final Combatant actor;
    private final CombatLogger logger;

    public DefendCommand(Combatant actor, CombatLogger logger) {
        this.actor = actor;
        this.logger = logger;
    }

    @Override
    public void execute() {
        actor.performAction(null, EntityState.DEFEND, 0, logger);
    }
}