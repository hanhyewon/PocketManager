package com.example.gpdnj.pocketmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class EventAddActivity extends AppCompatActivity {

    Toolbar toolbar;

    TextView eventDate;
    EditText eventTitle, eventSubTitle, eventDetailText;
    ImageButton eventImgAddBtn;
    ImageView eventImgPreview;
    Button eventDataAddBtn;

    String title, subTitle, detailText, date, img;

    Uri imgUri;

    DatabaseReference databaseRef;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("행사등록");

        eventTitle = findViewById(R.id.eventTitle);
        eventSubTitle = findViewById(R.id.eventSubTitle);
        eventDetailText = findViewById(R.id.eventDetailText);
        eventDate = findViewById(R.id.eventDate);
        eventDataAddBtn = findViewById(R.id.eventDataAddBtn);
        eventImgAddBtn = findViewById(R.id.eventImgAddBtn);
        eventImgPreview = findViewById(R.id.eventImgPreview);

        //이미지 선택
        eventImgAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);
            }
        });

        //기간 선택
        eventDate.setOnClickListener(new View.OnClickListener() {
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

        //등록하기
        eventDataAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //업로드할 이미지 파일이 있으면 수행
                if (imgUri != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_kkmmss");
                    String filename = formatter.format(new Date());

                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://pocket-manager-9207f.appspot.com/");
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://pocket-manager-9207f.appspot.com").child("Event/" + filename);

                    img = "Event/" + filename; //DB에 이미지 정보 담기 위한 String 객체

                    storageRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            eventDataAdd();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EventAddActivity.this, "등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            addProgressDialog();
                        }
                    });
                }
                else {
                    eventDataAdd();
                }
            }
        });
    }

    //행사정보 DB 등록
    private void eventDataAdd() {
        title = eventTitle.getText().toString();
        subTitle = eventSubTitle.getText().toString();
        detailText = eventDetailText.getText().toString();

        EventDTO eventDTO = new EventDTO(title, subTitle, date, detailText, img);

        databaseRef.child("행사").push().setValue(eventDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EventAddActivity.this, "등록완료", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    //선택한 이미지 파일을 화면에 바로 보여주는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            imgUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                eventImgPreview.setImageBitmap(bitmap);
                eventImgPreview.setBackground(getDrawable(R.drawable.rounded_transparent));
                eventImgPreview.setClipToOutline(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                    date = new SimpleDateFormat("yy.MM.dd(E)").format(firstDate.getTime());
                    //eventDate.setText(date.substring(3, 11));
                    eventDate.setText(date);
                } else {
                    date = new SimpleDateFormat("yy.MM.dd(E)").format(firstDate.getTime())
                            + " - " + new SimpleDateFormat("yy.MM.dd(E)").format(secondDate.getTime());
                    //String str = date.substring(3, 14) + date.substring(17, 25);
                    eventDate.setText(date);
                }
            }
        }
    };

    //ProgressDialog
    public void addProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("등록 중...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();
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
