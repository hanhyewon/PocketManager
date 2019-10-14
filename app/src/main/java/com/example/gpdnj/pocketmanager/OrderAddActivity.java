package com.example.gpdnj.pocketmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soyeon.ProductDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.gpdnj.pocketmanager.MoneyFormatClass.moneyFormatToWon;

public class OrderAddActivity extends AppCompatActivity implements OrderAmountPriceAdapter.BtnClickListener{

    public Toolbar toolbar;

    OrderRecyclerViewAdapter orderRecyclerViewAdapter;
    RecyclerView orderRecyclerView;

    OrderAmountPriceAdapter orderAmountPriceAdapter;
    ListView orderPdetailListview;


    OrderDialogListviewAdapter orderDialogListviewAdapter;

    TextView orderPriceSumItem;

    String paySelect;
    static ArrayList<ProductDTO> arrayProduct = new ArrayList<ProductDTO>();
    static ArrayList<OrderDTO> arrayOrder = new ArrayList<OrderDTO>();

    FirebaseDatabase database;
    DatabaseReference databasePRef, databaseORef;

    String salesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add);
        salesId = getIntent().getStringExtra("salesId");

        database = FirebaseDatabase.getInstance();
        databasePRef = database.getReference("상품/" + salesId);
        databaseORef = database.getReference("주문/" + salesId);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("주문관리");

        TextView orderListBtn = findViewById(R.id.orderListBtn);
        orderListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });

        //상품명과 상품이미지 RecyclerView 설정
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerViewAdapter = new OrderRecyclerViewAdapter(this.getBaseContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        orderRecyclerView.setLayoutManager(gridLayoutManager);
        orderRecyclerView.setAdapter(orderRecyclerViewAdapter);
        orderRecyclerView.addItemDecoration(new OrderDecoration(this));

        //상품 선택 시, 상품명/수량/선택취소 ListView 설정
        orderPdetailListview = findViewById(R.id.orderPdetailListview);
        orderAmountPriceAdapter = new OrderAmountPriceAdapter(this.getBaseContext(), this);
        orderPdetailListview.setAdapter(orderAmountPriceAdapter);

        arrayOrder.clear();

        //어댑터클래스에 직접 이벤트처리관련 코드를 작성해줘야함 (리스트뷰처럼 구현되어있지않음 직접 정의해놔야한다.)
        //setOnItemClickListener라는 이름으로 이벤트 메소드 직접 정의한거임
        orderRecyclerViewAdapter.setOnItemClickListener(new OrderRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OrderRecyclerViewholder holder, View view, int position) {
                ProductDTO item = orderRecyclerViewAdapter.getItem(position);
                //Toast.makeText(OrderAddActivity.this, "상품명 : " + item.getName(), Toast.LENGTH_SHORT).show();

                OrderDTO orderDTO = new OrderDTO(item.getName(),  Integer.parseInt(item.getPrice()), 1);
                arrayOrder.add(orderDTO);
                orderAmountPriceAdapter.addItems(arrayOrder);
                orderAmountPriceAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(orderPdetailListview);
                setPriceSum();
            }
        });

        displayProduct();

        RadioGroup payGroup = findViewById(R.id.payGroup);
        payGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        orderPriceSumItem = findViewById(R.id.orderPriceSumItem);

        //결제하기(주문 DB 등록)
        TextView orderDataAddBtn = findViewById(R.id.orderDataAddBtn);
        orderDataAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paySelect != null) {
                    showOrderList();
                } else {
                    Toast.makeText(OrderAddActivity.this, "결제수단을 선택해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public int setPriceSum() {
        int priceSum = 0;
        for(OrderDTO dto : arrayOrder) {
            priceSum = priceSum + (dto.getPrice() * dto.getAmount());
        }
        String str = moneyFormatToWon(priceSum) + "원";
        orderPriceSumItem.setText(str);
        return priceSum;
    }

    //라디오 그룹 클릭 리스너
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.cash) {
                paySelect = "cash";
            } else {
                paySelect = "account";
            }
        }
    };

    public void orderDataAdd() {
        String date = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date());
        final String orderId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        OrderFirstDTO dto = new OrderFirstDTO(date, setPriceSum(), paySelect);
        databaseORef.child(orderId).setValue(dto).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                for(OrderDTO dto : arrayOrder) {
                    OrderDTO orderDTO = new OrderDTO(dto.getName(), dto.getPrice(), dto.getAmount());
                    databaseORef.child(orderId).child("상품수량").push().setValue(orderDTO);
                }
                finish();
            }
        });
    }

    @Override
    public void minusBtnClickListener(int position) {
        //orderAmountPriceAdapter.getItem(position);
        String name = arrayOrder.get(position).getName();
        int price = arrayOrder.get(position).getPrice();
        int amount = arrayOrder.get(position).getAmount()-1;
        if(amount > 0) {
            OrderDTO dto = new OrderDTO(name, price, amount);
            arrayOrder.set(position, dto);
        }

        orderAmountPriceAdapter.notifyDataSetChanged();
        setPriceSum();
    }

    @Override
    public void plusBtnClickListener(int position) {
        String name = arrayOrder.get(position).getName();
        int price = arrayOrder.get(position).getPrice();
        int amount = arrayOrder.get(position).getAmount()+1;
        OrderDTO dto = new OrderDTO(name, price, amount);
        arrayOrder.set(position, dto);

        orderAmountPriceAdapter.notifyDataSetChanged();
        setPriceSum();
    }

    @Override
    public void deleteBtnClickListener(int position) {
        arrayOrder.remove(position);
        orderAmountPriceAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(orderPdetailListview);
        setPriceSum();
    }

    //ScrollView 안에 상품명/수량/선택취소 ListView 높이 설정
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void displayProduct() {
        databasePRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayProduct.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String productId = data.getKey();
                    String name = (String) data.child("name").getValue();
                    String price = (String) data.child("price").getValue();
                    String img = (String) data.child("imgUrl").getValue();

                    ProductDTO productDTO = new ProductDTO(productId, name, price, img);
                    arrayProduct.add(productDTO);
                }
                orderRecyclerViewAdapter.addItems(arrayProduct);
                orderRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //주문
    private void showOrderList() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_order_list, null, false);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(dialogView);
        final Dialog dialog = alertDialog.create();

        ListView orderDialogListview = dialogView.findViewById(R.id.orderDialogListview);
        orderDialogListviewAdapter = new OrderDialogListviewAdapter(this.getBaseContext());
        orderDialogListview.setAdapter(orderDialogListviewAdapter);

        orderDialogListviewAdapter.addItems(arrayOrder);
        orderDialogListviewAdapter.notifyDataSetChanged();

        TextView orderPay = dialogView.findViewById(R.id.orderDialogPayInfo);
        TextView orderSum = dialogView.findViewById(R.id.orderDialogSumInfo);

        if(paySelect.equals("cash")) {
            orderPay.setText("현금");
        } else {
            orderPay.setText("계좌이체");
        }

        String str = moneyFormatToWon(setPriceSum()) + "원";
        orderSum.setText(str);

        dialog.show();

        dialogView.findViewById(R.id.lblPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDataAdd();
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });

        dialogView.findViewById(R.id.lblback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            if(OrderManagerActivity.activity != null) {
                OrderManagerActivity activity = OrderManagerActivity.activity;
                activity.finish();
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
            finish();
            overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(OrderManagerActivity.activity != null) {
            OrderManagerActivity activity = OrderManagerActivity.activity;
            activity.finish();
            overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
        }
        finish();
        overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
    }
}
