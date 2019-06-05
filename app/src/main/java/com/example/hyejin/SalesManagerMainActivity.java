package com.example.hyejin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpdnj.pocketmanager.R;

public class SalesManagerMainActivity extends ParentActivity implements View.OnClickListener{

    public Toolbar toolbar;
    public TextView lblSetting;

    public View layoutRealOut;
    public View layoutOutManager;
    public View layoutOrdertManager;
    public View lblProductManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_manager_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("판매 관리");

        lblProductManager = findViewById(R.id.lblProductManager);
        lblProductManager.setOnClickListener(this);
        layoutRealOut = findViewById(R.id.layoutRealOut);
        layoutRealOut.setOnClickListener(this);
        layoutOutManager = findViewById(R.id.layoutOutManager);
        layoutOutManager.setOnClickListener(this);
        layoutOrdertManager = findViewById(R.id.layoutOrdertManager);
        layoutOrdertManager.setOnClickListener(this);


        lblSetting = findViewById(R.id.lblSetting);
        lblSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalesManagerMainActivity.this, SalesManagerSettingActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == layoutOrdertManager) {
            showToast("주문관리입니다");
        }
        else if(v == lblProductManager) {
            showToast("판매관리입니다");
        }
        else if(v == layoutRealOut) {
            showToast("실시간 조회입니다");
        }
        else if(v == layoutOutManager) {
            showToast("지출관리 입니다");
        }

    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
