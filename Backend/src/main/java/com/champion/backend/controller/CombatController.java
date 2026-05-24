package com.champion.backend.controller;

import com.champion.backend.dto.CombatSetupResponse;
import com.champion.backend.entity.Enemy;
import com.champion.backend.entity.GameCharacter;
import com.champion.backend.entity.GameRun;
import com.champion.backend.entity.Player;
import com.champion.backend.repository.EnemyRepository;
import com.champion.backend.repository.GameRunRepository;
import com.champion.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/combat")
public class CombatController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private EnemyRepository enemyRepository;

    @Autowired
    private GameRunRepository gameRunRepository;

    /**
     * GET /api/combat/setup?stage=1
     * Returns initial combat data for the player and the enemy for a specific stage.
     *
     * @param stage The stage ID
     * @return CombatSetupResponse containing player and enemy combat data
     */
    @GetMapping("/setup")
    public ResponseEntity<CombatSetupResponse> getCombatSetup(@RequestParam int stage, @RequestParam(required = false) String runId) {
        try {
            CombatSetupResponse.PlayerCombatData playerData;
            if (runId != null && !runId.isBlank()) {
                UUID parsedRunId = UUID.fromString(runId);
                Optional<GameRun> runOpt = gameRunRepository.findById(parsedRunId);
                if (runOpt.isPresent() && runOpt.get().getCharacter() != null) {
                    GameCharacter selected = runOpt.get().getCharacter();
                    int basic = selected.getLightAttackDmg();
                    int heavy = selected.getHeavyAttackDmg();
                    int skill = selected.getSpecialAttackDmg();
                    playerData = new CombatSetupResponse.PlayerCombatData(
                        selected.getCharId(),
                        selected.getName(),
                        selected.getBaseHp(),
                        100,
                        basic,
                        basic,
                        heavy,
                        skill
                    );
                } else {
                    playerData = defaultPlayerData();
                }
            } else {
                playerData = defaultPlayerData();
            }

            Enemy enemy = resolveEnemyForStage(stage);

            CombatSetupResponse.EnemyCombatData enemyData = new CombatSetupResponse.EnemyCombatData(
                enemy.getId(),
                enemy.getName(),
                enemy.getStageId(),
                enemy.getMaxHp(),
                enemy.getBasicAttackDamage(),
                enemy.getHeavyAttackDamage(),
                enemy.getSkillDamage()
            );

            CombatSetupResponse response = new CombatSetupResponse(playerData, enemyData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getCombatSetup for stage " + stage + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to setup combat for stage " + stage, e);
        }
    }

    private Enemy defaultEnemyForStage(int stage) {
        if (stage == 1) {
            return new Enemy("Mr. Van", 1, 100, 15, 30, 50);
        }
        if (stage == 2) {
            return new Enemy("Chen Long", 2, 150, 20, 40, 65);
        }
        if (stage == 3) {
            return new Enemy("Kagetsu", 3, 220, 28, 55, 90);
        }
        if (stage == 4) {
            return new Enemy("Joe", 4, 300, 35, 70, 120);
        }
        return new Enemy("Mr. Van Stage " + stage, stage, 100 + (stage * 10), 15 + (stage * 5), 30 + (stage * 5), 50 + (stage * 5));
    }

    private CombatSetupResponse.PlayerCombatData defaultPlayerData() {
        Player player = playerRepository.findByName("Kael The Phantom")
            .orElseGet(() -> playerRepository.save(new Player("Kael The Phantom", 90, 100, 10)));
        return new CombatSetupResponse.PlayerCombatData(
            player.getId(),
            player.getName(),
            player.getMaxHp(),
            player.getMaxEnergy(),
            player.getBaseDamage(),
            player.getBaseDamage(),
            25,
            45
        );
    }

    @GetMapping("/enemy/{stageId}")
    public ResponseEntity<Enemy> getEnemyByStage(@PathVariable int stageId) {
        Enemy enemy = resolveEnemyForStage(stageId);
        if (enemy == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(enemy);
    }

    private Enemy resolveEnemyForStage(int stage) {
        java.util.List<Enemy> enemies = enemyRepository.findAllByStageId(stage);
        if (enemies == null || enemies.isEmpty()) {
            return enemyRepository.save(defaultEnemyForStage(stage));
        }

        String preferredName = preferredEnemyNameForStage(stage);
        if (preferredName != null) {
            for (Enemy enemy : enemies) {
                if (preferredName.equals(enemy.getName())) {
                    return enemy;
                }
            }
        }

        return enemies.get(0);
    }

    private String preferredEnemyNameForStage(int stage) {
        if (stage == 1) {
            return "Mr. Van";
        }
        if (stage == 2) {
            return "Chen Long";
        }
        if (stage == 3) {
            return "Kagetsu";
        }
        if (stage == 4) {
            return "Joe";
        }
        return null;
    }
}
