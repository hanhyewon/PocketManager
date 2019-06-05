package com.example.gpdnj.pocketmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.navdrawer.SimpleSideDrawer;

public class MenuNavigation extends AppCompatActivity {

    //Toolbar toolbar;
    SimpleSideDrawer slide_menu1;
    ImageView menu_close;


    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_menu);
        firebaseAuth = FirebaseAuth.getInstance();



        //툴바 사용 설정
        //toolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 False
        //getSupportActionBar().setDisplayShowTitleEnabled(false); //타이틀 사용 여부 False

        //툴바 타이틀명 설정
        //TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        //toolbar_title.setText("Pocket Manager");

        //slide_menu1 = new SimpleSideDrawer(this);

    }

    /**
     * menu.xml에 정의한 내용(close 버튼) 툴바에 적용시키는 메소드
     * @Override
     *     public boolean onCreateOptionsMenu(Menu menu) {
     *         getMenuInflater().inflate(R.menu.menu, menu);
     *         return super.onCreateOptionsMenu(menu);
     *     }
     * */

    /**
     * 툴바에 있는 항목의 select 이벤트를 처리하는 메소드
     * @Override
     *     public boolean onOptionsItemSelected(MenuItem item) {
     *         switch (item.getItemId()) {
     *             case R.id.action_close : //X 버튼을 눌렀을 때
     *                 slide_menu1.closeLeftSide();
     *         }
     *         return super.onOptionsItemSelected(item);
     *     }
     * */

}
