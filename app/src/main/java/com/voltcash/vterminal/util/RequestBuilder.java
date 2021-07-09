package com.voltcash.vterminal.util;

import com.kofax.kmc.ken.engines.data.Image;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by roberto.rodriguez on 2/17/2020.
 */

public class RequestBuilder {

    public static RequestBody buildStringBodyFromTxData(String fieldName) throws IOException {
        return buildStringBody(TxData.getString(fieldName));
    }

    public static RequestBody buildStringBody(String value) throws IOException {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    public static MultipartBody.Part buildMultipartBody(String fieldName) throws Exception {
        String path = android.os.Environment
                .getExternalStorageDirectory()
                + File.separator;

        String extension = ".tiff";

        if(Field.TX.ID_FRONT.equals(fieldName) || Field.TX.ID_BACK.equals(fieldName)){
            extension = ".jpg";
        }

        String fileName = fieldName + "_" + System.currentTimeMillis() + extension;


        Image image =  TxData.getImage(fieldName);

        if(image == null)return null;

     //   Bitmap bitmap = image.getImageBitmap();

        image.setImageFilePath(path + fileName);
        image.imageWriteToFile();

        Thread.sleep(1000);

        final File file = new File(path, fileName);

//        if(!file.exists()){
//            file.createNewFile();
//        }

//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
//        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
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

        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData(fieldName , file.getName(), reqFile);

        return body;
    }



//    public static MultipartBody.Part buildMultipartBody(String fieldName, List<File> filesToDelete) throws IOException {
//        String path = android.os.Environment
//                .getExternalStorageDirectory()
//                + File.separator;
//
//        String fileName = fieldName + "_" + System.currentTimeMillis() + ".tiff";
//        final File file = new File(path, fileName);
//
//        Image image =  TxData.getImage(fieldName);
//
//        if(image == null)return null;
//
//        Bitmap bitmap = image.getImageBitmap();
//
//        if(!file.exists()){
//            file.createNewFile();
//        }
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
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
//        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData(fieldName , file.getName(), reqFile);
//
//        filesToDelete.add(file);
//
//        return body;
//    }

}
