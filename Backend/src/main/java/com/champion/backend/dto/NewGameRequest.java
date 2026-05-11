package com.champion.backend.dto;

import java.util.UUID;

public class NewGameRequest {
    private UUID userId;
    private Long charId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Long getCharId() {
        return charId;
    }

    public void setCharId(Long charId) {
        this.charId = charId;
    }

    // Ingat untuk Generate Getter dan Setter di sini!
}