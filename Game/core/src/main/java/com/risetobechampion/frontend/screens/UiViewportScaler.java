package com.risetobechampion.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.utils.viewport.Viewport;

final class UiViewportScaler {
    private UiViewportScaler() {
    }

    // update status secara berkala
    static void update(Viewport viewport, int width, int height, float fullscreenWorldWidth, float fullscreenWorldHeight, float windowedScale) {
        boolean fullscreen = isFullscreen(width, height);
        float worldWidth = fullscreen ? fullscreenWorldWidth : fullscreenWorldWidth * windowedScale;
        float worldHeight = fullscreen ? fullscreenWorldHeight : fullscreenWorldHeight * windowedScale;

        viewport.setWorldSize(worldWidth, worldHeight);
        // update status secara berkala
        viewport.update(width, height, true);
    }

    static void syncNow(Viewport viewport, float fullscreenWorldWidth, float fullscreenWorldHeight, float windowedScale) {
        // update status secara berkala
        update(viewport, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), fullscreenWorldWidth, fullscreenWorldHeight, windowedScale);
    }

    private static boolean isFullscreen(int width, int height) {
        DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        return width >= displayMode.width && height >= displayMode.height;
    }
}