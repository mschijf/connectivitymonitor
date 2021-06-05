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
                    " coalesce(sum(packets_transmitted),0) total_transmitted, coalesce(sum(packets_received),0) total_received, " +
                    " coalesce(min(mintime_millis),0) min_time_millis, coalesce(avg(avgtime_millis),0) avg_time_millis, " +
                    " coalesce(max(maxtime_millis),0) max_time_millis " +
                    "from cmddata.ping ";

    String HOUR_SUMMARY_SELECT =
            "select " +
                    " date_trunc('hour', run_date_time) date_time," +
                    " coalesce(sum(packets_transmitted),0) total_transmitted, coalesce(sum(packets_received),0) total_received, " +
                    " coalesce(min(mintime_millis),0) min_time_millis, coalesce(avg(avgtime_millis),0) avg_time_millis, " +
                    " coalesce(max(maxtime_millis),0) max_time_millis " +
                    "from cmddata.ping ";

    String MINUTE_SUMMARY_SELECT =
            "select " +
                    " date_trunc('minute', run_date_time) date_time," +
                    " coalesce(packets_transmitted,0) total_transmitted, coalesce(packets_received,0) total_received, " +
                    " coalesce(mintime_millis,0) min_time_millis, coalesce(avgtime_millis,0) avg_time_millis, " +
                    " coalesce(maxtime_millis,0) max_time_millis " +
                    "from cmddata.ping ";

    @Query(value = DAY_SUMMARY_SELECT +
            " where run_date_time > now() - interval '28 days'" +
            " group by 1" +
            " order by date_trunc('day', run_date_time) ", nativeQuery = true)
    Collection<PingSummary> getDaySummary();

    @Query(value = HOUR_SUMMARY_SELECT +
            " where run_date_time > now() - interval '48 hours'" +
            " group by 1" +
            " order by date_trunc('hour', run_date_time) ", nativeQuery = true)
    Collection<PingSummary> getHourSummary();

    @Query(value = MINUTE_SUMMARY_SELECT +
            " where run_date_time > now() - interval '60 minutes'" +
            " order by run_date_time ", nativeQuery = true)
    Collection<PingSummary> getMinuteSummary();
}
