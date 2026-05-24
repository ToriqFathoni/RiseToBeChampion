package com.risetobechampion.frontend.game.input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

public class TestController {
    public void test() {
        if (Controllers.getControllers().size > 0) {
            Controller c = Controllers.getControllers().get(0);
            boolean a = c.getButton(c.getMapping().buttonA);
            boolean x = c.getButton(c.getMapping().buttonX);
            boolean r1 = c.getButton(c.getMapping().buttonR1);
            boolean l1 = c.getButton(c.getMapping().buttonL1);
            boolean start = c.getButton(c.getMapping().buttonStart);
            
            float lx = c.getAxis(c.getMapping().axisLeftX);
        }
    }
}
