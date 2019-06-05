package com.example.jiyeong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;

public class currentSalesDetail extends AppCompatActivity {

    Spinner spinner;
    TextView title, date, salesShow;
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_sales_detail);

        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("매출 관리");

        final String[] showDetails = {"손익분기점 매출","상품별 매출","결제수단별 매출"};

        spinner =(Spinner) findViewById(R.id.spinner1);
        title=(TextView)findViewById(R.id.title);
        date=(TextView)findViewById(R.id.date);
        salesShow=(TextView)findViewById(R.id.textView6);

        ArrayAdapter<String> adapter;
        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,showDetails);
        spinner.setAdapter(adapter);


        Intent intent = getIntent(); // 보내온 Intent를 얻는다
        title.setText(intent.getStringExtra("title"));
        date.setText(intent.getStringExtra("date"));




    }
}
