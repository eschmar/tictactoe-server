package actors;

import akka.actor.*;
import com.google.gson.Gson;
import models.Message;
import play.libs.akka.InjectedActorSupport;
import services.PlayerLobby;
import java.util.LinkedList;

public class MatchActor extends AbstractActor implements InjectedActorSupport {
    private Gson gson;
    private PlayerLobby lobby;
    private LinkedList<String> queuedMessages;
    private final ActorRef out;
    private ActorRef opponent;

    public MatchActor(ActorRef out, PlayerLobby lobby) {
        this.out = out;
        this.lobby = lobby;

        gson = new Gson();
        queuedMessages = new LinkedList<>();

        System.out.println(" > DEBUGd: out path = " + out.path());
        System.out.println(" > DEBUGd: self path = " + self().path());

        if (lobby.hasWaitingPlayers()) {
            opponent = lobby.getOpponent();
            opponent.tell(out, self());

            while (!queuedMessages.isEmpty()) {
                opponent.tell(queuedMessages.poll(), self());
            }
        }else {
            lobby.joinLobby(out);
        }

//        Message temp = new Message(Message.TYPE_START, "PlayFramwork");
//        Gson gson = new Gson();
//        this.out.tell(gson.toJson(temp), self());
    }

    public static Props props(ActorRef out, PlayerLobby lobby) {
        return Props.create(MatchActor.class, out, lobby);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, message -> {
                Message msg = gson.fromJson(message, Message.class);

                if (opponent == null) {
                    queuedMessages.add(message);
                    return;
                }

                opponent.tell(message, self());
            })
            .match(ActorRef.class, ref -> {
                opponent = ref;
            })
            .build();
    }
}
