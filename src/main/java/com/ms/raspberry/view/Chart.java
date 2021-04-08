package com.ms.raspberry.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Chart {

    public enum Type{BAR, LINE};

    private final String identifier;
    private final ArrayList<String> labels;
    private final ArrayList<ChartDataSet> allData;
    private Type type;

    private static final String htmlTemplate = "<canvas id=\"%s\" width=\"600\" height=\"350\"></canvas>\n";

    private static final String barChartDataSetTemplate =
            "{\n" +
                    "   label: \"%s\",\n" +
                    "   backgroundColor: [%s],\n" +
                    "   data: [%s], \n" +
                    "   fill: %s" +
                    "}\n";

    private static final String lineChartDataSetTemplate =
            "{\n" +
                    "   label: \"%s\",\n" +
                    "   borderColor: \"%s\",\n" +
                    "   data: [%s], \n" +
                    "   fill: %s" +
                    "}\n";


    private static final String jsChartTemplate =
            "<script>\n" +
            "new Chart(document.getElementById(\"%s\"), {\n" +
            "    type: '%s',\n" +
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



    public Chart(Type type, ArrayList<String> labels, ArrayList<ChartDataSet> allData) {
        this.type = type != null ? type : Type.BAR;
        this.identifier = generateUniqueIdentifier();
        this.labels = labels;
        this.allData = allData;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getHtml() {
        return String.format(htmlTemplate, identifier);
    }

    public String getJs() {
        return String.format(jsChartTemplate,
                identifier, type.toString().toLowerCase(),
                listToString(labels),
                getAllDataSets(allData),
                0);
    }

    private String getAllDataSets(ArrayList<ChartDataSet> allData) {
        return allData.stream()
                .map(item->getDataSet(item.getLabel(), item.getColor(), item.getList()))
                .collect(Collectors.joining(","));
    }

    private String getDataSet(String label, String color, List<? extends Number> data) {
        if (type == Type.BAR) {
            return String.format(barChartDataSetTemplate,
                    label,
                    getColorList(data.size(), color),
                    numericalListToString(data),
                    "true");
        } else {// if (type == Type.LINE){
            return String.format(lineChartDataSetTemplate,
                    label,
                    color,
                    numericalListToString(data),
                    "false");
        }
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

    private String numericalListToString(List<? extends Number> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }


    public static final class Builder {
        private ArrayList<String> labels;
        private ArrayList<ChartDataSet> allData = null;
        private Type type;

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setLabels(ArrayList<String> labels) {
            this.labels = labels;
            return this;
        }

        public Builder addDataSet(String label, String color, ArrayList<? extends Number> data) {
            if (allData == null) allData = new ArrayList<ChartDataSet>();
            allData.add(new ChartDataSet(label, color, data));
            return this;
        }

        public Chart build() {
            return new Chart(type, labels, allData);
        }
    }

    private String generateUniqueIdentifier() {
        Random rand = new Random();
        return "chartId" + String.valueOf(rand.nextInt(10000000));
    }
}
