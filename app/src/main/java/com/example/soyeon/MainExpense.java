package com.example.soyeon;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.example.gpdnj.pocketmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.gpdnj.pocketmanager.MoneyFormatClass.moneyFormatToWon;


public class MainExpense extends AppCompatActivity implements ExpenseListviewAdapter.BtnClickListener, ActionSheet.ActionSheetListener{

    Toolbar toolbar;
    ExpenseListviewAdapter expenseListviewAdapter;
    static ArrayList<ExpenseDTO> arrayExpense = new ArrayList<ExpenseDTO>();

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    String salesId,selectedExpenseId;

    Intent addIntent, editItent;

    TextView expenseTotalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_main);
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
        toolbar_title.setText("지출관리");

        //지출 어댑터와 리스트뷰 연결
        ListView expenseListview = findViewById(R.id.expenseListview);
        expenseListviewAdapter = new ExpenseListviewAdapter(this.getBaseContext(), this);
        expenseListview.setAdapter(expenseListviewAdapter);

        //지출 목록 보여주기
        displayExpense();

        addIntent = new Intent(this, AddExpense.class);
        editItent = new Intent(this, EditExpense.class);

        Button expenseAddShowBtn = findViewById(R.id.expenseAddShowBtn);
        expenseAddShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIntent.putExtra("salesId", salesId); //해당 판매ID 넘기기
                startActivity(addIntent);
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });

        expenseTotalSum = findViewById(R.id.expenseTotalSum);
    }

    @Override
    public void moreBtnClickListener(String expenseId) {
        //더보기 버튼 클릭 시, 수정/삭제 메뉴 보여주기
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("수정", "삭제")
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
        setTheme(R.style.modifyDeleteTheme);
        this.selectedExpenseId = expenseId;
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if(index == 0) {
            //수정을 선택했을 때
            editItent.putExtra("expenseId", selectedExpenseId); //선택한 지출의 ID 넘기기
            editItent.putExtra("salesId", salesId); //해당 지출의 판매ID 넘기기
            startActivity(editItent);
            overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
        } else {
            //삭제를 선택했을 때
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
            dialog.setMessage("해당 지출을 삭제하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseRef.child(selectedExpenseId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MainExpense.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create();
            dialog.show();
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    public void displayExpense(){
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayExpense.clear();
                int sum = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String expenseId = data.getKey();

                    String date = (String) data.child("date").getValue();
                    String context = (String) data.child("context").getValue();
                    String charge = (String) data.child("charge").getValue();
                    String group = (String) data.child("group").getValue();

                    sum = sum + Integer.parseInt(charge);

                    ExpenseDTO expenseDTO = new ExpenseDTO(expenseId, date, context, charge, group);
                    arrayExpense.add(expenseDTO);
                }
                expenseListviewAdapter.addItems(arrayExpense);
                Collections.reverse(arrayExpense); //최신정렬
                expenseListviewAdapter.notifyDataSetChanged();

                String str = moneyFormatToWon(sum) + "원";
                expenseTotalSum.setText(str);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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
