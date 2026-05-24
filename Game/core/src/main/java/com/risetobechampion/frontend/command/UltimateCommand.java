package com.risetobechampion.frontend.command;

import com.risetobechampion.frontend.combat.CombatLogger;
import com.risetobechampion.frontend.combat.Combatant;
import com.risetobechampion.frontend.combat.ActionState;

public class UltimateCommand implements Command {
    private final Combatant executor;
    private final Combatant target;
    private final int damage;
    private final CombatLogger logger;

    public UltimateCommand(Combatant executor, Combatant target, int damage, CombatLogger logger) {
        this.executor = executor;
        this.target = target;
        this.damage = damage;
        this.logger = logger;
    }

    @Override
    public void execute() {
        executor.performAction(target, ActionState.ULTIMATE, damage, logger);
    }
}
