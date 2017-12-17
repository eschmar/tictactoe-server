package actor;

import akka.actor.*;

public class MatchActor extends AbstractActor {
    private final ActorRef out;

    public MatchActor(ActorRef out) {
        this.out = out;
        this.out.tell("Hello my friend!", self());
    }

    public static Props props(ActorRef out) {
        return Props.create(MatchActor.class, out);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, message -> {
                out.tell("I received your message: " + message, self());
            }).build();
    }
}
