package com.ms.raspberry.repository;

import com.ms.raspberry.entity.PingData;
import org.springframework.data.repository.CrudRepository;

public interface PingRepository extends CrudRepository<PingData, Integer> {
}
