package com.ms.connectivitymonitor.view;

import java.util.ArrayList;

public class ChartData {
    public static int identifierCount = 0;

    private final int identifier;
    private final ArrayList<String> labels;
    private final ArrayList<ChartDataSet> allData;

    private ChartData(ArrayList<String> labels, ArrayList<ChartDataSet> allData) {
        this.identifier = ++identifierCount;
        this.labels = labels;
        this.allData = allData;
    }

    public static int getIdentifierCount() {
        return identifierCount;
    }

    public int getIdentifier() {
        return identifier;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public ArrayList<ChartDataSet> getAllData() {
        return allData;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private ArrayList<String> labels;
        private ArrayList<ChartDataSet> allData = null;

        public Builder setLabels(ArrayList<String> labels) {
            this.labels = labels;
            return this;
        }

        public Builder addDataSet(ChartDataSet.Type type, String label, String color, ArrayList<? extends Number> data) {
            if (allData == null) allData = new ArrayList<ChartDataSet>();
            allData.add(new ChartDataSet(type, label, color, data));
            return this;
        }

        public ChartData build() {
            return new ChartData(labels, allData);
        }
    }

}
