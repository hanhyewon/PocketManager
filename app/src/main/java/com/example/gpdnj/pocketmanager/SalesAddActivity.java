package com.example.gpdnj.pocketmanager;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class SalesAddActivity extends AppCompatActivity {

    Toolbar toolbar;

    EditText salesTitle;
    TextView salesDate, salesAddBtn;

    String salesTitleStr, salesDateStr;
    boolean salesState = true;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_add);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("판매");

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("판매등록");

        salesTitle = findViewById(R.id.salesTitle);
        salesDate = findViewById(R.id.salesDate);
        salesAddBtn = findViewById(R.id.salesAddBtn);

        salesDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlyCalendarDialog()
                        .setSingle(false)
                        .setFirstMonday(false)
                        .setHeaderColor(Color.parseColor("#0eafc4"))
                        .setSelectedColor(Color.parseColor("#0eafc4"))
                        .setCallback(callback)
                        .show(getSupportFragmentManager(), "TAG_SLYCALENDAR");
            }
        });

        salesAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salesDataAdd();
            }
        });
    }

    //판매 DB 등록
    private void salesDataAdd() {
        //uid = firebaseAuth.getUid();
        salesTitleStr = salesTitle.getText().toString();

        if(!TextUtils.isEmpty(salesTitleStr) && salesDateStr != null) {
            SalesDTO salesDTO = new SalesDTO(salesTitleStr, salesDateStr, salesState);
            databaseRef.push().setValue(salesDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //Toast.makeText(SalesAddActivity.this, "등록 완료!", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
                }
            });
        } else {
            Toast.makeText(this, "모두 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    //기간 다이얼로그 콜백 메소드
    SlyCalendarDialog.Callback callback = new SlyCalendarDialog.Callback() {
        @Override
        public void onCancelled() {

        }

        @Override
        public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {
            if (firstDate != null) {
                if (secondDate == null) {
                    salesDateStr = new SimpleDateFormat("yy.MM.dd(E)").format(firstDate.getTime());
                    //eventDate.setText(date.substring(3, 11));
                    salesDate.setText(salesDateStr);
                } else {
                    salesDateStr = new SimpleDateFormat("yy.MM.dd(E)").format(firstDate.getTime())
                            + " - " + new SimpleDateFormat("yy.MM.dd(E)").format(secondDate.getTime());
                    //String str = date.substring(3, 14) + date.substring(17, 25);
                    salesDate.setText(salesDateStr);
                }
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
