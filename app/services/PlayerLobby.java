package services;

import akka.actor.ActorRef;

import javax.inject.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public class PlayerLobby {
    private volatile ConcurrentLinkedQueue<ActorRef> waitingPlayers;

    public PlayerLobby() {
        waitingPlayers = new ConcurrentLinkedQueue<ActorRef>();
    }

    public void joinLobby(ActorRef ref) {
        this.waitingPlayers.add(ref);
    }

    public boolean hasWaitingPlayers() {
        return this.waitingPlayers.size() > 0;
    }

    public ActorRef getOpponent() {
        return this.waitingPlayers.poll();
    }

    public int getWaitingPlayersCount() {
        return this.waitingPlayers.size();
    }

    public void withdrawFromLobby(ActorRef out) {
        this.waitingPlayers.remove(out);
    }
}
