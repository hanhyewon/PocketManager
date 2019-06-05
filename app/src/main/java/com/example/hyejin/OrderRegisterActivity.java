package com.example.hyejin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpdnj.pocketmanager.R;

import java.util.ArrayList;

public class OrderRegisterActivity extends ParentActivity {

    public Toolbar toolbar;
    public RecyclerView recyclerView;
    public OrderAdapter mAdapter;

    public ImageView lblMinus1;
    public ImageView lblMinus2;
    public ImageView lblPlus1;
    public ImageView lblPlus2;
    public TextView lblCount1;
    public TextView lblCount2;

    public View layoutCash;
    public View layoutAccount;
    public ImageView imgAccount;
    public ImageView imgCash;
    public int count1;
    public int count2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_register);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("주문등록");

        count1 = 1;
        count2 = 1;

        recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new OrderAdapter();
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        imgAccount = findViewById(R.id.imgAccount);
        imgCash = findViewById(R.id.imgCash);

        lblMinus1 = findViewById(R.id.lblMinus1);
        lblMinus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count1 -= 1;
                lblCount1.setText(String.valueOf(count1));
            }
        });
        lblMinus2 = findViewById(R.id.lblMinus2);
        lblMinus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count2 -= 1;
                lblCount2.setText(String.valueOf(count2));
            }
        });
        lblCount1 = findViewById(R.id.lblCount1);

        lblCount2 = findViewById(R.id.lblCount2);
        lblPlus1 = findViewById(R.id.lblPlus1);
        lblPlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count1 += 1;
                lblCount1.setText(String.valueOf(count1));
            }
        });
        lblPlus2 = findViewById(R.id.lblPlus2);
        lblPlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count2 += 1;
                lblCount2.setText(String.valueOf(count2));
            }
        });

        layoutCash = findViewById(R.id.layoutCash);
        layoutCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgCash.setSelected(true);
                imgAccount.setSelected(false);
            }
        });

        layoutAccount = findViewById(R.id.layoutAccount);
        layoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgCash.setSelected(false);
                imgAccount.setSelected(true);
            }
        });

    }


    private class OrderAdapter extends RecyclerView.Adapter<ViewHolder> {

        public ArrayList<Order> data;

        public OrderAdapter() {
            data = new ArrayList<>();
            data.add(new Order("상품명 A"));
            data.add(new Order("상품명 B"));
            data.add(new Order("상품명 C"));
            data.add(new Order("상품명 D"));
            data.add(new Order("상품명 E"));
            data.add(new Order("상품명 F"));
            data.add(new Order("상품명 G"));
            data.add(new Order("상품명 H"));
            data.add(new Order("상품명 I"));
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_order_register, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.onBindViewHolder(data.get(i));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(OrderRegisterActivity.this, "item click=" + getLayoutPosition(), Toast.LENGTH_LONG).show();
                    showoOrderList();
                }
            });
        }

        public void onBindViewHolder(Order o) {
            textView.setText(o.title);
        }
    }

    public void showoOrderList() {
        View view = getLayoutInflater().inflate(R.layout.dialog_order_list, null, false);

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setView(view);
        final Dialog d = b.create();
        d.show();
        view.findViewById(R.id.lblPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderRegisterActivity.this, OrderManagerActivity.class));
                d.dismiss();
            }
        });
        view.findViewById(R.id.lblback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
    }

}
