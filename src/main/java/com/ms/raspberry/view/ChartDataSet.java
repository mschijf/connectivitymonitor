package com.ms.raspberry.view;

import java.util.ArrayList;

public class ChartDataSet {

    public enum Type{bar, line};


    private Type type;
    private String label;
    private String color;
    private ArrayList<? extends Number> list;
    private boolean fill;


    public ChartDataSet(Type type, String label, String color, ArrayList<? extends Number> list) {
        this.type = type;
        this.label = label;
        this.color = color;
        this.fill = (type == Type.bar);
        this.list = list;
    }

    public Type getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }

    public boolean isFill() {
        return fill;
    }

    public ArrayList<? extends Number> getList() {
        return list;
    }
}
