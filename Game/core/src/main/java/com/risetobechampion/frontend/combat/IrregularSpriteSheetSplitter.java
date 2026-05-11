package com.risetobechampion.frontend.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class IrregularSpriteSheetSplitter {
    public static final class SplitResult {
        private final Texture texture;
        private final Array<TextureRegion> frames;

        private SplitResult(Texture texture, Array<TextureRegion> frames) {
            this.texture = texture;
            this.frames = frames;
        }

        public Texture getTexture() {
            return texture;
        }

        public Array<TextureRegion> getFrames() {
            return frames;
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

            boolean insideSprite = false;
            int startX = 0;

            for (int x = 0; x < width; x++) {
                boolean transparentColumn = isTransparentColumn(pixmap, x, height);
                if (!insideSprite && !transparentColumn) {
                    insideSprite = true;
                    startX = x;
                } else if (insideSprite && transparentColumn) {
                    int frameWidth = x - startX;
                    if (frameWidth > 0) {
                        frames.add(new TextureRegion(texture, startX, 0, frameWidth, height));
                    }
                    insideSprite = false;
                }
            }

            if (insideSprite) {
                int frameWidth = width - startX;
                if (frameWidth > 0) {
                    frames.add(new TextureRegion(texture, startX, 0, frameWidth, height));
                }
            }

            return new SplitResult(texture, frames);
        } finally {
            if (pixmap != null) {
                pixmap.dispose();
            }
        }
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