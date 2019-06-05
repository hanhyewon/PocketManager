package com.example.soyeon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;

public class EditProduct extends AppCompatActivity {

    Toolbar toolbar;

    private Button btn_PushProductEdit = null;
    private Button btn_PushProductDelete = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_edit);

        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("상품수정");

        btn_PushProductEdit=(Button)findViewById(R.id.btn_pushProductEdit);
        btn_PushProductDelete=(Button)findViewById(R.id.btn_pushProductDelete);

        btn_PushProductEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //수정 버튼 눌렸다는 신호를 main에 폼에 적힌 정보들과 함께 보내면 메인에서 DB연동후 처리
            }
        });

        btn_PushProductDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //삭제 버튼 눌렸다는 신호를 main에 주면 메인에서 해당 데이터 삭제처리(DB연동해도 같은 방식으로)
            }
        });

    }
}
