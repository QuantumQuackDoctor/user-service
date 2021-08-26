package com.ss.user.repo;

import com.database.ormlibrary.order.OrderEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepo extends CrudRepository<OrderEntity, Long> {
}
