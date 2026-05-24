package com.risetobechampion.frontend.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class IrregularSpriteSheetSplitter {
    private static final int MIN_FRAME_WIDTH = 8;

    public static final class SplitResult {
        private final Texture texture;
        private final Array<TextureRegion> frames;
        private final Array<Float> frameBottomPadding;

        private SplitResult(Texture texture, Array<TextureRegion> frames, Array<Float> frameBottomPadding) {
            this.texture = texture;
            this.frames = frames;
            this.frameBottomPadding = frameBottomPadding;
        }

        public Texture getTexture() {
            return texture;
        }

        public Array<TextureRegion> getFrames() {
            return frames;
        }

        public Array<Float> getFrameBottomPadding() {
            return frameBottomPadding;
        }
    }

    public Array<TextureRegion> autoSplitIrregularSpriteSheet(String path) {
        SplitResult result = autoSplitIrregularSpriteSheetWithTexture(path);
        return result.getFrames();
    }

    public SplitResult autoSplitIrregularSpriteSheetWithTexture(String path) {
        Pixmap pixmap = null;
        try {
            pixmap = new Pixmap(Gdx.files.internal(path));
            Texture texture = new Texture(Gdx.files.internal(path));
            texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

            int width = pixmap.getWidth();
            int height = pixmap.getHeight();
            Array<TextureRegion> frames = new Array<>();
            Array<Float> frameBottomPadding = new Array<>();

            boolean insideSprite = false;
            int startX = 0;

            for (int x = 0; x < width; x++) {
                boolean transparentColumn = isTransparentColumn(pixmap, x, height);
                if (!insideSprite && !transparentColumn) {
                    insideSprite = true;
                    startX = x;
                } else if (insideSprite && transparentColumn) {
                    int frameWidth = x - startX;
                    if (frameWidth >= MIN_FRAME_WIDTH) {
                        frames.add(new TextureRegion(texture, startX, 0, frameWidth, height));
                        frameBottomPadding.add(getBottomPadding(pixmap, startX, frameWidth, height));
                    }
                    insideSprite = false;
                }
            }

            if (insideSprite) {
                int frameWidth = width - startX;
                if (frameWidth >= MIN_FRAME_WIDTH) {
                    frames.add(new TextureRegion(texture, startX, 0, frameWidth, height));
                    frameBottomPadding.add(getBottomPadding(pixmap, startX, frameWidth, height));
                }
            }

            return new SplitResult(texture, frames, frameBottomPadding);
        } finally {
            if (pixmap != null) {
                pixmap.dispose();
            }
        }
    }

    private float getBottomPadding(Pixmap pixmap, int startX, int frameWidth, int height) {
        for (int y = height - 1; y >= 0; y--) {
            for (int x = startX; x < startX + frameWidth; x++) {
                int pixel = pixmap.getPixel(x, y);
                int alpha = pixel & 0x000000ff;
                if (alpha != 0) {
                    return height - 1 - y;
                }
            }
        }

        return 0f;
    }

    private boolean isTransparentColumn(Pixmap pixmap, int x, int height) {
        for (int y = 0; y < height; y++) {
            int pixel = pixmap.getPixel(x, y);
            int alpha = pixel & 0x000000ff;
            if (alpha != 0) {
                return false;
            }
        }
        return true;
    }
}