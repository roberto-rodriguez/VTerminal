package com.voltcash.vterminal.util;

import android.content.ContextWrapper;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class PDFBuilder {


    public boolean createPDF(String rawHTML, String fileName, ContextWrapper context)throws Exception {
        final String APPLICATION_PACKAGE_NAME = context.getBaseContext().getPackageName();
        File path = new File(Environment.getExternalStorageDirectory(), APPLICATION_PACKAGE_NAME);
        if (!path.exists()) {
            path.mkdir();
        }
        File file = new File(path, fileName);

        try {

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Подготавливаем HTML
            String htmlText = Jsoup.clean(rawHTML, Whitelist.relaxed());
            InputStream inputStream = new ByteArrayInputStream(htmlText.getBytes());

            // Печатаем документ PDF
            XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                    inputStream, null, Charset.defaultCharset() );

            document.close();
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    }
