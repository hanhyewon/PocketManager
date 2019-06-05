package com.example.hyejin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;

import java.util.Calendar;

public class SalesManagerActivity extends ParentActivity {

    public Toolbar toolbar;
    public TextView editStart;
    public TextView editEnd;
    public TextView lblOk;
    public EditText editArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_manager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("판매관리");

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

        updateUI();
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
}
