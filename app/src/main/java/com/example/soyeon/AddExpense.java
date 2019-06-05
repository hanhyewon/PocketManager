package com.example.soyeon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;

public class AddExpense extends AppCompatActivity {

    Toolbar toolbar;

    private Button btn_ExpenseReturnByAdd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_add);

        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("지출등록");

        final Intent intent_EEdit = new Intent(this, EditExpense.class);
        final Intent intent_EMain = new Intent(this, MainExpense.class);

        btn_ExpenseReturnByAdd = findViewById(R.id.btn_ExpenseReturnByAdd);

        btn_ExpenseReturnByAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_EMain);
            }
        });


    }
}
