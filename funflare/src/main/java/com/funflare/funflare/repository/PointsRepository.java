// src/main/java/com/funflare/funflare/repository/PointsRepository.java
package com.funflare.funflare.repository;

import com.funflare.funflare.model.Points;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {
}