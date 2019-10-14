package com.example.gpdnj.pocketmanager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MoneyFormatClass {

    public static String moneyFormatToWon(String inputMoney) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');
        decimalFormat.setGroupingSize(3);
        decimalFormat.setDecimalFormatSymbols(dfs);

        double num = Double.parseDouble(inputMoney);

        return decimalFormat.format(num);
    }

    public static String moneyFormatToWon(int inputMoney) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');
        decimalFormat.setGroupingSize(3);
        decimalFormat.setDecimalFormatSymbols(dfs);

        double num = Double.parseDouble(String.valueOf(inputMoney));

        return decimalFormat.format(num);
    }
}
