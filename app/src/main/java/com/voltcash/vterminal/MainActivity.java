package com.voltcash.vterminal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.voltcash.vterminal.auth.AuthTerminal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


//        verifyStoragePermissions(this);
//
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
//        api = new Retrofit.Builder().baseUrl("http://149.97.166.38:8085/").client(client).build().create(WebAPIService.class);

    }

    protected void onTerminalAuth(View view){
        Toast.makeText(getApplicationContext(), "Some text", Toast.LENGTH_SHORT).show();

        Intent login = new Intent(getApplicationContext(), AuthTerminal.class);
        startActivity(login);


//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, PICK_IMAGE);

    }
//
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
//
//    public static final int PICK_IMAGE = 100;
//
//    WebAPIService api;
//
//    public interface WebAPIService {
//        @Multipart
//        @POST("FrontTerminal/v1/terminal/upload")
//        Call<ResponseBody> upload(@Part MultipartBody.Part file);  //, @Part("image") RequestBody image
//    }
//
//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//    }
//
//
//
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
//
//            android.net.Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            android.database.Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//            if (cursor == null)
//                return;
//
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String filePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            Log.d("filePath ", "filePath = " + filePath);
//
//            Bitmap bitmap = BitmapFactory.decodeFile(filePath);// BitmapFactory.decodeResource(getResources(), R.drawable.me);
//
//            sendImage(bitmap, filePath);
//        }
//    }
//
//    public void sendImage(Bitmap bitmap, String filePath){
//        File file = new File(filePath);
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
//        byte[] bitmapdata = bos.toByteArray();
//
////write the bytes in file
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            fos.write(bitmapdata);
//            fos.flush();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
//
//
//        Call<ResponseBody> call = api.upload(body);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call,
//                                   Response<ResponseBody> response) {
//                Log.i("-------- onResponse", "success XXX");
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("-------- onFailure", t.getMessage());
//            }
//        });
//    }

}
