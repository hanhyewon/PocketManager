package com.example.gpdnj.pocketmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.gpdnj.pocketmanager.MoneyFormatClass.moneyFormatToWon;

public class RevenueDetailActivity extends AppCompatActivity {

    private ProfitHandler profitHandler = new ProfitHandler();

    ArrayList<BarEntry> values = new ArrayList<>();
    ArrayList<PieEntry> payValues = new ArrayList<PieEntry>();
    ArrayList<Integer> colors = new ArrayList<Integer>();
    HashMap<String, Integer> product = new HashMap<String, Integer>();

    XAxis xl;

    TextView revenueDataView, expenseDataView, profitDataView;
    PieChart payChart;
    BarChart orderProductChart;

    FirebaseDatabase database;
    DatabaseReference salesRef, orderRef, expenseRef, revenueRef;

    String salesId, salesTitle, salesDate;
    String name; int amount; //파베에서 데이터 가져올 때 사용
    String[] str; //판매된 상품명

    //순수익 데이터 설정하기 위해 사용
    int revenueValue = 0, expenseValue = 0, profitValue = 0;
    boolean revenueSet = false, expenseSet = false;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_detail);

        salesId = getIntent().getStringExtra("salesId");
        salesTitle = getIntent().getStringExtra("salesTitle");
        salesDate = getIntent().getStringExtra("salesDate");

        database = FirebaseDatabase.getInstance();
        salesRef = database.getReference("판매/" + salesId);
        orderRef = database.getReference("주문/" + salesId);
        expenseRef = database.getReference("지출/" + salesId);
        revenueRef = database.getReference("매출/" + salesId);

        //툴바 사용 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(salesTitle); //판매명으로 설정

        revenueDataView = findViewById(R.id.revenueDataView);
        expenseDataView = findViewById(R.id.expenseDataView);
        profitDataView = findViewById(R.id.profitDataView);
        payChart = findViewById(R.id.payChart);
        orderProductChart = findViewById(R.id.orderProductChart);

        customColorSet(); //그래프 색상 설정

        payChartSet(); //결제수단 데이터 설정 및 그래프 그리기

        orderProductChartSet(); //상품판매 데이터 설정

        //순이익(수입/지출/순이익) TextView 세팅
        profitTextSet();
        BackRunnable backRunnable = new BackRunnable();
        Thread th = new Thread(backRunnable);
        th.start();

        //결제수단, 상품판매 그래프 선택
        RadioGroup chartRadioGroup = findViewById(R.id.chartRadioGroup);
        chartRadioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        //라디오버튼 선택 디폴트 -> 결제수단 그래프
        RadioButton payRadio = findViewById(R.id.payRadioBtn);
        //RadioButton productRadioBtn = findViewById(R.id.productRadioBtn);
        payRadio.setChecked(true);
    }

    float cashValue, accountValue;

    //결제수단 그래프
    private void payChartSet() {
        payChart.setUsePercentValues(true);
        payChart.getDescription().setEnabled(false);
        payChart.setExtraOffsets(5, 10, 5, 5);
        payChart.setDragDecelerationFrictionCoef(0.95f);
        payChart.setDrawHoleEnabled(false);
        //payChart.setHoleColor(Color.TRANSPARENT);
        //payChart.setHoleRadius(50f);
        //payChart.setTransparentCircleColor(Color.WHITE);
        //payChart.setTransparentCircleRadius(54f);
        //payChart.setTransparentCircleAlpha(110);
        payChart.setRotationEnabled(false);

        Legend legend = payChart.getLegend();
        legend.setEnabled(false);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cashValue = 0f; accountValue = 0f;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String pay = (String)data.child("pay").getValue();
                    if(pay.equals("cash")) {
                        cashValue = cashValue + 10f;
                    } else {
                        accountValue = accountValue + 10f;
                    }
                }
                payValues.add(new PieEntry(cashValue, "현금"));
                payValues.add(new PieEntry(accountValue, "계좌이체"));

                payChart.animateY(1300, Easing.EaseInOutQuad);

                PieDataSet dataSet = new PieDataSet(payValues, "결제수단");
                dataSet.setDrawIcons(false);
                dataSet.setSliceSpace(4f); //차트간격
                dataSet.setIconsOffset(new MPPointF(0, 40));
                dataSet.setSelectionShift(5f);
                dataSet.setColors(colors);

                PieData data = new PieData((dataSet));
                data.setValueFormatter(new PercentFormatter(payChart));
                data.setValueTextSize(12f);
                data.setValueTextColor(Color.WHITE);

                payChart.setData(data);
                payChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //상품판매별 그래프 설정 및 데이터 저장
    private void orderProductChartSet() {
        orderProductChart.setDrawBarShadow(false);
        orderProductChart.setDrawValueAboveBar(true);
        orderProductChart.getDescription().setEnabled(false);
        orderProductChart.setMaxVisibleValueCount(60); //차트 항목이 60이상이면 텍스트값 표시 안함
        orderProductChart.setPinchZoom(false);
        orderProductChart.setDrawGridBackground(false);

        //상품명
        xl = orderProductChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawGridLines(false);
        xl.setTextSize(10f);
        xl.setLabelRotationAngle(-50f);
        xl.setTextColor(Color.rgb(40, 109, 125));

        YAxis yl = orderProductChart.getAxisLeft();
        yl.setSpaceTop(15f);
        yl.setAxisMinimum(0f);
        yl.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        YAxis yr = orderProductChart.getAxisRight();
        yr.setDrawGridLines(false);
        yr.setSpaceTop(15f);
        yr.setAxisMinimum(0f);

        Legend legend = orderProductChart.getLegend();
        legend.setEnabled(false);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String orderId = data.getKey();
                    if(orderId != null) {
                        revenueRef.child(orderId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot data2 : dataSnapshot.getChildren()) {
                                    name = (String) data2.child("name").getValue();
                                    amount = Integer.parseInt(String.valueOf(data2.child("amount").getValue()));
                                    //Log.v("정보 : ", "" + name + " " + amount);

                                    if(product.containsKey(name)) {
                                        //해쉬맵에 이미 저장된 상품명이 있다면
                                        int value = product.get(name); //기존의 수량 데이터 가져오기
                                        product.put(name, value + amount); //수량 증가
                                    } else {
                                        product.put(name, amount);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //해시맵에 저장된 상품 데이터로 그래프 그리기
    private void orderProductChartDraw() {
        ValueFormatter xAxisFormatter;
        str = new String[product.size()];

        int i = 0;
        for(String key : product.keySet()) {
            if(i < product.size()) {
                str[i] = key;
                values.add(new BarEntry(i * 10f, product.get(key)));
                i = i + 1;
            }
        }

        xAxisFormatter = new ProductNameAxisValueFormatter(str, orderProductChart);
        xl.setValueFormatter(xAxisFormatter);

        BarDataSet set1;

        if(orderProductChart.getData() != null && orderProductChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) orderProductChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            orderProductChart.getData().notifyDataChanged();
            orderProductChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "상품명");
            set1.setDrawIcons(false);
            set1.setColors(colors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(11f);
            data.setBarWidth(9f);
            orderProductChart.setData(data);
            orderProductChart.setFitBars(true);
        }
    }

    //순이익(수입/지출/순이익) TextView 세팅
    private void profitTextSet() {
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    revenueValue = revenueValue + Integer.parseInt(String.valueOf(data.child("sum").getValue()));
                }
                revenueSet = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    expenseValue = expenseValue + Integer.parseInt(String.valueOf(data.child("charge").getValue()));
                }
                expenseSet = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //finish() 할 때 스레드 종료시켜주기
    class BackRunnable implements Runnable {
        //private boolean stop;

        /*
        public void setStop(boolean stop) {
            this.stop = stop;
        }

         */

        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            profitHandler.sendEmptyMessage(0);
        }
    }

    String profitStr, revenueStr, expenseStr;

    @SuppressLint("HandlerLeak")
    class ProfitHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (revenueSet && expenseSet) { //수입과 지출의 값을 DB에서 가져와 세팅되면
                profitValue = Math.abs(revenueValue) - Math.abs(expenseValue); //순이익 절대값 계산

                revenueStr = moneyFormatToWon(revenueValue) + "원";
                expenseStr = moneyFormatToWon(expenseValue) + "원";
                profitStr = moneyFormatToWon(profitValue) + "원";

                revenueDataView.setText(revenueStr);
                expenseDataView.setText(expenseStr);
                profitDataView.setText(profitStr);
            }
        }
    }

    //라디오 그룹 클릭 리스너
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.payRadioBtn) {
                //결제수단별 그래프
                payChart.setVisibility(View.VISIBLE);
                orderProductChart.setVisibility(View.GONE);
                payChart.animateY(1300, Easing.EaseInOutQuad);
            } else {
                //상품별 판매량 그래프
                payChart.setVisibility(View.GONE);
                orderProductChart.setVisibility(View.VISIBLE);
                orderProductChartDraw();
                orderProductChart.animateY(2000);
            }
        }
    };

    private void customColorSet() {
        colors.add(0, Color.rgb(68, 223, 207));
        colors.add(1, Color.rgb(4, 153, 222));
        colors.add(2, Color.rgb(255, 101, 125));
        colors.add(3, Color.rgb(255, 201, 74));
        colors.add(4, Color.rgb(255, 247, 115));
        colors.add(5, Color.rgb(158, 255, 110));
        colors.add(6, Color.rgb(240, 188, 225));
        colors.add(7, Color.rgb(221, 221, 221));
    }

    private void createExcel() {
        Workbook xlsWb = new HSSFWorkbook(); //Excel 2007 이전
        //Workbook xlsxWb = new XSSFWorkbook(); //Excel 2007 이상

        Sheet sheet = xlsWb.createSheet(salesTitle); //시트 생성
        //sheet.setDisplayGridlines(false); //눈금선 없애기

        Row row = null;
        Cell cell = null;

        //행사명 스타일
        CellStyle style1 = xlsWb.createCellStyle();
        Font font1 = xlsWb.createFont();
        font1.setFontHeightInPoints((short)21);
        font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style1.setFont(font1);

        //날짜 스타일
        CellStyle style2 = xlsWb.createCellStyle();
        Font font2 = xlsWb.createFont();
        font2.setFontHeightInPoints((short)17);
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style2.setFont(font2);

        //순이익, 결제수단/상품판매 분석 스타일
        CellStyle style3 = xlsWb.createCellStyle();
        Font font3 = xlsWb.createFont();
        font3.setFontHeightInPoints((short)15);
        font3.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style3.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style3.setFont(font3);

        //결제수단/상품판매 분류 스타일
        CellStyle style6 = xlsWb.createCellStyle();
        Font font6 = xlsWb.createFont();
        font6.setFontHeightInPoints((short)13);
        //font6.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style6.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style6.setFont(font6);

        //분류 컬럼 스타일
        CellStyle style4 = xlsWb.createCellStyle();
        style4.setAlignment(CellStyle.ALIGN_CENTER);
        style4.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style4.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        style4.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style4.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style4.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style4.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style4.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        Font font4 = xlsWb.createFont();
        font4.setFontHeightInPoints((short)12);
        style4.setFont(font4);

        //데이터 스타일 (상품명)
        CellStyle style5 = xlsWb.createCellStyle();
        style5.setAlignment(CellStyle.ALIGN_CENTER);
        style5.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style5.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style5.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style5.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style5.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        Font font5 = xlsWb.createFont();
        font5.setFontHeightInPoints((short)10);
        style5.setFont(font5);

        //데이터 스타일 (결제수단)
        CellStyle style7 = xlsWb.createCellStyle();
        style7.setAlignment(CellStyle.ALIGN_RIGHT);
        style7.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style7.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style7.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style7.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style7.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        Font font7 = xlsWb.createFont();
        font7.setFontHeightInPoints((short)10);
        style7.setFont(font7);

        //데이터 스타일 (금액, 수량)
        CellStyle style8 = xlsWb.createCellStyle();
        style8.setAlignment(CellStyle.ALIGN_RIGHT);
        style8.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style8.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style8.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style8.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style8.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style8.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
        Font font8 = xlsWb.createFont();
        font8.setFontHeightInPoints((short)10);
        style8.setFont(font8);


        row = sheet.createRow(1);
        row.setHeightInPoints((short)35);
        cell = row.createCell(0);
        cell.setCellValue(salesTitle);
        cell.setCellStyle(style1);

        row = sheet.createRow(2);
        row.setHeightInPoints((short)20);
        cell = row.createCell(0);
        cell.setCellValue(salesDate);
        cell.setCellStyle(style2);

        row = sheet.createRow(4);
        row.setHeightInPoints((short)20);
        cell = row.createCell(0);
        cell.setCellValue("순이익");
        cell.setCellStyle(style3);

        row = sheet.createRow(5);
        cell = row.createCell(0);
        cell.setCellValue("수입");
        cell.setCellStyle(style4);
        cell = row.createCell(1);
        cell.setCellValue("지출");
        cell.setCellStyle(style4);
        cell = row.createCell(2);
        cell.setCellValue("순이익");
        cell.setCellStyle(style4);

        row = sheet.createRow(6);
        cell = row.createCell(0);
        cell.setCellValue(revenueValue);
        cell.setCellStyle(style8);
        cell = row.createCell(1);
        cell.setCellValue(expenseValue);
        cell.setCellStyle(style8);
        cell = row.createCell(2);
        cell.setCellValue(profitValue);
        cell.setCellStyle(style8);

        row = sheet.createRow(8);
        row.setHeightInPoints((short)7);

        row = sheet.createRow(9);
        row.setHeightInPoints((short)20);
        cell = row.createCell(0);
        cell.setCellValue("결제수단/상품판매 분석");
        cell.setCellStyle(style3);

        row = sheet.createRow(10);
        row.setHeightInPoints((short)7);

        row = sheet.createRow(11);
        row.setHeightInPoints((short)17);
        cell = row.createCell(0);
        cell.setCellValue("결제수단 비율");
        cell.setCellStyle(style6);

        row = sheet.createRow(12);
        cell = row.createCell(0);
        cell.setCellValue("현금");
        cell.setCellStyle(style4);
        cell = row.createCell(1);
        cell.setCellValue("계좌이체");
        cell.setCellStyle(style4);

        row = sheet.createRow(13);
        cell = row.createCell(0);
        cell.setCellValue(Math.round(cashValue/(cashValue+accountValue)*100) + "%");
        cell.setCellStyle(style5);
        cell = row.createCell(1);
        cell.setCellValue(Math.round(accountValue/(cashValue+accountValue)*100) + "%");
        cell.setCellStyle(style5);

        row = sheet.createRow(15);
        row.setHeightInPoints((short)17);
        cell = row.createCell(0);
        cell.setCellValue("상품별 판매량");
        cell.setCellStyle(style6);

        row = sheet.createRow(16);
        cell = row.createCell(0);
        cell.setCellValue("상품명");
        cell.setCellStyle(style4);
        cell = row.createCell(1);
        cell.setCellValue("수량");
        cell.setCellStyle(style4);

        //상품 데이터 입력
        int i = 0;
        for(String key : product.keySet()) {
            if(i < product.size()+17) {
                row = sheet.createRow(i+17);
                cell = row.createCell(0);
                cell.setCellValue(key);
                cell.setCellStyle(style5);
                cell = row.createCell(1);
                cell.setCellValue(product.get(key));
                cell.setCellStyle(style8);
                i = i + 1;
            }
        }

        File xlsFile = new File(getExternalFilesDir(null), salesTitle + ".xls");
        try {
            FileOutputStream os = new FileOutputStream(xlsFile);
            xlsWb.write(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri path = Uri.fromFile(xlsFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/excel");
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        startActivity(Intent.createChooser(shareIntent,"엑셀 파일로 내보내기"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.excel, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : {
                finish();
                return true;
            }
            case R.id.excel: {
                createExcel();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
