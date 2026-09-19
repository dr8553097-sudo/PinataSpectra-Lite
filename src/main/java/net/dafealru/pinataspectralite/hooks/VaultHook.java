package net.dafealru.pinataspectralite.hooks;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private final PinataPartyLite plugin;
    private Economy economy = null;

    public VaultHook(PinataPartyLite plugin) {
        this.plugin = plugin;
        setupEconomy();
    }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
    }

    public boolean hasVault() {
        return economy != null;
    }

    public void deposit(Player player, double amount) {
        if (economy != null && player != null && amount > 0) {
            try {
                economy.depositPlayer(player, amount);
            } catch (Exception ignored) {}
        }
    }
}
