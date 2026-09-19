package net.dafealru.pinataspectralite.pool;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.pinata.PinataProfile;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PinataPoolManager {

    private final PinataPartyLite plugin;
    private double currentPool = 0.0;
    private double targetPool = 5000.0;
    private boolean enabled = true;

    public PinataPoolManager(PinataPartyLite plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.enabled = plugin.getConfig().getBoolean("pinata-pool.enabled", true);
        this.targetPool = plugin.getConfig().getDouble("pinata-pool.target-money", 5000.0);
        this.currentPool = plugin.getConfig().getDouble("pinata-pool.current-money", 0.0);
    }

    public void savePool() {
        plugin.getConfig().set("pinata-pool.current-money", currentPool);
        plugin.saveConfig();
    }

    public boolean contribute(Player player, double amount) {
        if (!enabled) {
            player.sendMessage(ColorUtils.colorize("&cThe community Pinata Pool is currently disabled."));
            return false;
        }
        if (amount <= 0) {
            player.sendMessage(ColorUtils.colorize("&cPlease specify a positive donation amount."));
            return false;
        }

        if (!plugin.getVaultHook().hasVault()) {
            player.sendMessage(ColorUtils.colorize("&cVault economy is not available on this server."));
            return false;
        }

        // Check player balance
        net.milkbowl.vault.economy.Economy eco = getEconomy();
        if (eco == null || eco.getBalance(player) < amount) {
            player.sendMessage(ColorUtils.colorize("&cYou do not have sufficient funds! Required: &e$" + (int) amount));
            return false;
        }

        // Deduct money
        eco.withdrawPlayer(player, amount);
        currentPool += amount;
        savePool();

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.8f);

        // Broadcast progress
        int pct = (int) Math.min(100, (currentPool / targetPool) * 100);
        Bukkit.broadcast(ColorUtils.colorizeComponent(
                "&#FCD34D💰 &#f" + player.getName() + " &#71717Adonated &#22C55E+$" + (int) amount +
                        " &#71717Ato the Piñata Pool! &#8B5CF6(Progress: &#22C55E$" + (int) currentPool + " &7/ &#FCD34D$" + (int) targetPool + " &8- &#EC4899" + pct + "%&#8B5CF6)"
        ));

        if (currentPool >= targetPool) {
            triggerPoolParty();
            currentPool = 0.0;
            savePool();
        }
        return true;
    }

    private void triggerPoolParty() {
        Bukkit.broadcast(ColorUtils.colorizeComponent(
                "&#FCD34D★══════════════════════════════════════════════════★\n" +
                        "&#22C55E&l🎉 COMMUNITY PIÑATA POOL GOAL REACHED! 🎉\n" +
                        "&#FCD34DA total of &#22C55E$" + (int) targetPool + " &#FCD34Dwas collected! Spawning the Grand Piñata!\n" +
                        "&#FCD34D★══════════════════════════════════════════════════★"
        ));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);
        }

        String profileId = plugin.getConfig().getString("pinata-pool.reward-profile", "FESTIVE_LLAMA");
        PinataProfile profile = plugin.getRegistry().getProfile(profileId);
        if (profile == null) {
            profile = plugin.getRegistry().getAllProfiles().stream().findFirst().orElse(null);
        }

        if (profile != null && !Bukkit.getOnlinePlayers().isEmpty()) {
            Location spawnLoc = plugin.getSavedLocation("default");
            if (spawnLoc == null) {
                Player ref = Bukkit.getOnlinePlayers().iterator().next();
                spawnLoc = ref.getLocation().add(0, 2.5, 0);
            }
            plugin.spawnPinata(spawnLoc, profile);
        }
    }

    public void showPoolInfo(Player player) {
        if (player == null) return;
        int pct = (int) Math.min(100, (currentPool / targetPool) * 100);

        player.sendMessage(ColorUtils.colorize("&#8B5CF6━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        player.sendMessage(ColorUtils.colorize(" <gradient:#FCD34D:#F59E0B><bold>💰 SERVER PIÑATA COMMUNITY POOL</bold></gradient>"));
        player.sendMessage(ColorUtils.colorize(" &#71717ACollected: &#22C55E$" + (int) currentPool + " &7/ &#FCD34D$" + (int) targetPool + " &#8B5CF6(" + pct + "%)"));
        player.sendMessage(ColorUtils.colorize(" &#71717AContribute using: &#FCD34D/pinata pool <amount>"));
        player.sendMessage(ColorUtils.colorize("&#8B5CF6━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.6f);
    }

    private net.milkbowl.vault.economy.Economy getEconomy() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) return null;
        org.bukkit.plugin.RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp =
                Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        return rsp != null ? rsp.getProvider() : null;
    }

    public double getCurrentPool() { return currentPool; }
    public double getTargetPool() { return targetPool; }
    public boolean isEnabled() { return enabled; }
}
