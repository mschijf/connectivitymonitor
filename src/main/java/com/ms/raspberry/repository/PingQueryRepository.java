package com.ms.raspberry.repository;

import com.ms.raspberry.entity.PingSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Collection;

public interface PingQueryRepository extends CrudRepository<PingSummary, LocalDateTime> {
    String DAY_SUMMARY_SELECT =
            "select " +
                    " date_trunc('day', run_date_time) date_time," +
                    " sum(packets_transmitted) total_transmitted, sum(packets_received) total_received, " +
                    " min(mintime_millis) min_time_millis, avg(avgtime_millis) avg_time_millis, max(maxtime_millis) max_time_millis " +
                    "from cmddata.ping ";

    String HOUR_SUMMARY_SELECT =
            "select " +
                    " date_trunc('hour', run_date_time) date_time," +
                    " sum(packets_transmitted) total_transmitted, sum(packets_received) total_received, " +
                    " min(mintime_millis) min_time_millis, avg(avgtime_millis) avg_time_millis, max(maxtime_millis) max_time_millis " +
                    "from cmddata.ping ";

    String MINUTE_SUMMARY_SELECT =
            "select " +
                    " date_trunc('minute', run_date_time) date_time," +
                    " packets_transmitted total_transmitted, packets_received total_received, " +
                    " mintime_millis min_time_millis, avgtime_millis avg_time_millis, maxtime_millis max_time_millis " +
                    "from cmddata.ping ";

    @Query(value = DAY_SUMMARY_SELECT +
            " where run_date_time > now() - interval '28 days'" +
            " group by 1" +
            " order by date_trunc('day', run_date_time) ", nativeQuery = true)
    Collection<PingSummary> getDaySummary();

    @Query(value = HOUR_SUMMARY_SELECT +
            " where run_date_time > now() - interval '168 hours'" +
            " group by 1" +
            " order by date_trunc('hour', run_date_time) ", nativeQuery = true)
    Collection<PingSummary> getHourSummary();

    @Query(value = MINUTE_SUMMARY_SELECT +
            " where run_date_time > now() - interval '30 minutes'" +
            " order by run_date_time ", nativeQuery = true)
    Collection<PingSummary> getMinuteSummary();
}
