package com.example.soyeon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.gpdnj.pocketmanager.MainActivity;
import com.example.gpdnj.pocketmanager.R;
import com.example.hyejin.SalesManagerMainActivity;
import com.example.jiyeong.pastSalesMode;
import com.google.firebase.auth.FirebaseAuth;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;

public class MainProduct extends AppCompatActivity {

    Toolbar toolbar;
    SimpleSideDrawer slide_menu;
    private FirebaseAuth firebaseAuth;
    private TextView nav_userName;
    private TextView nav_userEmail;

    //리스트뷰를 담을 어댑터뷰 생성
    private ListView pListView = null;
    private ListViewAdapter pAdapter = null;
    private Button btn_ProductPush = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_main);
        firebaseAuth = FirebaseAuth.getInstance();

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

        //startActivity(intent_EMain);

        btn_ProductPush = findViewById(R.id.btn_ProductPush);
        pListView = findViewById(R.id.pListView);
        pAdapter = new ListViewAdapter(this.getBaseContext());

        //pAdapter.addItem(getResources().getDrawable(R.drawable.ic_launcher_background),
        //        "상품명A",
        //        "7,000원");

        pListView.setAdapter(pAdapter);
        registerForContextMenu(pListView);

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
                                startActivity(intent_PEdit);
                                break;
                            case R.id.pdelete:
                                pAdapter.remove(position);
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

    //리스트뷰 어댑터
    private class ListViewAdapter extends BaseAdapter {
        private Context pContext = null;
        private ArrayList<ProductListData> pListData = new ArrayList<ProductListData>();

        public ListViewAdapter(Context pContext){
            super();
            this.pContext = pContext;
        }

        @Override
        public int getCount() {
            return pListData.size();
        }

        @Override
        public Object getItem(int position) {
            return pListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
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
            }

            ProductListData pData = pListData.get(position);

            //if (pData.pImage != null) {
            //    holder.pImage.setVisibility(View.VISIBLE);
            //    holder.pImage.setImageDrawable(pData.pImage);
            //}else{
            //    holder.pImage.setVisibility(View.GONE);
            //}

            //holder.pName.setText(pData.pName);
            //holder.pPrice.setText(pData.pPrice);

            return convertView;
        }
        //////////
        //임의추가

        //임의삭제
        public void remove(int position){
            pListData.remove(position);
            dataChange();
        }
        //임의수정
        public void dataChange(){
            pAdapter.notifyDataSetChanged();
        }

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
