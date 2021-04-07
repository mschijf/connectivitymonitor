package com.ms.raspberry.view;

import com.ms.raspberry.entity.PingSummary;
import com.ms.raspberry.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class HtmlPageService {
    private static final String pageTemplate =
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.min.js\"></script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<a href=\"/swagger-ui/\">GA NAAR SWAGGER</a>\n" +
                    "<table>\n" +
                    "    <thead>\n" +
                    "        <tr>\n" +
                    "            <th colspan=\"2\">Ping statistics</th>\n" +
                    "        </tr>\n" +
                    "    </thead>\n" +
                    "    <tbody>\n" +
                    "        <tr>\n" +
                    "            <td>%s</td>\n" +
                    "            <td>%s</td>\n" +
                    "        <tr>\n" +
                    "            <td>%s</td>\n" +
                    "            <td></td>\n" +
                    "        </tr>\n" +
                    "    </tbody>\n" +
                    "</table>\n" +
                    "%s" +
                    "%s" +
                    "%s" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

    @Autowired
    private PingService pingService;

    public String getPage() {

        Collection<PingSummary> summaryDay = pingService.getPingDaySummary();
        Collection<PingSummary> summaryHour = pingService.getPingHourSummary();
        Collection<PingSummary> summaryMinute = pingService.getPingMinuteSummary();

        Chart chart = Chart.newBuilder()
                .setType(Chart.Type.BAR)
                .setLabels(getRunDayMonth(summaryDay))
                .addDataSet("Transmitted", "#3e95cd", getTotalTransmitted(summaryDay))
                .addDataSet("Received", "#0000ff", getTotalReceived(summaryDay))
                .build();

        Chart chart2 = Chart.newBuilder()
                .setType(Chart.Type.LINE)
                .setLabels(getRunTime(summaryHour))
                .addDataSet("Min time (ms)", "#00ff00", getMinTime(summaryHour))
                .addDataSet("Avg time (ms)", "#0000ff", getAvgTime(summaryHour))
                .addDataSet("Max time (ms)", "#ff0000", getMaxTime(summaryHour))
                .build();

        Chart chart3 = Chart.newBuilder()
                .setType(Chart.Type.LINE)
                .setLabels(getRunTime(summaryMinute))
                .addDataSet("Min time (ms)", "#00ff00", getMinTime(summaryMinute))
                .addDataSet("Avg time (ms)", "#0000ff", getAvgTime(summaryMinute))
                .addDataSet("Max time (ms)", "#ff0000", getMaxTime(summaryMinute))
                .build();

        return String.format(pageTemplate,
                chart.getHtml(),
                chart2.getHtml(),
                chart3.getHtml(),
                chart.getJs(),
                chart2.getJs(),
                chart3.getJs()
                );
    }


    private ArrayList<Integer> getTotalReceived(Collection<PingSummary> summary) {
        return summary.stream()
                .map(PingSummary::getTotalPacketsReceived)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    private ArrayList<Integer> getTotalTransmitted(Collection<PingSummary> summary) {
        return summary.stream()
                .map(PingSummary::getTotalPacketsTransmitted)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    private ArrayList<Integer> getMinTime(Collection<PingSummary> summary) {
        return summary.stream()
                .map(PingSummary::getMinTimeMillis)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    private ArrayList<Integer> getMaxTime(Collection<PingSummary> summary) {
        return summary.stream()
                .map(PingSummary::getMaxTimeMillis)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    private ArrayList<Integer> getAvgTime(Collection<PingSummary> summary) {
        return summary.stream()
                .map(PingSummary::getAvgTimeMillis)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    private ArrayList<String> getRunTime(Collection<PingSummary> summary) {
        return summary.stream()
                .map(s->getTimeString(s.getFromDate()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    private ArrayList<String> getRunDayMonth(Collection<PingSummary> summary) {
        return summary.stream()
                .map(s->getDayMonthString(s.getFromDate()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String getTimeString(LocalDateTime dateTime) {
        return String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
    }
    private String getDayMonthString(LocalDateTime dateTime) {
        return String.format("%02d-%02d", dateTime.getMonthValue(), dateTime.getDayOfMonth());
    }


}
