package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.jar.Attributes;

import static com.example.gpdnj.pocketmanager.MoneyFormatClass.moneyFormatToWon;

public class OrderAmountPriceAdapter extends BaseAdapter implements View.OnClickListener {

    OrderDTO dto;
    ArrayList<OrderDTO> items = new ArrayList<>();
    Context context;

    //-, +, 취소 이벤트를 위한 Listener 인터페이스 정의
    public interface BtnClickListener {
        void minusBtnClickListener(int position);
        void plusBtnClickListener(int position);
        void deleteBtnClickListener(int position);
    }

    private BtnClickListener btnListener;

    public OrderAmountPriceAdapter(Context context) {
        super();
        this.context = context;
    }

    public OrderAmountPriceAdapter(Context context, BtnClickListener btnListener) {
        super();
        this.context = context;
        this.btnListener = btnListener;
    }

    // Adapter에 사용되는 데이터의 개수 리턴
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
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
            convertView = layoutInflater.inflate(R.layout.activity_order_amount_price_view, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.orderPnameItem = convertView.findViewById(R.id.orderPnameItem);
            viewHolder.orderPminusBtn = convertView.findViewById(R.id.orderPminusBtn);
            viewHolder.orderPamountItem = convertView.findViewById(R.id.orderPamountItem);
            viewHolder.orderPplusBtn = convertView.findViewById(R.id.orderPplusBtn);
            viewHolder.orderPpriceItem = convertView.findViewById(R.id.orderPpriceItem);
            viewHolder.orderPdeleteBtn = convertView.findViewById(R.id.orderPdeleteBtn);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        dto = items.get(position);

        viewHolder.orderPnameItem.setText(dto.getName());
        String priceText = moneyFormatToWon(dto.getPrice() * dto.getAmount()) + "원";
        viewHolder.orderPpriceItem.setText(priceText);

        viewHolder.orderPamountItem.setText(String.valueOf(dto.getAmount()));

        viewHolder.orderPminusBtn.setTag(position);
        viewHolder.orderPminusBtn.setOnClickListener(this);

        viewHolder.orderPplusBtn.setTag(position);
        viewHolder.orderPplusBtn.setOnClickListener(this);

        viewHolder.orderPdeleteBtn.setTag(position);
        viewHolder.orderPdeleteBtn.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(this.btnListener != null) {
            if(v.getId() == R.id.orderPminusBtn) {
                //수량 - 일 때
                this.btnListener.minusBtnClickListener((int)v.getTag());
            } else if(v.getId() == R.id.orderPplusBtn){
                //수량 + 일 때
                this.btnListener.plusBtnClickListener((int)v.getTag());
            } else {
                //상품 선택 취소할 때
                this.btnListener.deleteBtnClickListener((int)v.getTag());
            }
        }
    }

    public void addItem(OrderDTO item) {
        items.add(item);
    }

    public void addItems(ArrayList<OrderDTO> item) {
        this.items = item;
    }


    public class ViewHolder {
        TextView orderPnameItem;
        ImageView orderPminusBtn;
        TextView orderPamountItem;
        ImageView orderPplusBtn;
        TextView orderPpriceItem;
        TextView orderPdeleteBtn;
    }
}
