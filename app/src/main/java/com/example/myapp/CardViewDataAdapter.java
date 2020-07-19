package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardViewDataAdapter extends RecyclerView.Adapter<CardViewDataAdapter.ViewHolder> {

    private List<Contact> stList;

    public CardViewDataAdapter(List<Contact> students) {
        this.stList = students;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;


        if ( v == null ) {

            // vi(layoutInflater)는 Layout Inflater를 사용해 만든다.
            LayoutInflater vi = (LayoutInflater)v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.cardview_row, null);
        }
        // 현재의 position을 가지고 item을 가져온다. item은 이름과 전화번호가 들어있다.
        Contact p = stList.get(position);

        if ( p != null )
        {
            // 2개의 텍스트뷰를 셋팅해준다.
            TextView tt = (TextView)v.findViewById(R.id.tvName);
            TextView bt = (TextView)v.findViewById(R.id.tvEmailId);

            // 셋팅한 텍스트뷰의 텍스트에 이름과 전화번호를 넣어준다.
            tt.setText(p.getName());
            bt.setText("  " + p.getNumber());
        }

        // imagebutton 셋팅
        ImageView ib_call = (ImageView) v.findViewById(R.id.button_call);

        // 현재의 태그 입력, 이미지 버튼 클릭시 사용하기 위해 저장

        ib_call.setTag(position);

        return v;
    }

    @Override
    public CardViewDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cardview_row, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final int pos = position;

        viewHolder.tvName.setText(stList.get(position).getName());

        viewHolder.tvEmailId.setText(stList.get(position).getNumber());

        viewHolder.chkSelected.setChecked(stList.get(position).isSelected());

        viewHolder.chkSelected.setTag(stList.get(position));


        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                Contact contact = (Contact) cb.getTag();

                contact.setSelected(cb.isChecked());
                stList.get(pos).setSelected(cb.isChecked());
            }
        });

    }

    @Override
    public int getItemCount() {
        return stList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvEmailId;

        public CheckBox chkSelected;

        public Contact singlestudent;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvName = (TextView) itemLayoutView.findViewById(R.id.tvName);

            tvEmailId = (TextView) itemLayoutView.findViewById(R.id.tvEmailId);
            chkSelected = (CheckBox) itemLayoutView
                    .findViewById(R.id.chkSelected);

        }
    }

    // method to access in activity after updating selection
    public List<Contact> getStudentist() {
        return stList;
    }
}
