package com.example.gpdnj.pocketmanager;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class ProductNameAxisValueFormatter extends ValueFormatter {

    private String[] productName;

    private final BarLineChartBase<?> chart;

    public ProductNameAxisValueFormatter(String[] productName, BarLineChartBase<?> chart) {
        this.productName = productName;
        this.chart = chart;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return productName[(int) (value/10)];
    }
}
