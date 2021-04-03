package com.ms.raspberry.repository;

import com.ms.raspberry.entity.SpeedTestData;
import org.springframework.data.repository.CrudRepository;

public interface SpeedTestRepository extends CrudRepository<SpeedTestData, Integer> {
}
