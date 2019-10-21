package com.example.soyeon;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class EditExpense extends AppCompatActivity {

    Toolbar toolbar;
    TextView expenseDateEdit;
    EditText expenseContextEdit, expenseChargeEdit;
    RadioGroup expenseRadioGroupEdit;
    Button expenseDataEditBtn;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    String salesId, expenseId, groupSelect, expenseDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_edit);
        salesId = getIntent().getStringExtra("salesId");
        expenseId = getIntent().getStringExtra("expenseId");

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("지출/" + salesId + "/" + expenseId);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("지출수정");

        expenseDateEdit = findViewById(R.id.expenseDateEdit);
        expenseContextEdit = findViewById(R.id.expenseContextEdit);
        expenseChargeEdit = findViewById(R.id.expenseChargeEdit);
        expenseRadioGroupEdit = findViewById(R.id.expenseRadioGroupEdit);
        expenseDataEditBtn = findViewById(R.id.expenseDataEditBtn);
        expenseDataEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataEdit();
            }
        });

        //라디오 그룹 리스너 및 초기화
        expenseRadioGroupEdit.setOnCheckedChangeListener(radioGroupButtonChangeListener);
        final RadioButton rb_Fixed = findViewById(R.id.rb_Fixed_ed);
        final RadioButton rb_Variable = findViewById(R.id.rb_Variable_ed);

        //수정하고자 하는 지출의 이전 정보를 보여주기 위해 세팅
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                expenseDateStr = (String) data.child("date").getValue();
                expenseDateEdit.setText(expenseDateStr);
                expenseContextEdit.setText((String) data.child("context").getValue());
                expenseChargeEdit.setText((String) data.child("charge").getValue());
                groupSelect = (String) data.child("group").getValue();

                if(groupSelect != null) {
                    if(groupSelect.equals("고정비")) {
                        expenseRadioGroupEdit.check(rb_Fixed.getId());
                    } else {
                        expenseRadioGroupEdit.check(rb_Variable.getId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //날짜 선택
        expenseDateEdit.setOnClickListener(new View.OnClickListener() {
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
    }

    //수정하기
    private void expenseDataEdit() {
        String context = expenseContextEdit.getText().toString();
        String charge = expenseChargeEdit.getText().toString();

        ExpenseDTO expenseDTO = new ExpenseDTO(expenseDateStr, context, charge, groupSelect);
        databaseRef.setValue(expenseDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });
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
                    expenseDateEdit.setText(expenseDateStr);
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
