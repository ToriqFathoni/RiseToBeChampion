package com.risetobechampion.frontend.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.risetobechampion.frontend.utils.SessionManager;

/**
 * Handles saving progress cleanly without cluttering the screen logic.
 */
public class ProgressManager {

    public interface SaveCallback {
        void onSuccess();
        void onFailure(String errorMsg);
    }

    public static void saveCurrentProgress(int currentStage, int deathCount, int timeElapsed, String status, SaveCallback callback) {
        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.getRunId() == null || sessionManager.getRunId().isEmpty()) {
            if (callback != null) callback.onFailure("No Run ID available.");
            return;
        }

        ApiClient.saveProgress(
            sessionManager.getRunId(),
            currentStage,
            deathCount,
            timeElapsed,
            status,
            sessionManager.getPlayerHpBonus(),
            sessionManager.getPlayerAttack1Bonus(),
            sessionManager.getPlayerAttack2Bonus(),
            sessionManager.getPlayerAttack3Bonus(),
            false,
            new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    final int statusCode = httpResponse.getStatus().getStatusCode();
                    Gdx.app.postRunnable(() -> {
                        if (statusCode >= 200 && statusCode < 300) {
                            if (callback != null) callback.onSuccess();
                        } else {
                            if (callback != null) callback.onFailure("Server returned " + statusCode);
                        }
                    });
                }

                @Override
                public void failed(Throwable t) {
                    Gdx.app.postRunnable(() -> {
                        if (callback != null) callback.onFailure(t.getMessage());
                    });
                }

                @Override
                public void cancelled() {
                    Gdx.app.postRunnable(() -> {
                        if (callback != null) callback.onFailure("Request cancelled.");
                    });
                }
            }
        );
    }
}
