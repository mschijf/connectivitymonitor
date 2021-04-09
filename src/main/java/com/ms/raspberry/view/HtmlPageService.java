package com.ms.raspberry.view;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.ms.raspberry.entity.PingSummary;
import com.ms.raspberry.service.PingService;
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

    @Autowired
    private PingService pingService;

    public String getPage() {

        Collection<PingSummary> summaryDay = pingService.getPingDaySummary();
        Collection<PingSummary> summaryHour = pingService.getPingHourSummary();
        Collection<PingSummary> summaryMinute = pingService.getPingMinuteSummary();

        HashMap<String, ChartData> allCharts = new HashMap<>();
        allCharts.put("packetsLostPerDay",
                ChartData.newBuilder()
                        .setType(ChartData.Type.bar)
                        .setLabels(getRunDayMonth(summaryDay))
                        .addDataSet("Missed packets", "#3e95cd", getTotalPacketsMissed(summaryDay))
                        .build()
        );

        allCharts.put("pingTimesPerHour",
                ChartData.newBuilder()
                    .setType(ChartData.Type.line)
                    .setLabels(getRunTime(summaryHour))
                    .addDataSet("Min time (ms)", "#00ff00", getMinTime(summaryHour))
                    .addDataSet("Avg time (ms)", "#0000ff", getAvgTime(summaryHour))
                    .addDataSet("Max time (ms)", "#ff0000", getMaxTime(summaryHour))
                    .build()
        );

        allCharts.put("pingTimesPerMinute",
                ChartData.newBuilder()
                    .setType(ChartData.Type.line)
                    .setLabels(getRunTime(summaryMinute))
                    .addDataSet("Min time (ms)", "#00ff00", getMinTime(summaryMinute))
                    .addDataSet("Avg time (ms)", "#0000ff", getAvgTime(summaryMinute))
                    .addDataSet("Max time (ms)", "#ff0000", getMaxTime(summaryMinute))
                    .build()
        );


        TemplateLoader loader = new ClassPathTemplateLoader("/handlebars", ".hbs");
        Handlebars handlebars = new Handlebars(loader);
        try {
            Template template = handlebars.compile("chart");
            return template.apply(allCharts);
        } catch (IOException ioe) {
            System.out.println("foutje!" + ioe);
            return "foutje!" + ioe;
        }
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
    private ArrayList<Integer> getTotalPacketsMissed(Collection<PingSummary> summary) {
        return summary.stream()
                .map(s->(s.getTotalPacketsTransmitted() - s.getTotalPacketsReceived()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    private ArrayList<Double> getPercentagePacketsMissed(Collection<PingSummary> summary) {
        return summary.stream()
                .map(s->(100.0 * (s.getTotalPacketsTransmitted() - s.getTotalPacketsReceived())/s.getTotalPacketsTransmitted()))
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
