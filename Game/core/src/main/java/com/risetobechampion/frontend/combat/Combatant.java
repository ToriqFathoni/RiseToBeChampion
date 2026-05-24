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
    private static final int BASIC_ATTACK_ENERGY_PERCENT = 20;
    private static final int HEAVY_ATTACK_ENERGY_PERCENT = 40;
    private static final int SKILL_ENERGY_PERCENT = 75;
    private static final int ULTIMATE_ENERGY_PERCENT = 40;
    private static final int JUMP_ENERGY_PERCENT = 10;
    private static final float DEFEND_DURATION = 1.35f;
    private static final int DEFEND_ENERGY_HIT_COST = 5;
    private static final float LOCOMOTION_EPSILON = 8f;

    private static final class AnimationClip {
        private final Animation<TextureRegion> animation;
        private final float scale;
        private final int frameCount;
        private final float[] frameBottomPadding;

        private AnimationClip(Animation<TextureRegion> animation, float scale, int frameCount, float[] frameBottomPadding) {
            this.animation = animation;
            this.scale = scale;
            this.frameCount = frameCount;
            this.frameBottomPadding = frameBottomPadding;
        }

        private float getFrameBottomPadding(int frameIndex) {
            if (frameBottomPadding == null || frameIndex < 0 || frameIndex >= frameBottomPadding.length) {
                return 0f;
            }

            return frameBottomPadding[frameIndex];
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
    private MovementState movementState = MovementState.GROUNDED;
    private ActionState actionState = ActionState.NONE;
    private boolean isWalking = false;
    private float stateTime;
    private AiStrategy ai;

    private final Map<String, AnimationClip> animations = new java.util.HashMap<>();
    private final List<CombatantObserver> observers = new ArrayList<>();
    private final List<Texture> ownedTextures = new ArrayList<>();
    private final IrregularSpriteSheetSplitter irregularSplitter = new IrregularSpriteSheetSplitter();
    private float renderWidth = -1f;
    private float renderHeight = -1f;
    private float renderOffsetY = 0f;

    private static final class PendingAttack {
        Combatant target;
        int damage;
        ActionState action;
        float triggerTime;
        int triggerFrame;
        boolean useFrame;
        boolean applied;

        PendingAttack(Combatant target, int damage, ActionState action, float triggerTime) {
            this.target = target;
            this.damage = damage;
            this.action = action;
            this.triggerTime = triggerTime;
            this.triggerFrame = -1;
            this.useFrame = false;
            this.applied = false;
        }

        PendingAttack(Combatant target, int damage, ActionState action, int triggerFrame, boolean useFrame) {
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

    private final Map<ActionState, Integer> attackHitFrames = new EnumMap<>(ActionState.class);
    private final Map<ActionState, Float> attackRanges = new EnumMap<>(ActionState.class);
    private float energyRegenAccumulator = 0f;
    private boolean defenseActive = false;
    private float defenseTimer = 0f;

    public void setAttackHitFrame(ActionState action, int frameIndex) {
        if (action == null) return;
        attackHitFrames.put(action, Math.max(-1, frameIndex));
    }

    public void setAttackRange(ActionState action, float range) {
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

    public void loadAnimation(String state, String path, int frameWidth, int columns, float frameDuration, boolean loop, float scale) {
        loadAnimation(state, path, frameWidth, columns, frameDuration, loop, true, scale);
    }

    public void loadAnimation(String state, String path, int frameWidth, int columns, float frameDuration, boolean loop, boolean useAutoSplit, float scale) {
        try {
            int safeColumns = Math.max(1, columns);
            Array<TextureRegion> frames = new Array<>(safeColumns);
            float[] frameBottomPadding = null;

            if (useAutoSplit) {
                IrregularSpriteSheetSplitter.SplitResult splitResult = irregularSplitter.autoSplitIrregularSpriteSheetWithTexture(path);
                ownedTextures.add(splitResult.getTexture());

                int maxFrames = Math.min(safeColumns, splitResult.getFrames().size);
                frameBottomPadding = new float[maxFrames];
                for (int i = 0; i < maxFrames; i++) {
                    frames.add(splitResult.getFrames().get(i));
                    frameBottomPadding[i] = splitResult.getFrameBottomPadding().get(i);
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

            animations.put(state, new AnimationClip(new Animation<>(frameDuration, frames, loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL), scale, frames.size, frameBottomPadding));
            notifyObservers();
        } catch (Exception exception) {
            System.err.println("ERROR MEMUAT ASET: " + path + ". Pastikan file ada!");
        }
    }

    public void loadAnimation(String state, String path, int frameWidth, int columns, float frameDuration, boolean loop) {
        loadAnimation(state, path, frameWidth, columns, frameDuration, loop, true, 1f);
    }

    public void loadAnimation(String state, String path, int frameWidth, int columns, float frameDuration, boolean loop, boolean useAutoSplit) {
        loadAnimation(state, path, frameWidth, columns, frameDuration, loop, useAutoSplit, 1f);
    }

    public void setAi(AiStrategy ai) {
        this.ai = ai;
    }

    public void jump(float jumpVelocity) {
        // cek darah kalau habis
        if (hp <= 0 || locked || !grounded) {
            return;
        }

        int jumpEnergyCost = getJumpEnergyCost();
        if (jumpEnergyCost > 0 && energy < jumpEnergyCost) {
            return;
        }

        if (jumpEnergyCost > 0) {
            setEnergy(energy - jumpEnergyCost);
        }

        velocity.y = jumpVelocity;
        grounded = false;
        setMovementState(MovementState.AIRBORNE);
    }

    public void setRenderSize(float renderWidth, float renderHeight) {
        this.renderWidth = Math.max(1f, renderWidth);
        this.renderHeight = Math.max(1f, renderHeight);
    }

    public float getRenderWidth() {
        return renderWidth > 0f ? renderWidth : 0f;
    }

    public float getRenderHeight() {
        return renderHeight > 0f ? renderHeight : 0f;
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

    public ActionState getActionState() {
        return actionState;
    }

    public void setActionState(ActionState newState) {
        if (actionState == ActionState.DEFEATED) {
            return;
        }

        if (actionState != newState) {
            actionState = newState;
            stateTime = 0f;
            locked = newState == ActionState.ATTACK_BASIC || newState == ActionState.ATTACK_HEAVY || newState == ActionState.SKILL || newState == ActionState.ULTIMATE || newState == ActionState.HIT || newState == ActionState.TAUNT || newState == ActionState.DEFEND;
            if (locked) {
                velocity.x = 0f;
            }
            notifyObservers();
        }
    }

    public MovementState getMovementState() {
        return movementState;
    }

    public void setMovementState(MovementState newState) {
        if (movementState != newState) {
            movementState = newState;
            notifyObservers();
        }
    }

    public void takeDamage(int damage) {
        com.risetobechampion.frontend.utils.AudioManager.getInstance().playPunchSound();
        // cek darah kalau habis
        if (hp <= 0) {
            return;
        }

        int finalDamage = Math.max(0, damage);
        if (defenseActive) {
            finalDamage = 0;
            if (energy > 0) {
                setEnergy(energy - DEFEND_ENERGY_HIT_COST);
            }
        }

        hp = Math.max(0, hp - finalDamage);
        setActionState(hp == 0 ? ActionState.DEFEATED : ActionState.HIT);

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

        }
        notifyObservers();
    }

    public void performAction(Combatant target, ActionState action, int damage, CombatLogger logger) {
        // cek darah kalau habis
        if (hp <= 0) {
            return;
        }

        if (action == ActionState.DEFEND) {
            defenseActive = true;
            defenseTimer = DEFEND_DURATION;
            setActionState(ActionState.DEFEND);
            if (logger != null) {
                // tulis log pertarungan
                logger.log(name + " bersiap menangkis serangan!");
            }
            return;
        }

        if (action == ActionState.TAUNT) {
            if (logger != null) {
                // tulis log pertarungan
                logger.log(name + " melakukan Taunt!");
            }
            setActionState(ActionState.TAUNT);
            return;
        }

        if (target == null) {
            return;
        }

        int energyCost = getEnergyCost(action);
        if (energyCost > 0 && energy < energyCost) {
            // tulis log pertarungan
            logger.log(name + " energi tidak cukup! (butuh " + energyCost + ", punya " + energy + ")");
            return;
        }

        if (energyCost > 0) {
            setEnergy(energy - energyCost);
        }

        setActionState(action);

        AnimationClip clip = animations.get(action.name());
        float trigger = 0.5f; // default halfway
        if (clip != null) {
            float animDur = clip.animation.getAnimationDuration();
            if (animDur > 0f) {
                trigger = animDur * 0.45f; // slightly before half
            }
        }

        Integer hitFrame = attackHitFrames.get(action);
        if (hitFrame != null && hitFrame >= 0) {
            pendingAttack = new PendingAttack(target, damage, action, hitFrame, true);
        } else {
            pendingAttack = new PendingAttack(target, damage, action, trigger);
        }

    }

    // update status secara berkala
    public void update(float delta, Combatant target, CombatLogger logger) {
        stateTime += delta;

        // cek darah kalau habis
        if (hp <= 0) {
            actionState = ActionState.DEFEATED;
            locked = true;
            velocity.set(0f, 0f);
            grounded = true;
            return;
        }

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

        if (defenseActive) {
            defenseTimer = Math.max(0f, defenseTimer - delta);
            if (defenseTimer <= 0f) {
                defenseActive = false;
            }
        }

        if (ai != null && !locked) {
            ai.execute(this, target, delta, logger);
        }

        if (pendingAttack != null && !pendingAttack.applied && actionState == pendingAttack.action) {
            boolean shouldApply = false;
            if (pendingAttack.useFrame) {
                AnimationClip clip = animations.get(actionState.name());
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
                    // tulis log pertarungan
                    logger.log(name + " mendaratkan serangan! (" + pendingAttack.damage + " DMG)");
                } else {
                    // tulis log pertarungan
                    logger.log(name + " menyerang, tapi meleset!");
                }
                pendingAttack.applied = true;
                pendingAttack = null;
            }
        }

        if (locked) {
            AnimationClip clip = animations.get(actionState.name());
            boolean finished = clip != null && clip.animation.isAnimationFinished(stateTime);
            if (actionState == ActionState.DEFEND) {
                finished = !defenseActive;
            }
            if (finished) {
                locked = false;
                // cek darah
                if (hp > 0) {
                    setActionState(ActionState.NONE);
                } else {
                    actionState = ActionState.DEFEATED;
                }
            }
        }

    }

    private float getAttackRange(ActionState action) {
        switch (action) {
            case ATTACK_HEAVY:
                return 220f;
            case SKILL:
                return 260f;
            default:
                return 200f;
        }
    }

    public int getEnergyCost(ActionState action) {
        if (action == null) {
            return 0;
        }

        switch (action) {
            case ATTACK_BASIC:
                return getEnergyCostFromPercent(BASIC_ATTACK_ENERGY_PERCENT);
            case ATTACK_HEAVY:
                return getEnergyCostFromPercent(HEAVY_ATTACK_ENERGY_PERCENT);
            case SKILL:
                return getEnergyCostFromPercent(SKILL_ENERGY_PERCENT);
            case ULTIMATE:
                return getEnergyCostFromPercent(ULTIMATE_ENERGY_PERCENT);
            default:
                return 0;
        }
    }

    private int getJumpEnergyCost() {
        return getEnergyCostFromPercent(JUMP_ENERGY_PERCENT);
    }

    private int getEnergyCostFromPercent(int percent) {
        if (maxEnergy <= 0 || percent <= 0) {
            return 0;
        }

        return Math.max(1, Math.round(maxEnergy * (percent / 100f)));
    }

    public void applyPhysics(float delta, float gravity, float floorY) {
        // cek darah kalau habis
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
                setMovementState(MovementState.GROUNDED);
            }
        }

        // cek darah
        if (hp > 0) {
            isWalking = Math.abs(velocity.x) > LOCOMOTION_EPSILON;
        }
    }

    public void draw(SpriteBatch batch) {
        String animKey = "IDLE";
        if (actionState != ActionState.NONE) {
            animKey = actionState.name();
        } else {
            if (movementState == MovementState.AIRBORNE) {
                animKey = "JUMP";
            } else if (isWalking) {
                animKey = "WALK";
            } else {
                animKey = "IDLE";
            }
        }

        AnimationClip clip = animations.get(animKey);
        if (clip == null && actionState == ActionState.DEFEND) {
            clip = animations.get("IDLE");
        }
        if (clip == null && actionState == ActionState.NONE) {
            clip = animations.get("IDLE");
        }
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

        float drawWidth = renderWidth > 0f ? renderWidth : frame.getRegionWidth() * clip.scale;
        float drawHeight = renderHeight > 0f ? renderHeight : frame.getRegionHeight() * clip.scale;
        int frameIndex = clip.animation.getKeyFrameIndex(stateTime);
        float frameBottomPadding = clip.getFrameBottomPadding(frameIndex);
        float scaledBottomPadding = frame.getRegionHeight() > 0f ? frameBottomPadding * (drawHeight / frame.getRegionHeight()) : 0f;
        // render karakter/gambar
        batch.draw(frame, position.x, position.y + renderOffsetY - scaledBottomPadding, drawWidth, drawHeight);
    }

    public void dispose() {
        for (Texture texture : ownedTextures) {
            texture.dispose();
        }
        ownedTextures.clear();
    }
}
