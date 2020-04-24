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
import java.util.Date;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class ReceiptCardToBankActivity extends AppCompatActivity {

//    protected String[] achLines;
//
//
//    protected void setAchParams(){
//        String achLinesStr = (String)getIntent().getExtras().get(Constants.RECEIPT_ACH_LINES);
//        this.achLines = achLinesStr.split("@@");
//    }
//
//    public void addAchForm(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {
//        addHeader(document, pdfWriter, "ACH Authorization Form");
//
//        addAchBody(document, pdfWriter);
//    }
//
//    public void addAchBody(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {
//        PdfPTable separator = new PdfPTable(1);
//        separator.setWidthPercentage(100.0f);
//        PdfPCell line = new PdfPCell(new Paragraph(""));
//        line.setBorder(Rectangle.BOTTOM);
//        separator.addCell(line);
//
//        document.add(separator);
//
//        StringBuilder body = new StringBuilder("<table style=\"width: 100%; margin-top: 40px;\">");
//
//
//        for(int i = 0; i < this.lines.length; i++){
//            String lineTuple = this.lines[i];
//            String[] parts = lineTuple.split("->");
//            buildTR(body,  parts[0], parts[1]);
//
//            if(i == 0){
//                body.append("<tr style=\"width: 100%; height:30px;\">");
//                body.append(  "<td colspan=\"2\"><p style=\"width: 100%;text-align: center;\">Funds Settlement Information</p></td>");
//                body.append("</tr>");
//            }
//        }
//
//        body.append("</table>");
//
//        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
//        worker.parseXHtml(pdfWriter, document, new StringReader(body.toString()));
//
//        document.newPage();
//    }
 }
