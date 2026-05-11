package com.risetobechampion.frontend.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Combatant {
    private static final float ENERGY_REGEN_PER_SECOND = 12f;
    private static final int BASIC_ATTACK_ENERGY_COST = 8;
    private static final int HEAVY_ATTACK_ENERGY_COST = 18;
    private static final int SKILL_ENERGY_COST = 30;

    private static final class AnimationClip {
        private final Animation<TextureRegion> animation;
        private final float scale;
        private final int frameCount;

        private AnimationClip(Animation<TextureRegion> animation, float scale, int frameCount) {
            this.animation = animation;
            this.scale = scale;
            this.frameCount = frameCount;
        }
    }

    private final String name;
    private final int maxHp;
    private int hp;
    private final int maxEnergy;
    private int energy;
    private final Vector2 position = new Vector2();
    private final Vector2 velocity = new Vector2();
    private boolean facingRight;
    private boolean grounded = true;
    private boolean locked;
    private EntityState currentState = EntityState.IDLE;
    private float stateTime;
    private AiStrategy ai;

    private final Map<EntityState, AnimationClip> animations = new EnumMap<>(EntityState.class);
    private final List<CombatantObserver> observers = new ArrayList<>();
    private final List<Texture> ownedTextures = new ArrayList<>();
    private final IrregularSpriteSheetSplitter irregularSplitter = new IrregularSpriteSheetSplitter();
    private float renderWidth = -1f;
    private float renderHeight = -1f;
    private float renderOffsetY = 0f;

    // Pending attack scheduled during an attack animation; damage applied at triggerTime or triggerFrame
    private static final class PendingAttack {
        Combatant target;
        int damage;
        EntityState action;
        float triggerTime;
        int triggerFrame;
        boolean useFrame;
        boolean applied;

        PendingAttack(Combatant target, int damage, EntityState action, float triggerTime) {
            this.target = target;
            this.damage = damage;
            this.action = action;
            this.triggerTime = triggerTime;
            this.triggerFrame = -1;
            this.useFrame = false;
            this.applied = false;
        }

        PendingAttack(Combatant target, int damage, EntityState action, int triggerFrame, boolean useFrame) {
            this.target = target;
            this.damage = damage;
            this.action = action;
            this.triggerTime = 0f;
            this.triggerFrame = triggerFrame;
            this.useFrame = useFrame;
            this.applied = false;
        }
    }

    private PendingAttack pendingAttack;

    private final Map<EntityState, Integer> attackHitFrames = new EnumMap<>(EntityState.class);
    private final Map<EntityState, Float> attackRanges = new EnumMap<>(EntityState.class);
    private float hitFlashTimer = 0f;
    private float energyRegenAccumulator = 0f;

    public void setAttackHitFrame(EntityState action, int frameIndex) {
        if (action == null) return;
        attackHitFrames.put(action, Math.max(-1, frameIndex));
    }

    public void setAttackRange(EntityState action, float range) {
        if (action == null) return;
        attackRanges.put(action, Math.max(0f, range));
    }

    public Combatant(String name, int hp, float x, float y, boolean facingRight) {
        this(name, hp, 100, x, y, facingRight);
    }

    public Combatant(String name, int hp, int maxEnergy, float x, float y, boolean facingRight) {
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.position.set(x, y);
        this.facingRight = facingRight;
    }

    public void addObserver(CombatantObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(CombatantObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (CombatantObserver observer : observers) {
            observer.onCombatantChanged(this);
        }
    }

    public void loadAnimation(EntityState state, String path, int frameWidth, int columns, float frameDuration, boolean loop, float scale) {
        loadAnimation(state, path, frameWidth, columns, frameDuration, loop, true, scale);
    }

    public void loadAnimation(EntityState state, String path, int frameWidth, int columns, float frameDuration, boolean loop, boolean useAutoSplit, float scale) {
        try {
            int safeColumns = Math.max(1, columns);
            Array<TextureRegion> frames = new Array<>(safeColumns);

            if (useAutoSplit) {
                IrregularSpriteSheetSplitter.SplitResult splitResult = irregularSplitter.autoSplitIrregularSpriteSheetWithTexture(path);
                ownedTextures.add(splitResult.getTexture());

                int maxFrames = Math.min(safeColumns, splitResult.getFrames().size);
                for (int i = 0; i < maxFrames; i++) {
                    frames.add(splitResult.getFrames().get(i));
                }
            } else {
                Texture sheet = new Texture(Gdx.files.internal(path));
                sheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
                ownedTextures.add(sheet);

                int safeFrameWidth = Math.max(1, frameWidth);
                int frameHeight = sheet.getHeight();

                TextureRegion[][] splitFrames = TextureRegion.split(sheet, safeFrameWidth, frameHeight);
                int maxFrames = Math.min(safeColumns, splitFrames[0].length);
                for (int i = 0; i < maxFrames; i++) {
                    frames.add(splitFrames[0][i]);
                }
            }

            if (frames.size == 0) {
                System.err.println("ERROR MEMUAT ASET: " + path + ". Frame tidak ditemukan.");
                return;
            }

            animations.put(state, new AnimationClip(new Animation<>(frameDuration, frames, loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL), scale, frames.size));
            notifyObservers();
        } catch (Exception exception) {
            System.err.println("ERROR MEMUAT ASET: " + path + ". Pastikan file ada!");
        }
    }

    public void loadAnimation(EntityState state, String path, int frameWidth, int columns, float frameDuration, boolean loop) {
        loadAnimation(state, path, frameWidth, columns, frameDuration, loop, true, 1f);
    }

    public void loadAnimation(EntityState state, String path, int frameWidth, int columns, float frameDuration, boolean loop, boolean useAutoSplit) {
        loadAnimation(state, path, frameWidth, columns, frameDuration, loop, useAutoSplit, 1f);
    }

    public void setAi(AiStrategy ai) {
        this.ai = ai;
    }

    public void jump(float jumpVelocity) {
        if (hp <= 0 || locked || !grounded) {
            return;
        }

        velocity.y = jumpVelocity;
        grounded = false;
        setState(EntityState.JUMP);
    }

    public void setRenderSize(float renderWidth, float renderHeight) {
        this.renderWidth = Math.max(1f, renderWidth);
        this.renderHeight = Math.max(1f, renderHeight);
    }

    public void setRenderOffsetY(float renderOffsetY) {
        this.renderOffsetY = renderOffsetY;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public float getHpPercent() {
        return maxHp <= 0 ? 0f : (float) hp / (float) maxHp;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(maxEnergy, energy));
        notifyObservers();
    }

    public float getEnergyPercent() {
        return maxEnergy <= 0 ? 0f : (float) energy / (float) maxEnergy;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public boolean isLocked() {
        return locked;
    }

    public EntityState getCurrentState() {
        return currentState;
    }

    public void setState(EntityState newState) {
        if (currentState == EntityState.DEFEATED) {
            return;
        }

        if (currentState != newState) {
            currentState = newState;
            stateTime = 0f;
            locked = newState == EntityState.ATTACK_BASIC || newState == EntityState.ATTACK_HEAVY || newState == EntityState.SKILL || newState == EntityState.HIT || newState == EntityState.TAUNT;
            if (locked) {
                velocity.x = 0f;
            }
            notifyObservers();
        }
    }

    public void takeDamage(int damage) {
        if (hp <= 0) {
            return;
        }

        hp = Math.max(0, hp - Math.max(0, damage));
        setState(hp == 0 ? EntityState.DEFEATED : EntityState.HIT);
        // trigger a brief flash for feedback
        hitFlashTimer = 0.12f;
        // try to play optional hit sound if present
        try {
            if (Gdx.files.internal("sfx/hit.wav").exists()) {
                com.badlogic.gdx.audio.Sound s = Gdx.audio.newSound(Gdx.files.internal("sfx/hit.wav"));
                s.play(1f);
                s.dispose();
            } else if (Gdx.files.internal("ui/hit.wav").exists()) {
                com.badlogic.gdx.audio.Sound s = Gdx.audio.newSound(Gdx.files.internal("ui/hit.wav"));
                s.play(1f);
                s.dispose();
            }
        } catch (Exception e) {
            // ignore missing/invalid sound files
        }
        notifyObservers();
    }

    public void performAction(Combatant target, EntityState action, int damage, CombatLogger logger) {
        if (target == null || hp <= 0) {
            return;
        }

        int energyCost = getEnergyCost(action);
        if (energyCost > 0 && energy < energyCost) {
            logger.log(name + " energi tidak cukup! (butuh " + energyCost + ", punya " + energy + ")");
            return;
        }

        if (energyCost > 0) {
            setEnergy(energy - energyCost);
        }

        // Schedule attack to apply damage at the proper hit frame of the animation
        setState(action);
        if (action == EntityState.TAUNT) {
            logger.log(name + " melakukan Taunt!");
            return;
        }

        // Determine trigger time based on animation duration (fallback to mid animation)
        AnimationClip clip = animations.get(action);
        float trigger = 0.5f; // default halfway
        if (clip != null) {
            float animDur = clip.animation.getAnimationDuration();
            if (animDur > 0f) {
                trigger = animDur * 0.45f; // slightly before half
            }
        }

        // if a specific hit frame is configured for this action, use it (frame-based trigger)
        Integer hitFrame = attackHitFrames.get(action);
        if (hitFrame != null && hitFrame >= 0) {
            pendingAttack = new PendingAttack(target, damage, action, hitFrame, true);
        } else {
            pendingAttack = new PendingAttack(target, damage, action, trigger);
        }
        // logger message will be emitted when the hit frame resolves in update()
    }

    public void update(float delta, Combatant target, CombatLogger logger) {
        stateTime += delta;

        if (hp <= 0) {
            currentState = EntityState.DEFEATED;
            locked = true;
            velocity.set(0f, 0f);
            grounded = true;
            return;
        }

        // Energy regeneration over time, regardless of state.
        if (energy < maxEnergy) {
            energyRegenAccumulator += ENERGY_REGEN_PER_SECOND * delta;
            if (energyRegenAccumulator >= 1f) {
                int regenAmount = (int) energyRegenAccumulator;
                energyRegenAccumulator -= regenAmount;
                setEnergy(energy + regenAmount);
            }
        } else {
            energyRegenAccumulator = 0f;
        }

        if (ai != null && !locked) {
            ai.execute(this, target, delta, logger);
        }

        // If there's a pending attack, check for hit timing and apply damage once
        if (pendingAttack != null && !pendingAttack.applied && currentState == pendingAttack.action) {
            boolean shouldApply = false;
            if (pendingAttack.useFrame) {
                AnimationClip clip = animations.get(currentState);
                if (clip != null) {
                    int frameCount = clip.frameCount;
                    float fd = clip.animation.getFrameDuration();
                    int idx = fd > 0f ? Math.min(frameCount - 1, (int) (stateTime / fd)) : 0;
                    if (idx >= pendingAttack.triggerFrame) {
                        shouldApply = true;
                    }
                }
            } else {
                if (stateTime >= pendingAttack.triggerTime) {
                    shouldApply = true;
                }
            }

            if (shouldApply) {
                float distance = Math.abs(pendingAttack.target.position.x - position.x);
                float range = attackRanges.getOrDefault(pendingAttack.action, getAttackRange(pendingAttack.action));
                if (distance <= range) {
                    pendingAttack.target.takeDamage(pendingAttack.damage);
                    logger.log(name + " mendaratkan serangan! (" + pendingAttack.damage + " DMG)");
                } else {
                    logger.log(name + " menyerang, tapi meleset!");
                }
                pendingAttack.applied = true;
                pendingAttack = null;
            }
        }

        if (locked) {
            AnimationClip clip = animations.get(currentState);
            if (clip != null && clip.animation.isAnimationFinished(stateTime)) {
                locked = false;
                if (hp > 0) {
                    setState(EntityState.IDLE);
                } else {
                    currentState = EntityState.DEFEATED;
                }
            }
        }

        // Advance hit flash timer
        if (hitFlashTimer > 0f) {
            hitFlashTimer = Math.max(0f, hitFlashTimer - delta);
        }
    }

    private float getAttackRange(EntityState action) {
        switch (action) {
            case ATTACK_HEAVY:
                return 220f;
            case SKILL:
                return 260f;
            default:
                return 200f;
        }
    }

    private int getEnergyCost(EntityState action) {
        if (action == null) {
            return 0;
        }

        switch (action) {
            case ATTACK_BASIC:
                return BASIC_ATTACK_ENERGY_COST;
            case ATTACK_HEAVY:
                return HEAVY_ATTACK_ENERGY_COST;
            case SKILL:
                return SKILL_ENERGY_COST;
            default:
                return 0;
        }
    }

    public void applyPhysics(float delta, float gravity, float floorY) {
        if (hp <= 0) {
            velocity.set(0f, 0f);
            grounded = true;
            position.y = floorY;
            return;
        }

        position.add(velocity.x * delta, velocity.y * delta);

        if (!grounded) {
            velocity.y += gravity * delta;
            if (position.y <= floorY) {
                position.y = floorY;
                velocity.y = 0f;
                grounded = true;
                if (currentState == EntityState.JUMP && !locked) {
                    setState(velocity.x == 0f ? EntityState.IDLE : EntityState.WALK);
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        AnimationClip clip = animations.get(currentState);
        if (clip == null) {
            return;
        }

        TextureRegion src = clip.animation.getKeyFrame(stateTime);
        TextureRegion frame = new TextureRegion(src);
        if (!facingRight && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (facingRight && frame.isFlipX()) {
            frame.flip(true, false);
        }

        // If recently hit, tint the sprite briefly for feedback
        boolean flashed = hitFlashTimer > 0f;
        if (flashed) {
            batch.setColor(1f, 0.6f, 0.6f, 1f);
        }

        float drawWidth = renderWidth > 0f ? renderWidth : frame.getRegionWidth() * clip.scale;
        float drawHeight = renderHeight > 0f ? renderHeight : frame.getRegionHeight() * clip.scale;
        batch.draw(frame, position.x, position.y + renderOffsetY, drawWidth, drawHeight);
        if (flashed) {
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }

    public void dispose() {
        for (Texture texture : ownedTextures) {
            texture.dispose();
        }
        ownedTextures.clear();
    }
}
