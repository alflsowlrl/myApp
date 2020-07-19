package com.example.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PhoneTab extends FragmentTab{
    PhoneRecycleAdapter phoneAdapter = new PhoneRecycleAdapter();
    RecyclerView recycleview;
    HttpRequestHelper helper = new HttpRequestHelper();
    public final int PHONE_ADD_REQUEST = 99;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final SwipeRefreshLayout view = (SwipeRefreshLayout) getLayoutInflater().inflate(R.layout.phone_tab, container, false);

        recycleview = (RecyclerView)view.findViewById(R.id.PhoneRecycleView);

        recycleview.setAdapter(phoneAdapter);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));


        final LoadContactFromDb lcfd = new LoadContactFromDb();
        lcfd.execute();

//        Log.d("myApp", string);

        FloatingActionButton floatingButton = (FloatingActionButton)view.findViewById(R.id.phoneAddFloating);
            floatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    Intent intent = new Intent(getContext(), CardViewActivity.class);

                    getActivity().startActivityForResult(intent, PHONE_ADD_REQUEST);

//                    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
//                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

//                    getActivity().startActivity(intent);
                }
            });

        view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadContactFromDb lcfd = new LoadContactFromDb();
                lcfd.execute();
                view.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadContactFromDb lcfd = new LoadContactFromDb();
        lcfd.execute();


        phoneAdapter.notifyDataSetChanged();
    }

    public class LoadContactFromDb extends AsyncTask<Void, Void, ArrayList<Contact>> {
        @Override
        protected ArrayList<Contact> doInBackground(Void... voids) {

            String user_id = PreferenceManager.getString(getContext(), "user_id");

            String result = helper.GETAll(Long.valueOf(user_id));
            ArrayList<Contact> phoneArrayList = new ArrayList<Contact>();

            try {


                JSONArray array = new JSONArray(result);
                for (int index = 0; index < array.length(); ++index) {
                    JSONObject offerObject = array.getJSONObject(index);
                    String name = offerObject.getString("name");
                    String number = offerObject.getString("number");
//                    String id = offerObject.getString("_id");
                    phoneArrayList.add(new Contact(name, number,false));

                }

//                phoneAdapter.setList(phoneArrayList);


            } catch (JSONException e) {
                Log.d("myApp", e.toString());
            } catch (Exception e) {
                Log.d("myApp", e.toString());
            }

            return phoneArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> contacts) {
            super.onPostExecute(contacts);

            phoneAdapter.setList(contacts);
        }
    }
}

