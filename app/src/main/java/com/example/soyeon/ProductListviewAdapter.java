package com.example.soyeon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.gpdnj.pocketmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.example.gpdnj.pocketmanager.MoneyFormatClass.moneyFormatToWon;

public class ProductListviewAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
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
        ViewHolder viewHolder;
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
            Glide.with(context).clear(viewHolder.img);
        }

        ProductDTO dto = item.get(position);

        String str = moneyFormatToWon(dto.getPrice()) + "원";
        viewHolder.bind(dto.getName(), str, dto.getImgUrl(), dto.getProductId());

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

        void bind(String nameData, String priceData, String imgData, String productIdData) {
            name.setText(nameData);
            price.setText(priceData);

            StorageReference imgRef = FirebaseStorage.getInstance().getReference(imgData); //해당 경로명으로 참조하는 파일명 지정
            imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() { //다운로드 Url 가져옴
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Glide.with(context).clear(img);
                    Glide.with(context)
                            .load(task.getResult())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(img);
                }
            });
            more.setTag(productIdData);
        }
    }
}
