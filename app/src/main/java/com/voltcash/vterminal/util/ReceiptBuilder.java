package com.voltcash.vterminal.util;

import android.text.Layout;

import com.zcs.sdk.Printer;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextFont;
import com.zcs.sdk.print.PrnTextStyle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReceiptBuilder {
    private static PrnStrFormat HEADER_FORMAT = new PrnStrFormat();
    private static PrnStrFormat LINE_FORMAT = new PrnStrFormat();
    private static PrnStrFormat CENTERED_LINE_FORMAT = new PrnStrFormat();

    static{
        HEADER_FORMAT.setTextSize(25);
        HEADER_FORMAT.setAli(Layout.Alignment.ALIGN_CENTER);
        HEADER_FORMAT.setStyle(PrnTextStyle.BOLD);
        HEADER_FORMAT.setFont(PrnTextFont.DEFAULT);

        createLineFormat(LINE_FORMAT, Layout.Alignment.ALIGN_NORMAL);
        createLineFormat(CENTERED_LINE_FORMAT, Layout.Alignment.ALIGN_CENTER);
    }

    private static void createLineFormat(PrnStrFormat LINE_FORMAT, Layout.Alignment alignment){
        LINE_FORMAT.setTextSize(22);
        LINE_FORMAT.setAli(alignment);
        LINE_FORMAT.setStyle(PrnTextStyle.NORMAL);
        LINE_FORMAT.setFont(PrnTextFont.DEFAULT);
    }

    public static String build(List<String> lines, Printer PRINTER){
        String existentContent = "";

        String title = "";
        List<String> subReceiptLines = new ArrayList<>();
        int leftSideChars = 0;

        for(String line: lines){
            if(line.startsWith("<title>")){
                if(!title.isEmpty()){// if is NOT first
                    String newContent = buildSubReceipt(title, subReceiptLines, PRINTER, leftSideChars);
                    existentContent = appendContent(existentContent, newContent, PRINTER);
                    subReceiptLines = new ArrayList<>();
                    leftSideChars = 0;
                }
                title = line;
            }else{
                subReceiptLines.add(line);

                if(line.contains("->")){
                    int leftSide = line.split("->")[0].trim().length();

                    if(leftSide > leftSideChars){
                        leftSideChars = leftSide;
                    }
                }
            }
        }

        String newContent = buildSubReceipt(title, subReceiptLines, PRINTER, leftSideChars);
        existentContent = appendContent(existentContent, newContent, PRINTER);

        return existentContent;
    }

    private static String buildSubReceipt(String title, List<String> lines, Printer PRINTER, int leftSideChars){
        StringBuilder sb = new StringBuilder();
        buildHeader(sb, title, PRINTER);
        buildBody(sb, lines, PRINTER, leftSideChars);
        return sb.toString();
    }

    public static String appendContent(String existentContent, String newContent, Printer PRINTER){
        StringBuilder sb = new StringBuilder("<div>");
        if(!existentContent.isEmpty()){
            write(sb, PRINTER, existentContent);
        }

        write(sb, PRINTER, newContent);
        write(sb, PRINTER, "<br/><br/><br/><br/> </div>");

        print(PRINTER, " ", LINE_FORMAT);
        print(PRINTER, " ", LINE_FORMAT);
        print(PRINTER, " ", LINE_FORMAT);
        print(PRINTER, " ", LINE_FORMAT);

        return sb.toString();
    }

    public static void addTitle(List<String> receiptLines, String title){
        receiptLines.add("<title>" + title);
    }

    public static void addDateTimeLines(List<String> receiptLines){
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateTime = df.format(new Date());
        String[] dateAndTime = dateTime.split(" ");

        receiptLines.add("Date -> " + dateAndTime[0]);
        receiptLines.add("Time -> " + dateAndTime[1]);
    }

    public static List<String> dateTimeLines(){
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateTime = df.format(new Date());
        String[] dateAndTime = dateTime.split(" ");

        List<String> dateTimeLines = new ArrayList();
        dateTimeLines.add("Date -> " + dateAndTime[0]);
        dateTimeLines.add("Time -> " + dateAndTime[1]);
        return dateTimeLines;
    }

    private static void buildHeader(StringBuilder sb, String title, Printer PRINTER){
        if(title.startsWith("<title>")){
            title = title.substring(7);
        }

        write(sb, PRINTER, "<table  style=\"width: 100%;\">");

        headerLine( sb,"Service Provided by", PRINTER);
        headerLine( sb,"Voltcash, Inc.", PRINTER);
        headerLine( sb,"1-800-249-3042", PRINTER);
        headerLine( sb,"www.voltcash.com", PRINTER);

        write(sb, PRINTER, "</table>");
        write(sb, PRINTER, "<p style=\"width: 100%;text-align: center;font-size:17px\">" + title + "</p>");
        write(sb, PRINTER, "<br/>");

        print(PRINTER, title, HEADER_FORMAT);
        print(PRINTER, "", HEADER_FORMAT);
    }

    private static void buildBody(StringBuilder sb, List<String>  lines, Printer PRINTER, int leftSideChars){
        write(sb, PRINTER, "<table  style=\"width: 100%;\">");
        for (String line: lines){
            buildLine(sb, line, PRINTER, leftSideChars);
        }
        write(sb, PRINTER, "</table>");
    }

    private static void buildLine(StringBuilder sb, String line, Printer PRINTER, int leftSideChars){
        write(sb, PRINTER, "<tr style=\"width: 100%;margin-top:8px\">");
        if(line.contains("->")){
            String[] parts = line.split("->");
            String name = parts[0];
            String value= parts[1];
            write(sb, PRINTER, "<td  colspan=\"1\" >" + name + "</td><td  colspan=\"1\" style=\"float:right; text-align: right;text-align: right; float:right;\">" +  value  + "</td>");
            StringBuilder spacer = new StringBuilder("    ");
            int spacesToBeAdded = leftSideChars - name.length();

            if(spacesToBeAdded > 0){
                for(int i = 0; i < spacesToBeAdded * 3; i++){
                    spacer.append(" ");
                }
            }

            print(PRINTER, name +  spacer.toString() + value, LINE_FORMAT);
        }else{
            if(line.trim().equals("<br/>")){
                print(PRINTER, "", CENTERED_LINE_FORMAT);
            }else{
                print(PRINTER, line, CENTERED_LINE_FORMAT);
            }

            if(!line.startsWith("<")){
                line = "<p style=\"width: 100%;text-align: center;\">" + line + "</p>";
            }
            write(sb, PRINTER, "<td colspan=\"2\">" + line + "</td>");
        }
        write(sb, PRINTER, "</tr>");
    }

    private static String centeredLine(String line){
        return "<span style=\"width: 100%;text-align: center;\">" + line + "</span>";
    }

    private static void headerLine(StringBuilder sb, String line, Printer PRINTER){
        write(sb, PRINTER, "<tr style=\"width: 100%;text-align: center\"><td><span style=\"width: 100%;text-align: center\">" + line +"</span></td></tr>");
        print(PRINTER, line, HEADER_FORMAT);
    }

    public static String achDisclaimer(String customerName){
        return "I, " + customerName + " authorize Merchant to initiate ACH transfer entries and to debit account " +
                "identified herein for purchase of goods or services at Merchant's location." +
                "This authorization shall remain in effect unless and until Merchant has received written notification " +
                "from myself that this authorization has been terminated in such time and manner to allow merchant to act. " +
                "Undersigned represents and warrants to Merchant that the person executing this Release is the Account owner referenced " +
                "above and all information regarding the Account and Account Owner is true and correct.";
    }

    public static List<String> buildCardToBankReceiptLines(Map response, Double amt, Double fee, Double payout, boolean includeDisclaimer){
        String amount = StringUtil.formatCurrency(amt);
        String customerName = (String)response.get(Field.TX.CUSTUMER_NAME);

        List<String> receiptLines = new ArrayList();
        addTitle(receiptLines, "ACH Authorization Form");
        List<String> dateTimeLines = ReceiptBuilder.dateTimeLines();


        receiptLines.add("Merchant -> "+ response.get(Field.TX.MERCHANT_NAME));
        receiptLines.add("Funds Settlement Information");
        receiptLines.add("Bank Name -> "+ response.get(Field.TX.BANK_NAME));
        receiptLines.add("Customer -> "+ customerName);
        receiptLines.add("Address -> "+ response.get(Field.TX.CUSTUMER_ADDRESS));
        receiptLines.add("Routing# -> "+ response.get(Field.TX.ROUTING_BANK_NUMBER));
        receiptLines.add("Account# -> "+ response.get(Field.TX.ACCOUNT_NUMBER));
        receiptLines.add("<br/>");

        if(includeDisclaimer){
            receiptLines.add(ReceiptBuilder.achDisclaimer(customerName));
            receiptLines.add("<br/>");
            receiptLines.add("______________________________");
            receiptLines.add("Customer Signature");
        }

        receiptLines.add(dateTimeLines.get(0).replace(" ->", ":"));

        String card     = TxData.getString(Field.TX.CARD_NUMBER);
        String merchant = PreferenceUtil.read(Field.AUTH.MERCHANT_NAME);
        String requestId= StringUtil.formatRequestId(response);;

        if(card != null && card.length() > 4){
            card = card.substring(card.length() - 4, card.length());
        }

        addTitle(receiptLines, "ACH Transfer");

        receiptLines.addAll(dateTimeLines);

        receiptLines.add("Location Name -> "    + merchant);
        receiptLines.add("Card Number -> **** **** " + card);
        receiptLines.add("Amount to Transfer -> " + amount);

        if(fee != null){
            receiptLines.add("Fee Amount-> " +  StringUtil.formatCurrency(fee));
        }

        if(payout != null){
            receiptLines.add("Payout Amount -> " +  StringUtil.formatCurrency(payout));
        }

        receiptLines.add("Account to Transfer -> " + response.get(Field.TX.ACCOUNT_NUMBER));
        receiptLines.add("Transaction # -> " + requestId);

        return receiptLines;
    }

    private static void print(Printer PRINTER, String text, PrnStrFormat format){
        if(PRINTER != null){
            PRINTER.setPrintAppendString(text, format);
        }
    }

    private static void write(StringBuilder sb, Printer PRINTER, String text){
        if(PRINTER == null){
            sb.append(text);
        }
    }
}

