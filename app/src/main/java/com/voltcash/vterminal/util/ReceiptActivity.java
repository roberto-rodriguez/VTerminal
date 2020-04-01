package com.voltcash.vterminal.util;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.voltcash.vterminal.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class ReceiptActivity  extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private String FILENAME;
    private String[] lines;
    private String title = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.title = (String)getIntent().getExtras().get(Constants.RECEIPT_TITLE);
        String linesStr = (String)getIntent().getExtras().get(Constants.RECEIPT_LINES);

        try{
            this.lines = linesStr.split("@@");
        }catch(Exception e){
            e.printStackTrace();

            ViewUtil.showError(this, "Ex", e.getMessage());
        }


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                printPdf();
            } else {
                requestPermission(); // Code for permission
            }
        } else {
            printPdf();
        }
    }

    private void printPdf() {

        FILENAME = Environment.getExternalStorageDirectory().toString()
                + "/PDF/" + "Voltcash Receipt.pdf";


        // Create New Blank Document
        Rectangle pagesize = new Rectangle(288, 720);
        Document document = new Document(pagesize);

        // Create Directory in External Storage
        String root = Environment.getExternalStorageDirectory().toString();

        File myDir = new File(root + "/PDF");

        if (!myDir.exists())
            myDir.mkdirs();


        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(FILENAME));

            document.open();

            addHeader(document, pdfWriter);
            addBody(document, pdfWriter);

            document.newPage();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            ViewUtil.showError(this, "printPdf", Log.getStackTraceString(e));
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            ViewUtil.showError(this, "printPdf", Log.getStackTraceString(e));
        } catch (IOException e) {
            e.printStackTrace();

            ViewUtil.showError(this, "printPdf", Log.getStackTraceString(e));
        }

        document.close();

        openGeneratedPDF();
    }

    public void addHeader(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {

        Font subHeaderFont  = new Font(Font.FontFamily.UNDEFINED, 14, Font.NORMAL);

        Paragraph header = new Paragraph();
        header.setFont(subHeaderFont);

        header.add("Service Provided by\n");
        header.add("Voltcash, Inc.\n");
        header.add("1-800-249-3042\n");
        header.add("www.voltcash.com\n");

        header.add("\n" + this.title + "\n\n");
        header.setAlignment(Element.ALIGN_CENTER);

        // Add all above details into Document
        document.add(header);
    }

    public void addBody(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {
        PdfPTable separator = new PdfPTable(1);
        separator.setWidthPercentage(100.0f);
        PdfPCell line = new PdfPCell(new Paragraph(""));
        line.setBorder(Rectangle.BOTTOM);
        separator.addCell(line);

        document.add(separator);

        StringBuilder body = new StringBuilder("<table style=\"width: 100%; margin-top: 40px;\">");

        String dateTime = getDateTime();
        String[] dateAndTime = dateTime.split(" ");

        buildTR(body, "Date", dateAndTime[0]);
        buildTR(body, "Time", dateAndTime[1]);

        for(String lineTuple: this.lines){
            String[] parts = lineTuple.split("->");
            buildTR(body,  parts[0], parts[1]);
        }

        body.append("</table>");

        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
        worker.parseXHtml(pdfWriter, document, new StringReader(body.toString()));

        document.newPage();
    }

    private void buildTR(StringBuilder body, String name, String value){
        body.append("<tr style=\"width: 100%; height:30px;\">");
        body.append(  "<td style=\"width: 50%;\"><b>" + name + "</b></td><td style=\"width:  50%; float:right; text-align: right;\"><p style=\"width:100%; text-align: right; float:right\">" +  value  + "</p></td>");
        body.append("</tr>");
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission( this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale( this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText( this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions( this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private void openGeneratedPDF() {
        File file = new File(FILENAME);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

//            Uri uri = Uri.fromFile(file);
            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String getDateTime(){
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return df.format(new Date());
    }
}
