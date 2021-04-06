package com.ms.raspberry.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Chart {

    private final String identifier;
    private final ArrayList<String> labels;
    private final ArrayList<ChartDataSet> allData;

    private static final String dataSetTemplate =
            "{\n" +
            "   label: \"%s\",\n" +
            "   backgroundColor: [%s],\n" +
            "   data: [%s], \n" +
            "   fill: %s" +
            "}\n";

    private static final String jsChartTemplate =
            "<script>\n" +
            "new Chart(document.getElementById(\"%s\"), {\n" +
            "    type: 'bar',\n" +
            "    data: {\n" +
            "      labels: [%s],\n" +
            "      datasets: [%s]\n" +
            "    },\n" +
            "    options: {\n" +
            "        scales: {\n" +
            "            yAxes: [{\n" +
            "                ticks: {\n" +
            "                   beginAtZero: true,\n" +
            "                   suggestedMax: %s\n" +
            "                }\n" +
            "            }]\n" +
            "        }\n" +
            "    }\n" +
            "});\n" +
            "</script>\n";



    public Chart(ArrayList<String> labels, ArrayList<ChartDataSet> allData) {
        this.identifier = generateUniqueIdentifier();
        this.labels = labels;
        this.allData = allData;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getHtml() {
        return "<canvas id=\"" + identifier + "\" width=\"800\" height=\"450\"></canvas>\n";
    }

    public String getJs() {
        return String.format(jsChartTemplate,
                identifier, listToString(labels),
                getAllDataSets( allData),
                35);
    }

    private String getAllDataSets(ArrayList<ChartDataSet> allData) {
        return allData.stream()
                .map(item->getDataSet(item.getLabel(), item.getColor(), item.getList(), true))
                .collect(Collectors.joining(","));
    }

    private String getDataSet(String label, String color, List<Integer> data, boolean fill) {
        return String.format(dataSetTemplate,
                label,
                getColorList(data.size(), color),
                numericalListToString(data),
                Boolean.toString(fill));
    }

    private String listToString(List<String> list) {
        return list.stream()
                .map(s->"\"" + s + "\"")
                .collect(Collectors.joining(","));
    }

    private String getColorList(int numberOfElements, String color) {
        ArrayList<String> colorList = new ArrayList<>(numberOfElements);
        for (int i=0; i < numberOfElements; ++i) {
            colorList.add(color);
        }
        return listToString(colorList);
    }

    private String numericalListToString(List<Integer> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }


    public static final class Builder {
        private ArrayList<String> labels;
        private ArrayList<ChartDataSet> allData = null;

        public Builder setLabels(ArrayList<String> labels) {
            this.labels = labels;
            return this;
        }

        public Builder setDataSet(String label, String color, ArrayList<Integer> data) {
            if (allData == null) allData = new ArrayList<ChartDataSet>();
            allData.add(new ChartDataSet(label, color, data));
            return this;
        }

        public Chart build() {
            return new Chart(labels, allData);
        }
    }

    private String generateUniqueIdentifier() {
        Random rand = new Random();
        return "chartId" + String.valueOf(rand.nextInt(10000000));
    }
}
