package Diffuseur;

import java.util.LinkedList;
import java.net.*;
import java.util.Random;

import Messages.*;

public class DiffuseurUDP implements Runnable {

    Diffuseur diffuseur;

    public DiffuseurUDP(Diffuseur diffuseur) {
        this.diffuseur = diffuseur;
    }

    @Override
    public void run() {
        try {
            Random rand = new Random();
            DatagramSocket dso = new DatagramSocket();
            
            while (true) {
                Thread.sleep(2000);
                String diffuseur_message = "";
                diffuseur_message = MessageType.DIFF.getValue() + " ";
                diffuseur_message += Message.formatNumber(diffuseur.diffuseur_messages.getNum_mess(), "0000") + " ";
                
                String mess = diffuseur.diffuseur_messages.getMessage();
                String client_id = mess.substring(0,9);
                String formattedMessage = Message.completeMessage(mess.substring(9));
                diffuseur_message += client_id;
                diffuseur_message += formattedMessage;
                diffuseur.messages_sent.add(diffuseur_message.substring(5));
                diffuseur_message += "\r\n";

                byte[] data = diffuseur_message.getBytes();


                /* Add message the message sent without the keyword "DIFF" */
                

                InetSocketAddress ia = new InetSocketAddress(diffuseur.multidiffusion_address, diffuseur.multidiffusion_port);
                DatagramPacket paquet = new DatagramPacket(data, data.length, ia);
                dso.send(paquet);
              
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}