package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SalesManagerActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView salesNullText;
    ListView salesListview;
    SalesListviewAdapter salesListviewAdapter;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    static ArrayList<SalesDTO> arraySales = new ArrayList<SalesDTO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_manager);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("판매");

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("판매관리");

        //판매 어댑터와 리스트뷰 연결
        salesListview = findViewById(R.id.salesListview);
        salesListviewAdapter = new SalesListviewAdapter(getBaseContext());
        salesListview.setAdapter(salesListviewAdapter);

        salesNullText = findViewById(R.id.salesNullText);

        //판매 DB 리스트뷰 보여주기
        displaySalesList();

        salesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mainIntent = new Intent(SalesManagerActivity.this, SalesManagerMainActivity.class);
                SalesDTO salesDTO = (SalesDTO) parent.getAdapter().getItem(position);

                mainIntent.putExtra("salesId", salesDTO.getSalesId()); //선택한 판매의 ID 넘기기
                startActivity(mainIntent);

                overridePendingTransition(R.anim.right_in_activity, R.anim.not_move_activity);
            }
        });

        //판매등록 화면으로 이동
        TextView salesAddShowBtn = findViewById(R.id.salesAddShowBtn);
        salesAddShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(SalesManagerActivity.this, SalesAddActivity.class);
                startActivity(addIntent);

                //사용법
                //overridePendingTransition(int showAnim, int hideAnim);
                //overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });
    }


    //판매 DB 정보 출력
    private void displaySalesList() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arraySales.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //현재 판매관리 중인(state = true) 판매만 보이기
                    if((boolean)data.child("state").getValue()) {
                        String salesId = data.getKey();

                        String title = (String) data.child("title").getValue();
                        String date = (String) data.child("date").getValue();

                        SalesDTO salesDTO = new SalesDTO(salesId, title, date);
                        arraySales.add(salesDTO);
                    }
                }
                salesListviewAdapter.addItems(arraySales);
                salesListviewAdapter.notifyDataSetChanged();
                nullCheckShow();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //현재 판매관리 중인 판매가 없을 경우, 보여줄 메시지 Show
    private void nullCheckShow() {
        if(salesListviewAdapter.items.isEmpty()) {
            salesNullText.setVisibility(View.VISIBLE);
        } else {
            salesNullText.setVisibility(View.INVISIBLE);
        }

    }
}
