package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadPost extends AppCompatActivity {

    private final static int IMAGE_RESULT = 200;
    private String uploadFilePath;
    private final String server_url = "http://192.249.19.244:1880";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        final Button btn = findViewById(R.id.uploadPostBtn);
        final ImageView imageView = findViewById(R.id.uploadPostImageView);
        final EditText editText = findViewById(R.id.uploadPostEditText);

        final String id = PreferenceManager.getString(getApplicationContext(),"user_id");
        final String group_name = PreferenceManager.getString(getApplicationContext(),"group_name");

        imageView.setImageResource(R.drawable.phone_on);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uploadFilePath != null){

                    EditText editText = findViewById(R.id.uploadPostEditText);
                    String content = editText.getText().toString();
                    String fileNameInDB;

                    File file = new File(uploadFilePath);
                    String fileName = file.getName();

                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part imageBody = MultipartBody.Part.createFormData("productImg",fileName,requestBody);
                    Gson gson = new GsonBuilder().setLenient().create();
                    UploadService uploadService = new Retrofit.Builder().
                            baseUrl(server_url).
                            addConverterFactory(GsonConverterFactory.create(gson)).
                            build().
                            create(UploadService.class);

                    uploadService.postImage(content, id, group_name, imageBody).enqueue(new Callback<FileNameBody>() {
                        @Override
                        public void onResponse(Call<FileNameBody> call, Response<FileNameBody> response) {
                            Log.d("upload", response.body().file_name);

                            final String  file_name = response.body().file_name;

                            class AddFileName extends AsyncTask{
                                @Override
                                protected Object doInBackground(Object[] objects) {
                                    HttpRequestHelper helper = new HttpRequestHelper();
                                    helper.ADD_FILE_NAME(group_name, file_name);

                                    return null;
                                }
                            }

                            AddFileName addFileName = new AddFileName();
                            addFileName.execute();


                        }

                        @Override
                        public void onFailure(Call<FileNameBody> call, Throwable t) {
                            Log.d("upload", "error");
                        }
                    });






                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });


    }

    public Intent getPickImageChooserIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
//
            if (requestCode == IMAGE_RESULT) {

                String filePath = getImageFilePath(data);
                if (filePath != null) {
//                    frameLayout.setVisibility(GONE);
                    Bitmap mBitmap = BitmapFactory.decodeFile(filePath);
//                    getByteArrayInBackground(mBitmap);
                    final ImageView imageView = findViewById(R.id.uploadPostImageView);
                    imageView.setImageBitmap(mBitmap);
                    Log.d("filepath", filePath);
                }

                uploadFilePath = filePath;
            }

        }
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor =getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

}