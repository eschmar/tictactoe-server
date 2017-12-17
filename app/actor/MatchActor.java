package actor;

import akka.actor.*;

public class MatchActor extends AbstractActor {
    public static Props props(ActorRef out) {
        return Props.create(MatchActor.class, out);
    }

    private final ActorRef out;

    public MatchActor(ActorRef out) {
        this.out = out;
        this.out.tell("Andele", self());
        System.out.println("PEW PEW PEW");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, message ->
                        out.tell("I received your message: " + message, self())
                )
                .build();
    }
}
