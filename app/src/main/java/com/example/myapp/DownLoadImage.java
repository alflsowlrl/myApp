package com.example.myapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DownLoadImage extends AppCompatActivity {
    private String base_url = "http://192.249.19.244:1880/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_image);

        downloadImage();
    }

    private void downloadImage(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(new OkHttpClient.Builder().build())
                .build();
        Log.d("download", "download image");
        ApiService download = retrofit.create(ApiService.class);
        Call<ResponseBody> call = download.getImage("048ab4f1b5c61dca9db77c4ab43f0875.png");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("download", "Got the body for the file");

                    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();

                    class DownloadZipFileTask extends AsyncTask<ResponseBody, Void, Void> {

                        @Override
                        protected Void doInBackground(ResponseBody... responseBodies) {
                            saveToDisk(responseBodies[0], "download.png");
                            return null;
                        }
                    }

                    DownloadZipFileTask downloadZipFileTask = new DownloadZipFileTask();
                    downloadZipFileTask.execute(response.body());

                } else {
                    Log.d("download", "Connection failed " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private void saveToDisk(ResponseBody body, String filename) {
        try {
//
            File destinationFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");

            Log.d("download", "path: " + destinationFile.getAbsolutePath());

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(destinationFile);
                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                long fileSize = body.contentLength();
                Log.d("download", "File Size=" + fileSize);
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
//                    Log.d("download", "Progress: " + progress + "/" + fileSize + " >>>> " + (float) progress / fileSize);
                }

                outputStream.flush();

                return;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("download", "Failed to save the file!");
                return;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("download", "Failed to save the file!");
            return;
        }
    }
}
