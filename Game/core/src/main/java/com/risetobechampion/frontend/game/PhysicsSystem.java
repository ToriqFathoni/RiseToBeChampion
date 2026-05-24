package com.risetobechampion.frontend.game;

import com.risetobechampion.frontend.combat.Combatant;

public class PhysicsSystem {
    private final float floorY;
    private final float gravity;
    private final float worldWidth;
    private final float collisionGap;
    private final float collisionWidthRatio;
    private final float collisionHeightRatio;

    public PhysicsSystem(float floorY, float gravity, float worldWidth, float collisionGap, float collisionWidthRatio, float collisionHeightRatio) {
        this.floorY = floorY;
        this.gravity = gravity;
        this.worldWidth = worldWidth;
        this.collisionGap = collisionGap;
        this.collisionWidthRatio = collisionWidthRatio;
        this.collisionHeightRatio = collisionHeightRatio;
    }

    public void updatePhysics(Combatant p1, Combatant p2, float delta) {

        p1.applyPhysics(delta, gravity, floorY);
        p2.applyPhysics(delta, gravity, floorY);

        resolveCollision(p1, p2);

        clampToBounds(p1);
        clampToBounds(p2);
    }

    private void resolveCollision(Combatant player1, Combatant player2) {
        if (player1.getHp() <= 0 || player2.getHp() <= 0) return;

        float p1Width = Math.max(1f, player1.getRenderWidth() * collisionWidthRatio);
        float p2Width = Math.max(1f, player2.getRenderWidth() * collisionWidthRatio);
        float p1Height = Math.max(1f, player1.getRenderHeight() * collisionHeightRatio);
        float p2Height = Math.max(1f, player2.getRenderHeight() * collisionHeightRatio);

        float p1Left = player1.getPosition().x + (player1.getRenderWidth() - p1Width) * 0.5f;
        float p1Right = p1Left + p1Width;
        float p1Bottom = player1.getPosition().y;
        float p1Top = p1Bottom + p1Height;

        float p2Left = player2.getPosition().x + (player2.getRenderWidth() - p2Width) * 0.5f;
        float p2Right = p2Left + p2Width;
        float p2Bottom = player2.getPosition().y;
        float p2Top = p2Bottom + p2Height;

        boolean horizontalOverlap = p1Right + collisionGap > p2Left && p2Right + collisionGap > p1Left;
        boolean verticalOverlap = p1Top > p2Bottom && p2Top > p1Bottom;
        
        if (!horizontalOverlap || !verticalOverlap) return;

        if (p1Left <= p2Left) {
            float shift = (p1Right + collisionGap) - p2Left;
            if (shift > 0f) {
                player1.getPosition().x -= shift * 0.5f;
                player2.getPosition().x += shift * 0.5f;
            }
        } else {
            float shift = (p2Right + collisionGap) - p1Left;
            if (shift > 0f) {
                player2.getPosition().x -= shift * 0.5f;
                player1.getPosition().x += shift * 0.5f;
            }
        }
    }

    private void clampToBounds(Combatant player) {
        float minX = 0f;
        float maxX = worldWidth - player.getRenderWidth();
        if (player.getPosition().x < minX) {
            player.getPosition().x = minX;
        } else if (player.getPosition().x > maxX) {
            player.getPosition().x = maxX;
        }
    }
}
