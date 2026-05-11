package com.risetobechampion.frontend.combat;

public interface AiStrategy {
    void execute(Combatant self, Combatant target, float delta, CombatLogger logger);
}