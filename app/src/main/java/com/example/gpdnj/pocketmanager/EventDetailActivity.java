package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EventDetailActivity extends AppCompatActivity {

    Toolbar toolbar;

    //위치도 나중에 추가해야 함
    TextView eventTitleView, eventDateView, eventDetailTextView;
    ImageView eventImgView;

    String eventId;

    FirebaseDatabase database;
    DatabaseReference databaseRef;
    StorageReference imgRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("행사");

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("행사 상세정보");

        eventTitleView = findViewById(R.id.eventTitleView);
        eventDateView = findViewById(R.id.eventDateView);
        eventDetailTextView = findViewById(R.id.eventDetailTextView);
        eventImgView = findViewById(R.id.eventImgView);

        eventId = getIntent().getStringExtra("eventId");

        databaseRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                eventTitleView.setText(data.child("title").getValue().toString());
                eventDateView.setText(data.child("date").getValue().toString());
                eventDetailTextView.setText(data.child("detailText").getValue().toString());

                imgRef = FirebaseStorage.getInstance().getReference(data.child("imgUrl").getValue().toString()); //해당 경로명으로 참조하는 파일명 지정
                imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() { //다운로드 Url 가져옴
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Glide.with(EventDetailActivity.this).load(task.getResult()).into(eventImgView); //해당 이미지로 세팅
                    }
                });
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
