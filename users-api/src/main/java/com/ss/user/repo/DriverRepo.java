package com.ss.user.repo;

import com.database.ormlibrary.driver.DriverEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DriverRepo extends PagingAndSortingRepository<DriverEntity, Long> {
    Optional<DriverEntity> findByUserEmail(String email);

}
