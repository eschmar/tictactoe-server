package models;

public class Message {
    public static final String TYPE_MOVE = "move";
    public static final String TYPE_START = "start";
    public static final String TYPE_QUIT = "quit";
    public static final String TYPE_ACTOR_PATH = "path";

    private String type;
    private String payload;

    public Message(String type) {
        if (!type.equals(TYPE_MOVE) && !type.equals(TYPE_START) && !type.equals(TYPE_QUIT) && !type.equals(TYPE_ACTOR_PATH)) {
            throw new IllegalArgumentException("Illegal message detected.");
        }

        this.type = type;
    }

    public Message(String type, String payload) {
        this(type);
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }
}
