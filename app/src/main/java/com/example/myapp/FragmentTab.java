package com.example.myapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FragmentTab extends Fragment {
    String name = "";

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle savedInstanceState){
        View view =inflater.inflate(R.layout.fragment_tab, container, false);
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(name);

        return view;
    }


}

