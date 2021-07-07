package com.ss.user.repo;

import com.database.ormlibrary.user.UserRoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepo extends CrudRepository<UserRoleEntity, Long> {
    Optional<UserRoleEntity> findByRole(String role);
}
