package com.example.hyejin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gpdnj.pocketmanager.MainActivity;
import com.example.gpdnj.pocketmanager.R;
import com.example.jiyeong.pastSalesMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;

public class SalesManagerModifyActivity extends ParentActivity {

    private FirebaseDatabase database;
    private FirebaseStorage fs = FirebaseStorage.getInstance();

    public Toolbar toolbar;
    SimpleSideDrawer slide_menu;
    private FirebaseAuth firebaseAuth;
    private TextView nav_userName;
    private TextView nav_userEmail;

    ArrayList<String> sNames = new ArrayList<String>();
    ArrayList<String> sDate_Starts = new ArrayList<String>();
    ArrayList<String> sDate_Ends = new ArrayList<String>();
    ArrayList<String> sIds = new ArrayList<String>();

    //리스트뷰를 담을 어댑터뷰 생성
    private ListView sListView = null;
    private ListViewAdapter sAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_manager_modify);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("판매관리");

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);

        final Intent intent_SEdit = new Intent(this, SalesManagerActivity.class);

        sListView = findViewById(R.id.sListView);
        sAdapter = new ListViewAdapter(this.getBaseContext());

        sListView.setAdapter(sAdapter);
        registerForContextMenu(sListView);

        displaySale();

        //행사 등록 버튼
        TextView salesManagerEventInsert = (TextView) findViewById(R.id.salesManagerEventInsert);
        salesManagerEventInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_SEdit.putExtra("edit","0");
                startActivity(intent_SEdit);
            }
        });

        //리스너
        sListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(SalesManagerModifyActivity.this, view);
                getMenuInflater().inflate(R.menu.sale_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.sModify:
                                intent_SEdit.putExtra("edit","1");
                                intent_SEdit.putExtra("position",position);
                                intent_SEdit.putExtra("sId",sIds.get(position));
                                intent_SEdit.putExtra("sName",sNames.get(position));
                                intent_SEdit.putExtra("sDate_Start",sDate_Starts.get(position));
                                intent_SEdit.putExtra("sDate_End",sDate_Ends.get(position));
                                startActivity(intent_SEdit);

                                break;
                            case R.id.sDelete:
                                sAdapter.remove(position);
                                displaySale();
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });
    }

    //뷰홀더
    private class ViewHolder {
        public TextView sName;
        public TextView sDate_Start;
        public TextView sDate_End;
    }

    //커스텀
    private class ListViewAdapter extends BaseAdapter {
        private Context sContext = null;
        private ArrayList<SalesListData> sListData = new ArrayList<SalesListData>();

        public ListViewAdapter(Context sContext){
            super();
            this.sContext = sContext;
        }

        @Override
        public int getCount() {
            return sIds.size();
        }

        @Override
        public Object getItem(int position) {
            return sIds.get(position);
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

                LayoutInflater inflater = (LayoutInflater) sContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.sale_listview, null);

                holder.sName = (TextView) convertView.findViewById(R.id.sName);
                holder.sDate_Start = (TextView) convertView.findViewById(R.id.sDate_Start);
                holder.sDate_End = (TextView) convertView.findViewById(R.id.sDate_End);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.sName.setText(sNames.get(position));
            holder.sDate_Start.setText(sDate_Starts.get(position));
            holder.sDate_End.setText("~" + sDate_Ends.get(position));
            return convertView;
        }

        //삭제
        public void remove(int position){
            //나중에 uid랑 판매코드 추가해야함
            String uid = "abc123";
            String ref = "판매/" + uid +"/"+ sIds.get(position);
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
            sListData.clear();
        }

    }


    /**
     * 리스트 가지고 오는 함수
     */
    public void displaySale(){
        //uid랑 pid 가지고 와서 추가 해야 함
        String uid = "abc123";
        database.getReference("판매/"+uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                sIds.add(dataSnapshot.getKey());

                sNames.add(dataSnapshot.child("sname").getValue().toString());
                sDate_Starts.add(dataSnapshot.child("sdate_Start").getValue().toString());
                sDate_Ends.add(dataSnapshot.child("sdate_End").getValue().toString());

                sAdapter.notifyDataSetChanged();
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
                sIds.clear();
                sNames.clear();
                sDate_Starts.clear();
                sDate_Ends.clear();

                sIds.add(dataSnapshot.getKey());

                sNames.add(dataSnapshot.child("sname").getValue().toString());
                sDate_Starts.add(dataSnapshot.child("sdate_Start").getValue().toString());
                sDate_Ends.add(dataSnapshot.child("sdate_End").getValue().toString());

                sAdapter.notifyDataSetChanged();
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
                        Intent intent = new Intent(SalesManagerModifyActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = (Button) findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(SalesManagerModifyActivity.this, SalesManagerMainActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = (Button)findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(SalesManagerModifyActivity.this, pastSalesMode.class);
                        startActivity(intent);
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }
}
