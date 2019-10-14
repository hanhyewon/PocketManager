package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.gpdnj.pocketmanager.MoneyFormatClass.moneyFormatToWon;

public class OrderDialogListviewAdapter extends BaseAdapter {

    private OrderDTO dto;
    private ViewHolder viewHolder;
    private Context context;

    ArrayList<OrderDTO> item = new ArrayList<>();

    public OrderDialogListviewAdapter(Context context) {
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
            convertView = layoutInflater.inflate(R.layout.order_dialog_listview, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.orderName = convertView.findViewById(R.id.orderDialogNameItem);
            viewHolder.orderSum = convertView.findViewById(R.id.orderDialogSumItem);
            viewHolder.orderAmount = convertView.findViewById(R.id.orderDialogAmountItem);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        dto = item.get(position);

        viewHolder.orderName.setText(dto.getName());

        String sumStr = moneyFormatToWon(dto.getPrice() * dto.getAmount()) + "원";
        viewHolder.orderSum.setText(sumStr);

        viewHolder.orderAmount.setText(String.valueOf(dto.getAmount()));

        return convertView;
    }

    public void addItem(OrderDTO items) {
        item.add(items);
    }

    public void addItems(ArrayList<OrderDTO> items) {
        this.item = items;
    }

    public class ViewHolder {
        TextView orderName;
        TextView orderSum;
        TextView orderAmount;
    }
}