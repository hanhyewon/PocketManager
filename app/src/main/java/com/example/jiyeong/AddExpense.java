package com.example.jiyeong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.MainActivity;
import com.example.gpdnj.pocketmanager.R;
import com.example.gpdnj.pocketmanager.SalesManagerMainActivity;
import com.example.jiyeong.pastSalesMode;
import com.google.firebase.auth.FirebaseAuth;
import com.navdrawer.SimpleSideDrawer;

public class AddExpense extends AppCompatActivity {

    Toolbar toolbar;
    SimpleSideDrawer slide_menu;
    private FirebaseAuth firebaseAuth;
    private TextView nav_userName;
    private TextView nav_userEmail;

    private Button btn_ExpenseReturnByAdd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_add);
        firebaseAuth = FirebaseAuth.getInstance();

        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("지출등록");

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);


        final Intent intent_EEdit = new Intent(this, com.example.jiyeong.EditExpense.class);
        final Intent intent_EMain = new Intent(this, com.example.jiyeong.MainExpense.class);

        btn_ExpenseReturnByAdd = findViewById(R.id.btn_ExpenseReturnByAdd);

        btn_ExpenseReturnByAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_EMain);
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
                        Intent intent = new Intent(AddExpense.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = (Button) findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(AddExpense.this, SalesManagerMainActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = (Button)findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(AddExpense.this, pastSalesMode.class);
                        startActivity(intent);
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }
}
