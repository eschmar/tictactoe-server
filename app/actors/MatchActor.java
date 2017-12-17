package actors;

import akka.actor.*;
import akka.util.Timeout;
import com.google.gson.Gson;
import models.Message;
import play.libs.akka.InjectedActorSupport;
import scala.concurrent.Await;
import scala.concurrent.duration.FiniteDuration;
import services.PlayerLobby;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class MatchActor extends AbstractActor implements InjectedActorSupport {
    private Gson gson;
    private PlayerLobby lobby;
    private LinkedList<String> queuedMessages;
    private final Timeout timeout = new Timeout(2, TimeUnit.SECONDS);
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

            // send path to opponent
            Message msg = new Message(Message.TYPE_ACTOR_PATH, self().path().toString());
            opponent.tell(gson.toJson(msg), self());
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

                if (msg.getType().equals(Message.TYPE_ACTOR_PATH)) {
                    parseOpponent(msg.getPayload());
                    return;
                }

                if (opponent == null) {
                    queuedMessages.add(message);
                    return;
                }

                opponent.tell(message, self());
            })
            .match(ActorRef.class, ref -> {
                System.out.println(" ----> " + self().path() + " vs " + ref.path());
                opponent = ref;
            })
            .build();
    }

    private void parseOpponent(String path) {
        ActorSelection selection = getContext().actorSelection(path);

        selection.resolveOneCS(new FiniteDuration(2, TimeUnit.SECONDS)).thenAccept(ref -> {
            opponent = ref;
            while (!queuedMessages.isEmpty()) {
                String message = queuedMessages.poll();
                opponent.tell(message, self());
            }
        });
    }
}
