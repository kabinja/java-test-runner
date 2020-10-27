package tech.ikora.evolution.communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Sender {
    private final String host;
    private final int port;
    private final List<Message> messages;

    public Sender(String host, int port){
        this.host = host;
        this.port = port;
        this.messages = new ArrayList<>();
    }

    public Sender addMessage(Message message){
        this.messages.add(message);
        return this;
    }

    final public void send() throws IOException {
        try (Socket socket = new Socket(host, port)) {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            for(Message message : messages){
                sendMessage(out, message);
            }

            out.flush();
        }
    }

    private void sendMessage(DataOutputStream out, Message message) throws IOException {
        final byte[] bytes = message.getPayLoad().getBytes(StandardCharsets.UTF_8);

        out.writeChar(message.getType());

        if(bytes.length > 0){
            out.writeInt(bytes.length);
            out.write(bytes);
        }
    }
}
