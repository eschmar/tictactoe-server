package models;

public class Message {
    public static final String TYPE_MOVE = "move";
    public static final String TYPE_START = "start";
    public static final String TYPE_QUIT = "quit";
    public static final String TYPE_ACTOR_PATH = "path";
    public static final String TYPE_SERVER_ABORT = "abort";
    public static final String TYPE_WITHDRAW = "withdraw";

    private String type;
    private String payload;

    public Message(String type) throws IllegalArgumentException {
        this.type = type;

        if (!isValidType()) {
            throw new IllegalArgumentException("Illegal message detected.");
        }
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

    public boolean isValidType() {
        return (
            type.equals(TYPE_MOVE) ||
            type.equals(TYPE_START) ||
            type.equals(TYPE_QUIT) ||
            type.equals(TYPE_WITHDRAW) ||
            type.equals(TYPE_ACTOR_PATH) ||
            type.equals(TYPE_SERVER_ABORT)
        );
    }

    public boolean isValidGameType() {
        return (
            type.equals(TYPE_MOVE) ||
            type.equals(TYPE_START) ||
            type.equals(TYPE_QUIT)
        );
    }
}
