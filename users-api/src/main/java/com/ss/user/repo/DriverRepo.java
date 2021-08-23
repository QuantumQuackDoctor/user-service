package com.ss.user.repo;

import com.database.ormlibrary.driver.DriverEntity;
import com.database.ormlibrary.user.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverRepo extends PagingAndSortingRepository<DriverEntity, Long> {
}
