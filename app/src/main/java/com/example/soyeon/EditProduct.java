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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gpdnj.pocketmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProduct extends AppCompatActivity {

    Toolbar toolbar;

    private EditText ed_productName_ed;
    private EditText ed_productPrice_ed;

    ImageView ivPreview;

    FirebaseDatabase database;
    DatabaseReference databaseRef;
    StorageReference imgRef;

    Uri imgUri;
    String img;
    String salesId, productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_edit);
        salesId = getIntent().getStringExtra("salesId");
        productId = getIntent().getStringExtra("productId");

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("상품/" + salesId + "/" + productId);

        //툴바 사용 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("상품수정");

        ed_productName_ed = findViewById(R.id.et_ProductEditName);
        ed_productPrice_ed = findViewById(R.id.et_ProductEditPrice);
        ivPreview = findViewById(R.id.iv_preview_edit);

        Button btn_PushProductEdit = findViewById(R.id.btn_pushProductEdit);
        ImageButton btn_PushProductImageChange = findViewById(R.id.btn_et_ProductImage);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                img = data.child("imgUrl").getValue().toString();

                ed_productName_ed.setText((String) data.child("name").getValue());
                ed_productPrice_ed.setText((String) data.child("price").getValue());

                imgRef = FirebaseStorage.getInstance().getReference(img); //해당 경로명으로 참조하는 파일명 지정
                imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() { //다운로드 Url 가져옴
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Glide.with(EditProduct.this).load(task.getResult()).into(ivPreview); //해당 이미지로 세팅
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //이미지 선택
        btn_PushProductImageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);
            }
        });

        //수정하기
        btn_PushProductEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지를 수정했다면
                if (imgUri != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_kkmmss");
                    String filename = formatter.format(new Date());

                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://pocket-manager-9207f.appspot.com/");
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://pocket-manager-9207f.appspot.com").child("Product/" + filename);

                    img = "Product/" + filename; //DB에 이미지 정보 담기 위한 String 객체

                    storageRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            productDataEdit();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProduct.this, "등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            addProgressDialog();
                        }
                    });
                }
                else {
                    //이미지를 수정하지 않은 경우
                    productDataEdit();
                }
            }
        });
    }

    private void productDataEdit() {
        String name = ed_productName_ed.getText().toString();
        String price = ed_productPrice_ed.getText().toString();

        ProductDTO productDTO = new ProductDTO(name, price, img);

        databaseRef.setValue(productDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
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
                ivPreview.setImageBitmap(bitmap);
                ivPreview.setBackground(getDrawable(R.drawable.rounded_transparent)); //수정 시 처음에는
                ivPreview.setClipToOutline(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //ProgressDialog
    public void addProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("수정 중...");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
