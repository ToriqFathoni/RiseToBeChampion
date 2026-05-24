package com.risetobechampion.frontend.command;

import com.risetobechampion.frontend.combat.CombatLogger;
import com.risetobechampion.frontend.combat.Combatant;
import com.risetobechampion.frontend.combat.ActionState;

public class TauntCommand implements Command {
    private final Combatant actor;
    private final CombatLogger logger;

    public TauntCommand(Combatant actor, CombatLogger logger) {
        this.actor = actor;
        this.logger = logger;
    }

    @Override
    public void execute() {
        actor.performAction(null, ActionState.TAUNT, 0, logger);
    }
}
