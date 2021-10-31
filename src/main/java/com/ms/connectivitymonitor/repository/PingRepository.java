package com.ms.connectivitymonitor.repository;

import com.ms.connectivitymonitor.entity.PingData;
import org.springframework.data.repository.CrudRepository;

public interface PingRepository extends CrudRepository<PingData, Integer> {
}
