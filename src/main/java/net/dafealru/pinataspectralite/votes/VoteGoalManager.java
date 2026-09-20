package net.dafealru.pinataspectralite.votes;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.pinata.PinataProfile;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class VoteGoalManager {

    private final PinataPartyLite plugin;
    private int currentVotes = 0;
    private int targetVotes = 25;
    private boolean enabled = true;

    public VoteGoalManager(PinataPartyLite plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.enabled = plugin.getConfig().getBoolean("vote-goal.enabled", true);
        this.targetVotes = plugin.getConfig().getInt("vote-goal.target-votes", 25);
    }

    public void registerVote(String voterName) {
        if (!enabled) return;
        currentVotes++;

        int interval = plugin.getConfig().getInt("vote-goal.broadcast-interval", 5);
        if (currentVotes % interval == 0 || currentVotes >= targetVotes) {
            Bukkit.broadcast(ColorUtils.colorizeComponent("&#EC4899🗳️ &#FCD34D" + voterName + " &#71717Avoted for the server! " +
                    "&#8B5CF6(Progress: &#FCD34D" + currentVotes + " &7/ &#FCD34D" + targetVotes + " Votes&#8B5CF6)"));
        }

        if (currentVotes >= targetVotes) {
            triggerCommunityParty();
            if (plugin.getConfig().getBoolean("vote-goal.reset-on-trigger", true)) {
                currentVotes = 0;
            }
        }
    }

    public void triggerCommunityParty() {
        Bukkit.broadcast(ColorUtils.colorizeComponent("&#FCD34D★══════════════════════════════════════════════════★\n" +
                "&#EC4899&l🎉 COMMUNITY VOTE GOAL REACHED! 🎉\n" +
                "&#FCD34DThe Piñata Festival has been awakened! Gather at spawn!\n" +
                "&#FCD34D★══════════════════════════════════════════════════★"));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);
        }

        String profileId = plugin.getConfig().getString("vote-goal.reward-profile", "FESTIVE_LLAMA");
        PinataProfile profile = plugin.getRegistry().getProfile(profileId);
        if (profile == null) {
            profile = plugin.getRegistry().getAllProfiles().stream().findFirst().orElse(null);
        }

        if (profile != null && !Bukkit.getOnlinePlayers().isEmpty()) {
            Player ref = Bukkit.getOnlinePlayers().iterator().next();
            Location spawnLoc = ref.getLocation().add(0, 2.5, 0);
            plugin.spawnPinata(spawnLoc, profile);
        }
    }

    public void sendVoteInfo(Player player) {
        if (player == null) return;
        if (!enabled) {
            plugin.getMessageManager().send(player, "vote.disabled");
            return;
        }
        player.sendMessage(ColorUtils.colorize("&#EC4899━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        player.sendMessage(ColorUtils.colorize(" &#FCD34D&l🗳️ SERVER VOTE GOAL & PROGRESS"));
        player.sendMessage(ColorUtils.colorize(" &#71717ACommunity Progress: &#FCD34D" + currentVotes + " &7/ &#FCD34D" + targetVotes + " &#8B5CF6(" + (int)(((double)currentVotes/targetVotes)*100) + "%)"));
        player.sendMessage(ColorUtils.colorize(" &#71717AVote links:"));

        List<String> links = plugin.getConfig().getStringList("vote-goal.vote-links");
        for (String link : links) {
            player.sendMessage(ColorUtils.colorize("   " + link));
        }
        player.sendMessage(ColorUtils.colorize("&#EC4899━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
    }

    public int getCurrentVotes() { return currentVotes; }
    public int getTargetVotes() { return targetVotes; }
    public void setCurrentVotes(int votes) { this.currentVotes = votes; }
}
