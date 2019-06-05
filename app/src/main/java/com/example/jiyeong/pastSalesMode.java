package com.example.jiyeong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gpdnj.pocketmanager.R;

import java.util.ArrayList;

public class pastSalesMode extends AppCompatActivity {

    ListView listView;
    Button btn1, btn2,riAdd;
    Spinner spinner;
    ArrayList<titleList> al = new ArrayList<titleList>();
    final String[] showTitles = {"오름차순","내림차순"};
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_sales_mode);


        //툴바 사용 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); //왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //툴바 타이틀명 설정
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("매출 관리");

        //오름차순,내림차순 스피너
        spinner=(Spinner)findViewById(R.id.spinner);

        ArrayAdapter<String> spinnerAdapter;
        spinnerAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,showTitles);
        spinner.setAdapter(spinnerAdapter);

        // 리스트 데이터
        al.add(new titleList("종로청소년 플리마켓","2019.4.20-4.28"));
        al.add(new titleList("강남 리엔나 공예전시","2019.4.20-4.28"));
        al.add(new titleList("네이버 아동 프리마켓","2019.4.20-4.28"));

        MyAdapter adapter = new MyAdapter(
                getApplicationContext(), // 현재화면의 제어권자
                R.layout.row,  // 리스트뷰의 한행의 레이아웃
                al);         // 데이터

        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 상세정보 화면으로 이동하기(인텐트 날리기)
                // 1. 다음화면을 만든다
                // 2. AndroidManifest.xml 에 화면을 등록한다
                // 3. Intent 객체를 생성하여 날린다
                Intent intent = new Intent(
                        getApplicationContext(), // 현재화면의 제어권자
                        currentSalesDetail.class); // 다음넘어갈 화면

                // intent 객체에 데이터를 실어서 보내기
                // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다
                intent.putExtra("title", al.get(position).title);
                intent.putExtra("date", al.get(position).date);

                startActivity(intent);
            }
        });

        btn2=(Button)findViewById(R.id.button2);
        riAdd=(Button)findViewById(R.id.riAdd);

        riAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),writeReview.class);
                startActivity(intent1);

            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(),currentSalesMode.class);
                startActivity(intent2);

            }
        });


    }
}

class MyAdapter extends BaseAdapter { // 리스트 뷰의 아답타
    Context context;
    int layout;
    ArrayList<titleList> al;
    LayoutInflater inf;
    public MyAdapter(Context context, int layout, ArrayList<titleList> al) {
        this.context = context;
        this.layout = layout;
        this.al = al;
        inf = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return al.size();
    }
    @Override
    public Object getItem(int position) {
        return al.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = inf.inflate(layout, null);
        }
        TextView slTitle = (TextView)convertView.findViewById(R.id.title);
        TextView slDate = (TextView)convertView.findViewById(R.id.date);

        titleList m = al.get(position);
        slTitle.setText(m.title);
        slDate.setText(m.date);

        return convertView;
    }
}


class titleList { // 자바빈
    String title = ""; //  title 판매명
    String date = ""; // date 기간
    public titleList(String title, String date) {
        super();
        this.title = title;
        this.date = date;
    }
    public titleList() {}
}

