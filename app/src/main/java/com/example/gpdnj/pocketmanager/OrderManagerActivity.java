package com.example.gpdnj.pocketmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.example.jiyeong.pastSalesMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;
import java.util.Collections;

public class OrderManagerActivity extends AppCompatActivity implements OrderListviewAdapter.BtnClickListener, ActionSheet.ActionSheetListener{

    public Toolbar toolbar;
    private ListView orderListview;
    private OrderListviewAdapter orderListviewAdapter;

    static ArrayList<OrderFirstDTO> arrayOrder = new ArrayList<OrderFirstDTO>();
    private String selectedOrderId, salesId;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    //다른 액티비티에서 종료시키기 위한 변수 선언
    public static OrderManagerActivity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);
        activity = this;
        salesId = getIntent().getStringExtra("salesId");

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("주문/" + salesId);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("주문관리");

        orderListview = findViewById(R.id.orderListview);
        setAdapter();
        displayOrderList();

        TextView orderRegister = findViewById(R.id.orderRegister);
        orderRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(OrderManagerActivity.this, OrderAddActivity.class);
                addIntent.putExtra("salesId", salesId);
                startActivity(addIntent);
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
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
                Collections.reverse(arrayOrder); //최신정렬
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
    }
}
