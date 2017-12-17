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
            System.out.println(" > DEBUGd: opponent path = " + opponent.path());

            // send path to opponent
            Message msg = new Message(Message.TYPE_ACTOR_PATH, self().path().toString());
            opponent.tell(gson.toJson(msg), self());

            Message msg2 = new Message(Message.TYPE_ACTOR_PATH, out.path().toString());
            opponent.tell(gson.toJson(msg2), self());
        }else {
            lobby.joinLobby(out);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, message -> {
                Message msg = gson.fromJson(message, Message.class);

                if (msg.touched) {
                    out.tell(message, self());
                    return;
                }

                if (msg.getType().equals(Message.TYPE_ACTOR_PATH)) {
                    parseOpponent(msg.getPayload());
                    return;
                }

                msg.touched = true;

                if (opponent == null) {
                    queuedMessages.add(gson.toJson(msg));
                    return;
                }

                opponent.tell(gson.toJson(msg), self());
            })
//            .match(ActorRef.class, ref -> {
//                System.out.println(" ----> " + self().path() + " vs " + ref.path());
//                opponent = ref;
//            })
            .build();
    }

    private void parseOpponent(String path) {
        ActorSelection selection = getContext().actorSelection(path);
        System.out.println(" -----> Trying to resolve");

        selection.resolveOneCS(new FiniteDuration(2, TimeUnit.SECONDS)).thenAccept(ref -> {
            opponent = ref;
            System.out.println(" > DEBUGd: resolvdOP = " + opponent.path());
            while (!queuedMessages.isEmpty()) {
                String message = queuedMessages.poll();
                opponent.tell(message, self());
            }
        });
    }

    public static Props props(ActorRef out, PlayerLobby lobby) {
        return Props.create(MatchActor.class, out, lobby);
    }
}
