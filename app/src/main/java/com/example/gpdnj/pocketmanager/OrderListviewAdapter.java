package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderListviewAdapter extends BaseAdapter implements View.OnClickListener{

    private OrderFirstDTO dto;
    private ViewHolder viewHolder;
    private Context context;

    ArrayList<OrderFirstDTO> item = new ArrayList<>();

    //수정, 삭제 이벤트를 위한 Listener 인터페이스 정의
    private BtnClickListener btnListener;
    public interface BtnClickListener {
        void moreBtnClickListener(String orderId);
    }

    public OrderListviewAdapter(Context context) {
        super();
        this.context = context;
    }

    public OrderListviewAdapter(Context context, BtnClickListener btnListener) {
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
            convertView = layoutInflater.inflate(R.layout.activity_order_listview, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.orderDate = convertView.findViewById(R.id.orderDateItemView);
            viewHolder.orderSum = convertView.findViewById(R.id.orderSumItemView);
            viewHolder.orderMore = convertView.findViewById(R.id.orderMore);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        dto = item.get(position);

        viewHolder.orderDate.setText(dto.getDate());

        String sumStr = dto.getSum() + "원";
        viewHolder.orderSum.setText(sumStr);

        if(dto.getPay().equals("cash")) {
            viewHolder.orderSum.setTextColor(Color.parseColor("#169455"));
        } else {
            viewHolder.orderSum.setTextColor(Color.parseColor("#ab2a20"));
        }

        viewHolder.orderMore.setTag(dto.getOrderId());
        viewHolder.orderMore.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(this.btnListener != null) {
            this.btnListener.moreBtnClickListener((String)v.getTag());
        }
    }

    public void addItem(OrderFirstDTO items) {
        item.add(items);
    }

    public void addItems(ArrayList<OrderFirstDTO> items) {
        this.item = items;
    }

    public class ViewHolder {
        TextView orderDate;
        TextView orderSum;
        ImageView orderMore;
    }
}