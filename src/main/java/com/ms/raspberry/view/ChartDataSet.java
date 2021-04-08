package com.ms.raspberry.view;

import java.util.ArrayList;

public class ChartDataSet {
    private String label;
    private String color;
    private ArrayList<? extends Number> list;

    public ChartDataSet(String label, String color, ArrayList<? extends Number> list) {
        this.label = label;
        this.color = color;
        this.list = list;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }

    public ArrayList<? extends Number> getList() {
        return list;
    }
}
