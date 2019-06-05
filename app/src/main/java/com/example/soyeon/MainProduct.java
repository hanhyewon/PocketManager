package com.example.soyeon;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;

import java.util.ArrayList;

public class MainProduct extends AppCompatActivity {

    Toolbar toolbar;

    //리스트뷰를 담을 어댑터뷰 생성
    private ListView pListView = null;
    private ListViewAdapter pAdapter = null;
    private Button btn_ProductPush = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_main);

        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("상품관리");

        final Intent intent_PEdit = new Intent(this, EditProduct.class);
        final Intent intent_PAdd = new Intent(this, AddProduct.class);
        final Intent intent_EMain = new Intent(this, MainExpense.class);

        //startActivity(intent_EMain);

        btn_ProductPush = findViewById(R.id.btn_ProductPush);
        pListView = findViewById(R.id.pListView);
        pAdapter = new ListViewAdapter(this.getBaseContext());

        pAdapter.addItem(getResources().getDrawable(R.drawable.ic_launcher_background),
                "예시 게시글",
                "2014-02-18");

        pListView.setAdapter(pAdapter);
        registerForContextMenu(pListView);

        //리스너
        pListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(MainProduct.this, view);
                getMenuInflater().inflate(R.menu.product_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.pModify:
                                intent_PEdit.putExtra("position",position);
                                startActivity(intent_PEdit);
                                break;
                            case R.id.pdelete:
                                pAdapter.remove(position);
                        }
                    return false;
                    }
                });
                popup.show();
            return false;
            }
        });

        btn_ProductPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_PAdd);
            }
        });

    }

    //뷰홀더
    private class ViewHolder {
        public ImageView pImage;
        public TextView pName;
        public TextView pPrice;
    }

    //리스트뷰 어댑터
    private class ListViewAdapter extends BaseAdapter {
        private Context pContext = null;
        private ArrayList<ProductListData> pListData = new ArrayList<ProductListData>();

        public ListViewAdapter(Context pContext){
            super();
            this.pContext = pContext;
        }

        @Override
        public int getCount() {
            return pListData.size();
        }

        @Override
        public Object getItem(int position) {
            return pListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.product_listview, null);

                holder.pImage = (ImageView) convertView.findViewById(R.id.pImage);
                holder.pName = (TextView) convertView.findViewById(R.id.pName);
                holder.pPrice = (TextView) convertView.findViewById(R.id.pPrice);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            ProductListData pData = pListData.get(position);

            if (pData.pImage != null) {
                holder.pImage.setVisibility(View.VISIBLE);
                holder.pImage.setImageDrawable(pData.pImage);
            }else{
                holder.pImage.setVisibility(View.GONE);
            }

            holder.pName.setText(pData.pName);
            holder.pPrice.setText(pData.pPrice);

            return convertView;
        }
        //////////
        //임의추가
        public void addItem(Drawable pImage, String pName, String pPrice){
            ProductListData addInfo = null;
            addInfo = new ProductListData();
            addInfo.pImage = pImage;
            addInfo.pName = pName;
            addInfo.pPrice = pPrice;

            pListData.add(addInfo);
        }
        //임의삭제
        public void remove(int position){
            pListData.remove(position);
            dataChange();
        }
        //임의수정
        public void dataChange(){
            pAdapter.notifyDataSetChanged();
        }

    }

}
