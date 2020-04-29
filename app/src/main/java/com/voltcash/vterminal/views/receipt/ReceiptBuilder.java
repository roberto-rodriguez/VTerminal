package com.voltcash.vterminal.views.receipt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by roberto.rodriguez on 4/25/2020.
 */

public class ReceiptBuilder {

    public static String build(String title, List<String> lines){
        StringBuilder sb = new StringBuilder();

        buildHeader(sb, title);
        buildBody(sb, lines);

        return div(sb.toString());
    }

    public static String div(String content){
        return "<div>" + content + "<br/><br/><br/><br/><br/><p style=\"width:100%;text-align: center;color:white\">____________________________________</p></div>";
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
        sb.append("<table  style=\"width: 100%;\">");

        headerLine( sb,"Service Provided by");
        headerLine( sb,"Voltcash, Inc.");
        headerLine( sb,"1-800-249-3042");
        headerLine( sb,"www.voltcash.com");

//        sb.append(centeredLine("Service Provided by"));
//        sb.append(centeredLine("Service Provided by"));
//        sb.append(centeredLine("Voltcash, Inc."));
//        sb.append(centeredLine("1-800-249-3042"));
//        sb.append(centeredLine("www.voltcash.com"));


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
                "identified herein for purchase of good or services at Merchant location." +
                "This authorization shall remain in effect unless and until Merchant has received written notification " +
                "from myself that this authorization has been terminated in such time and manner to allow merchant to act." +
                "Undersigned represents and warrants to Merchant that the person executing this Release is the Account owner referenced" +
                "above and all information regarding the Account and Account Owner is true and correct.</span>";
    }
}
