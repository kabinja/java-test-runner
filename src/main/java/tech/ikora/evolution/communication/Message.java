package tech.ikora.evolution.communication;

public class Message {
    private final char type;
    private final String payLoad;

    public Message(char type, String payLoad){
        this.type = type;
        this.payLoad = payLoad;
    }

    public char getType() {
        return type;
    }

    public String getPayLoad() {
        return payLoad;
    }
}
