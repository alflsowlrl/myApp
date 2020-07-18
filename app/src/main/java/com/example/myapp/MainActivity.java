package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.loader.app.LoaderManager;

import com.google.android.material.tabs.TabLayout;

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

public class MainActivity extends AppCompatActivity {

    //Gallery
    private final static int READ_EXTERNAL_STORAGE_PERMMISSION_RESULT = 0;
    private final static int MEDIASTORE_LOADER_ID = 0;

    enum TAB {GALLERY_TAB, PHONE_TAB, MEMO_TAB}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadContactsAyscn lca = new LoadContactsAyscn();
        lca.execute();

//        initViewPager(); // 뷰페이저와 어댑터 장착
    }

    private View createView(String tabName){
        View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_button, null);
        TextView tabTextView = (TextView)tabView.findViewById(R.id.tab_text);
        ImageView tabLogoView = (ImageView)tabView.findViewById(R.id.tab_logo);

        tabTextView.setText(tabName);
        switch (tabName) {

            case "메모장":
                tabLogoView.setImageResource(android.R.drawable.ic_menu_edit);
                return tabView;
            case "갤러리":
                tabLogoView.setImageResource(android.R.drawable.ic_menu_edit);
                return tabView;
            case "연락처":
                tabLogoView.setImageResource(android.R.drawable.ic_menu_edit);
                return tabView;
            default:
                return tabView;
        }
    }

    private void initViewPager(){
        FragmentTab searchFragment = new FragmentTab();

        searchFragment.name = "";
        FragmentTab cameraFragment = new GalleryTab();
        cameraFragment.name = "";
        FragmentTab callFragment =  new PhoneTab();
        callFragment.name = "";



        PageAdapter adapter = new PageAdapter(getSupportFragmentManager()); // PageAdapter 생성
        adapter.addItems(callFragment);
        adapter.addItems(cameraFragment);
        adapter.addItems(searchFragment);

        ViewPager main_viewPager = (ViewPager) findViewById(R.id.main_viewPager);
        TabLayout main_tablayout = (TabLayout) findViewById(R.id.main_tablayout);




        main_viewPager.setAdapter(adapter); // 뷰페이저에 adapter 장착
        main_tablayout.setupWithViewPager(main_viewPager); // 탭레이아웃과 뷰페이저를 연동


        main_tablayout.getTabAt(0).setCustomView(createView("연락처"));
        main_tablayout.getTabAt(1).setCustomView(createView("갤러리"));
        main_tablayout.getTabAt(2).setCustomView(createView("일기장"));

//        main_tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int mSelectedPosition = tab.getPosition();
//            }
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//            }
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });


    }


    class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<Contact>> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pd = ProgressDialog.show(MainActivity.this, "Loading Contacts","Please Wait");
        }

        @Override
        protected ArrayList<Contact> doInBackground(Void... params) {
            ArrayList<Contact> contacts = new ArrayList<Contact>();
            Contact temp;

            Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, "upper("+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");

            while (c.moveToNext()) {

                final String contactName =  c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                final String phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                final long phId = c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

                HttpRequestHelper helper = new HttpRequestHelper();
                helper.POST(new Contact(contactName, phNumber));

                temp = new Contact(contactName, phNumber);
                contacts.add(temp);
            }

            c.close();



            return contacts;
        }

        @Override
        protected void onPostExecute(final ArrayList<Contact> contacts) {
            super.onPostExecute(contacts);

            initViewPager(); // 뷰페이저와 어댑터 장착

//            pd.cancel();
//            pd.dismiss();


//            ContactsAdapter adapter = new ContactsAdapter(getApplicationContext(), R.layout.text, contacts);
//
//            list.setAdapter(adapter);
        }
    }

    //갤러리 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case READ_EXTERNAL_STORAGE_PERMMISSION_RESULT:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, (LoaderManager.LoaderCallbacks<Object>) this);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkReadExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Start cursor loader
                getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, (LoaderManager.LoaderCallbacks<Object>) this);
            } else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to view thumbnails", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMMISSION_RESULT);
            }
        } else {
            // Start cursor loader
            getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, (LoaderManager.LoaderCallbacks<Object>) this);
        }
    }


}