package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ManagerEventSettingActivity extends AppCompatActivity {

    Toolbar toolbar;

    private ListView eventListview;
    private EventListviewAdapter eventAdapter;

    static ArrayList<EventDTO> arrayEvent = new ArrayList<EventDTO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_event_setting);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("행사정보");

        eventListview = findViewById(R.id.eventListview);
        eventAdapter = new EventListviewAdapter(this.getBaseContext());

        eventAdapter.addItem(new EventDTO("테스트","이렇게해도 보여지는건가"));
        eventAdapter.addItem(new EventDTO("서울 금손 페스티벌","나중에 이미지랑 날짜 처리도 해야함"));
        eventAdapter.addItem(new EventDTO("부산 곰손 작품전!","DB까지 해야해"));

        eventListview.setAdapter(eventAdapter);

        //행사등록 버튼
        ImageView eventInputBtn = findViewById(R.id.eventInputBtn);
        eventInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerEventSettingActivity.this, EventAddActivity.class);
                startActivity(intent);
            }
        });
    }
}
