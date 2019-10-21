package com.example.gpdnj.pocketmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.baoyz.actionsheet.ActionSheet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MyReviewActivity extends AppCompatActivity implements MyReviewListviewAdapter.BtnClickListener, ActionSheet.ActionSheetListener {

    Toolbar toolbar;
    private ListView myReviewListview;
    private MyReviewListviewAdapter myReviewAdapter;
    Intent detailIntent, modifyIntent;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseRef;

    String currentUid, selectedReviewId;
    static ArrayList<ReviewDTO> arrayReview = new ArrayList<ReviewDTO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("리뷰");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUid = firebaseAuth.getUid(); //현재 접속 중인 회원의 Uid 식별

        detailIntent = new Intent(MyReviewActivity.this, ReviewDetailActivity.class);
        modifyIntent = new Intent(MyReviewActivity.this, ReviewModifyActivity.class);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("내 게시글 관리");

        myReviewListview = findViewById(R.id.myReviewListview);
        setAdapter();
        displayReviewList();

        //리뷰 상세정보 보기
        myReviewListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReviewDTO reviewDTO = (ReviewDTO) parent.getAdapter().getItem(position);
                detailIntent.putExtra("reviewId", reviewDTO.getReviewId()); //선택한 리뷰의 ID 넘기기
                startActivity(detailIntent);
                overridePendingTransition(R.anim.not_move_activity, R.anim.right_out_activity);
            }
        });
    }

    @Override
    public void moreBtnClickListener(String reviewId) {
        //더보기 버튼 클릭 시, 수정/삭제 메뉴 보여주기
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("수정", "삭제")
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
        setTheme(R.style.modifyDeleteTheme);
        this.selectedReviewId = reviewId;
        Log.v("테스트", "선택한 리뷰ID : " + reviewId);
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if(index == 0) {
            //수정을 선택했을 때
            modifyIntent.putExtra("reviewId", selectedReviewId); //선택한 리뷰의 ID 넘기기
            startActivity(modifyIntent);
        } else {
            //삭제를 선택했을 때
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
            dialog.setMessage("해당 글을 삭제하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseRef.child(selectedReviewId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MyReviewActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    //리뷰 어댑터와 리스트뷰 연결
    private void setAdapter() {
        myReviewAdapter = new MyReviewListviewAdapter(this.getBaseContext(), this);
        myReviewListview.setAdapter(myReviewAdapter);
    }

    //MY 리뷰 DB 정보 출력
    private void displayReviewList() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayReview.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.child("uid").getValue().toString().equals(currentUid)) {
                        String reviewId = data.getKey();
                        String reviewUid = (String) data.child("uid").getValue();

                        String category = (String) data.child("category").getValue();
                        String title = (String) data.child("title").getValue();
                        String reviewDate = (String) data.child("reviewDate").getValue();
                        String detailText = (String) data.child("detailText").getValue();
                        String location = (String) data.child("location").getValue();

                        ReviewDTO reviewDTO = new ReviewDTO(reviewId, reviewUid, category, title, reviewDate, detailText, location);
                        arrayReview.add(reviewDTO);
                    }
                }
                myReviewAdapter.addItems(arrayReview);
                Collections.reverse(arrayReview); //최신정렬
                myReviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
