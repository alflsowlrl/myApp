package com.example.myapp;

import android.os.AsyncTask;
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
           //this.itemView = itemView.getRootView();
           itemView.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View view) {
                   listData.remove(getAdapterPosition());
                   String name = listData.get(getAdapterPosition()).getName();
//                   final String id = listData.get(getAdapterPosition()).getId();
                   Toast.makeText(itemView.getContext(), name + "님이 삭제 되었습니다", Toast.LENGTH_SHORT).show();
                   notifyItemRemoved(getAdapterPosition());
                   //동시에 여러개 누르면 안됨

                   //db에서 삭제
//                   class DeleteContact extends AsyncTask<Void, Void, String> {
//                       @Override
//                       protected String doInBackground(Void... voids) {
//                           HttpRequestHelper helper = new HttpRequestHelper();
//                           helper.DELETE(new Contact("", "", id));
//
//                           return "";
//                       }
//                   }
//
//                   DeleteContact lcfd = new DeleteContact();
//                   lcfd.execute();

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

