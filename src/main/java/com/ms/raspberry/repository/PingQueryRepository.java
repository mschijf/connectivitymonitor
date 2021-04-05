package com.ms.raspberry.repository;

import com.ms.raspberry.entity.PingSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public interface PingQueryRepository extends CrudRepository<PingSummary, LocalDateTime> {
    static final String DAY_SUMMARY =
            "select " +
                    " date_trunc('day', run_date_time) date_time_hour," +
                    " sum(packets_transmitted) total_transmitted, sum(packets_received) total_received, " +
                    " min(mintime_millis) min_time_millis, avg(avgtime_millis) avg_time_millis, max(maxtime_millis) max_time_millis " +
                    "from cmddata.ping " +
                    "group by 1 ";

    @Query(value = DAY_SUMMARY, nativeQuery = true)
    Collection<PingSummary> getDaySummary();

    @Query(value = DAY_SUMMARY + "having date_trunc('day', run_date_time) = :day ", nativeQuery = true)
    Collection<PingSummary> getDaySummary(@Param("day") LocalDate day);

    @Query(value =
            "select " +
                    " date_trunc('hour', run_date_time) date_time_hour," +
                    " sum(packets_transmitted) total_transmitted, sum(packets_received) total_received, " +
                    " min(mintime_millis) min_time_millis, avg(avgtime_millis) avg_time_millis, max(maxtime_millis) max_time_millis " +
                    "from cmddata.ping " +
                    "group by 1 ",
            nativeQuery = true)
    Collection<PingSummary> getHourSummary();
}
