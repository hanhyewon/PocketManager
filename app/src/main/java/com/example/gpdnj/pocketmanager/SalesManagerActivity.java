package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;

public class SalesManagerActivity extends AppCompatActivity {

    Toolbar toolbar;
    SimpleSideDrawer slide_menu;

    TextView salesNullText;
    ListView salesListview;
    SalesListviewAdapter salesListviewAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseRef;

    static ArrayList<SalesDTO> arraySales = new ArrayList<SalesDTO>();

    //다른 액티비티에서 종료시키기 위한 변수 선언
    public static SalesManagerActivity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_manager);

        activity = this;

        firebaseAuth = FirebaseAuth.getInstance();
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

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);

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

                mainIntent.putExtra("salesId", salesDTO.getSalesId()); //선택한 판매의 Key 넘기기
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
                    //현재 판매관리 중인(state == true) 판매만 보이기
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

    @Override
    public void onStop() {
        super.onStop();
        slide_menu.closeRightSide();
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
            case android.R.id.home : { //왼쪽 메뉴 버튼을 눌렀을 때
                slide_menu.toggleLeftDrawer(); //슬라이드 동작

                //navigation_menu.xml 이벤트 처리
                //현재 회원의 정보 설정
                TextView nav_userName = findViewById(R.id.nav_userName);
                TextView nav_userEmail = findViewById(R.id.nav_userEmail);
                nav_userName.setText(firebaseAuth.getCurrentUser().getDisplayName() + "님");
                nav_userEmail.setText(firebaseAuth.getCurrentUser().getEmail());

                //메뉴창 닫기
                ImageView menu_close = findViewById(R.id.menu_close);
                menu_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slide_menu.closeLeftSide();
                    }
                });

                //로그아웃
                Button logoutBtn = findViewById(R.id.logoutBtn);
                logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        Intent intent = new Intent(SalesManagerActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(SalesManagerActivity.this, SalesManagerActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(SalesManagerActivity.this, RevenueMainActivity.class);
                        startActivity(intent);
                    }
                });

                //행사정보
                Button eventBtn = findViewById(R.id.eventBtn);
                eventBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(SalesManagerActivity.this, ManagerEventSettingActivity.class);
                        startActivity(intent);
                    }
                });

                //커뮤니티
                Button communityBtn = findViewById(R.id.communityBtn);
                communityBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(SalesManagerActivity.this, ReviewMainActivity.class);
                        startActivity(intent);
                    }
                });

                //내 게시글 관리
                TextView myReviewSettingBtn = findViewById(R.id.myReviewSettingBtn);
                myReviewSettingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(SalesManagerActivity.this, MyReviewActivity.class);
                        startActivity(intent);
                    }
                });

                return true;
            }
            case R.id.homeMove: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
