package com.example.gpdnj.pocketmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.example.hyejin.ParentActivity;
import com.example.hyejin.SalesManagerMainActivity;
import com.example.jiyeong.pastSalesMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;
import java.util.Objects;

public class OrderManagerActivity extends ParentActivity implements OrderListviewAdapter.BtnClickListener, ActionSheet.ActionSheetListener{

    public Toolbar toolbar;
    SimpleSideDrawer slide_menu;

    private ListView orderListview;
    private OrderListviewAdapter orderListviewAdapter;
    static ArrayList<OrderFirstDTO> arrayOrder = new ArrayList<OrderFirstDTO>();
    private String selectedOrderId;

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseRef;

    private TextView nav_userName;
    private TextView nav_userEmail;
    private TextView orderRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("주문");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("주문관리");

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);

        orderListview = findViewById(R.id.orderListview);
        setAdapter();
        displayOrderList();

        orderRegister = (TextView) findViewById(R.id.orderRegister);
        orderRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderManagerActivity.this, OrderAddActivity.class));
            }
        });
    }

    //주문 어댑터와 리스트뷰 연결
    private void setAdapter() {
        orderListviewAdapter = new OrderListviewAdapter(this.getBaseContext(), this);
        orderListview.setAdapter(orderListviewAdapter);
    }

    //주문 목록 보여주기
    private void displayOrderList() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayOrder.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    String orderId = data.getKey();
                    String date = (String) data.child("date").getValue();
                    int sum = Integer.parseInt(String.valueOf(data.child("sum").getValue()));
                    String pay = (String) data.child("pay").getValue();

                    OrderFirstDTO orderFirstDTO = new OrderFirstDTO(orderId, date, sum, pay);
                    arrayOrder.add(orderFirstDTO);
                }
                orderListviewAdapter.addItems(arrayOrder);
                orderListviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void moreBtnClickListener(String orderId) {
        //더보기 버튼 클릭 시, 삭제 메뉴 보여주기
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("삭제")
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
        setTheme(R.style.modifyDeleteTheme);
        this.selectedOrderId = orderId;
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if(index == 0) {
            //삭제를 선택했을 때
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
            dialog.setMessage("해당 주문을 삭제하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseRef.child(selectedOrderId).removeValue();
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
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

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
