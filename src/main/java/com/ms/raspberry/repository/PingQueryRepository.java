package com.ms.raspberry.repository;

import com.ms.raspberry.entity.PingSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public interface PingQueryRepository extends CrudRepository<PingSummary, LocalDateTime> {
    static final String HOUR_SUMMARY_SELECT =
            "select " +
                    " date_trunc('hour', run_date_time) date_time_hour," +
                    " sum(packets_transmitted) total_transmitted, sum(packets_received) total_received, " +
                    " min(mintime_millis) min_time_millis, avg(avgtime_millis) avg_time_millis, max(maxtime_millis) max_time_millis " +
                    "from cmddata.ping ";

    static final String DAY_SUMMARY_SELECT =
            "select " +
                    " date_trunc('day', run_date_time) date_time_hour," +
                    " sum(packets_transmitted) total_transmitted, sum(packets_received) total_received, " +
                    " min(mintime_millis) min_time_millis, avg(avgtime_millis) avg_time_millis, max(maxtime_millis) max_time_millis " +
                    "from cmddata.ping ";

    @Query(value = HOUR_SUMMARY_SELECT +
                    " where run_date_time > now() - interval '168 hours'" +
                    " group by 1" +
                    " order by date_trunc('hour', run_date_time) ", nativeQuery = true)
    Collection<PingSummary> getHourSummary();

    @Query(value = HOUR_SUMMARY_SELECT +
            " group by 1" +
            " having date_trunc('day', run_date_time) = :day " +
            " order by date_trunc('hour', run_date_time) ", nativeQuery = true)
    Collection<PingSummary> getHourSummary(@Param("day") LocalDate day);

    @Query(value = DAY_SUMMARY_SELECT +
            " where run_date_time > now() - interval '28 days'" +
            " group by 1" +
            " order by date_trunc('day', run_date_time) ", nativeQuery = true)
    Collection<PingSummary> getDaySummary();

}
