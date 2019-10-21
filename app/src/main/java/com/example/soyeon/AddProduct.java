package com.example.soyeon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpdnj.pocketmanager.MainActivity;
import com.example.gpdnj.pocketmanager.R;
import com.example.gpdnj.pocketmanager.SalesManagerActivity;
import com.example.jiyeong.pastSalesMode;
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
import com.navdrawer.SimpleSideDrawer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProduct extends AppCompatActivity {

    Toolbar toolbar;

    EditText et_ProductNameAdd, et_ProductPriceAdd;
    Button btn_ProductAdd;
    ImageButton btn_ProductImgAdd;
    ImageView ivPreview;

    Uri imgUri;
    String img;

    String salesId;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_add);
        salesId = getIntent().getStringExtra("salesId");

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("상품/" + salesId);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("상품등록");

        et_ProductNameAdd = findViewById(R.id.et_ProductAddName);
        et_ProductPriceAdd = findViewById(R.id.et_ProductAddPrice);
        btn_ProductAdd =  findViewById(R.id.btn_pushProductAdd);
        btn_ProductImgAdd = findViewById(R.id.btn_ProductAddImage);
        ivPreview = findViewById(R.id.iv_preview);

        //이미지 선택 버튼 클릭시
        btn_ProductImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        //등록하기
        btn_ProductAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //업로드할 이미지 파일이 있으면 수행
                if (imgUri != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_kkmmss");
                    String filename = formatter.format(new Date());

                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://pocket-manager-9207f.appspot.com/");
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://pocket-manager-9207f.appspot.com").child("Product/" + filename);

                    img = "Product/" + filename; //DB에 이미지 정보 담기 위한 String 객체

                    storageRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            productDataAdd();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProduct.this, "등록에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            addProgressDialog();
                        }
                    });
                }
                else {
                    //이미지 파일이 없는 경우
                    img = "Product/question-mark-1750942_1280.png";
                    productDataAdd();
                }
            }
        });

    }

    //상품정보 DB 등록
    private void productDataAdd() {
        String name = et_ProductNameAdd.getText().toString();
        String price = et_ProductPriceAdd.getText().toString();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(price)) {
            ProductDTO productDTO = new ProductDTO(name, price, img);
            databaseRef.push().setValue(productDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
                }
            });
        } else {
            Toast.makeText(this, "모두 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    //선택한 이미지 파일을 화면에 바로 보여주는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            imgUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                ivPreview.setImageBitmap(bitmap);
                ivPreview.setBackground(getDrawable(R.drawable.rounded_transparent));
                ivPreview.setClipToOutline(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
