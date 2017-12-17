package controllers;

import actors.MatchActor;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import com.google.inject.Inject;
import play.libs.streams.ActorFlow;
import play.mvc.*;

public class MatchmakingController extends Controller {
    private final ActorSystem actorSystem;
    private final Materializer materializer;
    private MatchActor temp;

    @Inject
    public MatchmakingController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    public WebSocket connect() {
        return WebSocket.Text.accept(request -> {
            return ActorFlow.actorRef(MatchActor::props, actorSystem, materializer);
        });
    }
}
