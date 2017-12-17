package actors;

import akka.actor.*;
import com.google.gson.Gson;
import com.google.inject.Inject;
import services.PlayerLobby;

import java.util.LinkedList;

public class MatchActor extends AbstractActor {
    private Gson gson;
    private LinkedList<String> queuedMessages;
    private final ActorRef out;
    @Inject PlayerLobby lobby;

    private ActorRef opponent;

    public MatchActor(ActorRef out) {
        this.out = out;
        gson = new Gson();
        queuedMessages = new LinkedList<>();

        if (lobby.hasWaitingPlayers()) {
            opponent = lobby.getOpponent();
            opponent.tell(this.getSelf(), out);

            while (!queuedMessages.isEmpty()) {
                opponent.tell(queuedMessages.poll(), out);
            }
        }else {
            lobby.joinLobby(this.getSelf());
        }

//        Message temp = new Message(Message.TYPE_START, "PlayFramwork");
//        Gson gson = new Gson();
//        this.out.tell(gson.toJson(temp), self());
    }

    public static Props props(ActorRef out) {
        return Props.create(MatchActor.class, out);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, message -> {
                if (opponent == null) {
                    queuedMessages.add(message);
                    return;
                }

                opponent.tell(message, out);
//                out.tell("I received your message: " + message, self());
            })
            .match(ActorRef.class, ref -> {
                opponent = ref;
            })
            .build();
    }
}
