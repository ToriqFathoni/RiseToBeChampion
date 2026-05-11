package com.champion.backend.repository;

import com.champion.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

// JpaRepository sudah otomatis menyediakan fitur Save, FindById, Delete, dll.
public interface UserRepository extends JpaRepository<User, UUID> {
    // Kita tambahkan fungsi khusus untuk mencari user berdasarkan username
    Optional<User> findByUsername(String username);
}