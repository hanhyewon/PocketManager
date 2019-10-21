package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class ReviewAddActivity extends AppCompatActivity {

    Toolbar toolbar;
    Spinner category;
    EditText reviewTitle, reviewEventName, reviewLocation, reviewDetailText;
    TextView reviewSalesDate, reviewSalesLocation;
    RatingBar reviewRatingBar;
    Button reviewDataAddBtn;

    String selectedCategory, title, eventName, salesDate, detailText, reviewDate, uid, userName, location;
    float ratingScore = -1f;
    private int REQUEST_TEST = 1;

    DatabaseReference databaseRef;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_add);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("리뷰 등록");

        category = findViewById(R.id.category);
        reviewTitle = findViewById(R.id.reviewTitle);
        reviewEventName = findViewById(R.id.reviewEventName);
        reviewSalesDate = findViewById(R.id.reviewSalesDate);
        reviewRatingBar = findViewById(R.id.reviewRatingBar);
        reviewDetailText = findViewById(R.id.reviewDetailText);
        reviewDataAddBtn = findViewById(R.id.reviewDataAddBtn);
        reviewSalesLocation = findViewById(R.id.reviewLocation);

        final Intent intent_Map = new Intent(this, MapSearchActivity.class);

        //Spinner 설정
        String[] str = getResources().getStringArray(R.array.reviewArray);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.review_spinner_item, str);
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

        reviewDataAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDataAdd();
            }
        });

        reviewSalesLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewAddActivity.this, MapSearchActivity.class);
                startActivityForResult(intent, REQUEST_TEST);
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
                Toast.makeText(ReviewAddActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //리뷰글 DB 등록
    private void reviewDataAdd() {
        ReviewDTO reviewDTO;

        uid = firebaseAuth.getUid();
        userName = firebaseAuth.getCurrentUser().getDisplayName();
        title = reviewTitle.getText().toString();
        detailText = reviewDetailText.getText().toString();
        reviewDate = new SimpleDateFormat("yyyy.MM.dd").format(new Date());

        if(selectedCategory.equals("행사")) {
            eventName = reviewEventName.getText().toString();
            reviewDTO = new ReviewDTO(uid, userName, selectedCategory, title, eventName, location, salesDate, ratingScore, detailText, reviewDate);
        } else {
            reviewDTO = new ReviewDTO(uid, userName, selectedCategory, title, location, salesDate, ratingScore, detailText, reviewDate);
        }

        databaseRef.child("리뷰").push().setValue(reviewDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ReviewAddActivity.this, "등록 완료!", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
