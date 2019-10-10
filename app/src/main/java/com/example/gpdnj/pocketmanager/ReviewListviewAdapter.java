package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ReviewListviewAdapter extends BaseAdapter {

    private ReviewDTO dto;
    private ViewHolder viewHolder;
    private Context context;
    String uid;

    ArrayList<ReviewDTO> item = new ArrayList<>();

    public ReviewListviewAdapter(Context context) {
        super();
        this.context = context;
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

        return convertView;
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
    }
}
