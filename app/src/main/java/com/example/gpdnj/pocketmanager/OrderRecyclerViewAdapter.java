package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.soyeon.ProductDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<ProductDTO> items;
    List<ProductDTO> potionList= null;

    OnItemClickListener listener;
    public static interface  OnItemClickListener{
        public void onItemClick(OrderRecyclerViewholder holder, View view, int position);
    }

    public  OrderRecyclerViewAdapter(Context context, List<ProductDTO>potionList) {
        super();
        this.context = context;
        this.potionList=potionList;
        this.items = new ArrayList<ProductDTO>();
        this.items.addAll(potionList);


    }

    @NonNull
    @Override //뷰홀더가 만들어지는 시점에 호출되는 메소드
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.order_item_view, viewGroup, false);
        OrderRecyclerViewholder holder = new OrderRecyclerViewholder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final OrderRecyclerViewholder orderRecyclerViewholder = (OrderRecyclerViewholder) viewHolder;

        ProductDTO item = items.get(position);
        orderRecyclerViewholder.setItem(item, context);
        orderRecyclerViewholder.setOnItemClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //아이템을 한개 추가해주고싶을때
    public  void addItem(ProductDTO item){
        items.add(item);
    }

    //한꺼번에 추가해주고싶을때
    public void addItems(ArrayList<ProductDTO> items){
        this.items = items;
    }

    public ProductDTO getItem(int position){
        return items.get(position);
    }

    //클릭리스너관련
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void filter(String charText){
        charText= charText.toLowerCase(Locale.getDefault());
        potionList.clear();
        /*if(charText.length() ==0){
            potionList.addAll(items);
        }else{
            for(ProductDTO product: items){
                String name = context.getResources().getString(product.name);
                if(name.toLowerCase().contains(charText)){
                    potionList.add(product);
                }
            }
        }*/
        notifyDataSetChanged();
    }

}
