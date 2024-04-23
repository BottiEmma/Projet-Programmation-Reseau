package Messages;

import java.io.*;
import java.util.ArrayList;

public class MessageDiffuseur {
    ArrayList<String> diffuseur_messages;
    int num_mess = -1;

    public MessageDiffuseur(String id) {
        diffuseur_messages = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./Messages/diffuseur.txt"));
            String line = reader.readLine();
            while (line != null) {
                diffuseur_messages.add(id + " " + line);
                line = reader.readLine();
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSize() {
        return diffuseur_messages.size();
    }

    public int getNum_mess() {
        if (num_mess == 10000) {
            num_mess = 0;
        }
        return num_mess;
    }

    public synchronized String getMessage() {
        num_mess += 1;
        return diffuseur_messages.get(num_mess % getSize());
    }

    public synchronized String getMessage(int index) {
        return diffuseur_messages.get(index);
    }

    public void addMessage(String message) {
        diffuseur_messages.add(message);
    }

    public void print_array() {
        for (String message : diffuseur_messages) {
            System.out.println("Le message : " + message);
        }
    }

}
