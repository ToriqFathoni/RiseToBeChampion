package com.champion.backend.controller;

import com.champion.backend.dto.AuthRequest;
import com.champion.backend.entity.User;
import com.champion.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth") // Ini adalah awalan URL API kita
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username sudah terdaftar!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());

        newUser.setPasswordHash(request.getPassword());

        // simpan ke db
        userRepository.save(newUser);
        return ResponseEntity.ok("Registrasi berhasil!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (!userOpt.isPresent()) {
            return ResponseEntity.status(404).body("Akun tidak terdaftar");
        }

        User user = userOpt.get();
        if (user.getPasswordHash().equals(request.getPassword())) {

            return ResponseEntity.ok("Login sukses! UserID: " + user.getUserId());
        }

        return ResponseEntity.status(401).body("Password salah");
    }
}