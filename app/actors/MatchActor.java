package actors;

import akka.actor.*;
import com.google.gson.Gson;
import models.Message;
import play.libs.akka.InjectedActorSupport;
import scala.concurrent.duration.FiniteDuration;
import services.PlayerLobby;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class MatchActor extends AbstractActor implements InjectedActorSupport {
    private Gson gson;
    private PlayerLobby lobby;

    private final ActorRef out;
    private ActorRef opponent;
    private LinkedList<String> queuedMessages;

    public MatchActor(ActorRef out, PlayerLobby lobby) {
        this.out = out;
        this.lobby = lobby;

        gson = new Gson();
        queuedMessages = new LinkedList<>();

        if (lobby.hasWaitingPlayers()) {
            opponent = lobby.getOpponent();

            // inform opponent about myself
            Message msg = new Message(Message.TYPE_ACTOR_PATH, out.path().toString());
            opponent.tell(gson.toJson(msg), self());

            sendQueued();
        }else {
            lobby.joinLobby(out);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, message -> {
                Message msg = gson.fromJson(message, Message.class);

                if (msg.getType().equals(Message.TYPE_ACTOR_PATH)) {
                    parseOpponent(msg.getPayload());
                    return;
                } else if (!msg.isValidGameType()) {
                    abortGame();
                    return;
                } else if (opponent == null) {
                    queuedMessages.add(message);
                    return;
                }

                opponent.tell(message, self());
            })
            .build();
    }

    @Override
    public void unhandled(Object message) {
        super.unhandled(message);
        abortGame();
    }

    /**
     * Inform both players about illegal messages.
     */
    protected void abortGame() {
        Message msg = new Message(Message.TYPE_SERVER_ABORT);
        opponent.tell(gson.toJson(msg), self());
        out.tell(gson.toJson(msg), self());
    }

    /**
     * Make sure opponent's websocket gets closed as well.
     */
    public void postStop() {
        if (opponent == null) return;
        opponent.tell(PoisonPill.getInstance(), self());
    }

    /**
     * Resolve string path to actor flow.
     * @param path
     */
    private void parseOpponent(String path) {
        ActorSelection selection = getContext().actorSelection(path);
        selection.resolveOneCS(new FiniteDuration(2, TimeUnit.SECONDS)).thenAccept(ref -> {
            opponent = ref;
            sendQueued();
        });
    }

    /**
     * Send messages that were queued, when there was no opponent available.
     */
    private void sendQueued() {
        while (!queuedMessages.isEmpty() && this.opponent != null) {
            String message = queuedMessages.poll();
            opponent.tell(message, self());
        }
    }
}
