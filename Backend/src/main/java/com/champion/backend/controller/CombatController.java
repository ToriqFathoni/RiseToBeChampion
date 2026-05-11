package com.champion.backend.controller;

import com.champion.backend.dto.CombatSetupResponse;
import com.champion.backend.entity.Enemy;
import com.champion.backend.entity.Player;
import com.champion.backend.repository.EnemyRepository;
import com.champion.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/combat")
public class CombatController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private EnemyRepository enemyRepository;

    /**
     * GET /api/combat/setup?stage=1
     * Returns initial combat data for the player and the enemy for a specific stage.
     *
     * @param stage The stage ID
     * @return CombatSetupResponse containing player and enemy combat data
     */
    @GetMapping("/setup")
    public ResponseEntity<CombatSetupResponse> getCombatSetup(@RequestParam int stage) {
        Player player = playerRepository.findByName("Kael The Phantom")
            .orElseGet(() -> playerRepository.save(new Player("Kael The Phantom", 90, 100, 10)));

        Enemy enemy = enemyRepository.findByStageId(stage)
            .orElseGet(() -> enemyRepository.save(defaultEnemyForStage(stage)));

        // Create response DTO
        CombatSetupResponse.PlayerCombatData playerData = new CombatSetupResponse.PlayerCombatData(
            player.getId(),
            player.getName(),
            player.getMaxHp(),
            player.getMaxEnergy(),
            player.getBaseDamage()
        );

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
    }

    private Enemy defaultEnemyForStage(int stage) {
        if (stage == 1) {
            return new Enemy("Mr. Van", 1, 120, 25, 35, 45);
        }
        return new Enemy("Mr. Van Stage " + stage, stage, 120 + (stage * 10), 25 + (stage * 2), 35 + (stage * 3), 45 + (stage * 4));
    }

    @GetMapping("/enemy/{stageId}")
    public ResponseEntity<Enemy> getEnemyByStage(@PathVariable int stageId) {
        Optional<Enemy> enemyOpt = enemyRepository.findByStageId(stageId);
        return enemyOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
