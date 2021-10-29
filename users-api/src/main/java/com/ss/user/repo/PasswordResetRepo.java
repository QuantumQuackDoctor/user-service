package com.ss.user.repo;

import com.database.ormlibrary.user.PasswordResetEntity;
import com.database.ormlibrary.user.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetRepo extends CrudRepository<PasswordResetEntity, Long> {
    Optional<PasswordResetEntity> findByUserEmail(String email);
    void deleteByUser(UserEntity user);
    Optional<PasswordResetEntity> findByToken(UUID token);
}
