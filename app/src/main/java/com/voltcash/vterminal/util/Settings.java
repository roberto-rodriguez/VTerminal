package com.voltcash.vterminal.util;

public class Settings {

    public static int ENV = Constants.ENV_PROD;

    //  public static final String SERVER_URL = "http://test.girocheck.net:8085/";

    public static final String SERVER_URL = "https://www.girocheck.net:8222/";

    public static int CHECK_RESOLUTION = 200;// 175;

    public static final String VERSION = "21.05.13";

   // https://docs.idscan.net/camerascan/pdf417.html
    public static final String  ID_SCAN_CAMERA_SCANNING_KEY = "DqJ/LeYWD2HMpu/voDPQF3NA/OopTQ1coALkmGDZPaWZ2BmQ6yGS81WV82LGd0yJcepMUON2oH0P6cVU0+wCtwzBVITzln9binHmIHmasZjq9Gum/DXFGZFUpJiysZw56AgriXDwWJo7/hAPMUzeQJLl5Ktj1eunhOGrKeMB2qQ=";

    // https://docs.idscan.net/idparsing/android.html
    public static final String  ID_SCAN_ID_PARSING_KEY = "XxP0Ebh+ZolITQ3f4riVxNDLms6+WzGGBsBL1Q/05Friv139VyMkrKh3hcN5o16zpk9czW9xjNIj2tKUliIKEHPApsCQMk8wQ9XsocLPabd4EzIZWKLm0fDEkVInrFtTr6rcu/o4UH3U2sb0Ce7haPTd8+nP7S2ndIo8XRtUxx4=";


    public static final String DEMO_ACCESS_CODE = "demo";
    public static final String DEMO_USERNAME    = "demo";
    public static final String DEMO_PASSWORD    = "demo";

    //NOT USED
    //implementation 'net.idscan.components.android:scanmrz:1.0.0'

    // compile ('com.android.support:support-v4:21.0.3')
    //da problemas
}

