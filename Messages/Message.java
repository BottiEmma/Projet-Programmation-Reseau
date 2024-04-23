package Messages;

import java.io.*;

public class Message {

    public static String formatMessage(String message) {
        return message + "\r\n";
    }

    public static void sendMessage(PrintWriter out, String message) {
        out.print(formatMessage(message));
        out.flush();
    }

    public static String formatNumber(int nb_mess, String zero) {
        return formatNumber(Integer.toString(nb_mess), zero);
    }

    public static String formatNumber(String nb_mess, String zero) {
        return (zero + nb_mess).substring(nb_mess.length());
    }

    public static String formatID(String id) {
        return id = (id + "########").substring(0, 8);
    }

    public static String formatIPaddress(String ip) {
        String[] address = ip.split("\\.");
        String template = "000";
        String output = "";

        for (int i = 0; i < 4; i++) {
            String current = (template + address[i]);
            int current_length = current.length();
            output += current.substring(current_length - 3, current_length) + ".";
        }

        return output.substring(0, 15);
    }

    public static String completeMessage(String message) {
        if (message.length() > 140) {
            System.out.println("I'm here");
            return message.substring(0, 140);
        }
        String temp = "";
        for (int i = 0; i < 140; i++) {
            temp += "#";
        }
        temp =(message + temp).substring(0, 140);
        return temp;
    }

}
