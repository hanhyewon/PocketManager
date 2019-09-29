package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewMainActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton reviewAddBtn;
    Intent addIntent, detailIntent;

    private ListView reviewListview;
    private ReviewListviewAdapter reviewAdapter;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    static ArrayList<ReviewDTO> arrayReview = new ArrayList<ReviewDTO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_main);

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

        reviewListview = findViewById(R.id.reviewListview);
        setAdapter();
        displayReviewList();

        //리뷰 등록화면으로 이동
        reviewAddBtn = findViewById(R.id.reviewAddBtn);
        reviewAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(addIntent);
            }
        });

        //리뷰 상세정보 보기
        reviewListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReviewDTO reviewDTO = (ReviewDTO) parent.getAdapter().getItem(position);
                detailIntent.putExtra("reviewId", reviewDTO.getReviewId()); //선택한 리뷰의 ID 넘기기
                startActivity(detailIntent);
            }
        });
    }

    //리뷰 어댑터와 리스트뷰 연결
    private void setAdapter() {
        reviewAdapter = new ReviewListviewAdapter(this.getBaseContext());
        reviewListview.setAdapter(reviewAdapter);
    }

    //리뷰 DB 정보 출력
    private void displayReviewList() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayReview.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String reviewId = data.getKey();
                    String reviewUid = (String) data.child("uid").getValue();

                    String category = (String) data.child("category").getValue();
                    String title = (String) data.child("title").getValue();
                    String reviewDate = (String) data.child("reviewDate").getValue();
                    String detailText = (String) data.child("detailText").getValue();

                    ReviewDTO reviewDTO = new ReviewDTO(reviewId, reviewUid, category, title, reviewDate, detailText);
                    arrayReview.add(reviewDTO);
                }
                reviewAdapter.addItems(arrayReview);
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
