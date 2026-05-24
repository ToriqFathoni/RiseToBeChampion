package com.risetobechampion.frontend.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.risetobechampion.frontend.combat.CombatLogger;
import com.risetobechampion.frontend.combat.Combatant;
import com.risetobechampion.frontend.command.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.ControllerMapping;

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

    private boolean prevBtnA;
    private boolean prevBtnX;
    private boolean prevBtnY;
    private boolean prevBtnB;
    private boolean prevBtnL1;
    private boolean prevBtnR1;
    private boolean prevBtnStart;

    public PlayerInputController(InputProfile profile, int basicDamage, int heavyDamage, int skillDamage, int ultDamage, float jumpVelocity, float speed) {
        this.profile = profile;
        this.basicDamage = basicDamage;
        this.heavyDamage = heavyDamage;
        this.skillDamage = skillDamage;
        this.ultDamage = ultDamage;
        this.jumpVelocity = jumpVelocity;
        this.speed = speed;
    }

    // baca input tombol
    public void handleInput(Combatant self, Combatant target, CombatLogger logger) {
        if (self.getHp() <= 0 || self.isLocked()) return;

        boolean left = false;
        boolean right = false;
        boolean jump = false;
        boolean defendJustPressed = false;
        boolean basicAttackJustPressed = false;
        boolean heavyAttackJustPressed = false;
        boolean skillJustPressed = false;
        boolean ultJustPressed = false;
        boolean tauntJustPressed = false;
        
        self.getVelocity().x = 0f;

        Controller c = null;
        if (profile == InputProfile.PLAYER_1 && Controllers.getControllers().size > 0) {
            c = Controllers.getControllers().get(0);
        } else if (profile == InputProfile.PLAYER_2 && Controllers.getControllers().size > 1) {
            c = Controllers.getControllers().get(1);
        }

        if (c != null) {
            ControllerMapping m = c.getMapping();
            left = c.getAxis(m.axisLeftX) < -0.5f || c.getButton(m.buttonDpadLeft);
            right = c.getAxis(m.axisLeftX) > 0.5f || c.getButton(m.buttonDpadRight);
            
            boolean btnA = c.getButton(m.buttonA);
            boolean btnX = c.getButton(m.buttonX);
            boolean btnY = c.getButton(m.buttonY);
            boolean btnB = c.getButton(m.buttonB);
            boolean btnL1 = c.getButton(m.buttonL1);
            boolean btnR1 = c.getButton(m.buttonR1);
            boolean btnStart = c.getButton(m.buttonStart);

            jump = btnA;
            basicAttackJustPressed = btnX && !prevBtnX;
            heavyAttackJustPressed = btnY && !prevBtnY;
            skillJustPressed = btnB && !prevBtnB;
            ultJustPressed = btnR1 && !prevBtnR1;
            defendJustPressed = btnL1 && !prevBtnL1;
            tauntJustPressed = btnStart && !prevBtnStart;

            prevBtnA = btnA;
            prevBtnX = btnX;
            prevBtnY = btnY;
            prevBtnB = btnB;
            prevBtnL1 = btnL1;
            prevBtnR1 = btnR1;
            prevBtnStart = btnStart;
        }

        if (profile == InputProfile.PLAYER_1) {
            left = left || Gdx.input.isKeyPressed(Input.Keys.A);
            right = right || Gdx.input.isKeyPressed(Input.Keys.D);
            jump = jump || Gdx.input.isKeyPressed(Input.Keys.W);
            defendJustPressed = defendJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.P);
            basicAttackJustPressed = basicAttackJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.J);
            heavyAttackJustPressed = heavyAttackJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.K);
            skillJustPressed = skillJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.L);
            ultJustPressed = ultJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.I);
            tauntJustPressed = tauntJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.T);
        } else if (profile == InputProfile.PLAYER_2) {
            left = left || Gdx.input.isKeyPressed(Input.Keys.LEFT);
            right = right || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            jump = jump || Gdx.input.isKeyPressed(Input.Keys.UP);
            defendJustPressed = defendJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.NUM_5);
            basicAttackJustPressed = basicAttackJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1);
            heavyAttackJustPressed = heavyAttackJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2);
            skillJustPressed = skillJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3);
            ultJustPressed = ultJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.NUM_4);
            tauntJustPressed = tauntJustPressed || Gdx.input.isKeyJustPressed(Input.Keys.NUM_0);
        }

        if (defendJustPressed) {
            if (!(left || right) && self.isGrounded() && !self.isLocked()) {
                CommandInvoker.getInstance().execute(new DefendCommand(self, logger));
            }
            return;
        }

        if (left) {
            self.getVelocity().x = -speed;
            self.setFacingRight(false);
        } else if (right) {
            self.getVelocity().x = speed;
            self.setFacingRight(true);
        }

        if (jump && self.isGrounded()) {
            self.jump(jumpVelocity);
        }

        if (basicAttackJustPressed) {
            CommandInvoker.getInstance().execute(new BasicAttackCommand(self, target, basicDamage, logger));
        } else if (heavyAttackJustPressed) {
            CommandInvoker.getInstance().execute(new HeavyAttackCommand(self, target, heavyDamage, logger));
        } else if (skillJustPressed) {
            CommandInvoker.getInstance().execute(new SkillCommand(self, target, skillDamage, logger));
        } else if (ultJustPressed) {
            if ("Joe".equals(self.getName())) {
                CommandInvoker.getInstance().execute(new UltimateCommand(self, target, ultDamage, logger));
            }
        } else if (tauntJustPressed) {
            CommandInvoker.getInstance().execute(new TauntCommand(self, logger));
        }
    }
}
