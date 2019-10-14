package com.example.jiyeong;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;
import com.example.gpdnj.pocketmanager.UserDTO;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ExpenseList_Adapter extends BaseAdapter {

    ArrayList<ExpenseDTO> arrayExpense = new ArrayList<ExpenseDTO>();



    public int getCount(){
        return arrayExpense.size();
    }

    public Object getItem(int position){
        return arrayExpense.get(position);

    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        CustomVIewHolder holder;

        if(convertView ==null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_listview, null, false);

            holder= new CustomVIewHolder();
            holder.list_context = (TextView) convertView.findViewById(R.id.list_context);
            holder.list_group = (TextView) convertView.findViewById(R.id.list_group);
            holder.list_price = (TextView) convertView.findViewById(R.id.list_price);

            convertView.setTag(holder);

        }else{
            holder=(CustomVIewHolder) convertView.getTag();


        }

        ExpenseDTO dto = arrayExpense.get(position);

        holder.list_context.setText(dto.getContext());
        holder.list_group.setText(dto.getGroup());
        holder.list_price.setText(dto.getCharge());

        return convertView;
    }

    public void addItem(ExpenseDTO items) {
        arrayExpense.add(items);
    }

    public void addItems(ArrayList<ExpenseDTO> items) {
        this.arrayExpense = items;
    }

    class CustomVIewHolder{
        TextView list_context;
        TextView list_group;
        TextView list_price;
    }




}
