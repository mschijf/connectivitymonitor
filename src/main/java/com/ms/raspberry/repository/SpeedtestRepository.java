package com.ms.raspberry.repository;

import com.ms.raspberry.entity.SpeedtestData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface SpeedtestRepository extends CrudRepository<SpeedtestData, Integer> {

    @Query(value =
            "select id, run_date_time , coalesce(latency_millis,0) latency_millis, coalesce(jitter_millis,0) jitter_millis, " +
                    " coalesce(downloadspeed_bytes,0) downloadspeed_bytes , coalesce(uploadspeed_bytes,0) uploadspeed_bytes, " +
                    " coalesce(packet_loss_perc,0) packet_loss_perc, all_output " +
            " from cmddata.speedtest s " +
            " where run_date_time > now() - interval '48 hours' " +
            " order by run_date_time", nativeQuery = true)
    Collection<SpeedtestData> getHourResults();


}
