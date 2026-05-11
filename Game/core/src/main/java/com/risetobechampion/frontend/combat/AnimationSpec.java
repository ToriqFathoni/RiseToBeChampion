package com.risetobechampion.frontend.combat;

public final class AnimationSpec {
    private final EntityState state;
    private final String path;
    private final int frameWidth;
    private final int columns;
    private final float frameDuration;
    private final boolean loop;
    private final float scale;

    private AnimationSpec(EntityState state, String path, int frameWidth, int columns, float frameDuration, boolean loop, float scale) {
        this.state = state;
        this.path = path;
        this.frameWidth = frameWidth;
        this.columns = columns;
        this.frameDuration = frameDuration;
        this.loop = loop;
        this.scale = scale;
    }

    public static AnimationSpec of(EntityState state, String path, int frameWidth, int columns, float frameDuration, boolean loop, float scale) {
        return new AnimationSpec(state, path, frameWidth, columns, frameDuration, loop, scale);
    }

    public EntityState getState() {
        return state;
    }

    public String getPath() {
        return path;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getColumns() {
        return columns;
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    public boolean isLoop() {
        return loop;
    }

    public float getScale() {
        return scale;
    }
}