package com.ms.raspberry.view;

import java.util.ArrayList;

public class ChartData {
    public static int identifierCount = 0;

    public enum Type{bar, line};

    private final int identifier;
    private final ArrayList<String> labels;
    private final ArrayList<ChartDataSet> allData;
    private Type type;
    private boolean fill;


    public ChartData(Type type, ArrayList<String> labels, ArrayList<ChartDataSet> allData) {
        this.identifier = ++identifierCount;
        this.type = type != null ? type : Type.bar;
        this.fill = (type == Type.bar);
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

    public Type getType() {
        return type;
    }

    public boolean isFill() {
        return fill;
    }

    public static Builder newBuilder() {
        return new Builder();
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

        public ChartData build() {
            return new ChartData(type, labels, allData);
        }
    }

}
