package com.champion.backend.repository;

import com.champion.backend.entity.Enemy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnemyRepository extends JpaRepository<Enemy, Long> {
    Optional<Enemy> findByStageId(int stageId);
    Optional<Enemy> findByName(String name);
}
