package com.example.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class MainActivity extends AppCompatActivity{



    //Gallery
    private final static int READ_EXTERNAL_STORAGE_PERMMISSION_RESULT = 0;
    private final static int MEDIASTORE_LOADER_ID = 0;

    enum TAB {GALLERY_TAB, PHONE_TAB, MEMO_TAB}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("App Name");

        Intent intent =  getIntent();
        final Long id = intent.getLongExtra("id", -1);

        if(id == -1){
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
        else{

            class Register extends AsyncTask<Void, Void, Void>{
                @Override
                protected Void doInBackground(Void... voids) {
                    HttpRequestHelper helper = new HttpRequestHelper();
                    helper.REGISTER_USER(id);
                    return null;
                }
            }

            Register register = new Register();
            register.execute();

        }



        PreferenceManager.setString(this, "user_id", String.valueOf(id));
        PreferenceManager.setString(this, "group_name", "abs");
        initViewPager(); // 뷰페이저와 어댑터 장착
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_btn:
                //툴바의 아이콘이 할 기능 정의할 것
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for(Fragment fragment : getSupportFragmentManager().getFragments()){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


//    class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<Contact>> {
//        ProgressDialog pd;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
////            pd = ProgressDialog.show(MainActivity.this, "Loading Contacts","Please Wait");
//        }
//
//        @Override
//        protected ArrayList<Contact> doInBackground(Void... params) {
//            ArrayList<Contact> contacts = new ArrayList<Contact>();
//            Contact temp;
//
//            Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, "upper("+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
//
//            while (c.moveToNext()) {
//
//                final String contactName =  c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                final String phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                final long phId = c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
//
//                HttpRequestHelper helper = new HttpRequestHelper();
//
//                temp = new Contact(contactName, phNumber, false);
//                contacts.add(temp);
//            }
//
//            c.close();
//
//
//
//            return contacts;
//        }
//
//        @Override
//        protected void onPostExecute(final ArrayList<Contact> contacts) {
//            super.onPostExecute(contacts);
//
//            initViewPager(); // 뷰페이저와 어댑터 장착
//
////            pd.cancel();
////            pd.dismiss();
//
//
////            ContactsAdapter adapter = new ContactsAdapter(getApplicationContext(), R.layout.text, contacts);
////
////            list.setAdapter(adapter);
//        }
//    }

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