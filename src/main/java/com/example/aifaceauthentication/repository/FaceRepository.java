package com.example.aifaceauthentication.repository;

import com.example.aifaceauthentication.model.Face;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaceRepository extends JpaRepository<Face, Long> {
}
