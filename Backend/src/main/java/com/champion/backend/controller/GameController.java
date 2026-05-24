package com.champion.backend.controller;

import com.champion.backend.dto.NewGameRequest;
import com.champion.backend.dto.GameProgressResponse;
import com.champion.backend.entity.GameCharacter;
import com.champion.backend.entity.GameRun;
import com.champion.backend.entity.User;
import com.champion.backend.repository.GameCharacterRepository;
import com.champion.backend.repository.GameRunRepository;
import com.champion.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameCharacterRepository characterRepository;
    @Autowired
    private GameRunRepository runRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/characters")
    public ResponseEntity<List<GameCharacter>> getPlayerCharacters() {
        ensurePlayerCharacter("Kael", 90, "Basic Strike", 12, "Heavy Slash", 28, "Ultimate Skill", 50);
        ensurePlayerCharacter("Ryu", 105, "Hadoken", 9, "Shun Goku Satsu", 22, "Ryu's Fury", 40);

        List<GameCharacter> players = characterRepository.findAll().stream()
            .filter(c -> c.getType() != null)
            .filter(c -> "PLAYER".equalsIgnoreCase(c.getType().trim()))
            .collect(Collectors.toMap(
                c -> c.getName() == null ? "" : c.getName().trim().toLowerCase(),
                c -> c,
                (first, second) -> first,
                java.util.LinkedHashMap::new
            ))
            .values().stream()
            .filter(c -> c.getName() != null && !c.getName().isBlank())
            .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(players);
    }

    private void ensurePlayerCharacter(
        String name,
        int baseHp,
        String lightAttackName,
        int lightAttackDmg,
        String heavyAttackName,
        int heavyAttackDmg,
        String specialAttackName,
        int specialAttackDmg
    ) {
        Optional<GameCharacter> existing = characterRepository.findAll().stream()
            .filter(c -> c.getName() != null)
            .filter(c -> name.equalsIgnoreCase(c.getName().trim()))
            .findFirst();

        GameCharacter character = existing.orElseGet(GameCharacter::new);
        character.setName(name);
        character.setType("PLAYER");
        character.setBaseHp(baseHp);
        character.setLightAttackName(lightAttackName);
        character.setLightAttackDmg(lightAttackDmg);
        character.setHeavyAttackName(heavyAttackName);
        character.setHeavyAttackDmg(heavyAttackDmg);
        character.setSpecialAttackName(specialAttackName);
        character.setSpecialAttackDmg(specialAttackDmg);
        // simpan ke db
        characterRepository.save(character);
    }

    @PostMapping("/start")
    public ResponseEntity<String> startNewGame(@RequestBody NewGameRequest request) {
        // ambil data dari db
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        // ambil data dari db
        Optional<GameCharacter> charOpt = characterRepository.findById(request.getCharId());

        if (userOpt.isEmpty() || charOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User atau Karakter tidak ditemukan!");
        }

        Optional<GameRun> existingRun = runRepository.findByUser_UserIdAndStatus(request.getUserId(), "ONGOING");
        if (existingRun.isPresent()) {
            GameRun oldRun = existingRun.get();
            oldRun.setStatus("GAME_OVER");
            // simpan ke db
            runRepository.save(oldRun);
        }

        GameRun newRun = new GameRun();
        newRun.setUser(userOpt.get());
        newRun.setCharacter(charOpt.get());
        // simpan ke db
        runRepository.save(newRun);

        return ResponseEntity.ok("Game Baru Dimulai! RunID: " + newRun.getRunId());
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveProgress(@RequestBody com.champion.backend.dto.SaveProgressRequest request) {

        // ambil data dari db
        Optional<GameRun> runOpt = runRepository.findById(request.getRunId());

        if (runOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Sesi permainan (Run ID) tidak ditemukan!");
        }

        GameRun currentRun = runOpt.get();

        currentRun.setCurrentStage(request.getCurrentStage());
        currentRun.setDeathCount(request.getDeathCount());
        currentRun.setTimeElapsed(request.getTimeElapsed());
        currentRun.setStatus(request.getStatus());

        currentRun.setBonusMaxHp(request.getBonusMaxHp());
        currentRun.setBonusBasicDmg(request.getBonusBasicDmg());
        currentRun.setBonusSkillDmg(request.getBonusSkillDmg());
        currentRun.setBonusMaxEnergy(request.getBonusMaxEnergy());
        currentRun.setHasMidbossSkill(request.isHasMidbossSkill());

        // simpan ke db
        runRepository.save(currentRun);

        return ResponseEntity.ok("Progress dan Buff berhasil disimpan!");
    }

    @GetMapping("/progress/{userId}")
    public ResponseEntity<GameProgressResponse> getActiveProgress(@PathVariable java.util.UUID userId) {
        Optional<GameRun> runOpt = runRepository.findByUser_UserIdAndStatus(userId, "ONGOING");

        if (runOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        GameRun run = runOpt.get();
        GameProgressResponse response = new GameProgressResponse(
            run.getRunId(),
            run.getCurrentStage(),
            run.getStatus(),
            run.getBonusMaxHp(),
            run.getBonusBasicDmg(),
            run.getBonusSkillDmg(),
            run.getBonusMaxEnergy(),
            run.isHasMidbossSkill(),
            run.getDeathCount(),
            run.getTimeElapsed()
        );

        return ResponseEntity.ok(response);
    }
}