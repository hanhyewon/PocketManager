package com.example.gpdnj.pocketmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyejin.ParentActivity;
import com.example.hyejin.SalesManagerMainActivity;
import com.example.jiyeong.pastSalesMode;
import com.example.soyeon.ProductListData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.navdrawer.SimpleSideDrawer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.gpdnj.pocketmanager.ManagerEventSettingActivity.arrayEvent;

public class OrderAddActivity extends ParentActivity implements OrderAmountPriceAdapter.BtnClickListener{

    public Toolbar toolbar;

    OrderRecyclerViewAdapter orderRecyclerViewAdapter;
    RecyclerView orderRecyclerView;

    OrderAmountPriceAdapter orderAmountPriceAdapter;
    ListView orderPdetailListview;


    OrderDialogListviewAdapter orderDialogListviewAdapter;


    TextView orderPriceSumItem;

    int sum = 0;
    String paySelect;
    static ArrayList<ProductListData> arrayProduct = new ArrayList<ProductListData>();
    static ArrayList<OrderDTO> arrayOrder = new ArrayList<OrderDTO>();

    FirebaseDatabase database;
    DatabaseReference databasePRef, databaseORef;

    private TextView lblOk;

    SimpleSideDrawer slide_menu;
    private FirebaseAuth firebaseAuth;
    private TextView nav_userName;
    private TextView nav_userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add);

        database = FirebaseDatabase.getInstance();
        databasePRef = database.getReference("상품");
        databaseORef = database.getReference("주문");
        firebaseAuth = FirebaseAuth.getInstance();

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
                ProductListData item = orderRecyclerViewAdapter.getItem(position);
                Toast.makeText(OrderAddActivity.this, "상품명 : " + item.getpName(), Toast.LENGTH_SHORT).show();

                OrderDTO orderDTO = new OrderDTO(item.getpName(),  Integer.parseInt(item.getpPrice()), 1);
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



        //테스트용 결제하기(주문 DB 등록)
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
        /*
        lblOk = findViewById(R.id.lblOk);
        lblOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderList();
            }
        });
         */
    }

    public int setPriceSum() {
        int priceSum = 0;
        for(OrderDTO dto : arrayOrder) {
            priceSum = priceSum + (dto.getPrice() * dto.getAmount());
        }
        String str = priceSum + "원";
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
        databasePRef.child("abc123").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayProduct.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String productId = data.getKey();
                    String pname = data.child("pName").getValue().toString();
                    String pimage = (String) data.child("pImage").getValue();
                    String price = (String) data.child("pPrice").getValue();

                    ProductListData productDTO = new ProductListData(productId, pimage, pname, price);

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

        String str = setPriceSum() + "원";
        orderSum.setText(str);

        dialog.show();

        dialogView.findViewById(R.id.lblPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDataAdd();
                dialog.dismiss();
                finish();
            }
        });

        dialogView.findViewById(R.id.lblback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
                        Intent intent = new Intent(OrderAddActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = (Button) findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(OrderAddActivity.this, SalesManagerMainActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = (Button)findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(OrderAddActivity.this, pastSalesMode.class);
                        startActivity(intent);
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }


}
