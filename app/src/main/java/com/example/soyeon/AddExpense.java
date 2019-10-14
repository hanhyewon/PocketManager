package com.example.soyeon;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpdnj.pocketmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class AddExpense extends AppCompatActivity {

    Toolbar toolbar;
    TextView expenseDate;
    EditText expenseContext, expenseCharge;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    String salesId, groupSelect, expenseDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_add);
        salesId = getIntent().getStringExtra("salesId");

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("지출/" + salesId);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("지출등록");

        expenseDate = findViewById(R.id.expenseDate);
        expenseContext = findViewById(R.id.expenseContext);
        expenseCharge = findViewById(R.id.expenseCharge);
        Button expenseDataAddBtn = findViewById(R.id.expenseDataAddBtn);

        RadioGroup expenseRadioGroup = findViewById(R.id.expenseRadioGroup);
        expenseRadioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        //현재 시각으로 날짜 초기화
        expenseDateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date());
        expenseDate.setText(expenseDateStr);

        //날짜 선택
        expenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlyCalendarDialog()
                        .setSingle(true)
                        .setFirstMonday(false)
                        .setHeaderColor(Color.parseColor("#0eafc4"))
                        .setSelectedColor(Color.parseColor("#0eafc4"))
                        .setCallback(callback)
                        .show(getSupportFragmentManager(), "TAG_SLYCALENDAR");
            }
        });

        expenseDataAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataAdd();
            }
        });
    }

    //지출 DB 등록하기
    private void expenseDataAdd() {
        String context = expenseContext.getText().toString();
        String charge = expenseCharge.getText().toString();

        if(!TextUtils.isEmpty(context) && !TextUtils.isEmpty(charge) && !TextUtils.isEmpty(groupSelect)) {
            ExpenseDTO expenseDTO = new ExpenseDTO(expenseDateStr, context, charge, groupSelect);
            databaseRef.push().setValue(expenseDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
                }
            });
        } else {
            Toast.makeText(this, "모두 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    //기간 다이얼로그 콜백 메소드
    SlyCalendarDialog.Callback callback = new SlyCalendarDialog.Callback() {
        @Override
        public void onCancelled() {

        }

        @Override
        public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {
            if (firstDate != null) {
                if (secondDate == null) {
                    expenseDateStr = new SimpleDateFormat("yyyy.MM.dd " + hours + ":" + minutes).format(firstDate.getTime());
                    expenseDate.setText(expenseDateStr);
                }
            }
        }
    };

    //라디오 그룹 클릭 리스너
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.rb_Fixed) {
                groupSelect = "고정비";
            } else {
                groupSelect = "변동비";
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
    }
}
