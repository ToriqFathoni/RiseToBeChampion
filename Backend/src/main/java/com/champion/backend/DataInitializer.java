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

import java.util.Optional;

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

        if (userRepository.findByUsername("test").isEmpty()) {
            User testUser = new User();
            testUser.setUsername("test");
            testUser.setPasswordHash("test123"); // In production, use proper hashing (BCrypt)
            // simpan ke db
            userRepository.save(testUser);
            System.out.println("DataInitializer: inserted test user 'test'");
        } else {
            System.out.println("DataInitializer: test user already exists");
        }

        upsertPlayerCharacter(
            "Kael",
            90,
            "Basic Strike",
            12,
            "Heavy Slash",
            28,
            "Ultimate Skill",
            50
        );

        upsertPlayerCharacter(
            "Ryu",
            105,
            "Hadoken",
            9,
            "Shun Goku Satsu",
            22,
            "Ryu's Fury",
            40
        );

        boolean mrVanExists = enemyRepository.findByName("Mr. Van").isPresent();
        Enemy mrVan = enemyRepository.findByName("Mr. Van").orElseGet(() -> new Enemy("Mr. Van", 1, 100, 15, 30, 50));
        mrVan.setStageId(1);
        mrVan.setMaxHp(100);
        mrVan.setBasicAttackDamage(15);
        mrVan.setHeavyAttackDamage(30);
        mrVan.setSkillDamage(50);
        // simpan ke db
        enemyRepository.save(mrVan);
        if (!mrVanExists) {
            System.out.println("DataInitializer: inserted default enemy Mr. Van");
        } else {
            System.out.println("DataInitializer: updated default enemy Mr. Van");
        }

        boolean chenLongExists = enemyRepository.findByName("Chen Long").isPresent();
        Enemy chenLong = enemyRepository.findByName("Chen Long").orElseGet(() -> new Enemy("Chen Long", 2, 150, 20, 40, 65));
        chenLong.setStageId(2);
        chenLong.setMaxHp(150);
        chenLong.setBasicAttackDamage(20);
        chenLong.setHeavyAttackDamage(40);
        chenLong.setSkillDamage(65);
        // simpan ke db
        enemyRepository.save(chenLong);
        if (!chenLongExists) {
            System.out.println("DataInitializer: inserted default enemy Chen Long");
        } else {
            System.out.println("DataInitializer: updated default enemy Chen Long");
        }

        boolean kagetsuExists = enemyRepository.findByName("Kagetsu").isPresent();
        Enemy kagetsu = enemyRepository.findByName("Kagetsu").orElseGet(() -> new Enemy("Kagetsu", 3, 220, 28, 55, 90));
        kagetsu.setStageId(3);
        kagetsu.setMaxHp(220);
        kagetsu.setBasicAttackDamage(28);
        kagetsu.setHeavyAttackDamage(55);
        kagetsu.setSkillDamage(90);
        // simpan ke db
        enemyRepository.save(kagetsu);
        if (!kagetsuExists) {
            System.out.println("DataInitializer: inserted default enemy Kagetsu");
        } else {
            System.out.println("DataInitializer: updated default enemy Kagetsu");
        }

        boolean joeExists = enemyRepository.findByName("Joe").isPresent();
        Enemy joe = enemyRepository.findByName("Joe").orElseGet(() -> new Enemy("Joe", 4, 300, 35, 70, 120));
        joe.setStageId(4);
        joe.setMaxHp(300);
        joe.setBasicAttackDamage(35);
        joe.setHeavyAttackDamage(70);
        joe.setSkillDamage(120);
        // simpan ke db
        enemyRepository.save(joe);
        if (!joeExists) {
            System.out.println("DataInitializer: inserted default enemy Joe");
        } else {
            System.out.println("DataInitializer: updated default enemy Joe");
        }
    }

    private void upsertPlayerCharacter(
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

        if (existing.isPresent()) {
            System.out.println("DataInitializer: updated character '" + name + "'");
        } else {
            System.out.println("DataInitializer: inserted character '" + name + "'");
        }
    }
}
