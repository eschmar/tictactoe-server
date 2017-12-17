package controllers;

import actors.MatchActor;
import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
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
//            Props.create(MatchActor.class, lobby)
//            ActorRef imageActorRef = system.actorOf(Props.create(ResizePhotoActor.class, 1, 2, ""))
            return ActorFlow.actorRef(out -> Props.create(MatchActor.class, out, lobby), actorSystem, materializer);
//            return ActorFlow.actorRef(MatchActor::props, actorSystem, materializer);
        });
    }
}
