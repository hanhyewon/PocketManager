package com.example.hyejin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.MainActivity;
import com.example.gpdnj.pocketmanager.R;
import com.example.jiyeong.pastSalesMode;
import com.google.firebase.auth.FirebaseAuth;
import com.navdrawer.SimpleSideDrawer;

import java.util.Calendar;

public class SalesManagerActivity extends ParentActivity {

    public Toolbar toolbar;
    public TextView editStart;
    public TextView editEnd;
    public TextView lblOk;
    public EditText editArea;

    SimpleSideDrawer slide_menu;
    private FirebaseAuth firebaseAuth;
    private TextView nav_userName;
    private TextView nav_userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_manager);
        firebaseAuth = FirebaseAuth.getInstance();

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

        editStart = findViewById(R.id.editStart);
        editStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker((TextView) v);
            }
        });
        editEnd = findViewById(R.id.editEnd);
        editEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker((TextView) v);
            }
        });
        editArea = findViewById(R.id.editArea);
        lblOk = findViewById(R.id.lblOk);
        lblOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.mPrefEdit.putString("sales_content",editArea.getText().toString()).commit();
                App.mPrefEdit.putString("sales_start",editStart.getText().toString()).commit();
                App.mPrefEdit.putString("sales_end",editEnd.getText().toString()).commit();
                startActivity(new Intent(SalesManagerActivity.this, SalesManagerMainActivity.class));
            }
        });

        //updateUI();
    }

    public void updateUI() {
        String content = App.mPref.getString("sales_content", "");
        String start = App.mPref.getString("sales_start", "");
        String end = App.mPref.getString("sales_end", "");
        editArea.setText(content);
        editStart.setText(start);
        editEnd.setText(end);
    }

    public void showDatePicker(final TextView target) {
        // DatePickerDialog
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                target.setText(Util.format(calendar,"yyyy-MM-dd"));
            }
        }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dialog.show();
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
                        Intent intent = new Intent(SalesManagerActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = (Button) findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(SalesManagerActivity.this, SalesManagerMainActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = (Button)findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(SalesManagerActivity.this, pastSalesMode.class);
                        startActivity(intent);
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }
}
