package com.funflare.funflare.repository;

import com.funflare.funflare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.relation.Role;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
boolean existsByUsername(String username);
boolean existsByEmail(String email);
Long countByRole(User.Role role);
Optional<User> findByUsername(String username);
Optional<User> findById(Long user_id);
Optional<User> findByEmail(String email);
Optional<User> findByVerificationToken(String verification_token);
//Optional<User> findByVerified(Boolean verified);
}
