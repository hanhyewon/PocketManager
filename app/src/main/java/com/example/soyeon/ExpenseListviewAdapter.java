package com.example.soyeon;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import static com.example.gpdnj.pocketmanager.MoneyFormatClass.moneyFormatToWon;

public class ExpenseListviewAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    ArrayList<ExpenseDTO> item = new ArrayList<>();

    //수정, 삭제 이벤트를 위한 Listener 인터페이스 정의
    public interface BtnClickListener {
        void moreBtnClickListener(String expenseId);
    }

    private BtnClickListener btnListener;

    public ExpenseListviewAdapter(Context context) {
        super();
        this.context = context;
    }

    public ExpenseListviewAdapter(Context context, BtnClickListener btnListener) {
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
            convertView = layoutInflater.inflate(R.layout.expense_listview, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.date = convertView.findViewById(R.id.expenseDateItemView);
            viewHolder.context = convertView.findViewById(R.id.expenseContextItemView);
            viewHolder.charge = convertView.findViewById(R.id.expenseChargeItemView);
            viewHolder.more = convertView.findViewById(R.id.expenseMore);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ExpenseDTO dto = item.get(position);

        String str = moneyFormatToWon(dto.getCharge()) + "원";
        viewHolder.bind(dto.getDate(), dto.getContext(), str, dto.getGroup(), dto.getExpenseId());

        viewHolder.more.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(this.btnListener != null) {
            this.btnListener.moreBtnClickListener((String)v.getTag());
        }
    }

    public void addItem(ExpenseDTO items) {
        item.add(items);
    }

    public void addItems(ArrayList<ExpenseDTO> items) {
        this.item = items;
    }

    public class ViewHolder {
        TextView date;
        TextView context;
        TextView charge;
        ImageView more;

        void bind(String dateData, String contextData, String chargeData, String groupData, String expenseId) {
            date.setText(dateData);
            context.setText(contextData);
            charge.setText(chargeData);

            if(groupData.equals("변동비")) {
                charge.setTextColor(Color.parseColor("#1CB367"));
            } else { //고정비
                charge.setTextColor(Color.parseColor("#CF4F47"));
            }

            more.setTag(expenseId);
        }
    }
}
