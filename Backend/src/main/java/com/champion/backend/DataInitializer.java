package com.champion.backend;

import com.champion.backend.entity.Enemy;
import com.champion.backend.entity.GameCharacter;
import com.champion.backend.entity.User;
import com.champion.backend.repository.EnemyRepository;
import com.champion.backend.repository.GameCharacterRepository;
import com.champion.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EnemyRepository enemyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameCharacterRepository characterRepository;

    @Override
    public void run(String... args) throws Exception {
        // Seed test user if not exists
        if (userRepository.findByUsername("test").isEmpty()) {
            User testUser = new User();
            testUser.setUsername("test");
            testUser.setPasswordHash("test123"); // In production, use proper hashing (BCrypt)
            userRepository.save(testUser);
            System.out.println("DataInitializer: inserted test user 'test'");
        } else {
            System.out.println("DataInitializer: test user already exists");
        }

        // Seed Kael character if not exists
        if (characterRepository.findAll().stream().noneMatch(c -> "Kael".equals(c.getName()))) {
            GameCharacter kael = new GameCharacter();
            kael.setName("Kael");
            kael.setType("PLAYER");
            kael.setBaseHp(90);
            kael.setLightAttackName("Basic Strike");
            kael.setLightAttackDmg(10);
            kael.setHeavyAttackName("Heavy Slash");
            kael.setHeavyAttackDmg(25);
            kael.setSpecialAttackName("Ultimate Skill");
            kael.setSpecialAttackDmg(45);
            characterRepository.save(kael);
            System.out.println("DataInitializer: inserted character 'Kael'");
        } else {
            System.out.println("DataInitializer: Kael character already exists");
        }

        // Seed Mr. Van enemy if not exists
        if (enemyRepository.findByName("Mr. Van").isEmpty()) {
            Enemy mrVan = new Enemy("Mr. Van", 1, 120, 10, 20, 45);
            enemyRepository.save(mrVan);
            System.out.println("DataInitializer: inserted default enemy Mr. Van");
        } else {
            System.out.println("DataInitializer: Mr. Van already exists");
        }
    }
}
