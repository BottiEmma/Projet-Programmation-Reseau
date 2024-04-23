package Client;

import java.io.*;
import java.net.*;
import Messages.MessageType;
import Messages.Message;

public class MessageSender implements Runnable {

    Client client;

    public MessageSender(Client client) {
        this.client = client;
    }

    /* need function to check validity of a message */
    @Override
    public void run() {
        BufferedReader input_reader = new BufferedReader(new InputStreamReader(System.in));
        boolean validMessage = false;
        MessageType last_message_type = MessageType.NONE;
        while (true) {
            try {
                Socket socket = new Socket(client.diffuseur_address, client.tcp_port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                String message = "";
                String user_input = "";
                if (last_message_type == MessageType.NONE) {
                    user_input = input_reader.readLine();

                    if (user_input.equals("DISPLAY")) {
                        client.display = !client.display;
                        out.close();
                        socket.close();
                        continue;
                    }

                    if (user_input.startsWith(MessageType.MESS.getValue()) && user_input.length() > 5) {
                        message = user_input.substring(5, user_input.length());
                        Message.sendMessage(out, MessageType.MESS.getValue() + " " + client.client_id + " " + message);
                        validMessage = true;
                        last_message_type = MessageType.MESS;
                    } else if (user_input.startsWith(MessageType.LAST.getValue()) && user_input.length() > 5) {
                        try {
                            /* Quel comportement si nb > au nombre de message dans le diffuseur ? */
                            message = user_input.substring(5, user_input.length());
                            int nb = Integer.parseInt(message);
                            if (nb < 0 || nb > 999) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException nb) {
                            System.out.println("Wrong format : LAST␣nb-mess where nb-mess >=0 and nb-mess <= 999");
                            out.close();
                            socket.close();
                            continue;
                        }
                        Message.sendMessage(out,
                                MessageType.LAST.getValue() + " " + Message.formatNumber(message, "000"));
                        validMessage = true;
                        last_message_type = MessageType.LAST;
                    } else if (user_input.equals(MessageType.LIST.getValue())) {
                        Socket socket_l = new Socket(client.gestionnaire_address, client.gestionnaire_port);
                        BufferedReader in_l = new BufferedReader(new InputStreamReader(socket_l.getInputStream()));
                        PrintWriter out_l = new PrintWriter(new OutputStreamWriter(socket_l.getOutputStream()));

                        Message.sendMessage(out_l, "LIST");
                        String message_from_server = in_l.readLine();
                        System.out.println(message_from_server);
                        int num_diff = Integer.parseInt(message_from_server.substring(5, message_from_server.length()));
                        for (int i = 0; i < num_diff; i++) {
                            System.out.println(in_l.readLine());
                        }
                        validMessage = true;
                        last_message_type = MessageType.LIST;
                    } else if (user_input.startsWith(MessageType.MGES.getValue()) && user_input.length() > 5) {
                        Socket socket_l = new Socket(client.gestionnaire_address, client.gestionnaire_port);
                        BufferedReader in_l = new BufferedReader(new InputStreamReader(socket_l.getInputStream()));
                        PrintWriter out_l = new PrintWriter(new OutputStreamWriter(socket_l.getOutputStream()));
                        message = user_input.substring(5, user_input.length());
                        Message.sendMessage(out_l,
                                MessageType.MGES.getValue() + " " + client.client_id + " " + message);

                        validMessage = true;
                        last_message_type = MessageType.MGES;
                    } else if (user_input.startsWith(MessageType.MDIF.getValue()) && user_input.length() > 5) {
                        try {
                            message = user_input.substring(5, user_input.length());
                            int nb_d = Integer.parseInt(message);
                            if (nb_d < 0 || nb_d > 999) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException nb_d) {
                            System.out.println("Wrong format : MDIF␣num-mess where num-mess >=0 and num-mess <= 999");
                            out.close();
                            socket.close();
                            continue;
                        }
                        Message.sendMessage(out,
                                MessageType.MDIF.getValue() + " " + Message.formatNumber(message, "000"));
                        validMessage = true;
                        last_message_type = MessageType.MDIF;
                    } else {
                        System.out.println("Unknown command");
                        validMessage = false;
                    }
                }

                if (last_message_type == MessageType.LAST) {

                    String message_from_server = in.readLine();
                    System.out.println("\n================");
                    while (!message_from_server.equals(MessageType.ENDM.getValue())) {
                        System.out.println(message_from_server);
                        message_from_server = in.readLine();
                    }
                    System.out.println("================\n");

                    last_message_type = MessageType.NONE;
                } else if (last_message_type == MessageType.MESS) {
                    String message_from_server = in.readLine();
                    if (message_from_server.equals(MessageType.ACKM.getValue())) {
                        System.out.println(message_from_server);
                        last_message_type = MessageType.NONE;
                    }
                } else if (last_message_type == MessageType.MDIF) {
                    String message_from_server = in.readLine();
                    System.out.println("\n================");
                    if (message_from_server.startsWith(MessageType.MDOK.getValue())) {
                        System.out.println(message_from_server);
                        System.out.println("================\n");
                        last_message_type = MessageType.NONE;
                    } else if (message_from_server.equals(MessageType.MERR.getValue())) {
                        System.out.println(message_from_server);
                        System.out.println("================\n");
                        last_message_type = MessageType.NONE;
                    }
                } else if (last_message_type == MessageType.LIST) {
                    System.out.println("================\n");
                    last_message_type = MessageType.NONE;
                }
                
                else if (last_message_type == MessageType.MGES) {
                    System.out.println("================\n");
                    last_message_type = MessageType.NONE;
                } else {
                    if (validMessage) {
                        String message_from_server = in.readLine();
                        System.out.println(message_from_server);

                    }
                    last_message_type = MessageType.NONE;

                }

                out.close();
                socket.close();
            } catch (Exception exception) {
                System.out.println("Not connected to diffuseur yet...");
            }
        }

    }
}
