package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.omega_r.libs.OmegaCenterIconButton;

public class ManagerHomeActivity extends AppCompatActivity {

    private TextView managerName;
    private TextView managerEmail;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        firebaseAuth = FirebaseAuth.getInstance();

        //현재 접속한 관리자의 정보 설정
        managerName = findViewById(R.id.managerName);
        managerEmail = findViewById(R.id.managerEmail);
        managerName.setText(firebaseAuth.getCurrentUser().getDisplayName() + "님");
        managerEmail.setText(firebaseAuth.getCurrentUser().getEmail());

        //회원관리
        LinearLayout userSettingBtn = findViewById(R.id.userSettingBtn);
        userSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerHomeActivity.this, UserSettingActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout eventSettingBtn = findViewById(R.id.eventSettingBtn);
        eventSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerHomeActivity.this, ManagerEventSettingActivity.class);
                startActivity(intent);
            }
        });

        //관리자관리
        LinearLayout managerSettingBtn = findViewById(R.id.managerSettingBtn);
        managerSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerHomeActivity.this, ManagerSettingActivity.class);
                startActivity(intent);
            }
        });

        //로그아웃
        OmegaCenterIconButton managerLogoutBtn = findViewById(R.id.managerLogoutBtn);
        managerLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(ManagerHomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
