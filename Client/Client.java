package Client;

import java.io.*;
import java.net.*;
import Messages.Message;

public class Client {

    String client_id;
    String multidiffusion_address;
    int multidiffusion_port;
    String diffuseur_address;
    int tcp_port;
    int gestionnaire_port;
    String gestionnaire_address;
    boolean display;

    public Client(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            this.client_id = Message.formatID(reader.readLine());
            if (client_id.length() > 8) {
                this.client_id = client_id.substring(0, 8);
            }
            this.multidiffusion_address = Message.formatIPaddress(reader.readLine());
            this.multidiffusion_port = Integer.parseInt(reader.readLine());
            if (multidiffusion_port > 9999) {
                System.out.println("MULTIDIFFUSION PORT has to be < 9999");

            }
            this.diffuseur_address = Message.formatIPaddress(reader.readLine());
            this.tcp_port = Integer.parseInt(reader.readLine());
            if (tcp_port > 9999) {
                System.out.println("TCP_PORT has to be < 9999");
            }
            this.gestionnaire_address = Message.formatIPaddress(reader.readLine());

            this.gestionnaire_port = Integer.parseInt(reader.readLine());
            this.display = true;
            reader.close();
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();

        }
    }

    public void start_client() {
        MessageReceiver receiver = new MessageReceiver(this);
        MessageSender sender = new MessageSender(this);
        System.out.println(multidiffusion_address);

        Thread t_receiver = new Thread(receiver);
        Thread t_sender = new Thread(sender);

        t_receiver.start();
        t_sender.start();
    }

    public void display_diffuseur_information() {
        System.out.println("---------------------------------------");
        System.out.println("[CLIENT ID] : " + this.client_id);
        System.out.println("[MULTIDIFUSION ADDRESS] : " + this.multidiffusion_address);
        System.out.println("[MULTIDIFUSION PORT] : " + this.multidiffusion_port);
        System.out.println("[TCP PORT] : " + this.tcp_port);
        System.out.println("[GES ADDRESS] : " + this.gestionnaire_address);
        System.out.println("[GES PORT] : " + this.gestionnaire_port);
        System.out.println("---------------------------------------");
        System.out.println();
    }

    public static void main(String[] args) {
        Client client = new Client(args[0]);
        client.start_client();
    }
}