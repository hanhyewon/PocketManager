package com.example.soyeon;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.example.gpdnj.pocketmanager.MainActivity;
import com.example.gpdnj.pocketmanager.R;
import com.example.hyejin.SalesManagerMainActivity;
import com.example.jiyeong.pastSalesMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;

public class MainProduct extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseStorage fs = FirebaseStorage.getInstance();

    Toolbar toolbar;
    SimpleSideDrawer slide_menu;
    private FirebaseAuth firebaseAuth;
    private TextView nav_userName;
    private TextView nav_userEmail;

    ArrayList<String> pIds = new ArrayList<String>();
    ArrayList<String> pImages = new ArrayList<String>();
    ArrayList<String> pNames = new ArrayList<String>();
    ArrayList<String> pPrices = new ArrayList<String>();

    //리스트뷰를 담을 어댑터뷰 생성
    private ListView pListView = null;
    private ListViewAdapter pAdapter = null;
    private Button btn_ProductPush = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_main);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("상품관리");

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);

        final Intent intent_PEdit = new Intent(this, EditProduct.class);
        final Intent intent_PAdd = new Intent(this, AddProduct.class);
        final Intent intent_EMain = new Intent(this, MainExpense.class);

        btn_ProductPush = findViewById(R.id.btn_ProductPush);
        pListView = findViewById(R.id.pListView);
        pAdapter = new ListViewAdapter(this.getBaseContext());

        pListView.setAdapter(pAdapter);
        registerForContextMenu(pListView);

        displayProduct();

        //리스너
        pListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(MainProduct.this, view);
                getMenuInflater().inflate(R.menu.product_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.pModify:
                                intent_PEdit.putExtra("position",position);
                                intent_PEdit.putExtra("pId",pIds.get(position));
                                intent_PEdit.putExtra("pName",pNames.get(position));
                                intent_PEdit.putExtra("pPrice",pPrices.get(position));
                                intent_PEdit.putExtra("pImage",pImages.get(position));
                                startActivity(intent_PEdit);
                                break;
                            case R.id.pdelete:
                                pAdapter.remove(position);
                                displayProduct();
                        }
                    return false;
                    }
                });
                popup.show();
            return false;
            }
        });

        btn_ProductPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_PAdd);
            }
        });

    }

    //뷰홀더
    private class ViewHolder {
        public ImageView pImage;
        public TextView pName;
        public TextView pPrice;
    }

    //커스텀
    private class ListViewAdapter extends BaseAdapter {
        private Context pContext = null;
        private ArrayList<ProductListData> pListData = new ArrayList<ProductListData>();

        public ListViewAdapter(Context pContext){
            super();
            this.pContext = pContext;
        }

        @Override
        public int getCount() {
            return pIds.size();
        }

        @Override
        public Object getItem(int position) {
            return pIds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.product_listview, null);

                holder.pImage = (ImageView) convertView.findViewById(R.id.pImage);
                holder.pName = (TextView) convertView.findViewById(R.id.pName);
                holder.pPrice = (TextView) convertView.findViewById(R.id.pPrice);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
                //holder.pImage.setImageBitmap(null);
            }
            final StorageReference imagesRef = fs.getReference(pImages.get(position));
            //StorageReference에서 파일 다운로드 URL 가져옴
            imagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // Glide 이용하여 이미지뷰에 로딩
                        if(MainProduct.this.isFinishing()){
                            return;
                        }
                        Glide.with(MainProduct.this).load(task.getResult()).into(holder.pImage);
                    } else {

                    }
                }
            });


            holder.pName.setText(pNames.get(position));
            holder.pPrice.setText(pPrices.get(position)+" 원");
            return convertView;
        }

        //삭제
        public void remove(int position){
            //나중에 uid랑 판매코드 추가해야함
            String uid = "abc123";
            String ref = "상품/" + uid +"/"+ pIds.get(position);
            database.getReference(ref).removeValue();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        //수정
        public void dataChange(){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        //데이터 클리어
        public void clearItem(){
            pListData.clear();
        }

    }

    /**
     * 리스트 가지고 오는 함수
     */
    public void displayProduct(){
        //uid랑 pid 가지고 와서 추가 해야 함
        String uid = "abc123";
        database.getReference("상품/"+uid).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        pIds.add(dataSnapshot.getKey());

                        pImages.add(dataSnapshot.child("pImage").getValue().toString());
                        pNames.add(dataSnapshot.child("pName").getValue().toString());
                        pPrices.add(dataSnapshot.child("pPrice").getValue().toString());

                        pAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        pIds.clear();
                        pImages.clear();
                        pNames.clear();
                        pPrices.clear();

                        pIds.add(dataSnapshot.getKey());

                        pImages.add(dataSnapshot.child("pImage").getValue().toString());
                        pNames.add(dataSnapshot.child("pName").getValue().toString());
                        pPrices.add(dataSnapshot.child("pPrice").getValue().toString());

                        pAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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
                        Intent intent = new Intent(MainProduct.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = (Button) findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(MainProduct.this, SalesManagerMainActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = (Button)findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(MainProduct.this, pastSalesMode.class);
                        startActivity(intent);
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }
}
