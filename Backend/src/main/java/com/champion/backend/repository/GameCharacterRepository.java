package com.champion.backend.repository;

import com.champion.backend.entity.GameCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameCharacterRepository extends JpaRepository<GameCharacter, Long> {
    List<GameCharacter> findByType(String type);
}