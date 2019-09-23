package com.example.gpdnj.pocketmanager;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserSettingActivity extends AppCompatActivity {

    Toolbar toolbar;

    private ListView userListview;
    private ManagerUserListviewAdapter managerUserAdapter;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    static ArrayList<UserDTO> arrayUser = new ArrayList<UserDTO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("회원");

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("회원관리");

        userListview = findViewById(R.id.userListView);
        managerUserAdapter = new ManagerUserListviewAdapter(this.getBaseContext());

        //회원 DB 정보 출력
        displayUserList();

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        userListview.setAdapter(managerUserAdapter);

        userListview.setLongClickable(true);
        userListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                dialog.setMessage("선택한 회원을 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //ListView에서 롱클릭으로 선택한 item에 저장되어있는 UserDTO 객체를 가져옴
                                final UserDTO userDTO = (UserDTO) parent.getAdapter().getItem(position);
                                Log.v("선택한 회원의 UID", "출력 " + userDTO.getUid());

                                databaseRef.child(userDTO.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //회원 DB의 정보만 삭제가능, Firebase의 Auth는 삭제 불가
                                        Toast.makeText(UserSettingActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
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
                return false;
            }
        });
    }

    //회원 DB 정보 출력
    public void displayUserList() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayUser.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String uid = data.getKey();
                    String name = data.child("name").getValue().toString();
                    String email = data.child("email").getValue().toString();

                    UserDTO userDTO = new UserDTO(uid, name, email);
                    arrayUser.add(userDTO);
                }
                managerUserAdapter.addItems(arrayUser);
                managerUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
