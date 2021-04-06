package com.ms.raspberry.view;

import java.util.ArrayList;

public class ChartDataSet {
    String label;
    String color;
    ArrayList<Integer> list;

    public ChartDataSet(String label, String color, ArrayList<Integer> list) {
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

    public ArrayList<Integer> getList() {
        return list;
    }
}
