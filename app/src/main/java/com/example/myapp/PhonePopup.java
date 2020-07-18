package com.example.myapp;

import android.app.Activity;
import android.app.AsyncNotedAppOp;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;


public class PhonePopup extends Activity {
    TextView txtText;
    Contact contact = null;

    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_phone_popup);

        //UI 객체생성
        txtText = (TextView) findViewById(R.id.txtText);

        //데이터 가져오기
        Intent intent = getIntent();

//        val id = intent.getIntExtra("id", -100)
        String name = intent.getStringExtra("name");
        String number = intent.getStringExtra("number");
        txtText.setText(name + ": " + number);

        contact = new Contact(name, number);

        Button phoneMod = findViewById(R.id.phoneMod);
        Button phoneDel = findViewById(R.id.phoneDel);

        phoneMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contact != null){
                    editPhone(contact);
                }
                finish();
            }
        });

        phoneDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contact != null){
                    removePhone(contact);
                }
                Toast.makeText(view.getContext(), contact.getName() + "님이 삭제 되었습니다", Toast.LENGTH_SHORT).show();


                finish();
            }
        });
    }

    //확인 버튼 클릭
//    public void mOnMod(View v) {
//        //데이터 전달하기
//        if(contact != null){
//            editPhone(contact);
//        }
//
//        //액티비티(팝업) 닫기
//        finish();
//    }
//
//    public void mOnDel(View v) {
//        if(contact != null){
//            removePhone(contact);
//        }
//
//        Toast.makeText(v.getContext(), contact.getName() + "님이 삭제 되었습니다", Toast.LENGTH_SHORT).show();
//        //액티비티(팝업) 닫기
//        finish();
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP: {
                LinearLayout view = findViewById(R.id.phoneAddPopup);

                Rect rect = new Rect();
                view.getLocalVisibleRect(rect);

                if(!(rect.left < event.getX() && event.getX() < rect.right && rect.top < event.getY() && event.getY() < rect.bottom)){
                    finish();
                }
            }
        }
        return true;
    }

    private void removePhone(final Contact contact) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };


        String displayName;
        Uri contentUri = null;
//        Cursor cursor= getContentResolver().query(
//            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//            projection,
//            null,
//            null,
//            null
//        );


        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);



        while (cursor.moveToNext()) {

//            int idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID);
//            int displayNameColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//            long id = cursor.getLong(idColumn);
//            displayName = cursor.getString(displayNameColumn);
//            if (displayName != contact.getName()) {
//                continue;
//            }
//            contentUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, Long.toString(id));

            final String contactName =  cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            final String phNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            final long id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID));

            if(contactName.equals(contact.getName())  && phNumber.equals(contact.getNumber().replaceAll("-", ""))){

                //핸드폰 연락처에서 삭제하기
                contentUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, Long.toString(id));
                getContentResolver().delete(contentUri, null, null);

                //DB에서 삭제하기
                final HttpRequestHelper helper = new HttpRequestHelper();

                class DeleteAsync extends AsyncTask<Void, Void, Void>{
                    @Override
                    protected Void doInBackground(Void... params) {

                        helper.DELETE(contact);
                        return null;
                    }
                }

                DeleteAsync asn = new DeleteAsync();
                asn.execute();
            }
        }

        Log.d("myApp", "uri: " + contentUri);

//        getContentResolver().delete(contentUri, null, null);
    }

    private void editPhone(Contact contact) {
         String[] projection = {
                 ContactsContract.Contacts._ID,
                 ContactsContract.Contacts.DISPLAY_NAME,
                 ContactsContract.Contacts.LOOKUP_KEY
         };

        String displayName;

        // The lookup key from the Cursor
        String currentLookupKey;
        // The _ID value from the Cursor
        Long currentId = Long.valueOf(0);
        // A content URI pointing to the contact
        Uri selectedContactUri = null;


        Cursor cursor = getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            null
        );

        int displayNameColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int lookupKeyIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY);

        while (cursor.moveToNext()) {
            displayName = cursor.getString(displayNameColumn);
            currentLookupKey = cursor.getString(lookupKeyIndex);
            if (displayName != contact.getName()) {
                continue;
            }
            else{
                selectedContactUri = ContactsContract.Contacts.getLookupUri(currentId, currentLookupKey);
            }
        }

        Intent editIntent = new Intent(Intent.ACTION_EDIT);

        editIntent.setDataAndType(selectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);


        editIntent.putExtra("finishActivityOnSaveCompleted", true);
        this.startActivity(editIntent);
    }
}
