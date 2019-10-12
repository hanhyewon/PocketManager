package com.example.gpdnj.pocketmanager;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReviewDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView reviewCategoryView, reviewTitleView, reviewWriterDateView, reviewEventNameView, reviewSalesDateView, reviewDetailTextView;
    TextView reviewEventNameRow;
    RatingBar reviewRatingView;

    String reviewId, userName;

    FirebaseDatabase database;
    DatabaseReference databaseReviewRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        database = FirebaseDatabase.getInstance();
        databaseReviewRef = database.getReference("리뷰");

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("커뮤니티");

        reviewCategoryView = findViewById(R.id.reviewCategoryView);
        reviewTitleView = findViewById(R.id.reviewTitleView);
        reviewWriterDateView = findViewById(R.id.reviewWriterDateView);
        reviewEventNameView = findViewById(R.id.reviewEventNameView);
        reviewSalesDateView = findViewById(R.id.reviewSalesDateView);
        reviewDetailTextView = findViewById(R.id.reviewDetailTextView);

        reviewEventNameRow = findViewById(R.id.reviewEventNameRow);
        reviewRatingView = findViewById(R.id.reviewRatingView);

        reviewId = getIntent().getStringExtra("reviewId");

        databaseReviewRef.child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                reviewCategoryView.setText((String)data.child("category").getValue());
                reviewTitleView.setText((String)data.child("title").getValue());
                reviewSalesDateView.setText((String)data.child("salesDate").getValue());
                reviewDetailTextView.setText((String)data.child("detailText").getValue());

                float rate = Float.valueOf(data.child("rating").getValue().toString());
                reviewRatingView.setRating(rate);

                userName = (String)data.child("userName").getValue();
                String viewStr = userName + " | " + data.child("reviewDate").getValue(); // 작성자 l YYYY.MM.DD
                reviewWriterDateView.setText(viewStr);

                //카테고리에 따라 View 달라짐
                if(reviewCategoryView.getText().equals("행사")) {
                    reviewEventNameView.setVisibility(View.VISIBLE);
                    reviewEventNameView.setText((String)data.child("eventName").getValue());
                } else {
                    reviewEventNameRow.setVisibility(View.GONE);
                    reviewEventNameView.setVisibility(View.GONE);
                }
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
