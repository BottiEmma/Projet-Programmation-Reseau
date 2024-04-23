package Client;

import java.net.*;

public class MessageReceiver implements Runnable {

    Client client;

    public MessageReceiver(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            client.display_diffuseur_information();
            MulticastSocket socket = new MulticastSocket(client.multidiffusion_port);
            socket.joinGroup(InetAddress.getByName(client.multidiffusion_address));
            byte[] data = new byte[100];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            while (true) {
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());

                if(client.display){
                    System.out.println(message); 
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}