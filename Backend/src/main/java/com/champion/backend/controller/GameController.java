package com.champion.backend.controller;

import com.champion.backend.dto.NewGameRequest;
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

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameCharacterRepository characterRepository;
    @Autowired
    private GameRunRepository runRepository;
    @Autowired
    private UserRepository userRepository;

    // API Mengambil Daftar Karakter (Lengkap dengan jurus barunya)
    @GetMapping("/characters")
    public ResponseEntity<List<GameCharacter>> getPlayerCharacters() {
        return ResponseEntity.ok(characterRepository.findByType("PLAYER"));
    }

    // API Memulai Game
    @PostMapping("/start")
    public ResponseEntity<String> startNewGame(@RequestBody NewGameRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        Optional<GameCharacter> charOpt = characterRepository.findById(request.getCharId());

        if (userOpt.isEmpty() || charOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User atau Karakter tidak ditemukan!");
        }

        Optional<GameRun> existingRun = runRepository.findByUser_UserIdAndStatus(request.getUserId(), "ONGOING");
        if (existingRun.isPresent()) {
            GameRun oldRun = existingRun.get();
            oldRun.setStatus("GAME_OVER");
            runRepository.save(oldRun);
        }

        GameRun newRun = new GameRun();
        newRun.setUser(userOpt.get());
        newRun.setCharacter(charOpt.get());
        runRepository.save(newRun);

        return ResponseEntity.ok("Game Baru Dimulai! RunID: " + newRun.getRunId());
    }
    // 3. Endpoint untuk menyimpan progress (Save Game / Update Buff)
    // POST http://localhost:8080/api/game/save
    @PostMapping("/save")
    public ResponseEntity<String> saveProgress(@RequestBody com.champion.backend.dto.SaveProgressRequest request) {
        // Cari sesi game (run) berdasarkan ID yang dikirim
        Optional<GameRun> runOpt = runRepository.findById(request.getRunId());

        if (runOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Sesi permainan (Run ID) tidak ditemukan!");
        }

        GameRun currentRun = runOpt.get();

        // Update progress umum
        currentRun.setCurrentStage(request.getCurrentStage());
        currentRun.setDeathCount(request.getDeathCount());
        currentRun.setTimeElapsed(request.getTimeElapsed());
        currentRun.setStatus(request.getStatus());

        // Update status Buff
        currentRun.setBonusMaxHp(request.getBonusMaxHp());
        currentRun.setBonusBasicDmg(request.getBonusBasicDmg());
        currentRun.setBonusSkillDmg(request.getBonusSkillDmg());
        currentRun.setBonusMaxEnergy(request.getBonusMaxEnergy());
        currentRun.setHasMidbossSkill(request.isHasMidbossSkill());

        // Simpan pembaruan ke database
        runRepository.save(currentRun);

        return ResponseEntity.ok("Progress dan Buff berhasil disimpan!");
    }
}