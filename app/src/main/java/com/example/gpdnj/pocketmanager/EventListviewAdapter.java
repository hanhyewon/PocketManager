package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EventListviewAdapter  extends BaseAdapter {

    ArrayList<EventDTO> item = new ArrayList<>();
    Context context;

    public EventListviewAdapter(Context context) {
        super();
        this.context = context;
    }

    // Adapter에 사용되는 데이터의 개수 리턴
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
            convertView = layoutInflater.inflate(R.layout.activity_event_listview, parent, false);

            viewHolder = new ViewHolder();
            //이미지, 날짜 추가해야 함
            viewHolder.title = convertView.findViewById(R.id.eventItemMainText);
            viewHolder.subTitle = convertView.findViewById(R.id.eventItemSubText);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        EventDTO dto = item.get(position);

        viewHolder.title.setText(dto.getTitle());
        viewHolder.subTitle.setText(dto.getSubTitle());

        return convertView;
    }

    public void addItem(EventDTO items) {
        item.add(items);
    }

    public void addItems(ArrayList<EventDTO> items) {
        this.item = items;
    }

    //이미지, 날짜 추가해야 함
    public class ViewHolder {
        TextView title;
        TextView subTitle;
    }
}
