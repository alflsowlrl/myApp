package com.example.myapp;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.FragmentTab;
import com.example.myapp.FullScreenImageActivity;
import com.example.myapp.MediaStoreAdapter;
import com.example.myapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static java.lang.String.valueOf;

public class GalleryTab extends FragmentTab
        implements LoaderManager.LoaderCallbacks<Cursor>, MediaStoreAdapter.OnClickThumbListener, ProgressRequestBody.UploadCallbacks{


    private final static int READ_EXTERNAL_STORAGE_PERMMISSION_RESULT = 0;
    private final static int MEDIASTORE_LOADER_ID = 0;
    private MediaStoreAdapter mMediaStoreAdapter;
    private RecyclerView mThumbnailRecyclerView;
    private ConstraintLayout mConstraintLayout;

    private final String server_url = "http://192.249.19.244:1880";

    private final static int IMAGE_RESULT = 200;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mConstraintLayout = (ConstraintLayout)getLayoutInflater().inflate(R.layout.gallery_tab, container, false);

        mThumbnailRecyclerView = (RecyclerView) mConstraintLayout.findViewById(R.id.thumbnailRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 3);
        mThumbnailRecyclerView.setLayoutManager(gridLayoutManager);

        mMediaStoreAdapter = new MediaStoreAdapter(this.getActivity(), this);
        mThumbnailRecyclerView.setAdapter(mMediaStoreAdapter);

        final FloatingActionButton mAddImageButton = mConstraintLayout.findViewById(R.id.addImageButton);
        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UploadPost.class);
                startActivity(intent);

//                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });

        final FloatingActionButton mDownloadImageButton = mConstraintLayout.findViewById(R.id.downloadImageButton);
        mDownloadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DownLoadImage.class);
                startActivity(intent);
            }
        });

        loadDBFiles();

        checkReadExternalStoragePermission();

        return mConstraintLayout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
//
            if (requestCode == IMAGE_RESULT) {


                String filePath = getImageFilePath(data);
                if (filePath != null) {
//                    frameLayout.setVisibility(GONE);
                    Bitmap mBitmap = BitmapFactory.decodeFile(filePath);
                    getByteArrayInBackground(mBitmap);
//                    imageView.setImageBitmap(mBitmap);
                    Log.d("filepath", filePath);
                }
            }

        }
    }

    private void loadDBFiles(){
        final ArrayList<String> result = new ArrayList<String>();

        final String group_name = PreferenceManager.getString(getContext(),"group_name");

        Gson gson = new GsonBuilder().setLenient().create();
        UploadService uploadService = new Retrofit.Builder().
                baseUrl(server_url).
                addConverterFactory(GsonConverterFactory.create(gson)).
                build().
                create(UploadService.class);

        uploadService.getFileNames(group_name).enqueue(new Callback<FileNameBookBody>() {
            @Override
            public void onResponse(Call<FileNameBookBody> call, Response<FileNameBookBody> response) {
                Log.d("gallery", "success");
                result.addAll(response.body().file_name_list);
            }

            @Override
            public void onFailure(Call<FileNameBookBody> call, Throwable t) {
                Log.d("gallery", "fail");
            }
        });

        mMediaStoreAdapter.file_name_list = result;

    }

    private void getByteArrayInBackground(final Bitmap bitmap) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] byteArray = bos.toByteArray();

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        frameLayout.setVisibility(View.VISIBLE);
//                    }
//                });

                if (bitmap != null){
                    multipartImageUpload(byteArray);
                }



            }
        };
        thread.start();
    }

    private void multipartImageUpload(byte[] byteArray) {

        OkHttpClient client = new OkHttpClient.Builder().build();

        //change the ip to yours.
        ApiService apiService = new Retrofit.Builder().baseUrl(server_url).client(client).build().create(ApiService.class);


        try {

            if (byteArray != null) {
                File filesDir = this.getContext().getFilesDir();
                File file = new File(filesDir, "image" + ".png");


                FileOutputStream fos = new FileOutputStream(file);
                fos.write(byteArray);
                fos.flush();
                fos.close();
                fos.close();

//                textView.setTextColor(Color.BLUE);

                ProgressRequestBody fileBody = new ProgressRequestBody(file, this);
                MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), fileBody);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");

                Call<ResponseBody> req = apiService.postImage(body, name);
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(getContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        textView.setText("Uploaded Failed!");
//                        textView.setTextColor(Color.RED);
                        Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getContext().getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    public Intent getPickImageChooserIntent() {

//        Uri outputFileUri = getCaptureImageOutputUri();
//
//        List<Intent> allIntents = new ArrayList<>();
//        PackageManager packageManager = getContext().getPackageManager();
//
//        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
////        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
////        for (ResolveInfo res : listCam) {
////            Intent intent = new Intent(captureIntent);
////            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
////            intent.setPackage(res.activityInfo.packageName);
////            if (outputFileUri != null) {
////                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
////            }
////            allIntents.add(intent);
////        }
//
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
//        for (ResolveInfo res : listGallery) {
//            Intent intent = new Intent(galleryIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(res.activityInfo.packageName);
//            allIntents.add(intent);
//        }
//
//        Intent mainIntent = allIntents.get(allIntents.size() - 1);
//        for (Intent intent : allIntents) {
//            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
//                mainIntent = intent;
//                break;
//            }
//        }
//        allIntents.remove(mainIntent);
//
//        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

        return intent;
    }

    //갤러리 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case READ_EXTERNAL_STORAGE_PERMMISSION_RESULT:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.getActivity().getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkReadExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Start cursor loader
                this.getActivity().getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null,  this);
            } else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this.getContext(), "App needs to view thumbnails", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMMISSION_RESULT);
            }
        } else {
            // Start cursor loader
            this.getActivity().getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null,  this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        return new CursorLoader(
                this.getContext(),
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMediaStoreAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMediaStoreAdapter.changeCursor(null);
    }

    @Override
    public void OnClickImage(Uri imageUri) {
        Intent fullScreenIntent = new Intent(this.getContext(), FullScreenImageActivity.class);
        fullScreenIntent.setData(imageUri);
        startActivity(fullScreenIntent);
    }

    @Override
    public void OnClickVideo(Uri videoUri) {
        Intent videoPlayIntent = new Intent(this.getContext(), VideoPlayActivity.class);
        videoPlayIntent.setData(videoUri);
        startActivity(videoPlayIntent);

    }

    @Override
    public void onProgressUpdate(int percentage) {


    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void uploadStart() {
//        Toast.makeText(this.getContext(), "Upload started", Toast.LENGTH_SHORT).show();
    }
}
