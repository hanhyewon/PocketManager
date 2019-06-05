package com.example.hyejin;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;

public class OrderManagerActivity extends ParentActivity {

    public Toolbar toolbar;
    public ImageView imgDelete;
    public View layoutItem1;
    public View layoutItem2;
    public View layoutItem3;
    private boolean deleteMote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("주문관리");

        deleteMote = false;
        imgDelete = findViewById(R.id.imgDelete);
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteMote) {
                    deleteMote = false;
                    imgDelete.setBackgroundColor(0x00000000);
                } else {
                    deleteMote = true;
                    imgDelete.setBackgroundColor(0xffff0000);
                }
            }
        });
        layoutItem1 = findViewById(R.id.layoutItem1);
        layoutItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteMote) {
                    layoutItem1.setVisibility(View.GONE);
                }
            }
        });
        layoutItem2 = findViewById(R.id.layoutItem2);
        layoutItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteMote) {
                    layoutItem2.setVisibility(View.GONE);
                }
            }
        });
        layoutItem3 = findViewById(R.id.layoutItem3);
        layoutItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteMote) {
                    layoutItem3.setVisibility(View.GONE);
                }
            }
        });
    }
}
