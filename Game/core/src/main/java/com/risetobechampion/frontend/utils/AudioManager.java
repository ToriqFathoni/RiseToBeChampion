package com.risetobechampion.frontend.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private static AudioManager instance;

    private Music currentMusic;
    private String currentMusicName;

    private Sound punchSound;
    private Sound loseSound;

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    
    private void playMusic(String filePath, float volume) {

        if (currentMusic != null && filePath.equals(currentMusicName)) {
            if (!currentMusic.isPlaying()) {
                currentMusic.play();
            }
            return;
        }

        stopMusic();

        try {
            currentMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
            currentMusicName = filePath;
            currentMusic.setLooping(true); // Loop automatically
            currentMusic.setVolume(volume);
            currentMusic.play();
        } catch (Exception e) {
            System.err.println("Failed to load music: " + filePath);
            e.printStackTrace();
        }
    }

    // putar lagu menu
    public void playMainMusic() {
        playMusic("music/backsound_all.mp3", 0.5f);
    }

    public void playVictoryMusic() {
        playMusic("music/backsound_win.mp3", 0.5f);
    }

    // putar lagu berantem
    public void playFightMusic() {

        playMusic("music/backsound_fight.mp3", 0.25f);
    }

    public void playPunchSound() {
        if (punchSound == null) {
            punchSound = Gdx.audio.newSound(Gdx.files.internal("music/sfx-punch.mp3"));
        }
        punchSound.play(1.0f); // Volume penuh untuk SFX
    }

    public void playLoseSound() {
        if (loseSound == null) {
            loseSound = Gdx.audio.newSound(Gdx.files.internal("music/sfx-punch_lose.mp3"));
        }
        loseSound.play(1.0f);
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
            currentMusicName = null;
        }
    }

    public void dispose() {
        stopMusic();
        if (punchSound != null) {
            punchSound.dispose();
            punchSound = null;
        }
        if (loseSound != null) {
            loseSound.dispose();
            loseSound = null;
        }
    }
}
