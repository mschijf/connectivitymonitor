package com.ms.raspberry.view;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Chart {

    private final String identifier;
    private final ArrayList<String> labels;
    private final ArrayList<Integer> data;

    private static final String jsTemplate =
            "new Chart(document.getElementById(\"%s\"), {\n" +
            "    type: 'bar',\n" +
            "    data: {\n" +
            "      labels: [%s],\n" +
            "      datasets: [\n" +
            "        {\n" +
            "          label: \"%s\",\n" +
            "          backgroundColor: [%s],\n" +
            "          data: [%s]\n" +
            "        }\n" +
            "      ]\n" +
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
            "});\n";

    public Chart(String identifier, ArrayList<String> labels, ArrayList<Integer> data) {
        this.identifier = identifier;
        this.labels = labels;
        this.data = data;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getHtml() {
        return "<canvas id=\"" + identifier + "\" width=\"800\" height=\"450\"></canvas>\n";
    }

    public String getJs() {
        return String.format(jsTemplate,
                identifier, listToString(labels), "Hallo",
                getColorList(labels.size(), "#3e95cd"),
                numericalListToString(data),35);
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
        private String identifier;
        private ArrayList<String> labels;
        private ArrayList<Integer> data;

        public Builder setIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setLabels(ArrayList<String> labels) {
            this.labels = labels;
            return this;
        }

        public Builder setData(ArrayList<Integer> data) {
            this.data = data;
            return this;
        }

        public Chart build() {
            return new Chart(identifier, labels, data);
        }
    }
}
