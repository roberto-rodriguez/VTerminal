package com.voltcash.vterminal.util;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import net.idscan.android.dlparser.DLParser;
import net.idscan.components.android.scanpdf417.PDF417ScanActivity;
import net.idscan.components.pdf417.PDF417Data;

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
        String firstName = res.firstName;
        String middleName = res.middleName;
        String lastName = res.lastName;
        String address1 = res.address1;
        String birthdate = res.birthdate;
        String expirationDate = res.expirationDate;
    }
}
