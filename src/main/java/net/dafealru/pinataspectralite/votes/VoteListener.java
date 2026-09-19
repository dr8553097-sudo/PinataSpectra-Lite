package net.dafealru.pinataspectralite.votes;

import net.dafealru.pinataspectralite.PinataPartyLite;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

public class VoteListener implements Listener {

    private final PinataPartyLite plugin;

    public VoteListener(PinataPartyLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGenericEvent(Event event) {
        String eventName = event.getEventName();
        if (eventName.equals("VotifierEvent") || eventName.equals("VoteReceivedEvent")) {
            try {
                Method getVote = event.getClass().getMethod("getVote");
                Object vote = getVote.invoke(event);
                if (vote != null) {
                    Method getUsername = vote.getClass().getMethod("getUsername");
                    Object usernameObj = getUsername.invoke(vote);
                    if (usernameObj instanceof String username && !username.isEmpty()) {
                        plugin.getVoteGoalManager().registerVote(username);
                    }
                }
            } catch (Exception ignored) {}
        }
    }
}
