package com.voltcash.vterminal.util;

import com.kofax.kmc.ken.engines.data.Image;

public class Constants {
    public static Image RESULT_IMAGE = null;
    public final static String PHOTO_ALBUM_NAME = "KofaxMobileCapture";

    public static boolean IS_TORCH_SUPPORTED = false;

    public static String VERSION = "";

    public final static int DEFAULT_MANUAL_CAPTURE_TIME = 0;
    public final static int MONTHLY_LIMIT = 100;

    public final static int GALLERY_IMPORT_REQUEST_ID   = 56;
    public final static int PROCESSED_IMAGE_REQUEST_ID  = 57;
    public final static int SEND_EMAIL_REQUEST_ID       = 58;

    public final static int PROCESSED_IMAGE_RETAKE_RESPONSE_ID  = 156;
    public final static int PROCESSED_IMAGE_ACCEPT_RESPONSE_ID  = 157;

    //activity state constants
    public final static String IMAGE_GALLERY_PATH_NAME = "SavedImageGalleryPath";
    public final static String IMAGE_EXTERNAL_PATH_NAME = "SavedImageExternalPath";
}

