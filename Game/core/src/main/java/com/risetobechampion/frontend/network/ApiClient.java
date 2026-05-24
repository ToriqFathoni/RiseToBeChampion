package com.risetobechampion.frontend.network; // Sesuaikan jika nama package Anda berbeda

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

// tembak api backend
public class ApiClient {
    // ===== KONFIGURASI DEPLOYMENT =====
    // Ubah ke 'true' sebelum men-deploy game ke itch.io
    public static final boolean IS_PRODUCTION = false;
    
    // Ganti URL Railway ini dengan URL asli yang Anda dapatkan setelah mendeploy Backend ke Railway
    private static final String RAILWAY_URL = "https://risetobechampion-production.up.railway.app/api";
    private static final String LOCAL_URL = "http://localhost:8080/api";
    
    private static final String BASE_URL = IS_PRODUCTION ? RAILWAY_URL : LOCAL_URL;
    // ===================================
    public static void login(String username, String password, Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        String jsonBody = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/auth/login")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .content(jsonBody)
            .build();

        Gdx.net.sendHttpRequest(request, listener);
    }

    public static void register(String username, String password, Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        String jsonBody = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/auth/register")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .content(jsonBody)
            .build();

        Gdx.net.sendHttpRequest(request, listener);
    }

    public static void getCharacters(Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + "/game/characters")
            .header("Accept", "application/json")
            .build();

        Gdx.net.sendHttpRequest(request, listener);
    }

    public static void startGame(String userId, int charId, Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        String jsonBody = "{\"userId\":\"" + userId + "\", \"charId\":" + charId + "}";

        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/game/start")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .content(jsonBody)
            .build();

        Gdx.net.sendHttpRequest(request, listener);
    }

    public static void getCombatSetup(int stage, String runId, Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        String url = BASE_URL + "/combat/setup?stage=" + stage;
        if (runId != null && !runId.trim().isEmpty()) {
            url += "&runId=" + runId.trim();
        }

        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(url)
            .header("Accept", "application/json")
            .build();

        Gdx.net.sendHttpRequest(request, listener);
    }

    public static void getActiveProgress(String userId, Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + "/game/progress/" + userId)
            .header("Accept", "application/json")
            .build();

        Gdx.net.sendHttpRequest(request, listener);
    }

    public static void saveProgress(String runId, int currentStage, int deathCount, int timeElapsed, String status,
                                    int bonusMaxHp, int bonusBasicDmg, int bonusSkillDmg, int bonusMaxEnergy, boolean hasMidbossSkill,
                                    Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        String jsonBody = "{" +
            "\"runId\":\"" + runId + "\"," +
            "\"currentStage\":" + currentStage + "," +
            "\"deathCount\":" + deathCount + "," +
            "\"timeElapsed\":" + timeElapsed + "," +
            "\"status\":\"" + status + "\"," +
            "\"bonusMaxHp\":" + bonusMaxHp + "," +
            "\"bonusBasicDmg\":" + bonusBasicDmg + "," +
            "\"bonusSkillDmg\":" + bonusSkillDmg + "," +
            "\"bonusMaxEnergy\":" + bonusMaxEnergy + "," +
            "\"hasMidbossSkill\":" + hasMidbossSkill +
            "}";

        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/game/save")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .content(jsonBody)
            .build();

        Gdx.net.sendHttpRequest(request, listener);
    }
}
