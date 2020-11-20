package com.voltcash.vterminal.util;

import android.content.Context;
import android.content.Intent;
import net.idscan.android.dlparser.DLParser;
import net.idscan.components.android.scanpdf417.PDF417ScanActivity;

public class IDScanBarcodeParser {
    private static DLParser parser;

    public static boolean parse(Context ctx, int resultCode, Intent data) throws Exception{

        switch (resultCode) {
            case PDF417ScanActivity.RESULT_OK:
                if (data != null) {
                    PDF417ScanActivity.PDF417Data result = data.getParcelableExtra(PDF417ScanActivity.DOCUMENT_DATA);
                    if (result != null) {
                        parseData(ctx, result.barcodeData);
                    }
                }

                break;

            case PDF417ScanActivity.ERROR_INVALID_CAMERA_NUMBER:
            case PDF417ScanActivity.ERROR_CAMERA_NOT_AVAILABLE:
            case PDF417ScanActivity.ERROR_INVALID_CAMERA_ACCESS:
                throw new Exception("Invalid camera access.");
            case PDF417ScanActivity.ERROR_RECOGNITION:
                throw new Exception(data.getStringExtra(PDF417ScanActivity.ERROR_DESCRIPTION));
            case PDF417ScanActivity.RESULT_CANCELED:
                break;

            default:
                throw new Exception("Undefined error.");
        }
        return false;
    }

    public static void parseData(Context ctx, byte[] bytes) throws Exception{
        parser = new DLParser();
        parser.setup(ctx, Settings.ID_SCAN_ID_PARSING_KEY);

        DLParser.DLResult res = parser.parse(bytes);

        StringBuilder sb = new StringBuilder("<DriverLicense>");
        sb.append("<ID>"                 + res.licenseNumber  + "</ID>");
        sb.append("<ADDRESS>"            + res.address1       + "</ADDRESS>");
        sb.append("<GENDER>"             + res.gender         + "</GENDER>");
        sb.append("<CITY>"               + res.city           + "</CITY>");
        sb.append("<LAST_NAME>"          + res.lastName       + "</LAST_NAME>");
        sb.append("<ZIPCODE>"            + res.postalCode     + "</ZIPCODE>");
        sb.append("<FIRST_NAME>"         + res.firstName      + "</FIRST_NAME>");
        sb.append("<MIDDLE_NAME>"        + res.middleName     + "</MIDDLE_NAME>");
        sb.append("<IDSTATE>"            + res.issuedBy       + "</IDSTATE>");
        sb.append("<STATE_ABBREVIATION>" + res.issuedBy       + "</STATE_ABBREVIATION>");
        sb.append("<BORNDATE>"           + res.birthdate      + "</BORNDATE>");
        sb.append("<EXPIRATION_DATE>"    + res.expirationDate + "</EXPIRATION_DATE>");
        sb.append("</DriverLicense>");
        TxData.put(Field.TX.DL_DATA_SCAN, sb.toString());
    }
}
