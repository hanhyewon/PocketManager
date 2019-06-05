package com.example.jiyeong;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;

public class writeReview extends AppCompatActivity {

    TextView hashTag;
    EditText tagText;
    Button addTag;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("매출 관리");


        hashTag=(TextView)findViewById(R.id.hashtag);
        tagText=(EditText)findViewById(R.id.tag);
        addTag=(Button)findViewById(R.id.addTag);

        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashTag.setText(tagText.getText());
            }
        });



    }





}
