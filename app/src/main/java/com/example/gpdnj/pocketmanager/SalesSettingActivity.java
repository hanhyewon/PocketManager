package com.example.gpdnj.pocketmanager;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SalesSettingActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseDatabase database;
    DatabaseReference databaseRef;

    String salesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_setting);

        salesId = getIntent().getStringExtra("salesId");

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("판매/" + salesId);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("판매관리");

        final TextView salesSettingTitleView = findViewById(R.id.salesSettingTitleView);
        final TextView salesSettingDateView = findViewById(R.id.salesSettingDateView);

        //선택한 판매의 정보 보여주기
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                salesSettingTitleView.setText((String) data.child("title").getValue());
                salesSettingDateView.setText((String) data.child("date").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //판매 삭제하기
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        LinearLayout salesDeleteBtn = findViewById(R.id.salesDeleteBtn);
        salesDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("판매를 삭제하시겠습니까?\n삭제 시, 해당 판매에 관한 데이터가 모두 삭제됩니다")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database.getReference("상품/" + salesId).removeValue();
                                database.getReference("주문/" + salesId).removeValue();
                                database.getReference("지출/" + salesId).removeValue();
                                database.getReference("매출/" + salesId).removeValue();
                                databaseRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SalesSettingActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                                        if(SalesManagerMainActivity.activity != null) {
                                            SalesManagerMainActivity activity = SalesManagerMainActivity.activity;
                                            activity.finish();
                                            overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
                                        }
                                        finish();
                                        overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(false)
                        .create();
                dialog.show();
            }
        });

        //판매 종료하기
        TextView salesEndBtn = findViewById(R.id.salesEndBtn);
        salesEndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("판매를 종료하시겠습니까?\n종료 시, 이후 해당 판매에 관한 데이터 수정이 불가합니다")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseRef.child("state").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(SalesManagerMainActivity.activity != null) {
                                            SalesManagerMainActivity activity = SalesManagerMainActivity.activity;
                                            activity.finish();
                                            overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
                                        }
                                        finish();
                                        overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(false)
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : {
                finish();
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
                return true;
            }
            case R.id.homeMove: {
                if(SalesManagerActivity.activity != null && SalesManagerMainActivity.activity != null) {
                    SalesManagerActivity activity1 = SalesManagerActivity.activity;
                    SalesManagerMainActivity activity2 = SalesManagerMainActivity.activity;
                    activity1.finish();
                    activity2.finish();
                    overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
                }
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
    }
}
