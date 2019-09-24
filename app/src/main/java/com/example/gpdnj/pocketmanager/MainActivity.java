package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soyeon.MainProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //private static final String TAG = "tag";

    private EditText editEmail, editPassword;
    private TextView join;
    private Button emailLoginBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //실험용 추가 인텐트
        //final Intent intent_PMain = new Intent(this, MainProduct.class);
        //startActivity(intent_PMain);

        firebaseAuth = FirebaseAuth.getInstance();
        //getHashKey();

        //로그인 세션을 체크하는 부분
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.getDisplayName().equals("관리자")) {
                        //관리자가 로그인 한 상태
                        Intent intent2 = new Intent(MainActivity.this, ManagerHomeActivity.class);
                        startActivity(intent2);
                        finish();
                    }
                    else {
                        //일반 회원이 로그인 한 상태
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };

        //이메일 계정으로 로그인
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        emailLoginBtn = findViewById(R.id.emailLoginBtn);

        emailLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin(editEmail.getText().toString(), editPassword.getText().toString());
            }
        });

        // 회원가입 이동
        join = findViewById(R.id.join);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JoinActivity1.class);
                startActivity(intent);
            }
        });

        //소셜 계정으로 로그인
        Button socialLoginBtn = findViewById(R.id.socialLoginBtn);
        socialLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SocialLoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void emailLogin(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    if (firebaseAuth.getCurrentUser().getDisplayName().equals("관리자")) {
                        //관리자가 로그인 한 상태
                        Intent intent2 = new Intent(MainActivity.this, ManagerHomeActivity.class);
                        startActivity(intent2);
                        finish();
                    }
                    else {
                        //일반 회원이 로그인 한 상태
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "이메일 또는 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 앱이 실행될 때 Listener를 설정
    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    // 앱이 중지될 때 Listener를 해지
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /*
    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, "key_hash=" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    */
}
