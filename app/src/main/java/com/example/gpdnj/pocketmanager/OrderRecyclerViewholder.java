package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.soyeon.ProductDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class OrderRecyclerViewholder extends RecyclerView.ViewHolder {

    //뷰홀더 객체는 뷰를 담아두는 역할을 하면서 동시에 뷰에 표시될 데이터를 설정하는 역할을 맡을 수 있습니다.
    TextView orderProductName;
    ImageView orderProductImg;
    StorageReference imgRef;

    OrderRecyclerViewAdapter.OnItemClickListener listenr; //클릭이벤트처리관련 변수

    public OrderRecyclerViewholder(@NonNull final View itemView) { //뷰홀더는 각각의 아이템을 위한 뷰를 담고있다.
        super(itemView);

        orderProductName = itemView.findViewById(R.id.orderProductNameItem);
        orderProductImg = itemView.findViewById(R.id.orderProductImg);

        //https://swalloow.tistory.com/89
        orderProductImg.setColorFilter(Color.parseColor("#a6a6a6"), PorterDuff.Mode.MULTIPLY);

        //아이템 클릭이벤트처리
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if(listenr != null ){
                    listenr.onItemClick(OrderRecyclerViewholder.this, itemView, position);
                }
            }
        });
    }

    //setItem 메소드는 SingerItem 객체를 전달받아 뷰홀더 안에 있는 뷰에 데이터를 설정하는 역할을 합니다.
    public void setItem(ProductDTO item, final Context context) {
        this.orderProductName.setText(item.getName());

        imgRef = FirebaseStorage.getInstance().getReference(item.getImgUrl()); //해당 경로명으로 참조하는 파일명 지정
        imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() { //다운로드 Url 가져옴
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(context).load(task.getResult()).into(orderProductImg); //해당 이미지로 세팅
            }
        });

    }

    //클릭이벤트처리
    public void setOnItemClickListener(OrderRecyclerViewAdapter.OnItemClickListener listenr) {
        this.listenr = listenr;
    }
}
