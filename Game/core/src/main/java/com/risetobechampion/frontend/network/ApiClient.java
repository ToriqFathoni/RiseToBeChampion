package com.risetobechampion.frontend.network; // Sesuaikan jika nama package Anda berbeda

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

public class ApiClient {
    // Alamat dasar Backend Spring Boot Anda
    private static final String BASE_URL = "http://localhost:8080/api";

    // Fungsi untuk menembak API Login
    public static void login(String username, String password, Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        // Membentuk format JSON secara manual agar simpel
        String jsonBody = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

        // Merakit HTTP Request (Sama seperti yang Anda lakukan di Postman)
        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/auth/login")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .content(jsonBody)
            .build();

        // Mengirim request tersebut
        Gdx.net.sendHttpRequest(request, listener);
    }
    // Fungsi untuk menembak API Register
    public static void register(String username, String password, Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        // Membentuk format JSON secara manual
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
    // Fungsi untuk mengambil daftar karakter
    public static void getCharacters(Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + "/game/characters")
            .header("Accept", "application/json")
            .build();

        Gdx.net.sendHttpRequest(request, listener);
    }
    // Fungsi untuk memulai game baru
    public static void startGame(String userId, int charId, Net.HttpResponseListener listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        // Membentuk JSON Request Body sesuai DTO NewGameRequest Anda
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

    // Fungsi untuk mengambil setup combat (player dan enemy data) untuk stage tertentu
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
