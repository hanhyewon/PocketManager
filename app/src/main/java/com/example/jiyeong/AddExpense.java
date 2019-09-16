package com.example.jiyeong;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpdnj.pocketmanager.HomeActivity;
import com.example.gpdnj.pocketmanager.JoinActivity2;
import com.example.gpdnj.pocketmanager.MainActivity;
import com.example.gpdnj.pocketmanager.R;
import com.example.gpdnj.pocketmanager.UserDTO;
import com.example.hyejin.SalesManagerMainActivity;
import com.example.hyejin.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;
import java.util.Calendar;

public class AddExpense extends AppCompatActivity {

    Toolbar toolbar;
    SimpleSideDrawer slide_menu;
    private FirebaseAuth firebaseAuth;
    private TextView nav_userName;
    private TextView nav_userEmail;
    private TextView tv_Date;
    private EditText et_Date;
    private EditText et_Context;
    private EditText et_charge;
    private EditText et_option;
    private RadioGroup rg_group;

    private Button btn_ExpenseReturnByAdd = null;



    FirebaseDatabase database;
    DatabaseReference databaseRef;

    static ArrayList<ExpenseDTO> arrayExpense = new ArrayList<ExpenseDTO>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_add);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("지출").push(); //지출코드 발생



        //DB 객체
        et_Context = (EditText) findViewById(R.id.et_ExpenseAddText); //내용
        et_charge = (EditText) findViewById(R.id.et_ExpenseAddPrice);// 비용
        et_option = (EditText) findViewById(R.id.et_ExpenseAddDetail); //비고
        rg_group = (RadioGroup) findViewById(R.id.rg_ExpenseAddClassification); //지출 분류
        tv_Date=(TextView)findViewById(R.id.tv_ExpenseAddDate); //날짜 설정


        tv_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker((TextView) v);
            }
        });


        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("지출등록");

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);



    }

    @Override
    protected  void onStart(){
        super.onStart();

        databaseRef.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String  text = dataSnapshot.getValue(String.class);

                /* arrayExpense.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String edate = data.child("edate").getValue().toString();
                    String echarge = data.child("echarge").getValue().toString();
                    String econtext= data.child("econtext").getValue().toString();
                    String egroup= data.child("egroup").getValue().toString();
                    ExpenseDTO expenseDTO = new ExpenseDTO(econtext,egroup,echarge);
                    arrayExpense.add(expenseDTO);

                }

                */


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));

        final Intent intent_EEdit = new Intent(this, EditExpense.class);
        final Intent intent_EMain = new Intent(this, MainExpense.class);
        btn_ExpenseReturnByAdd = findViewById(R.id.btn_ExpenseReturnByAdd);


        btn_ExpenseReturnByAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    RadioButton rd=(RadioButton)findViewById(rg_group.getCheckedRadioButtonId());

                    databaseRef.child("edate").setValue(tv_Date.getText().toString());
                    databaseRef.child("echarge").setValue(et_charge.getText().toString());
                    databaseRef.child("econtext").setValue(et_Context.getText().toString());
                    databaseRef.child("eoption").setValue(et_option.getText().toString());
                    databaseRef.child("egroup").setValue(rd.getText().toString());

                    finish();


            }


            });
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

    /**
     * 툴바에 있는 항목과 메뉴 네비게이션의 select 이벤트를 처리하는 메소드
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : //왼쪽 메뉴 버튼을 눌렀을 때
                slide_menu.toggleLeftDrawer(); //슬라이드 동작

                //navigation_menu.xml 이벤트 처리
                //현재 회원의 정보 설정
                nav_userName = (TextView) findViewById(R.id.nav_userName);
                nav_userEmail = (TextView) findViewById(R.id.nav_userEmail);
                nav_userName.setText(firebaseAuth.getCurrentUser().getDisplayName() + "님");
                nav_userEmail.setText(firebaseAuth.getCurrentUser().getEmail());

                ImageView menu_close = (ImageView)findViewById(R.id.menu_close);
                menu_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slide_menu.closeLeftSide();
                    }
                });

                //로그아웃
                Button logoutBtn = (Button) findViewById(R.id.logoutBtn);
                logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        Intent intent = new Intent(AddExpense.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = (Button) findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(AddExpense.this, SalesManagerMainActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = (Button)findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(AddExpense.this, pastSalesMode.class);
                        startActivity(intent);
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }




}


