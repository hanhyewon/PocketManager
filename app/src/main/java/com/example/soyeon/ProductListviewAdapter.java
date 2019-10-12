package com.example.soyeon;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gpdnj.pocketmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProductListviewAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    StorageReference imgRef;

    ArrayList<ProductDTO> item = new ArrayList<>();

    //수정, 삭제 이벤트를 위한 Listener 인터페이스 정의
    public interface BtnClickListener {
        void moreBtnClickListener(String productId);
    }

    private BtnClickListener btnListener;

    public ProductListviewAdapter(Context context) {
        super();
        this.context = context;
    }

    public ProductListviewAdapter(Context context, BtnClickListener btnListener) {
        super();
        this.context = context;
        this.btnListener = btnListener;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.product_listview, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.productNameItemView);
            viewHolder.price = convertView.findViewById(R.id.productPriceItemView);
            viewHolder.img = convertView.findViewById(R.id.productImgItemView);
            viewHolder.more = convertView.findViewById(R.id.productMore);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ProductDTO dto = item.get(position);

        viewHolder.name.setText(dto.getName());

        String str = dto.getPrice() + "원";
        viewHolder.price.setText(str);

        imgRef = FirebaseStorage.getInstance().getReference(dto.getImgUrl()); //해당 경로명으로 참조하는 파일명 지정
        imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() { //다운로드 Url 가져옴
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(context).load(task.getResult()).into(viewHolder.img); //해당 이미지로 세팅
            }
        });

        viewHolder.more.setTag(dto.getProductId());
        viewHolder.more.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(this.btnListener != null) {
            this.btnListener.moreBtnClickListener((String)v.getTag());
        }
    }

    public void addItem(ProductDTO items) {
        item.add(items);
    }

    public void addItems(ArrayList<ProductDTO> items) {
        this.item = items;
    }

    public class ViewHolder {
        TextView name;
        TextView price;
        ImageView img;
        ImageView more;
    }
}
