package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ManagerUserListviewAdapter extends BaseAdapter {

    ArrayList<UserDTO> item = new ArrayList<>();
    Context context;

    public ManagerUserListviewAdapter(Context context) {
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
            convertView = layoutInflater.inflate(R.layout.activity_manager_user_listview, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.uid = convertView.findViewById(R.id.listUid);
            viewHolder.name = convertView.findViewById(R.id.listName);
            viewHolder.email = convertView.findViewById(R.id.listEmail);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserDTO dto = item.get(position);

        viewHolder.uid.setText(dto.getUid());
        viewHolder.name.setText(dto.getName());
        viewHolder.email.setText(dto.getEmail());

        return convertView;
    }

    public void addItem(UserDTO items) {
        item.add(items);
    }

    public void addItems(ArrayList<UserDTO> items) {
        this.item = items;
    }

    public class ViewHolder {
        TextView uid;
        TextView name;
        TextView email;
    }
}


