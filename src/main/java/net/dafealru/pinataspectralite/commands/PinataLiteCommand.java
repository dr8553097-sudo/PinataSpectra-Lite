package net.dafealru.pinataspectralite.commands;

import net.dafealru.pinataspectralite.PinataPartyLite;
import net.dafealru.pinataspectralite.gui.LanguageSelectorGUI;
import net.dafealru.pinataspectralite.gui.PinataStudioGUI;
import net.dafealru.pinataspectralite.gui.PinataUpgradeGUI;
import net.dafealru.pinataspectralite.messages.MessageManager;
import net.dafealru.pinataspectralite.pinata.PinataProfile;
import net.dafealru.pinataspectralite.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PinataLiteCommand implements CommandExecutor, TabCompleter {

    private final PinataPartyLite plugin;

    public PinataLiteCommand(PinataPartyLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();

        if (cmd.equals("clean") || cmd.equals("pinataclean")) {
            handleClean(sender);
            return true;
        }

        if (cmd.equals("pinatapro") || cmd.equals("pro") || cmd.equals("upgrade") || cmd.equals("comparison") || cmd.equals("compare")) {
            sendCompareInfo(sender);
            return true;
        }

        if (cmd.equals("vote") || cmd.equals("votes")) {
            if (sender instanceof Player player) {
                plugin.getVoteGoalManager().sendVoteInfo(player);
            } else {
                sender.sendMessage("Vote progress: " + plugin.getVoteGoalManager().getCurrentVotes() + "/" + plugin.getVoteGoalManager().getTargetVotes());
            }
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "clean", "purge", "clear" -> handleClean(sender);
            case "pro", "upgrade", "comparison", "compare", "editions", "tiers" -> sendCompareInfo(sender);
            case "gui", "menu" -> {
                if (sender instanceof Player player) {
                    PinataUpgradeGUI.open(player);
                } else {
                    sendCompareInfo(sender);
                }
            }
            case "editor", "studio" -> {
                if (!(sender instanceof Player player)) {
                    plugin.getMessageManager().send(sender, "general.only-players");
                    return true;
                }
                if (!player.hasPermission("pinataspectra.admin")) {
                    plugin.getMessageManager().send(sender, "general.no-permission");
                    return true;
                }
                PinataProfile profile = null;
                if (args.length > 1) {
                    profile = plugin.getRegistry().getProfile(args[1]);
                }
                if (profile == null) {
                    profile = plugin.getRegistry().getAllProfiles().stream().findFirst().orElse(null);
                }
                if (profile != null) {
                    PinataStudioGUI.open(player, profile);
                } else {
                    plugin.getMessageManager().send(player, "general.profile-not-found", "profile", "default");
                }
            }
            case "pool", "donate" -> {
                if (!(sender instanceof Player player)) {
                    plugin.getMessageManager().send(sender, "general.only-players");
                    return true;
                }
                if (args.length == 1) {
                    plugin.getPinataPoolManager().showPoolInfo(player);
                } else {
                    try {
                        double amt = Double.parseDouble(args[1]);
                        plugin.getPinataPoolManager().contribute(player, amt);
                    } catch (NumberFormatException e) {
                        plugin.getMessageManager().send(player, "pool.invalid-amount");
                    }
                }
            }
            case "setspawn" -> {
                if (!(sender instanceof Player player)) {
                    plugin.getMessageManager().send(sender, "general.only-players");
                    return true;
                }
                if (!player.hasPermission("pinataspectra.admin")) {
                    plugin.getMessageManager().send(sender, "general.no-permission");
                    return true;
                }
                String name = args.length > 1 ? args[1].toLowerCase() : "default";
                plugin.saveLocation(name, player.getLocation());
                plugin.getMessageManager().send(player, "general.setspawn-success", "name", name);
            }
            case "spawn" -> {
                if (!sender.hasPermission("pinataspectra.admin")) {
                    plugin.getMessageManager().send(sender, "general.no-permission");
                    return true;
                }
                if (plugin.getActiveInstance() != null && !plugin.getActiveInstance().isDead()) {
                    plugin.getMessageManager().send(sender, "general.already-active");
                    return true;
                }
                String profileId = args.length > 1 ? args[1] : plugin.getConfig().getString("settings.default-profile", "FESTIVE_LLAMA");
                PinataProfile profile = plugin.getRegistry().getProfile(profileId);
                if (profile == null) {
                    profile = plugin.getRegistry().getAllProfiles().stream().findFirst().orElse(null);
                }
                if (profile == null) {
                    plugin.getMessageManager().send(sender, "general.profile-not-found", "profile", profileId);
                    return true;
                }

                Location loc = null;
                if (args.length >= 5) {
                    try {
                        double x = Double.parseDouble(args[2]);
                        double y = Double.parseDouble(args[3]);
                        double z = Double.parseDouble(args[4]);
                        World w = (args.length > 5) ? Bukkit.getWorld(args[5]) : (sender instanceof Player p ? p.getWorld() : Bukkit.getWorlds().get(0));
                        if (w != null) {
                            loc = new Location(w, x, y, z);
                        }
                    } catch (NumberFormatException ignored) {}
                }

                if (loc == null && args.length > 2) {
                    loc = plugin.getSavedLocation(args[2]);
                    if (loc == null) {
                        sender.sendMessage(ColorUtils.colorize("&cSaved location '" + args[2] + "' not found."));
                        return true;
                    }
                } else if (loc == null && sender instanceof Player player) {
                    loc = player.getLocation().add(0, 2.0, 0);
                } else if (loc == null) {
                    loc = plugin.getSavedLocation("default");
                    if (loc == null) {
                        World defaultWorld = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
                        if (defaultWorld != null) {
                            loc = defaultWorld.getSpawnLocation().add(0, 2.0, 0);
                        } else {
                            sender.sendMessage(ColorUtils.colorize("&cNo default spawn point found. Use /pinata setspawn default"));
                            return true;
                        }
                    }
                }

                plugin.spawnPinata(loc, profile);
                plugin.getMessageManager().send(sender, "general.spawn-success", "profile", profile.getId());
            }
            case "kill", "remove" -> {
                if (!sender.hasPermission("pinataspectra.admin")) {
                    plugin.getMessageManager().send(sender, "general.no-permission");
                    return true;
                }
                if (plugin.getActiveInstance() != null) {
                    plugin.getActiveInstance().remove();
                    plugin.setActiveInstance(null);
                    plugin.getMessageManager().send(sender, "general.kill-success");
                } else {
                    plugin.getMessageManager().send(sender, "general.no-active");
                }
            }
            case "vote", "votes" -> {
                if (sender instanceof Player player) {
                    plugin.getVoteGoalManager().sendVoteInfo(player);
                } else {
                    sender.sendMessage("Vote progress: " + plugin.getVoteGoalManager().getCurrentVotes() + "/" + plugin.getVoteGoalManager().getTargetVotes());
                }
            }
            case "bat" -> {
                if (!sender.hasPermission("pinataspectra.admin")) {
                    plugin.getMessageManager().send(sender, "general.no-permission");
                    return true;
                }

                boolean giveToAll = false;
                if (args.length > 1 && (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("*") || args[1].equalsIgnoreCase("@a"))) {
                    giveToAll = true;
                } else if (args.length > 2 && args[1].equalsIgnoreCase("give") && (args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("*") || args[2].equalsIgnoreCase("@a"))) {
                    giveToAll = true;
                }

                if (giveToAll) {
                    ItemStack bat = net.dafealru.pinataspectralite.items.PinataBatItem.createBat(plugin);
                    int count = 0;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.getInventory().addItem(bat.clone());
                        plugin.getMessageManager().send(p, "bat.received");
                        p.playSound(p.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_GOLD, 1.0f, 1.2f);
                        p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.6f);
                        count++;
                    }
                    plugin.getMessageManager().send(sender, "bat.given-all", "count", String.valueOf(count));
                    return true;
                }

                Player target = null;
                if (args.length > 2 && args[1].equalsIgnoreCase("give")) {
                    target = Bukkit.getPlayer(args[2]);
                } else if (args.length > 1) {
                    target = Bukkit.getPlayer(args[1]);
                } else if (sender instanceof Player player) {
                    target = player;
                }

                if (target == null) {
                    plugin.getMessageManager().send(sender, "general.player-not-found");
                    return true;
                }

                ItemStack bat = net.dafealru.pinataspectralite.items.PinataBatItem.createBat(plugin);
                target.getInventory().addItem(bat);
                plugin.getMessageManager().send(target, "bat.received");
                target.playSound(target.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_GOLD, 1.0f, 1.2f);

                if (!target.equals(sender)) {
                    plugin.getMessageManager().send(sender, "bat.given", "player", target.getName());
                }
            }
            case "lang", "language", "idioma", "locales" -> {
                handleLanguageCommand(sender, args);
            }
            case "reload" -> {
                if (!sender.hasPermission("pinataspectra.admin")) {
                    plugin.getMessageManager().send(sender, "general.no-permission");
                    return true;
                }
                long start = System.currentTimeMillis();
                plugin.reloadConfig();
                if (plugin.getConfigUpdaterEngine() != null) {
                    plugin.getConfigUpdaterEngine().updateAllConfigs();
                }
                plugin.getMessageManager().loadAllLocales();
                plugin.getVoteGoalManager().loadConfig();
                plugin.getPinataPoolManager().loadConfig();
                plugin.getRegistry().loadProfiles();
                long elapsed = System.currentTimeMillis() - start;
                plugin.getMessageManager().send(sender, "general.reload-success", "time", String.valueOf(elapsed));
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void handleLanguageCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player player) {
                LanguageSelectorGUI.open(player, plugin);
            } else {
                String current = plugin.getMessageManager().getServerLanguage();
                String all = plugin.getMessageManager().getAllLocales().stream().map(MessageManager.LocaleInfo::getCode).collect(Collectors.joining(", "));
                sender.sendMessage(ColorUtils.colorize("&d[PinataSpectra] &7Current server language: &e" + current));
                sender.sendMessage(ColorUtils.colorize("&d[PinataSpectra] &7Available languages: &a" + all));
                sender.sendMessage(ColorUtils.colorize("&d[PinataSpectra] &7Use: &f/pinata lang <code|reload|list>"));
            }
            return;
        }

        String sub = args[1].toLowerCase();

        if (sub.equals("list")) {
            String all = plugin.getMessageManager().getAllLocales().stream()
                    .map(l -> l.getFlag() + " " + l.getName() + " (" + l.getCode() + ")")
                    .collect(Collectors.joining("&7, &f"));
            plugin.getMessageManager().send(sender, "general.language-list", "languages", all);
            return;
        }

        if (sub.equals("reload")) {
            if (!sender.hasPermission("pinataspectra.admin")) {
                plugin.getMessageManager().send(sender, "general.no-permission");
                return;
            }
            long start = System.currentTimeMillis();
            plugin.getMessageManager().loadAllLocales();
            long elapsed = System.currentTimeMillis() - start;
            plugin.getMessageManager().send(sender, "general.language-reloaded",
                    "count", String.valueOf(plugin.getMessageManager().getAllLocales().size()),
                    "time", String.valueOf(elapsed));
            return;
        }

        if (sub.equals("set") && args.length >= 4) {
            if (!sender.hasPermission("pinataspectra.admin")) {
                plugin.getMessageManager().send(sender, "general.no-permission");
                return;
            }
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                plugin.getMessageManager().send(sender, "general.player-not-found");
                return;
            }
            String targetLocale = args[3];
            if (!plugin.getMessageManager().isLocaleLoaded(targetLocale)) {
                String available = plugin.getMessageManager().getAllLocales().stream().map(MessageManager.LocaleInfo::getCode).collect(Collectors.joining(", "));
                plugin.getMessageManager().send(sender, "general.language-invalid", "languages", available);
                return;
            }
            plugin.getMessageManager().setPlayerLanguage(target, targetLocale);
            sender.sendMessage(ColorUtils.colorize("&a✔ Set language of player &e" + target.getName() + " &ato &b" + targetLocale));
            return;
        }

        // Direct code switch: /pinata lang <code_or_name>
        String targetCode = args[1];
        if (!plugin.getMessageManager().isLocaleLoaded(targetCode)) {
            String available = plugin.getMessageManager().getAllLocales().stream().map(MessageManager.LocaleInfo::getCode).collect(Collectors.joining(", "));
            plugin.getMessageManager().send(sender, "general.language-invalid", "languages", available);
            return;
        }

        if (sender instanceof Player player) {
            plugin.getMessageManager().setPlayerLanguage(player, targetCode);
        } else {
            plugin.getMessageManager().setServerLanguage(targetCode);
            sender.sendMessage(ColorUtils.colorize("&a✔ Server default language changed to &b" + targetCode));
        }
    }

    private void handleClean(CommandSender sender) {
        if (!sender.hasPermission("pinataspectra.admin")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }

        int removed = plugin.getCleanEngine().cleanAll();
        if (removed > 0) {
            plugin.getMessageManager().send(sender, "clean.success", "count", String.valueOf(removed));
        } else {
            plugin.getMessageManager().send(sender, "clean.none-found");
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ColorUtils.colorize("&#8B5CF6━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        sender.sendMessage(ColorUtils.colorize(" <gradient:#EC4899:#FCD34D><bold>🪅 PINATASPECTRA LITE &8v1.0.0</bold></gradient> &7(Free Community Edition)"));
        sender.sendMessage(ColorUtils.colorize(" &#FCD34D/pinata spawn [profile] [spawn] &7- Start a 3D Piñata boss event"));
        sender.sendMessage(ColorUtils.colorize(" &#FCD34D/pinata bat [give <player>|all] &7- Give the festive Piñata bat"));
        sender.sendMessage(ColorUtils.colorize(" &#FCD34D/pinata lang [code|list|reload] &7- Open language selector GUI / switch language"));
        sender.sendMessage(ColorUtils.colorize(" &#FCD34D/pinata clean &7- Purge residual entities across all worlds"));
        sender.sendMessage(ColorUtils.colorize(" &#FCD34D/pinata setspawn <name> &7- Set fixed Piñata spawn point"));
        sender.sendMessage(ColorUtils.colorize(" &#FCD34D/pinata pool [amount] &7- View or fund community pool"));
        sender.sendMessage(ColorUtils.colorize(" &#FCD34D/pinata vote &7- Check community vote goal progress"));
        sender.sendMessage(ColorUtils.colorize(" &#FCD34D/pinata editor &7- Open in-game visual editor GUI"));
        sender.sendMessage(ColorUtils.colorize(" &#FCD34D/pinata kill &7- Remove active Piñata immediately"));
        sender.sendMessage(ColorUtils.colorize(" &#FCD34D/pinata reload &7- Reload configurations and messages"));
        sender.sendMessage(ColorUtils.colorize(" &#60A5FA/pinata comparison &7- Compare & unlock Sovereign PRO features"));
        sender.sendMessage(ColorUtils.colorize("&#8B5CF6━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
    }

    private void sendCompareInfo(CommandSender sender) {
        sender.sendMessage(ColorUtils.colorize("<gradient:#8B5CF6:#EC4899:#F59E0B><bold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</bold></gradient>"));
        sender.sendMessage(ColorUtils.colorize(" <gradient:#D946EF:#F43F5E><bold>✦ PINATASPECTRA</bold></gradient> <#71717A>› <gradient:#F59E0B:#FEF08A><bold>EDITION COMPARISON</bold></gradient>"));
        sender.sendMessage(ColorUtils.colorize("<gradient:#8B5CF6:#EC4899><bold>──────────────────────────────────────────────────</bold></gradient>"));
        sender.sendMessage(ColorUtils.colorize(" <#A855F7>▪ <#FFFFFF>3D Models:   <#94A3B8>Lite: <#EF4444>1 Form <#71717A>| <#FCD34D>PRO: <#22C55E>8 Forms (Mecha/Dragon)"));
        sender.sendMessage(ColorUtils.colorize(" <#A855F7>▪ <#FFFFFF>Death FX:    <#94A3B8>Lite: <#EF4444>1 Fountain <#71717A>| <#FCD34D>PRO: <#22C55E>7 Supernovas (Black Hole)"));
        sender.sendMessage(ColorUtils.colorize(" <#A855F7>▪ <#FFFFFF>Profiles:    <#94A3B8>Lite: <#EF4444>Max 2 <#71717A>| <#FCD34D>PRO: <#22C55E>Unlimited & BossBars"));
        sender.sendMessage(ColorUtils.colorize(" <#A855F7>▪ <#FFFFFF>Studio GUI:  <#94A3B8>Lite: <#F59E0B>Basic <#71717A>| <#FCD34D>PRO: <#22C55E>6 Live Visual Editors"));
        sender.sendMessage(ColorUtils.colorize(" <#A855F7>▪ <#FFFFFF>Combat:      <#94A3B8>Lite: <#F59E0B>3 Phases <#71717A>| <#FCD34D>PRO: <#22C55E>6 Attacks & Minions"));
        sender.sendMessage(ColorUtils.colorize(" <#A855F7>▪ <#FFFFFF>Loot Safety: <#94A3B8>Lite: <#EF4444>Floor Loot <#71717A>| <#FCD34D>PRO: <#22C55E>Anti-Steal Vault"));
        sender.sendMessage(ColorUtils.colorize(" <#A855F7>▪ <#FFFFFF>Custom Bats: <#94A3B8>Lite: <#EF4444>1 Bat <#71717A>| <#FCD34D>PRO: <#22C55E>7 Mythic Bats (Mjolnir)"));
        sender.sendMessage(ColorUtils.colorize(" <#A855F7>▪ <#FFFFFF>Databases:   <#94A3B8>Lite: <#F59E0B>SQLite <#71717A>| <#FCD34D>PRO: <#22C55E>MySQL + MariaDB + Redis"));
        sender.sendMessage(ColorUtils.colorize(" <#A855F7>▪ <#FFFFFF>Webhooks:    <#94A3B8>Lite: <#F59E0B>Plain Alerts <#71717A>| <#FCD34D>PRO: <#22C55E>Luxury Embeds"));
        sender.sendMessage(ColorUtils.colorize(" <#A855F7>▪ <#FFFFFF>Jackpots:    <#94A3B8>Lite: <#EF4444>None <#71717A>| <#FCD34D>PRO: <#22C55E>3D Wheels & RGB Floor"));
        sender.sendMessage(ColorUtils.colorize("<gradient:#8B5CF6:#EC4899><bold>──────────────────────────────────────────────────</bold></gradient>"));
        sender.sendMessage(ColorUtils.colorize("  <#71717A>Active: <#22C55E><bold>🍃 Lite Edition</bold> <#71717A>• <#FCD34D><click:open_url:'https://builtbybit.com/pinataspectra'><hover:show_text:'<#22C55E>Click to unlock Sovereign PRO with 20% discount!'><u>Upgrade to Sovereign PRO »</u></hover></click>"));
        sender.sendMessage(ColorUtils.colorize("<gradient:#8B5CF6:#EC4899:#F59E0B><bold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</bold></gradient>"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.addAll(List.of("spawn", "bat", "clean", "setspawn", "pool", "kill", "editor", "vote", "lang", "language", "pro", "comparison", "reload", "help"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            for (PinataProfile p : plugin.getRegistry().getAllProfiles()) {
                list.add(p.getId());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("spawn")) {
            list.addAll(plugin.getSavedLocationNames());
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("lang") || args[0].equalsIgnoreCase("language") || args[0].equalsIgnoreCase("idioma") || args[0].equalsIgnoreCase("locales"))) {
            list.addAll(List.of("list", "reload", "set"));
            for (MessageManager.LocaleInfo l : plugin.getMessageManager().getAllLocales()) {
                list.add(l.getCode());
            }
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("lang") || args[0].equalsIgnoreCase("language")) && args[1].equalsIgnoreCase("set")) {
            for (Player p : Bukkit.getOnlinePlayers()) list.add(p.getName());
        } else if (args.length == 4 && (args[0].equalsIgnoreCase("lang") || args[0].equalsIgnoreCase("language")) && args[1].equalsIgnoreCase("set")) {
            for (MessageManager.LocaleInfo l : plugin.getMessageManager().getAllLocales()) {
                list.add(l.getCode());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("bat")) {
            list.addAll(List.of("give", "all", "*"));
            for (Player p : Bukkit.getOnlinePlayers()) list.add(p.getName());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("bat") && args[1].equalsIgnoreCase("give")) {
            list.addAll(List.of("all", "*"));
            for (Player p : Bukkit.getOnlinePlayers()) list.add(p.getName());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setspawn")) {
            list.addAll(List.of("default", "arena", "spawn", "center"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("pool")) {
            list.addAll(List.of("100", "500", "1000", "2500"));
        }
        return list;
    }
}
