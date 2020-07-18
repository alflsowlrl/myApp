package com.example.myapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapp.PhoneRecycleAdapter.PhoneHolder;
import java.util.ArrayList;
import java.util.Iterator;

class PhoneRecycleAdapter extends RecyclerView.Adapter<PhoneRecycleAdapter.PhoneHolder> {

    ArrayList<Contact> listData =  new ArrayList<Contact>();
    private final int IMAGE_VIEW_KEY = 1;

    private View itemView;

    class PhoneHolder extends RecyclerView.ViewHolder{
        TextView tvName;

       PhoneHolder(final View itemView){
           super(itemView);

           LinearLayout nameNumberHolder = itemView.findViewById(R.id.nameNumberHolder);

           nameNumberHolder.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View view) {
                   String name = listData.get(getAdapterPosition()).getName();
                   String number = listData.get(getAdapterPosition()).getNumber();

                   //동시에 여러개 누르면 안됨
                   Intent intent = new Intent(itemView.getContext(), PhonePopup.class);
                   intent.putExtra("name", name);
                   intent.putExtra("number", number);

                   itemView.getContext().startActivity(intent);

                   return true;
               }
           });

        }


        public void setPhone(Contact phone){

           TextView textTitle = (TextView)itemView.findViewById(R.id.textTitle);
           TextView textDate = (TextView)itemView.findViewById(R.id.textDate);

            textTitle.setText(phone.getName());
            textDate.setText(phone.getNumber());
            ImageView imageView = (ImageView)itemView.findViewById(R.id.phoneImageView);
            imageView.setImageResource(R.drawable.user);
        }

    }

    @NonNull
    @Override
    public PhoneHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout view = (LinearLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_recycler, parent, false);
        return new PhoneHolder(view);
    }





    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public void onBindViewHolder(PhoneHolder holder, int position) {
        Contact phone = listData.get(position);
        holder.setPhone(phone);
    }

    private void removeItemView(int position) {
        listData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listData.size());
    }



    public void setList(ArrayList<Contact> list){
        listData = list;


       // listData.sort(compareBy({it.second}, {it.first.name}));
        this.notifyDataSetChanged();
    }

}

