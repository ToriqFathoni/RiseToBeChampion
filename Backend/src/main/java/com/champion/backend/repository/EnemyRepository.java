package com.champion.backend.repository;

import com.champion.backend.entity.Enemy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EnemyRepository extends JpaRepository<Enemy, Long> {
    Optional<Enemy> findByStageId(int stageId);
    List<Enemy> findAllByStageId(int stageId);
    Optional<Enemy> findByName(String name);
}
