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

    // Endpoint untuk Register: POST http://localhost:8080/api/auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        // 1. Cek apakah username sudah ada di database
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username sudah terdaftar!");
        }

        // 2. Buat User baru dan simpan ke database
        User newUser = new User();
        newUser.setUsername(request.getUsername());

        // Catatan: Untuk proyek Oprec yang sesungguhnya, password sebaiknya di-hash (BCrypt).
        // Untuk saat ini kita simpan langsung agar mudah dites.
        newUser.setPasswordHash(request.getPassword());

        userRepository.save(newUser);
        return ResponseEntity.ok("Registrasi berhasil!");
    }

    // Endpoint untuk Login: POST http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        // 1. Cari user berdasarkan username
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        // 2. Jika user ditemukan, cocokkan passwordnya
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPasswordHash().equals(request.getPassword())) {
                // Berhasil login, kirimkan User ID untuk dipakai di game LibGDX
                return ResponseEntity.ok("Login sukses! UserID: " + user.getUserId());
            }
        }

        // Jika gagal
        return ResponseEntity.status(401).body("Username atau password salah!");
    }
}