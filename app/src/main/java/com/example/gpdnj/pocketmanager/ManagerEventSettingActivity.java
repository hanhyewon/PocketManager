package com.example.gpdnj.pocketmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navdrawer.SimpleSideDrawer;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

public class ManagerEventSettingActivity extends AppCompatActivity implements EventListviewAdapter.BtnClickListener {

    Toolbar toolbar;
    SimpleSideDrawer slide_menu;

    private ListView eventListview;
    private EventListviewAdapter eventAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseRef;

    static ArrayList<EventDTO> arrayEvent = new ArrayList<EventDTO>();

    Intent detailIntent, modifyIntent;
    boolean managerCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_event_setting);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("행사");
        if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals("관리자")) {
            managerCheck = true;
        } else {
            managerCheck = false;
        }

        detailIntent = new Intent(ManagerEventSettingActivity.this, EventDetailActivity.class);
        modifyIntent = new Intent(ManagerEventSettingActivity.this, EventModifyActivity.class);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("행사정보");

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);

        eventListview = findViewById(R.id.eventListview);

        setAdapter();
        displayEventList();

        //행사 상세정보 보기
        eventListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventDTO eventDTO = (EventDTO) parent.getAdapter().getItem(position);
                detailIntent.putExtra("eventId", eventDTO.getEventId()); //선택한 행사의 ID 넘기기
                startActivity(detailIntent);
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });

        //행사등록 버튼
        ImageView eventInputBtn = findViewById(R.id.eventInputBtn);
        if(managerCheck) {
            eventInputBtn.setVisibility(View.VISIBLE);
            eventInputBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ManagerEventSettingActivity.this, EventAddActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            eventInputBtn.setVisibility(View.GONE);
        }
    }

    private void setAdapter() {
        eventAdapter = new EventListviewAdapter(this.getBaseContext(), this, managerCheck);
        eventListview.setAdapter(eventAdapter);
    }

    @Override
    public void deleteBtnClickListener(final String eventId) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        dialog.setMessage("선택한 행사정보를 삭제하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v("선택한 행사의 ID", "출력 " + eventId);

                        databaseRef.child(eventId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ManagerEventSettingActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
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

    @Override
    public void modifyBtnClickListener(String eventId) {
        modifyIntent.putExtra("eventId", eventId); //선택한 행사의 ID 넘기기
        startActivity(modifyIntent);
        //Toast.makeText(this, eventId + "수정", Toast.LENGTH_SHORT).show();
    }


    //행사 DB 정보 출력
    private void displayEventList() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayEvent.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String eventId = data.getKey();

                    String title = data.child("title").getValue().toString();
                    String subTitle = data.child("subTitle").getValue().toString();
                    String date = data.child("date").getValue().toString();
                    String imgUrl = data.child("imgUrl").getValue().toString();

                    EventDTO eventDTO = new EventDTO(eventId, title, subTitle, date, null, imgUrl);
                    arrayEvent.add(eventDTO);
                }
                eventAdapter.addItems(arrayEvent);
                Collections.reverse(arrayEvent); //최신정렬
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        slide_menu.closeRightSide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : { //왼쪽 메뉴 버튼을 눌렀을 때
                slide_menu.toggleLeftDrawer(); //슬라이드 동작

                //navigation_menu.xml 이벤트 처리
                //현재 회원의 정보 설정
                TextView nav_userName = findViewById(R.id.nav_userName);
                TextView nav_userEmail = findViewById(R.id.nav_userEmail);
                nav_userName.setText(firebaseAuth.getCurrentUser().getDisplayName() + "님");
                nav_userEmail.setText(firebaseAuth.getCurrentUser().getEmail());

                //메뉴창 닫기
                ImageView menu_close = findViewById(R.id.menu_close);
                menu_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slide_menu.closeLeftSide();
                    }
                });

                //로그아웃
                Button logoutBtn = findViewById(R.id.logoutBtn);
                logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        Intent intent = new Intent(ManagerEventSettingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(ManagerEventSettingActivity.this, SalesManagerActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(ManagerEventSettingActivity.this, RevenueMainActivity.class);
                        startActivity(intent);
                    }
                });

                //행사정보
                Button eventBtn = findViewById(R.id.eventBtn);
                eventBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(ManagerEventSettingActivity.this, ManagerEventSettingActivity.class);
                        startActivity(intent);
                    }
                });

                //커뮤니티
                Button communityBtn = findViewById(R.id.communityBtn);
                communityBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(ManagerEventSettingActivity.this, ReviewMainActivity.class);
                        startActivity(intent);
                    }
                });

                //내 게시글 관리
                TextView myReviewSettingBtn = findViewById(R.id.myReviewSettingBtn);
                myReviewSettingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(ManagerEventSettingActivity.this, MyReviewActivity.class);
                        startActivity(intent);
                    }
                });

                return true;
            }
            case R.id.homeMove: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
