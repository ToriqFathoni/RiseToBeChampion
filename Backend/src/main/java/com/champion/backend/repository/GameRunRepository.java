package com.champion.backend.repository;

import com.champion.backend.entity.GameRun;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface GameRunRepository extends JpaRepository<GameRun, UUID> {
    Optional<GameRun> findByUser_UserIdAndStatus(UUID userId, String status);
}