package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soyeon.MapSearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class ReviewModifyActivity extends AppCompatActivity {

    Toolbar toolbar;
    Spinner category;
    EditText reviewTitle, reviewEventName, reviewLocation, reviewDetailText;
    TextView reviewSalesDate, reviewSalesLocation;
    RatingBar reviewRatingBar;
    Button reviewDataModifyBtn;

    String selectedCategory, title, eventName, salesDate, detailText, reviewDate, uid, userName, reviewId, location;
    float ratingScore = -1f;
    private int REQUEST_TEST = 1;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_modify);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("리뷰");

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("리뷰수정");

        category = findViewById(R.id.mCategory);
        reviewTitle = findViewById(R.id.mReviewTitle);
        reviewEventName = findViewById(R.id.mReviewEventName);
        reviewSalesDate = findViewById(R.id.mReviewSalesDate);
        reviewRatingBar = findViewById(R.id.mReviewRatingBar);
        reviewDetailText = findViewById(R.id.mReviewDetailText);
        reviewDataModifyBtn = findViewById(R.id.mReviewDataModifyBtn);
        reviewSalesLocation = findViewById(R.id.mReviewLocation);

        reviewId = getIntent().getStringExtra("reviewId");

        //Spinner 설정
        String[] str = getResources().getStringArray(R.array.reviewArray);
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.review_spinner_item, str);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        category.setAdapter(spinnerAdapter);

        //카테고리
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = category.getSelectedItem().toString();

                //카테고리-행사일 때만, 행사명 입력받기
                if(selectedCategory.equals("행사")) {
                    reviewEventName.setVisibility(View.VISIBLE);
                } else {
                    reviewEventName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //만족도(별점)
        reviewRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingScore = rating;
            }
        });

        //기간 선택
        reviewSalesDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlyCalendarDialog()
                        .setSingle(false)
                        .setFirstMonday(false)
                        .setHeaderColor(Color.parseColor("#0eafc4"))
                        .setSelectedColor(Color.parseColor("#0eafc4"))
                        .setCallback(callback)
                        .show(getSupportFragmentManager(), "TAG_SLYCALENDAR");
            }
        });

        reviewSalesLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewModifyActivity.this, MapSearchActivity.class);
                startActivityForResult(intent, REQUEST_TEST);
            }
        });

        //선택한 리뷰 DB 정보 세팅
        databaseRef.child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                selectedCategory = (String) data.child("category").getValue();
                int spinnerPosition = spinnerAdapter.getPosition(selectedCategory);
                category.setSelection(spinnerPosition);

                reviewTitle.setText((String)data.child("title").getValue());

                if(selectedCategory.equals("행사")) {
                    reviewEventName.setText((String)data.child("eventName").getValue());
                }
                salesDate = (String) data.child("salesDate").getValue();
                reviewSalesDate.setText(salesDate);

                float rate = Float.valueOf(data.child("rating").getValue().toString());

                reviewRatingBar.setRating(rate);

                reviewDetailText.setText((String)data.child("detailText").getValue());
                reviewDate = (String) data.child("reviewDate").getValue();
                uid = (String) data.child("uid").getValue();
                userName = (String) data.child("userName").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //리뷰 DB 수정
        reviewDataModifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDataModify();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEST) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(ReviewAddActivity.this, "Result: " + data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
                reviewSalesLocation.setText(data.getStringExtra("result"));
                location = data.getStringExtra("result");
            } else {   // RESULT_CANCEL
                Toast.makeText(ReviewModifyActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void reviewDataModify() {
        ReviewDTO reviewDTO;
        title = reviewTitle.getText().toString();
        detailText = reviewDetailText.getText().toString();

        if(selectedCategory.equals("행사")) {
            eventName = reviewEventName.getText().toString();
            reviewDTO = new ReviewDTO(uid, userName, selectedCategory, title, eventName, location, salesDate, ratingScore, detailText, reviewDate);
        } else {
            reviewDTO = new ReviewDTO(uid, userName, selectedCategory, title, location, salesDate, ratingScore, detailText, reviewDate);
        }

        databaseRef.child(reviewId).setValue(reviewDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ReviewModifyActivity.this, "수정 완료!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    //기간 다이얼로그 콜백 메소드
    SlyCalendarDialog.Callback callback = new SlyCalendarDialog.Callback() {
        @Override
        public void onCancelled() {

        }

        @Override
        public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {
            if (firstDate != null) {
                if (secondDate == null) {
                    salesDate = new SimpleDateFormat("yy.MM.dd(E)").format(firstDate.getTime());
                    //eventDate.setText(date.substring(3, 11));
                    reviewSalesDate.setText(salesDate);
                } else {
                    salesDate = new SimpleDateFormat("yy.MM.dd(E)").format(firstDate.getTime())
                            + " - " + new SimpleDateFormat("yy.MM.dd(E)").format(secondDate.getTime());
                    //String str = date.substring(3, 14) + date.substring(17, 25);
                    reviewSalesDate.setText(salesDate);
                }
            }
        }
    };
}
