package com.example.myapp;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhoneAddActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_phone);

        Button phoneAddButton = findViewById(R.id.phone_addbutton);

        phoneAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText editTextPersonName = (EditText)findViewById(R.id.editTextPersonName);
                final EditText editTextPhone = (EditText)findViewById(R.id.editTextPhone);

                ContentValues contentValues = new ContentValues();
                contentValues.put(ContactsContract.RawContacts.CONTACT_ID, 0);
                contentValues.put(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DISABLED);
                Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
                long rawContactId = ContentUris.parseId(rawContactUri);

                // 전화번호
                contentValues.clear();
                contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, editTextPhone.getText().toString());
                Uri dataUri = getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);

                 // 이름
                contentValues.clear();
                contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                contentValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, editTextPersonName.getText().toString());
                dataUri = getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);

                 class PostContact extends AsyncTask<Void, Void, String> {
                    @Override
                    protected String doInBackground(Void... voids) {
                        HttpRequestHelper helper = new HttpRequestHelper();
                        helper.POST(new Contact(editTextPersonName.getText().toString(), editTextPhone.getText().toString()));


                        return "";
                    }
                 }

                 PostContact lcfd = new PostContact();
                 lcfd.execute();


                finish();
            }
        });
    }



}
