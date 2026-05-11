package com.risetobechampion.frontend.utils; // Sesuaikan jika berbeda

public final class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private String userId;
    private String runId;
    private int currentStage = 1;

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // --- KODE BARU ---
    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }
    public int getCurrentStage() {
        return currentStage;
    }
    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public void reset() {
        userId = null;
        runId = null;
        currentStage = 1;
    }
    // -----------------
}
