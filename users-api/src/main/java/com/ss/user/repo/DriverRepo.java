package com.ss.user.repo;

import com.database.ormlibrary.driver.DriverEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DriverRepo extends PagingAndSortingRepository<DriverEntity, Long> {
}
