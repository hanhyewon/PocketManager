package com.example.soyeon;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gpdnj.pocketmanager.MainActivity;
import com.example.gpdnj.pocketmanager.R;
import com.example.hyejin.SalesManagerMainActivity;
import com.example.jiyeong.pastSalesMode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class EditProduct extends AppCompatActivity {

    Uri preImage;
    String editImage;
    String pId_e;

    Toolbar toolbar;
    SimpleSideDrawer slide_menu;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    private TextView nav_userName;
    private TextView nav_userEmail;

    private EditText ed_productName_ed;
    private EditText ed_productPrice_ed;

    private Button btn_PushProductEdit = null;
    private Button btn_PushProductDelete = null;

    ImageView ivPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_edit);
        firebaseAuth = FirebaseAuth.getInstance();

        final int position = getIntent().getIntExtra("position",1);
        final String pId = getIntent().getStringExtra("pId");
        final String pName = getIntent().getStringExtra("pName");
        final String pPrice = getIntent().getStringExtra("pPrice");
        final String pImage = getIntent().getStringExtra("pImage");

        pId_e = pId;

        ed_productName_ed = (EditText) findViewById(R.id.et_ProductEditName);
        ed_productPrice_ed = (EditText) findViewById(R.id.et_ProductEditPrice);

        ed_productName_ed.setText(pName);
        ed_productPrice_ed.setText(pPrice);

        ivPreview = (ImageView) findViewById(R.id.iv_preview_edit);

        if(pImage != null){
            Glide.with(this).load(pImage).into(ivPreview);
        }

        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("상품수정");

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);

        btn_PushProductEdit=(Button)findViewById(R.id.btn_pushProductEdit);
        btn_PushProductDelete=(Button)findViewById(R.id.btn_pushProductDelete);

        btn_PushProductEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        btn_PushProductDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = "abc123";
                String ref = "상품/" + uid +"/"+ pId;
                database.getReference(ref).removeValue();
            }
        });
    }

    /**
     * 이미지 선택 된 파일을 화면에 바로 보여주는 메소드
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            preImage = data.getData();
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), preImage);
                ivPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //이미지 업로드
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (preImage != null) {

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://pocket-manager-9207f.appspot.com/");

            //Unique한 파일명
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://pocket-manager-9207f.appspot.com").child("Product/" + filename);
            editImage = "gs://pocket-manager-9207f.appspot.com/Product/" + filename;
            //올라가거라...
            storageRef.putFile(preImage)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //성공하면 DB에 등록하는 함수 불러온다
                            onEditUpload();
                            Toast.makeText(getApplicationContext(), "저장 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다.
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다

                        }
                    });
        } else {
            //이미지 파일이 없는 경우
            editImage = "gs://pocket-manager-9207f.appspot.com/Product/20190921_1801.png";
            onEditUpload();
        }
    }

    /**
     *
     */
    public void onEditUpload(){
        String uid = "abc123";
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("상품").child(uid).child(pId_e).child("pname").setValue(ed_productName_ed.getText().toString());
        mDatabase.child("상품").child(uid).child(pId_e).child("price").setValue(ed_productPrice_ed.getText().toString());
        mDatabase.child("상품").child(uid).child(pId_e).child("pimage").setValue(editImage);
    }

    /**
     * 툴바에 있는 항목과 메뉴 네비게이션의 select 이벤트를 처리하는 메소드
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : //왼쪽 메뉴 버튼을 눌렀을 때
                slide_menu.toggleLeftDrawer(); //슬라이드 동작

                //navigation_menu.xml 이벤트 처리
                //현재 회원의 정보 설정
                nav_userName = (TextView) findViewById(R.id.nav_userName);
                nav_userEmail = (TextView) findViewById(R.id.nav_userEmail);
                nav_userName.setText(firebaseAuth.getCurrentUser().getDisplayName() + "님");
                nav_userEmail.setText(firebaseAuth.getCurrentUser().getEmail());

                ImageView menu_close = (ImageView)findViewById(R.id.menu_close);
                menu_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slide_menu.closeLeftSide();
                    }
                });

                //로그아웃
                Button logoutBtn = (Button) findViewById(R.id.logoutBtn);
                logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        Intent intent = new Intent(EditProduct.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = (Button) findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(EditProduct.this, SalesManagerMainActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = (Button)findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(EditProduct.this, pastSalesMode.class);
                        startActivity(intent);
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }
}
