package com.code81.onlinestore.repository;

import com.code81.onlinestore.entity.OwnerType;
import com.code81.onlinestore.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.ownerType = :ownerType and r.ownerId = :ownerId and r.revoked = false")
    void revokeAllForOwner(OwnerType ownerType, Long ownerId);
}
