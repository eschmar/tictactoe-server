package controllers;

import com.google.inject.Inject;
import play.mvc.*;
import services.PlayerLobby;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    private final PlayerLobby lobby;

    @Inject
    public HomeController(PlayerLobby lobby) {
        this.lobby = lobby;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render(lobby));
    }
}
