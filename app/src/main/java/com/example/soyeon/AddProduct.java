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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class AddProduct extends AppCompatActivity {

    Toolbar toolbar;
    SimpleSideDrawer slide_menu;
    private FirebaseAuth firebaseAuth;
    private TextView nav_userName;
    private TextView nav_userEmail;

    EditText et_ProductNameAdd;
    EditText et_ProductPriceAdd;

    Button btn_ProductAdd;
    ImageButton btn_ProductImgAdd;
    ImageView ivPreview;

    Uri preImage;
    String pImage;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_add);
        firebaseAuth = FirebaseAuth.getInstance();

        et_ProductNameAdd = findViewById(R.id.et_ProductAddName);
        et_ProductPriceAdd = findViewById(R.id.et_ProductAddPrice);
        btn_ProductAdd =  findViewById(R.id.btn_pushProductAdd);
        btn_ProductImgAdd = findViewById(R.id.btn_ProductAddImage);
        ivPreview = findViewById(R.id.iv_preview);

        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("상품등록");

        //툴바 메뉴 클릭 시, 나타날 navigation 화면 설정
        slide_menu = new SimpleSideDrawer(this);
        slide_menu.setLeftBehindContentView(R.layout.navigation_menu);

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

        //상품등록
        btn_ProductAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //인텐트로 회원 아이디(로받을건지 이메일로 받을건지 정해야함) + 해당 판매코드 받아와서 child()하고 그 하단에 데이터 넣어야함
                uploadFile();

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
            pImage = "Product/" + filename;
            //올라가거라...
            storageRef.putFile(preImage)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //성공하면 DB에 등록하는 함수 불러온다
                            uploadData();
                            Toast.makeText(getApplicationContext(), "저장 완료!", Toast.LENGTH_SHORT).show();
                            finish();
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
            pImage = "Product/question-mark-1750942_1280.png";
            uploadData();
            Toast.makeText(getApplicationContext(), "저장 완료!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * DB에 상품정보 등록하는 메소드
     */
    public void uploadData(){
        //상품 이름, 상품 가격 중 빈칸이 있으면 업로드가 되지 않는다.
        if(et_ProductNameAdd.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "상품 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(et_ProductPriceAdd.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "상품 가격을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        String uid = "abc123";
        //행사화면이 완료되어야 임의의 코드가 아닌 정보로 실험해볼 수 있음
        //String uid = firebaseAuth.getUid();
        //String sid = 판매 아이디
        ProductListData product = new ProductListData(pImage, et_ProductNameAdd.getText().toString(), et_ProductPriceAdd.getText().toString());
            mDatabase.child("상품").child(uid).push().setValue(product);
            //mDatabase.child("상품").child(uid).child(sid).push().setValue(product);
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
                        Intent intent = new Intent(AddProduct.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                //판매관리
                Button salesManagerBtn = (Button) findViewById(R.id.salesManagerBtn);
                salesManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(AddProduct.this, SalesManagerMainActivity.class);
                        startActivity(intent);
                    }
                });

                //매출관리
                Button moneyTotalManagerBtn = (Button)findViewById(R.id.moneyTotalBtn);
                moneyTotalManagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent intent = new Intent(AddProduct.this, pastSalesMode.class);
                        startActivity(intent);
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }

}
