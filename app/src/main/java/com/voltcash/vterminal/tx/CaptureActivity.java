package com.voltcash.vterminal.tx;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.kofax.kmc.ken.engines.data.DocumentDetectionSettings;
import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.kui.uicontrols.CameraInitializationEvent;
import com.kofax.kmc.kui.uicontrols.CameraInitializationFailedEvent;
import com.kofax.kmc.kui.uicontrols.CameraInitializationFailedListener;
import com.kofax.kmc.kui.uicontrols.CameraInitializationListener;
import com.kofax.kmc.kui.uicontrols.ImageCaptureView;
import com.kofax.kmc.kui.uicontrols.ImageCapturedEvent;
import com.kofax.kmc.kui.uicontrols.ImageCapturedListener;
import com.kofax.kmc.kui.uicontrols.captureanimations.DocumentCaptureExperience;
import com.kofax.kmc.kui.uicontrols.captureanimations.DocumentCaptureExperienceCriteriaHolder;
import com.kofax.kmc.kui.uicontrols.data.Flash;
import com.kofax.kmc.kut.utilities.AppContextProvider;
import com.kofax.kmc.kut.utilities.Licensing;
import com.kofax.samples.common.License;
import com.kofax.samples.common.PermissionsManager;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.*;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class CaptureActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, CameraInitializationListener, ImageCapturedListener, CameraInitializationFailedListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    WebAPIService api;

    private final PermissionsManager mPermissionsManager = new PermissionsManager(this);

    private boolean mTorchFlag = false;
    private FloatingActionButton mFabTorch;

    private ImageCaptureView mImageCaptureView;
    private FloatingActionButton mForceCapture;
    private DocumentCaptureExperience mDocumentCaptureExperience;

    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppContextProvider.setContext(getApplicationContext());
        Licensing.setMobileSDKLicense(License.PROCESS_PAGE_SDK_LICENSE);

        if (!mPermissionsManager.isGranted(PERMISSIONS)) {
            mPermissionsManager.request(PERMISSIONS);
        }

        setUp();

        OkHttpClient client = new OkHttpClient.Builder().build();
        api = new Retrofit.Builder().baseUrl("http://149.97.166.38:8085/").client(client).build().create(WebAPIService.class);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (mPermissionsManager.isGranted(PERMISSIONS)) {
            setUp();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.permissions_rationale)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void setUp() {
        setContentView(R.layout.activity_capture);

        mImageCaptureView = (ImageCaptureView) findViewById(R.id.view_capture);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mImageCaptureView.addCameraInitializationListener(this);

        SettingsHelperClass.DeviceDeclinationResult declinationPitchRes = SettingsHelperClass.getDeviceDeclinationPitch(this);
        if (declinationPitchRes.result) mImageCaptureView.setDeviceDeclinationPitch(declinationPitchRes.value);

        SettingsHelperClass.DeviceDeclinationResult declinationRollRes = SettingsHelperClass.getDeviceDeclinationRoll(this);
        if (declinationRollRes.result) mImageCaptureView.setDeviceDeclinationRoll(declinationRollRes.value);

        mDocumentCaptureExperience = new DocumentCaptureExperience(mImageCaptureView);
        DocumentCaptureExperienceCriteriaHolder criteriaHolder = new DocumentCaptureExperienceCriteriaHolder();

        DocumentDetectionSettings settings = new DocumentDetectionSettings();
        settings.setTargetFramePaddingPercent(8.0);
        criteriaHolder.setDetectionSettings(settings);
        mDocumentCaptureExperience.setCaptureCriteria(criteriaHolder);

        mDocumentCaptureExperience.addOnImageCapturedListener(this);

        mDocumentCaptureExperience.takePicture();

        mForceCapture = (FloatingActionButton) findViewById(R.id.fab_force_capture);
        mForceCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageCaptureView.forceTakePicture();
            }
        });

        if (Constants.IS_TORCH_SUPPORTED) {
            mFabTorch = (FloatingActionButton) findViewById(R.id.fab_torch);

            mFabTorch.setVisibility(View.VISIBLE);

            mFabTorch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFabTorch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), (mTorchFlag) ? R.drawable.torchoff : R.drawable.torchon));
                    mImageCaptureView.setFlash((mTorchFlag) ? Flash.OFF : Flash.TORCH);
                    mTorchFlag = !mTorchFlag;
                }
            });
        }

        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);

        if (SettingsHelperClass.isGalleryEnabled(this)) {
            fabGallery.setVisibility(View.VISIBLE);
            fabGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String[] GALLERY_PERMISSIONS_REQUIRED = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

                    if (mPermissionsManager.isGranted(GALLERY_PERMISSIONS_REQUIRED)) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, Constants.GALLERY_IMPORT_REQUEST_ID);
                    } else {
                        mPermissionsManager.request(GALLERY_PERMISSIONS_REQUIRED);
                    }
                }
            });
        } else {
            fabGallery.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mForceCapture.setVisibility(View.GONE);

        int manualCaptureTimeVal = SettingsHelperClass.getManualCaptureTime(this);
        if (manualCaptureTimeVal == -1) manualCaptureTimeVal = Constants.DEFAULT_MANUAL_CAPTURE_TIME;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mForceCapture.setVisibility(View.VISIBLE);
            }
        }, manualCaptureTimeVal*1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDocumentCaptureExperience != null) {
            mDocumentCaptureExperience.removeOnImageCapturedListener(this);
            mDocumentCaptureExperience.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case Constants.GALLERY_IMPORT_REQUEST_ID:
                Log.i("onActivityResult", requestCode + " - GALLERY_IMPORT_REQUEST_ID");
                if (resultCode == RESULT_OK && data != null) {
                    Constants.RESULT_IMAGE = decodeImageFromIntent(data);

                    if (Constants.RESULT_IMAGE == null)
                    {
                        new AlertDialog.Builder(this)
                                .setTitle("Error")
                                .setMessage( "Gallery image is not selected" )
                                .setPositiveButton(android.R.string.ok, null)
                                .setCancelable(true)
                                .setIcon(R.drawable.error)
                                .show();
                    }
                    else {


                        Toast.makeText(CaptureActivity.this, "Show PreviewActivity  ", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getApplicationContext(), com.kofax.samples.mobilecapturedemo.PreviewActivity.class);
//                        startActivityForResult(intent, Constants.PROCESSED_IMAGE_REQUEST_ID);
                    }
                }
                break;

            case Constants.PROCESSED_IMAGE_REQUEST_ID:
                Log.i("onActivityResult", requestCode + " - PROCESSED_IMAGE_REQUEST_ID");
                if (resultCode == RESULT_OK || resultCode == Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID) {
                    finish();
                    Toast.makeText(CaptureActivity.this, "Show ProcessImageActivity", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(getApplicationContext(), com.kofax.samples.mobilecapturedemo.ProcessImageActivity.class);
//                    startActivity(intent);
                }
                break;
        }
    }

    private Image decodeImageFromIntent(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        if (selectedImage == null) return null;

        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

        if (cursor == null) return null;

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if(bitmap == null) {
            return null;
        }

        try{
            sendImage(  bitmap,   filePath);
        }catch(Exception e){
            Log.i("sendImage", "Exception:: " + e.getMessage());
            e.printStackTrace();
        }

        return new Image(bitmap);
    }


    @Override
    public void onCameraInitialized(CameraInitializationEvent arg0) {
        mImageCaptureView.setUseVideoFrame(true);
        mImageCaptureView.setFlash(Flash.OFF);

        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    @Override
    public void onImageCaptured(final ImageCapturedEvent imageCapturedEvent) {
        Log.i("onImageCaptured",   " ----------");
        if (imageCapturedEvent != null) {
            if (imageCapturedEvent.getImage() != null) {
                Constants.RESULT_IMAGE = imageCapturedEvent.getImage();

                Toast.makeText(CaptureActivity.this, "Show PreviewActivity", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(getApplicationContext(), com.kofax.samples.mobilecapturedemo.PreviewActivity.class);
//                startActivityForResult(intent, Constants.PROCESSED_IMAGE_REQUEST_ID);
            } else {
                onBackPressed();
            }
        }
    }

    @Override
    public void onCameraInitializationFailed(CameraInitializationFailedEvent event) {
        String message = event.getCause().getMessage();
        if (message == null || message.equals("")) {
            message = getResources().getString(R.string.camera_unavailable);
        }
        Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_LONG).show();
        onBackPressed();
    }


//------- TODO move this to another class


    public interface WebAPIService {
        @Multipart
        @POST("FrontTerminal/v1/tx/checkAuth")
        Call<ResponseBody> upload(@Part MultipartBody.Part file);  //, @Part("image") RequestBody image
    }



    public void sendImage(Bitmap bitmap, String filePath){
        File file = new File(filePath);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);


        Call<ResponseBody> call = api.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.i("-------- onResponse", "success XXX");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("-------- onFailure", t.getMessage());
            }
        });
    }
}
