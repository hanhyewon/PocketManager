package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jiyeong.MainExpense;
import com.example.soyeon.MainProduct;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SalesManagerMainActivity extends AppCompatActivity {

    Toolbar toolbar;

    FirebaseDatabase database;
    DatabaseReference databaseSalesRef;

    //다른 액티비티에서 종료시키기 위한 변수 선언
    public static SalesManagerMainActivity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_manager_main);

        activity = this;

        database = FirebaseDatabase.getInstance();
        databaseSalesRef = database.getReference("판매");

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("판매관리");

        final TextView salesTitleMain = findViewById(R.id.salesTitleMain);
        final TextView salesDateMain = findViewById(R.id.salesDateMain);
        ImageView salesSettingBtn = findViewById(R.id.salesSettingBtn);
        LinearLayout productManagerBtn = findViewById(R.id.productManagerBtn);
        LinearLayout orderManagerBtn = findViewById(R.id.orderManagerBtn);
        LinearLayout expenseManagerBtn = findViewById(R.id.expenseManagerBtn);
        LinearLayout salesStatisticsBtn = findViewById(R.id.salesStatisticsBtn);

        final String salesId = getIntent().getStringExtra("salesId");

        databaseSalesRef.child(salesId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                salesTitleMain.setText((String)data.child("title").getValue());
                salesDateMain.setText((String)data.child("date").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //판매관리 설정
        salesSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesManagerMainActivity.this, SalesSettingActivity.class);
                intent.putExtra("salesId", salesId); //해당 판매ID 넘기기
                startActivity(intent);
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });

        //상품관리 이동
        productManagerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesManagerMainActivity.this, MainProduct.class);
                intent.putExtra("salesId", salesId); //해당 판매ID 넘기기
                startActivity(intent);
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });

        //주문관리 이동
        orderManagerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesManagerMainActivity.this, OrderManagerActivity.class);
                intent.putExtra("salesId", salesId); //해당 판매ID 넘기기
                startActivity(intent);
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });

        //지출관리 이동
        expenseManagerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalesManagerMainActivity.this, MainExpense.class));
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });

        //실시간 매출 이동
        salesStatisticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(SalesManagerMainActivity.this, MainExpense.class));
                //overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.not_move_activity, R.anim.right_out_activity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.not_move_activity, R.anim.right_out_activity);
    }
}
