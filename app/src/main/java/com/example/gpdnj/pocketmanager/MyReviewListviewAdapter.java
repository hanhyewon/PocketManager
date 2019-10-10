package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyReviewListviewAdapter extends BaseAdapter implements View.OnClickListener {

    private ReviewDTO dto;
    private ViewHolder viewHolder;
    private Context context;

    ArrayList<ReviewDTO> item = new ArrayList<>();

    //수정, 삭제 이벤트를 위한 Listener 인터페이스 정의
    public interface BtnClickListener {
        void moreBtnClickListener(String reviewId);
    }

    private BtnClickListener btnListener;

    public MyReviewListviewAdapter(Context context) {
        super();
        this.context = context;
    }

    public MyReviewListviewAdapter(Context context, BtnClickListener btnListener) {
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
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.activity_review_listview, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.category = convertView.findViewById(R.id.categoryItemView);
            viewHolder.title = convertView.findViewById(R.id.titleItemView);
            viewHolder.reviewDate = convertView.findViewById(R.id.reviewDateItemView);
            viewHolder.detailText = convertView.findViewById(R.id.reviewDetailItemView);
            viewHolder.reviewMore = convertView.findViewById(R.id.reviewMore);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        dto = item.get(position);

        viewHolder.category.setText(dto.getCategory());
        viewHolder.title.setText(dto.getTitle());
        viewHolder.reviewDate.setText(dto.getReviewDate());
        viewHolder.detailText.setText(dto.getDetailText());
        viewHolder.reviewMore.setVisibility(View.VISIBLE);

        viewHolder.reviewMore.setTag(dto.getReviewId());
        viewHolder.reviewMore.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(this.btnListener != null) {
            this.btnListener.moreBtnClickListener((String)v.getTag());
        }
    }

    public void addItem(ReviewDTO items) {
        item.add(items);
    }

    public void addItems(ArrayList<ReviewDTO> items) {
        this.item = items;
    }

    public class ViewHolder {
        TextView category;
        TextView title;
        TextView reviewDate;
        TextView detailText;
        ImageView reviewMore;
    }
}
