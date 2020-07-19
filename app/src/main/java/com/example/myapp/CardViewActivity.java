package com.example.myapp;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.kakao.usermgmt.StringSet.user_id;

public class CardViewActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Contact> studentList;

    private Button btnSelection;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_iist);
        btnSelection = (Button) findViewById(R.id.btnShow);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("핸드폰 주소록");



        LoadContactsAyscn lca = new LoadContactsAyscn();
        lca.execute();

        studentList = new ArrayList<Contact>();

//        for (int i = 1; i <= 15; i++) {
//            Contact st = new Contact("Name " + i, "Number" + i
//                    +  false);
//
//            studentList.add(st);
//        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Android Students");

        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create an Object for Adapter
        mAdapter = new CardViewDataAdapter(studentList);

        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);

        final Context context = getApplicationContext();

        final ArrayList<Contact> selected = new ArrayList<Contact>();

        btnSelection.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String data = "";
                final List<Contact> stList = ((CardViewDataAdapter) mAdapter)
                        .getStudentist();

                for (int i = 0; i < stList.size(); i++) {
                    Contact singleStudent = stList.get(i);


                    if (singleStudent.isSelected() == true) {

                        selected.add(singleStudent);

                        data = data + "\n" + singleStudent.getName().toString() + ", " + singleStudent.getNumber().toString();

                        /*
                         * Toast.makeText( CardViewActivity.this, " " +
                         * singleStudent.getName() + " " +
                         * singleStudent.getEmailId() + " " +
                         * singleStudent.isSelected(),
                         * Toast.LENGTH_SHORT).show();
                         */

                        Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();
                    }

                }

                class PostContacts extends AsyncTask<Void, Void, Void>{
                    @Override
                    protected Void doInBackground(Void... voids) {
                        HttpRequestHelper helper = new HttpRequestHelper();
                        helper.POST_CONTACTS(selected, Long.valueOf(PreferenceManager.getString(context,"user_id")));
                        return null;
                    }
                }

                PostContacts postContacts = new PostContacts();
                postContacts.execute();

            }
        });
    }

    //주소록 함수
    class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<Contact>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<Contact> doInBackground(Void... params) {
            ArrayList<Contact> contacts = new ArrayList<Contact>();
            Contact temp;

            Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, "upper("+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");

            while (c.moveToNext()) {

                String contactName =  c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                temp = new Contact(contactName, phNumber, false);
                studentList.add(temp);
            }

            //contacts.sort(contactName);
            c.close();

            return contacts;
        }

        @Override
        protected void onPostExecute(final ArrayList<Contact> contacts) {
            super.onPostExecute(contacts);

//            pd.cancel();


//            CardViewDataAdapter adapter = new CardViewDataAdapter(getApplicationContext(), R.layout.text, contacts);

            CardViewDataAdapter adapter = new CardViewDataAdapter(studentList);

            mRecyclerView.setAdapter(adapter);
        }
    }

}