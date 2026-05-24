package com.risetobechampion.frontend.game.input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.ControllerMapping;

public class UiControllerNavigator {
    private Array<Button> buttons = new Array<>();
    private int currentIndex = 0;
    private Controller controller;
    
    private boolean prevDown, prevUp, prevLeft, prevRight, prevA;

    public UiControllerNavigator(Controller controller) {
        this.controller = controller;
    }
    
    public UiControllerNavigator() {
        if (Controllers.getControllers().size > 0) {
            this.controller = Controllers.getControllers().get(0);
        }
    }

    public void addButton(Button button) {
        buttons.add(button);
        updateFocus();
    }

    // update status secara berkala
    public void update() {
        if (controller == null || buttons.size == 0) return;
        ControllerMapping m = controller.getMapping();

        boolean down = controller.getButton(m.buttonDpadDown) || controller.getAxis(m.axisLeftY) > 0.5f;
        boolean up = controller.getButton(m.buttonDpadUp) || controller.getAxis(m.axisLeftY) < -0.5f;
        boolean left = controller.getButton(m.buttonDpadLeft) || controller.getAxis(m.axisLeftX) < -0.5f;
        boolean right = controller.getButton(m.buttonDpadRight) || controller.getAxis(m.axisLeftX) > 0.5f;
        boolean a = controller.getButton(m.buttonA);

        boolean moved = false;

        if (down && !prevDown) {
            currentIndex = (currentIndex + 1) % buttons.size;
            moved = true;
        } else if (up && !prevUp) {
            currentIndex = (currentIndex - 1 + buttons.size) % buttons.size;
            moved = true;
        } else if (right && !prevRight) {
            currentIndex = (currentIndex + 1) % buttons.size;
            moved = true;
        } else if (left && !prevLeft) {
            currentIndex = (currentIndex - 1 + buttons.size) % buttons.size;
            moved = true;
        }

        if (moved) {
            updateFocus();
        }

        if (a && !prevA) {
            Button activeBtn = buttons.get(currentIndex);
            ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
            activeBtn.fire(changeEvent);
            Pools.free(changeEvent);
        }

        prevDown = down;
        prevUp = up;
        prevLeft = left;
        prevRight = right;
        prevA = a;
    }

    private void updateFocus() {
        for (int i = 0; i < buttons.size; i++) {
            if (i == currentIndex) {
                buttons.get(i).setColor(Color.GOLD);
                buttons.get(i).setTransform(true);
                buttons.get(i).setScale(1.05f);
            } else {
                buttons.get(i).setColor(Color.LIGHT_GRAY);
                buttons.get(i).setTransform(true);
                buttons.get(i).setScale(1.0f);
            }
        }
    }
}
