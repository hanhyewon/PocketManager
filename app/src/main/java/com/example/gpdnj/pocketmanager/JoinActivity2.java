package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class JoinActivity2 extends AppCompatActivity {

    Toolbar toolbar;
    private Button signup_btn;

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // 이름, 이메일, 비밀번호, 비밀번호 확인
    Boolean isEmptyName, isEmptyEmail, isEmptyPw, isEmptyPwCheck;
    private EditText editTextName, editTextEmail, editTextPassword, editTextPwCheck;
    private String name = "", email = "", password = "", pwCheck = "";

    // 이메일 회원가입시 비밀번호 체크를 위한 정규식
    /*
     * 하나 이상의 알파벳을 포함해야 함
     * 하나 이상의 숫자를 포함해야 함
     * 6~20자 이내로 입력해야 함
     */
    String pwPattern = "^(?=.*[a-zA-Z])(?=.*[0-9]).{6,20}$";
    Boolean isValidPassword, isValidEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);

        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("회원가입");

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        editTextName = (EditText) findViewById(R.id.edittext_name);
        editTextEmail = (EditText) findViewById(R.id.edittext_email);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);
        editTextPwCheck = (EditText) findViewById(R.id.edittext_pwCheck);
        signup_btn = (Button) findViewById(R.id.signup_btn);

        // 이메일을 통한 회원가입
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextName.getText().toString();
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                pwCheck = editTextPwCheck.getText().toString();

                // 이름, 이메일, 비밀번호, 비밀번호 확인까지 모두 입력해야 함
                isEmptyName = TextUtils.isEmpty(name);
                isEmptyEmail = TextUtils.isEmpty(email);
                isEmptyPw = TextUtils.isEmpty(password);
                isEmptyPwCheck = TextUtils.isEmpty(pwCheck);

                if(!isEmptyName && !isEmptyEmail && !isEmptyPw && !isEmptyPwCheck) {

                    // 이메일과 비밀번호 유효성 검사
                    isValidPassword = Pattern.matches(pwPattern, password);
                    isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

                    if(isValidEmail == true && isValidPassword == true) {
                        // 비밀번호 중복 확인
                        if(password.equals(pwCheck)) {
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(JoinActivity2.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                createUser();
                                                Toast.makeText(JoinActivity2.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(JoinActivity2.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(JoinActivity2.this, "이미 등록된 이메일 입니다", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(JoinActivity2.this, "동일한 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                        }

                    }
                    if(isValidEmail == false) {
                        Toast.makeText(JoinActivity2.this, "이메일 형식에 맞게 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                    else if(isValidPassword == false) {
                        Toast.makeText(JoinActivity2.this, "비밀번호 형식에 맞게 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(JoinActivity2.this, "모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void createUser() {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference();
        //DatabaseReference newUserDB = userDB.push();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String uid = firebaseAuth.getUid();
        String name = editTextName.getText().toString();
        String email = user.getEmail();
        //String photoUrl = user.getPhotoUrl().toString();

        UserDTO userDTO = new UserDTO(name, email);
        userDB.child("users").child(uid).setValue(userDTO);
    }

    //툴바 select
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : { //뒤로가기 버튼 눌렀을 때
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
