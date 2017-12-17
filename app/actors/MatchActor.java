package actors;

import akka.actor.*;
import com.google.gson.Gson;
import models.Message;

public class MatchActor extends AbstractActor {
    private final ActorRef out;

    public MatchActor(ActorRef out) {
        this.out = out;

        Message temp = new Message(Message.TYPE_START, "PlayFramwork");
        Gson gson = new Gson();
        this.out.tell(gson.toJson(temp), self());
    }

    public static Props props(ActorRef out) {
        return Props.create(MatchActor.class, out);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, message -> {
//                out.tell("I received your message: " + message, self());
            }).build();
    }
}
