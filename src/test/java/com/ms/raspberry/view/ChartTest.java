package com.ms.raspberry.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class ChartTest {

    private Chart chart;

    @BeforeEach
    public void init() {
        ArrayList<String> labels = new ArrayList(Arrays.asList("l1", "l2", "l3"));
        ArrayList<Integer> data = new ArrayList(Arrays.asList(1,2,3));
        chart = Chart.newBuilder()
                .setLabels(labels)
                .setDataSet("set1", "#aabbcc", data)
                .setDataSet("set2", "#xxyyzz", data)
                .build();
    }

    @Test
    public void testJs() {
        System.out.println(chart.getHtml());
        System.out.println(chart.getBarChartJs());
    }

}