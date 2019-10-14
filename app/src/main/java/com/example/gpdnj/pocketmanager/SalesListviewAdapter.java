package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SalesListviewAdapter extends BaseAdapter {

    private SalesDTO dto;
    private ViewHolder viewHolder;
    private Context context;

    ArrayList<SalesDTO> items = new ArrayList<>();

    public SalesListviewAdapter(Context context) {
        super();
        this.context = context;
    }

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
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.sale_listview, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.salesTitleItemView);
            viewHolder.date = convertView.findViewById(R.id.salesDateItemView);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        dto = items.get(position);

        viewHolder.title.setText(dto.getTitle());
        viewHolder.date.setText(dto.getDate());

        return convertView;
    }

    public void addItem(SalesDTO item) {
        items.add(item);
    }

    public void addItems(ArrayList<SalesDTO> item) {
        this.items = item;
    }

    public class ViewHolder {
        TextView title;
        TextView date;
    }
}
