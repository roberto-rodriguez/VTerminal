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
import com.itextpdf.text.PageSize;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class ReceiptActivity  extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private String FILENAME;
    private ArrayList<String> lines;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.lines = (ArrayList<String>)getIntent().getStringArrayListExtra(Constants.RECEIPT_LINES);

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


        // Create Pdf Writer for Writting into New Created Document
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(FILENAME));

            // Open Document for Writting into document
            document.open();
            // User Define Method
      //      addMetaData(document);
            addTitlePage(document, pdfWriter);
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

        // Close Document after writting all content
        document.close();

//        Toast.makeText(this, "PDF File is Created. Location : " + FILENAME,
//                Toast.LENGTH_LONG).show();

        openGeneratedPDF();
    }

    // Set PDF document Properties
//    public void addMetaData(Document document) {
////        document.addTitle("RESUME");
////        document.addSubject("Person Info");
////        document.addKeywords("Personal,	Education, Skills");
////        document.addAuthor("TAG");
////        document.addCreator("TAG");
//    }

    public void addTitlePage(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {
        // Font Style for Document
        Font headerFont= new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD  | Font.UNDERLINE, BaseColor.GRAY);
        Font subHeaderFont  = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
        Font footerFont= new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD  | Font.UNDERLINE, BaseColor.GRAY);

//        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
//        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // Start New Paragraph
        Paragraph header = new Paragraph();
        // Set Font in this Paragraph
        header.setFont(headerFont);
        // Add item into Paragraph
        header.add("Voltcash\n");

        header.setFont(subHeaderFont);
        header.add("\nDeposit Check Transaction \n\n");
        header.setAlignment(Element.ALIGN_CENTER);

        // Add all above details into Document
        document.add(header);

        // Create line separator
        PdfPTable separator = new PdfPTable(1);
        separator.setWidthPercentage(100.0f);
        PdfPCell line = new PdfPCell(new Paragraph(""));
        line.setBorder(Rectangle.BOTTOM);
        separator.addCell(line);

        document.add(separator);

        StringBuilder body = new StringBuilder("<table style=\"width: 100%; margin-top: 40px;\">");

        for(String lineTuple: this.lines){
            String[] parts = lineTuple.split("->");
            body.append("<tr style=\"width: 100%; height:30px;\">");
            body.append(  "<td style=\"width: 50%;\"><b>" + parts[0] + ":</b></td><td style=\"width:  50%;\"><p style=\"width:100%; text-align: right;\">" +  parts[1]  + "</p></td>");
            body.append("</tr>");
        }

        body.append("</table>");

        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
        worker.parseXHtml(pdfWriter, document, new StringReader(body.toString()));

        document.newPage();
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
             //   Toast.makeText( this, "No Application available to view pdf", Toast.LENGTH_LONG).show();

            }
        }
    }
}
