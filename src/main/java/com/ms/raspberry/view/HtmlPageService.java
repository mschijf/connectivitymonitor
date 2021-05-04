package com.ms.raspberry.view;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.ms.raspberry.entity.PingSummary;
import com.ms.raspberry.service.PingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class HtmlPageService {

    private static final Logger log = LoggerFactory.getLogger(HtmlPageService.class);

    private final PingService pingService;

    @Autowired
    public HtmlPageService(PingService pingService) {
        this.pingService = pingService;
    }

    public String getPage() {

        HashMap<String, ChartData> allCharts = new HashMap<>();
        allCharts.put("graphPerMinute", createPacketsLostPerMinuteChart());
        allCharts.put("graphPerHour", createPingTimesPerHour());

        TemplateLoader loader = new ClassPathTemplateLoader("/handlebars", ".hbs");
        Handlebars handlebars = new Handlebars(loader);
        try {
            Template template = handlebars.compile("chart-page");
            return template.apply(allCharts);
        } catch (IOException ioe) {
            log.error("Error during creating chart page", ioe);
            return "Error during creating chart page";
        }
    }

    private ChartData createPacketsLostPerMinuteChart() {
        Collection<PingSummary> summaryMinute = pingService.getPingMinuteSummary();
        return ChartData.newBuilder()
                .setLabels(getRunTime(summaryMinute))
                .addDataSet(ChartDataSet.Type.bar, "Missed packets", "#3e95cd", getTotalPacketsMissed(summaryMinute))
                .addDataSet(ChartDataSet.Type.line, "Max time (ms)", "#ff0000", getMaxTime(summaryMinute))
                .build();
    }

    private ChartData createPingTimesPerHour() {
        Collection<PingSummary> summaryHour = pingService.getPingHourSummary();
        return ChartData.newBuilder()
                .setLabels(getRunTime(summaryHour))
                .addDataSet(ChartDataSet.Type.bar, "Missed packets", "#3e95cd", getTotalPacketsMissed(summaryHour))
                .addDataSet(ChartDataSet.Type.line, "Max time (ms)", "#ff0000", getMaxTime(summaryHour))
                .build();
    }

    private ArrayList<Integer> getTotalPacketsMissed(Collection<PingSummary> summary) {
        return summary.stream()
                .map(s -> (s.getTotalPacketsTransmitted() - s.getTotalPacketsReceived()))
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
                .map(s -> getTimeString(s.getFromDate()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<String> getRunDayMonth(Collection<PingSummary> summary) {
        return summary.stream()
                .map(s -> getDayMonthString(s.getFromDate()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String getTimeString(LocalDateTime dateTime) {
        return String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
    }

    private String getDayMonthString(LocalDateTime dateTime) {
        return String.format("%02d-%02d", dateTime.getMonthValue(), dateTime.getDayOfMonth());
    }
}
