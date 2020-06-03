package com.voltcash.vterminal.views.receipt;

import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.StringUtil;
import com.voltcash.vterminal.util.TxData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by roberto.rodriguez on 4/25/2020.
 */

public class PrintBuilder {

    public static String build(List<String> lines){
        StringBuilder sb = new StringBuilder();

        buildHeader(sb, lines.get(0));
        buildBody(sb, lines.subList(1, lines.size()));

        return div(sb.toString());
    }

  //  @deprecated
    public static String build(String title, List<String> lines){
        StringBuilder sb = new StringBuilder();

        buildHeader(sb, title);
        buildBody(sb, lines);

        return div(sb.toString());
    }

    public static String div(String content){
        return "<div>" + content + "<br/><br/><br/><br/><p style=\"width:100%;text-align: center;color:white\">____________________________________</p></div>";
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

    private static void buildHeader(StringBuilder sb, String title){
        if(title.startsWith("<title>")){
            title = title.substring(7);
        }

        sb.append("<table  style=\"width: 100%;\">");

        headerLine( sb,"Service Provided by");
        headerLine( sb,"Voltcash, Inc.");
        headerLine( sb,"1-800-249-3042");
        headerLine( sb,"www.voltcash.com");

        sb.append("</table>");

        sb.append("<p style=\"width: 100%;text-align: center;font-size:17px\">" + title + "</p>");

        sb.append("<br/>");

    }

    private static void buildBody(StringBuilder sb, List<String>  lines){
        sb.append("<table  style=\"width: 100%;\">");
        for (String line: lines){
            buildLine(sb, line);
        }
        sb.append("</table>");
    }

    private static void buildLine(StringBuilder body, String line){
        body.append("<tr style=\"width: 100%;margin-top:8px\">");
        if(line.contains("->")){
            String[] parts = line.split("->");
            String name = parts[0];
            String value= parts[1];
            body.append(  "<td  colspan=\"1\" >" + name + "</td><td  colspan=\"1\" style=\"float:right; text-align: right;text-align: right; float:right;\">" +  value  + "</td>");
        }else{
            if(!line.startsWith("<")){
                line = "<p style=\"width: 100%;text-align: center;\">" + line + "</p>";
            }
            body.append(  "<td colspan=\"2\">" + line + "</td>");
        }
        body.append("</tr>");
    }

    private static String centeredLine(String line){
        return "<span style=\"width: 100%;text-align: center;\">" + line + "</span>";
    }

    private static void headerLine(StringBuilder sb, String line){
        sb.append("<tr style=\"width: 100%;text-align: center\"><td><span style=\"width: 100%;text-align: center\">" + line +"</span></td></tr>");
    }

    public static String achDisclaimer(String customerName){
        return "<br/><span style=\"width: 100%;text-align: justify;\">I, " + customerName + " authorize Merchant to initiate ACH transfer entries and to debit account " +
                "identified herein for purchase of goods or services at Merchant's location." +
                "This authorization shall remain in effect unless and until Merchant has received written notification " +
                "from myself that this authorization has been terminated in such time and manner to allow merchant to act. " +
                "Undersigned represents and warrants to Merchant that the person executing this Release is the Account owner referenced " +
                "above and all information regarding the Account and Account Owner is true and correct.</span>";
    }

    public static String buildCardToBankReceipt(Map response, Double amt, Double fee, Double payout){
        String amount = StringUtil.formatCurrency(amt);
        String customerName = (String)response.get(Field.TX.CUSTUMER_NAME);

        List<String> dateTimeLines = PrintBuilder.dateTimeLines();

        List<String> receiptLines = new ArrayList();
        receiptLines.add("Merchant -> "+ response.get(Field.TX.MERCHANT_NAME));
        receiptLines.add("Funds Settlement Information");
        receiptLines.add("Bank Name -> "+ response.get(Field.TX.BANK_NAME));
        receiptLines.add("Customer -> "+ customerName);
        receiptLines.add("Address -> "+ response.get(Field.TX.CUSTUMER_ADDRESS));
        receiptLines.add("Routing# -> "+ response.get(Field.TX.ROUTING_BANK_NUMBER));
        receiptLines.add("Account# -> "+ response.get(Field.TX.ACCOUNT_NUMBER));

        receiptLines.add(PrintBuilder.achDisclaimer(customerName));
        receiptLines.add("<br/>");
        receiptLines.add("______________________________");
        receiptLines.add("Customer Signature");
        receiptLines.add(dateTimeLines.get(0).replace(" ->", ":"));

        String achReceiptContent = PrintBuilder.build("ACH Authorization Form", receiptLines);

        String card     = TxData.getString(Field.TX.CARD_NUMBER);
        String merchant = PreferenceUtil.read(Field.AUTH.MERCHANT_NAME);
        String requestId= StringUtil.formatRequestId(response);;

        if(card != null && card.length() > 4){
            card = card.substring(card.length() - 4, card.length());
        }

        receiptLines = dateTimeLines;
        receiptLines.add("Location Name -> "    + merchant);
        receiptLines.add("Card Number -> **** " + card);
        receiptLines.add("Amount to Transfer -> " + amount);

        if(fee != null){
            receiptLines.add("Fee Amount-> " +  StringUtil.formatCurrency(fee));
        }

        if(payout != null){
            receiptLines.add("Payout Amount -> " +  StringUtil.formatCurrency(payout));
        }

        receiptLines.add("Account to Transfer -> " + response.get(Field.TX.ACCOUNT_NUMBER));
        receiptLines.add("Transaction # -> " + requestId);

        String txReceiptContent = PrintBuilder.build("ACH Transfer", receiptLines);

        return PrintBuilder.div(achReceiptContent + "<br/>" + txReceiptContent);
    }
}
