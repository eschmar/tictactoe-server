package controllers;

import actors.MatchActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import com.google.inject.Inject;
import play.libs.streams.ActorFlow;
import play.mvc.*;
import services.PlayerLobby;

public class MatchmakingController extends Controller {
    private final ActorSystem actorSystem;
    private final Materializer materializer;
    private final PlayerLobby lobby;
    private MatchActor temp;

    @Inject
    public MatchmakingController(ActorSystem actorSystem, Materializer materializer, PlayerLobby lobby) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
        this.lobby = lobby;
    }

    public WebSocket connect() {
        return WebSocket.Text.accept(request -> {
            return ActorFlow.actorRef(out -> Props.create(MatchActor.class, out, lobby), actorSystem, materializer);
        });
    }
}
