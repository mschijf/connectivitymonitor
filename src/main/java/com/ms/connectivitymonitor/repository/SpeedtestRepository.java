package com.ms.connectivitymonitor.repository;

import com.ms.connectivitymonitor.entity.SpeedtestData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface SpeedtestRepository extends CrudRepository<SpeedtestData, Integer> {

    @Query(value =
            "select * from cmddata.speedtest " +
            " where run_date_time > now() - interval '48 hours' " +
            " order by run_date_time", nativeQuery = true)
    Collection<SpeedtestData> getHourResults();

}
