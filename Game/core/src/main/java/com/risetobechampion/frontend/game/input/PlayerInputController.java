package com.risetobechampion.frontend.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.risetobechampion.frontend.combat.CombatLogger;
import com.risetobechampion.frontend.combat.Combatant;
import com.risetobechampion.frontend.command.*;

/**
 * Controller pattern to handle human inputs.
 * Separates the input polling from the rendering thread, achieving High Cohesion.
 */
public class PlayerInputController {
    public enum InputProfile {
        PLAYER_1, // WASD + JKL I T
        PLAYER_2  // Arrows + Numbers 1-5, 0
    }

    private final InputProfile profile;
    private final int basicDamage;
    private final int heavyDamage;
    private final int skillDamage;
    private final int ultDamage;
    private final float jumpVelocity;
    private final float speed;

    public PlayerInputController(InputProfile profile, int basicDamage, int heavyDamage, int skillDamage, int ultDamage, float jumpVelocity, float speed) {
        this.profile = profile;
        this.basicDamage = basicDamage;
        this.heavyDamage = heavyDamage;
        this.skillDamage = skillDamage;
        this.ultDamage = ultDamage;
        this.jumpVelocity = jumpVelocity;
        this.speed = speed;
    }

    public void handleInput(Combatant self, Combatant target, CombatLogger logger) {
        if (self.getHp() <= 0 || self.isLocked()) return;

        boolean left = false;
        boolean right = false;
        self.getVelocity().x = 0f;

        if (profile == InputProfile.PLAYER_1) {
            left = Gdx.input.isKeyPressed(Input.Keys.A);
            right = Gdx.input.isKeyPressed(Input.Keys.D);

            // Defend (P)
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                if (!(left || right) && self.isGrounded() && !self.isLocked()) {
                    CommandInvoker.getInstance().execute(new DefendCommand(self, logger));
                }
                return;
            }

            // Movement
            if (left) {
                self.getVelocity().x = -speed;
                self.setFacingRight(false);
            } else if (right) {
                self.getVelocity().x = speed;
                self.setFacingRight(true);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.W) && self.isGrounded()) {
                self.jump(jumpVelocity);
            }

            // Actions
            if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
                CommandInvoker.getInstance().execute(new BasicAttackCommand(self, target, basicDamage, logger));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
                CommandInvoker.getInstance().execute(new HeavyAttackCommand(self, target, heavyDamage, logger));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
                CommandInvoker.getInstance().execute(new SkillCommand(self, target, skillDamage, logger));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                if ("Joe".equals(self.getName())) {
                    CommandInvoker.getInstance().execute(new UltimateCommand(self, target, ultDamage, logger));
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                CommandInvoker.getInstance().execute(new TauntCommand(self, logger));
            }
        } else if (profile == InputProfile.PLAYER_2) {
            left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            // Defend (Num 5)
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                if (!(left || right) && self.isGrounded() && !self.isLocked()) {
                    CommandInvoker.getInstance().execute(new DefendCommand(self, logger));
                }
                return;
            }

            // Movement
            if (left) {
                self.getVelocity().x = -speed;
                self.setFacingRight(false);
            } else if (right) {
                self.getVelocity().x = speed;
                self.setFacingRight(true);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.UP) && self.isGrounded()) {
                self.jump(jumpVelocity);
            }

            // Actions
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                CommandInvoker.getInstance().execute(new BasicAttackCommand(self, target, basicDamage, logger));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                CommandInvoker.getInstance().execute(new HeavyAttackCommand(self, target, heavyDamage, logger));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                CommandInvoker.getInstance().execute(new SkillCommand(self, target, skillDamage, logger));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                if ("Joe".equals(self.getName())) {
                    CommandInvoker.getInstance().execute(new UltimateCommand(self, target, ultDamage, logger));
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
                CommandInvoker.getInstance().execute(new TauntCommand(self, logger));
            }
        }
    }
}
