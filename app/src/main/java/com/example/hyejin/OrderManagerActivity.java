package com.example.hyejin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.MainActivity;
import com.example.gpdnj.pocketmanager.R;
import com.example.jiyeong.pastSalesMode;
import com.google.firebase.auth.FirebaseAuth;
import com.navdrawer.SimpleSideDrawer;

public class OrderManagerActivity extends ParentActivity {

    public Toolbar toolbar;
    public ImageView imgDelete;
    public View layoutItem1;
    public View layoutItem2;
    public View layoutItem3;
    private boolean deleteMote;

    SimpleSideDrawer slide_menu;
    private FirebaseAuth firebaseAuth;
    private TextView nav_userName;
    private TextView nav_userEmail;

    private TextView orderRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);
        firebaseAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("주문관리");

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);

        deleteMote = false;
        imgDelete = findViewById(R.id.imgDelete);
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteMote) {
                    deleteMote = false;
                    imgDelete.setBackgroundColor(0x00000000);
                } else {
                    deleteMote = true;
                    imgDelete.setBackgroundColor(0xffff0000);
                }
            }
        });
        layoutItem1 = findViewById(R.id.layoutItem1);
        layoutItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteMote) {
                    layoutItem1.setVisibility(View.GONE);
                }
            }
        });
        layoutItem2 = findViewById(R.id.layoutItem2);
        layoutItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteMote) {
                    layoutItem2.setVisibility(View.GONE);
                }
            }
        });
        layoutItem3 = findViewById(R.id.layoutItem3);
        layoutItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteMote) {
                    layoutItem3.setVisibility(View.GONE);
                }
            }
        });

        orderRegister = (TextView) findViewById(R.id.orderRegister);
        orderRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderManagerActivity.this, OrderRegisterActivity.class));
            }
        });
    }

    /**
     * 툴바에 있는 항목과 메뉴 네비게이션의 select 이벤트를 처리하는 메소드
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : //왼쪽 메뉴 버튼을 눌렀을 때
                slide_menu.toggleLeftDrawer(); //슬라이드 동작

                //navigation_menu.xml 이벤트 처리
                //현재 회원의 정보 설정
                nav_userName = (TextView) findViewById(R.id.nav_userName);
                nav_userEmail = (TextView) findViewById(R.id.nav_userEmail);
                nav_userName.setText(firebaseAuth.getCurrentUser().getDisplayName() + "님");
                nav_userEmail.setText(firebaseAuth.getCurrentUser().getEmail());

                ImageView menu_close = (ImageView)findViewById(R.id.menu_close);
                menu_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slide_menu.closeLeftSide();
                    }
                });

                //로그아웃
                Button logoutBtn = (Button) findViewById(R.id.logoutBtn);
                logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        Intent intent = new Intent(OrderManagerActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = (Button) findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(OrderManagerActivity.this, SalesManagerMainActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = (Button)findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(OrderManagerActivity.this, pastSalesMode.class);
                        startActivity(intent);
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }
}
