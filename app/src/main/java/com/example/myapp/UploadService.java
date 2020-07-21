package com.example.myapp;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface UploadService {
    @Multipart
    @POST("/api/upload")
    Call<FileNameBody> postImage(@Part("text") String text, @Part("user_id") String id, @Part("group_name") String group_name, @Part MultipartBody.Part image);

    @Multipart
    @POST("/api/memoBook")
    Call<ResponseBody> addFileName(@Part("group_name") String group, @Part("file_name") String file);
}
