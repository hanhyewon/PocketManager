package com.example.gpdnj.pocketmanager;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class EventListviewAdapter  extends BaseAdapter implements View.OnClickListener {

    ArrayList<EventDTO> item = new ArrayList<>();
    Context context;
    boolean managerCheck;

    //수정, 삭제 이벤트를 위한 Listener 인터페이스 정의
    public interface BtnClickListener {
        void deleteBtnClickListener(String eventId);
        void modifyBtnClickListener(String eventId);
    }

    private BtnClickListener btnListener;

    public EventListviewAdapter(Context context) {
        super();
        this.context = context;
    }

    public EventListviewAdapter(Context context, BtnClickListener btnListener, boolean managerCheck) {
        super();
        this.context = context;
        this.btnListener = btnListener;
        this.managerCheck = managerCheck;
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
        final ViewHolder viewHolder;
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.activity_event_listview, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.eventItemTitle);
            viewHolder.subTitle = convertView.findViewById(R.id.eventItemSubTitle);
            viewHolder.date = convertView.findViewById(R.id.eventItemDate);
            viewHolder.img = convertView.findViewById(R.id.eventItemImg);
            viewHolder.deleteBtn = convertView.findViewById(R.id.eventDeleteBtn);
            viewHolder.modifyBtn = convertView.findViewById(R.id.eventModifyBtn);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.img.setImageBitmap(null);
        }

        final EventDTO dto = item.get(position);

        viewHolder.title.setText(dto.getTitle());
        viewHolder.subTitle.setText(dto.getSubTitle());
        viewHolder.date.setText(dto.getDate());

        StorageReference imgRef = FirebaseStorage.getInstance().getReference(dto.getImgUrl()); //해당 경로명으로 참조하는 파일명 지정
        imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() { //다운로드 Url 가져옴
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(context).load(task.getResult()).into(viewHolder.img); //해당 이미지로 세팅
            }
        });

        if (managerCheck) {
            //임의로 현재 이름이 관리자인 경우, 관리자 화면 보여주기

            //https://recipes4dev.tistory.com/45
            //삭제(휴지통)를 눌렀을 때, 해당 Item의 행사ID 값 TAG로 지정
            viewHolder.deleteBtn.setTag(dto.getEventId());
            viewHolder.deleteBtn.setOnClickListener(this);

            //수정(연필)을 눌렀을 때
            viewHolder.modifyBtn.setTag(dto.getEventId());
            viewHolder.modifyBtn.setOnClickListener(this);
        } else {
            //일반 회원인 경우, 수정/삭제 버튼 숨기기
            viewHolder.deleteBtn.setVisibility(View.GONE);
            viewHolder.modifyBtn.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(this.btnListener != null) {
            if(v.getId() == R.id.eventDeleteBtn) {
                //삭제일 때
                this.btnListener.deleteBtnClickListener((String)v.getTag());
            } else {
                //수정일 때
                this.btnListener.modifyBtnClickListener((String)v.getTag());
            }
        }
    }

    public void addItem(EventDTO items) {
        item.add(items);
    }

    public void addItems(ArrayList<EventDTO> items) {
        this.item = items;
    }

    public class ViewHolder {
        TextView title;
        TextView subTitle;
        TextView date;
        ImageView img;
        ImageView deleteBtn;
        ImageView modifyBtn;
    }
}
