package com.voltcash.vterminal.util;

import android.graphics.Bitmap;

import com.kofax.kmc.ken.engines.data.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by roberto.rodriguez on 2/17/2020.
 */

public class RequestBuilder {

    public static RequestBody buildStringBody(String value) throws IOException {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    public static MultipartBody.Part buildMultipartBody(String fieldName, List<File> filesToDelete) throws IOException {
        String path = android.os.Environment
                .getExternalStorageDirectory()
                + File.separator;

        String fileName = fieldName + "_" + System.currentTimeMillis() + ".jpg";
        final File file = new File(path, fileName);

        Image checkFront =  TxData.getImage(fieldName);
        Bitmap bitmap = checkFront.getImageBitmap();

        if(!file.exists()){
            file.createNewFile();
        }

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
        MultipartBody.Part body = MultipartBody.Part.createFormData(fieldName , file.getName(), reqFile);

        filesToDelete.add(file);

        return body;
    }
}
