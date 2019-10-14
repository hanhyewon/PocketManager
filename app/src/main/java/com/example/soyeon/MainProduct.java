package com.example.soyeon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.example.gpdnj.pocketmanager.EventDTO;
import com.example.gpdnj.pocketmanager.MainActivity;
import com.example.gpdnj.pocketmanager.R;
import com.example.gpdnj.pocketmanager.SalesManagerActivity;
import com.example.jiyeong.pastSalesMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;
import java.util.Collections;

public class MainProduct extends AppCompatActivity implements ProductListviewAdapter.BtnClickListener, ActionSheet.ActionSheetListener{

    Toolbar toolbar;

    String salesId, selectedProductId;

    private ProductListviewAdapter productListviewAdapter;
    static ArrayList<ProductDTO> arrayProduct = new ArrayList<ProductDTO>();

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    Intent editItent, addIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_main);
        salesId = getIntent().getStringExtra("salesId");

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("상품/" + salesId);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("상품관리");

        editItent = new Intent(this, EditProduct.class);
        addIntent = new Intent(this, AddProduct.class);

        //상품 어댑터와 리스트뷰 연결
        ListView productListview = findViewById(R.id.pListView);
        productListviewAdapter = new ProductListviewAdapter(this.getBaseContext(), this);
        productListview.setAdapter(productListviewAdapter);

        //상품 DB 보여주기
        displayProduct();

        Button btn_ProductPush = findViewById(R.id.btn_ProductPush);
        btn_ProductPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIntent.putExtra("salesId", salesId); //해당 판매ID 넘기기
                startActivity(addIntent);
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });
    }

    @Override
    public void moreBtnClickListener(String productId) {
        //더보기 버튼 클릭 시, 수정/삭제 메뉴 보여주기
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("수정", "삭제")
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
        setTheme(R.style.modifyDeleteTheme);
        this.selectedProductId = productId;
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if(index == 0) {
            //수정을 선택했을 때
            editItent.putExtra("productId", selectedProductId); //선택한 상품의 ID 넘기기
            editItent.putExtra("salesId", salesId); //해당 상품의 판매ID 넘기기
            startActivity(editItent);
        } else {
            //삭제를 선택했을 때
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
            dialog.setMessage("해당 상품을 삭제하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseRef.child(selectedProductId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MainProduct.this, "삭제 완료", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    public void displayProduct(){
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayProduct.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String productId = data.getKey();

                    String name = (String) data.child("name").getValue();
                    String price = (String) data.child("price").getValue();
                    String imgUrl = (String) data.child("imgUrl").getValue();

                    ProductDTO productDTO = new ProductDTO(productId, name, price, imgUrl);
                    arrayProduct.add(productDTO);
                }
                productListviewAdapter.addItems(arrayProduct);
                Collections.reverse(arrayProduct); //최신정렬
                productListviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
