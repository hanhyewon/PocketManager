package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;
import java.util.Collections;

public class ReviewMainActivity extends AppCompatActivity {

    Toolbar toolbar;
    SimpleSideDrawer slide_menu;
    FloatingActionButton reviewAddBtn;
    Intent addIntent, detailIntent;
    Spinner rvSpinner;
    final String[] showSpinner = {"카테고리","행사", "팝업스토어","푸드트럭","플리마켓","기타"};

    private ListView reviewListview;
    private ReviewListviewAdapter reviewAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseRef;

    static ArrayList<ReviewDTO> arrayReview = new ArrayList<ReviewDTO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_main);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("리뷰");

        addIntent = new Intent(ReviewMainActivity.this, ReviewAddActivity.class);
        detailIntent = new Intent(ReviewMainActivity.this, ReviewDetailActivity.class);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("커뮤니티");

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);

        //리뷰 어댑터와 리스트뷰 연결
        reviewListview = findViewById(R.id.reviewListview);
        setAdapter();

        //카테고리 스피너 검색
        rvSpinner = findViewById(R.id.rvSpinner1);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.category_spinner, showSpinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        rvSpinner.setAdapter(spinnerAdapter);

        rvSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spItem = rvSpinner.getItemAtPosition(position).toString();
                displaySpinnerList(spItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //리뷰 등록화면으로 이동
        reviewAddBtn = findViewById(R.id.reviewAddBtn);
        reviewAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(addIntent);
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });

        //리뷰 상세정보 보기
        reviewListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReviewDTO reviewDTO = (ReviewDTO) parent.getAdapter().getItem(position);
                detailIntent.putExtra("reviewId", reviewDTO.getReviewId()); //선택한 리뷰의 ID 넘기기
                startActivity(detailIntent);
                overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
            }
        });
    }

    //리뷰 어댑터와 리스트뷰 연결
    private void setAdapter() {
        reviewAdapter = new ReviewListviewAdapter(this.getBaseContext());
        reviewListview.setAdapter(reviewAdapter);
    }

    //리뷰 DB 정보 출력
    private void displaySpinnerList(final String spItem) {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayReview.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String reviewId = data.getKey();
                    String reviewUid = (String) data.child("uid").getValue();

                    final String category = (String) data.child("category").getValue();
                    String title = (String) data.child("title").getValue();
                    String reviewDate = (String) data.child("reviewDate").getValue();
                    String detailText = (String) data.child("detailText").getValue();

                    if(spItem.equals(category)) {
                        //카테고리 스피너 선택했을 경우에는 해당 카테고리 리뷰만 보여주기
                        ReviewDTO reviewDTO = new ReviewDTO(reviewId, reviewUid, category, title, reviewDate, detailText);
                        arrayReview.add(reviewDTO);
                    } else if(spItem.equals("카테고리")) {
                        //카테고리 선택 안했을 경우에는 전부 보여주기
                        ReviewDTO reviewDTO = new ReviewDTO(reviewId, reviewUid, category, title, reviewDate, detailText);
                        arrayReview.add(reviewDTO);
                    }
                }
                reviewAdapter.addItems(arrayReview);
                Collections.reverse(arrayReview); //최신정렬
                reviewAdapter.notifyDataSetChanged();
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
                        Intent intent = new Intent(ReviewMainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(ReviewMainActivity.this, SalesManagerActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(ReviewMainActivity.this, RevenueMainActivity.class);
                        startActivity(intent);
                    }
                });

                //행사정보
                Button eventBtn = findViewById(R.id.eventBtn);
                eventBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(ReviewMainActivity.this, ManagerEventSettingActivity.class);
                        startActivity(intent);
                    }
                });

                //커뮤니티
                Button communityBtn = findViewById(R.id.communityBtn);
                communityBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(ReviewMainActivity.this, ReviewMainActivity.class);
                        startActivity(intent);
                    }
                });

                //내 게시글 관리
                TextView myReviewSettingBtn = findViewById(R.id.myReviewSettingBtn);
                myReviewSettingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(ReviewMainActivity.this, MyReviewActivity.class);
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
