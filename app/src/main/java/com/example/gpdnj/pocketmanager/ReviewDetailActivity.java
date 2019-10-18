package com.example.gpdnj.pocketmanager;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class ReviewDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;

    Toolbar toolbar;
    TextView reviewCategoryView, reviewTitleView, reviewWriterDateView, reviewEventNameView, reviewSalesDateView, reviewDetailTextView;
    TextView reviewEventNameRow;
    RatingBar reviewRatingView;

    String reviewId, userName;
    private String address;

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

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.r_map);
        mapFragment.getMapAsync(this);

        reviewId = getIntent().getStringExtra("reviewId");
        databaseReviewRef.child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                reviewCategoryView.setText((String)data.child("category").getValue());
                reviewTitleView.setText((String)data.child("title").getValue());
                reviewSalesDateView.setText((String)data.child("salesDate").getValue());
                reviewDetailTextView.setText((String)data.child("detailText").getValue());

                float rate = Float.valueOf(data.child("rating").getValue().toString());
                address = (String)data.child("location").getValue();
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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        if(address == null) {
            address = getIntent().getStringExtra("Location");
        }

        String str= address;
        List<Address> addressList = null;
        try {
            // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
            addressList = geocoder.getFromLocationName(str,10);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(addressList.get(0).toString());
        // 콤마를 기준으로 split
        String []splitStr = addressList.get(0).toString().split(",");
        address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
        System.out.println(address);

        String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
        String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
        System.out.println(latitude);
        System.out.println(longitude);

        // 좌표(위도, 경도) 생성
        LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        // 마커 생성
        MarkerOptions mOptions2 = new MarkerOptions();
        mOptions2.title("search result");
        mOptions2.snippet(address);
        mOptions2.position(point);
        // 마커 추가
        mMap.addMarker(mOptions2);
        // 해당 좌표로 화면 줌
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
//Toast.makeText(getApplicationContext(), getIntent().getStringExtra("reviewLocation"), Toast.LENGTH_SHORT).show();