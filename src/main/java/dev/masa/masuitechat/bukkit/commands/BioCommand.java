package dev.masa.masuitechat.bukkit.commands;

import dev.masa.masuitechat.bukkit.MaSuiteChat;
import dev.masa.masuitechat.core.models.Bio;
import dev.masa.masuitecore.acf.BaseCommand;
import dev.masa.masuitecore.acf.annotation.*;
import dev.masa.masuitecore.core.channels.BukkitPluginChannel;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BioCommand extends BaseCommand {

    private MaSuiteChat plugin;
    private Map<UUID, Bio> editingBios = new HashMap<>();

    private String[] types = new String[]{
            "Student", "Alum", "Prefrosh", "Prospective", "Visitor", "Faculty", "Staff"
    };

    private String[] schools = new String[]{
            "A&S", "AAP", "CALS", "COE", "HumEc", "ILR", "Johnson", "Law", "SHA", "Tech", "Weill", "Vet", "Skip this"
    };

    // [✦]
    private final String PREFIX =
            ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "✦" + ChatColor.WHITE + "] ";

    public BioCommand(MaSuiteChat plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("biography|bio")
    @CommandPermission("masuitechat.bio")
    @Description("Edit your bio.")
    public void bioCommand(Player player) {
        editingBios.put(player.getUniqueId(), new Bio(player.getUniqueId()));

        player.sendMessage(ChatColor.LIGHT_PURPLE + "--------------");
        player.sendMessage(PREFIX + "You are now editing your bio. Others will be able to hover over your name to see it!");

        // Step 1: ask type
        player.sendMessage(PREFIX + "Who are you?");
        for (String type : types) {
            player.spigot().sendMessage(_generateType(type));
        }
    }

    @CommandAlias("biotype")
    @CommandPermission("masuite.bio")
    public void typeCommand(Player player, String type) {
        if(!editingBios.containsKey(player.getUniqueId()) || type == null || !Arrays.asList(types).contains(type)) {
            player.sendMessage(ChatColor.RED + "Type /bio to edit your biography.");
            return;
        }

        Bio bio = editingBios.get(player.getUniqueId());
        bio.setType(type);
        editingBios.put(player.getUniqueId(), bio);

        // Step 2: ask year OR pronouns (if they're not Cornell affiliated)
        if(type.equals("Student") || type.equals("Alum") || type.equals("Prefrosh")) {
            player.sendMessage(PREFIX + "Cool! What's your graduation year?");
            player.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', "&fType &6/gradyear <year>&f, where &6<year> &fis your year. For example, /year 2021."));
            player.sendMessage(PREFIX + "To skip this step, type " + ChatColor.GOLD + "/gradyear skip");
        } else {
            player.sendMessage(PREFIX + "Almost there! Lastly, what are your pronouns?");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fType &a/pronouns <pronouns>&f, where &a<pronouns> &fare your pronouns. For example, /year they/them sets your pronouns to they/them."));
        }
    }

    @CommandAlias("gradyear")
    @CommandPermission("masuite.bio")
    public void gradYearCommand(Player player, String year) {
        if(!editingBios.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Type /bio to edit your biography.");
            return;
        }

        if(year == null) {
            player.sendMessage(ChatColor.RED + "You forgot to include the year. " + ChatColor.WHITE + "For example, /year 2021 sets your graduation year to 2021.");
            return;
        }

        if(!year.trim().equalsIgnoreCase("skip")) {
            if (year.trim().length() != 4) {
                player.sendMessage(ChatColor.RED + "You must give the full, four-digit year. " + ChatColor.WHITE + "For example, /year 2021 sets your graduation year to 2021.");
                return;
            }

            Bio bio = editingBios.get(player.getUniqueId());
            bio.setYear(year.trim());
            editingBios.put(player.getUniqueId(), bio);
        }

        // Let them select a school.
        player.sendMessage(PREFIX + "Awesome! Now, which school or college are you affiliated with?");
        for (String school : schools) {
            player.spigot().sendMessage(_generateSchool(school));
        }
    }

    @CommandAlias("bioschool")
    @CommandPermission("masuite.bio")
    public void schoolCommand(Player player, String school) {
        if(!editingBios.containsKey(player.getUniqueId()) || school == null || !Arrays.asList(schools).contains(school)) {
            player.sendMessage(ChatColor.RED + "Type /bio to edit your biography.");
            return;
        }

        if(!school.trim().equalsIgnoreCase("skip")) {
            Bio bio = editingBios.get(player.getUniqueId());
            bio.setSchool(school.trim());
            editingBios.put(player.getUniqueId(), bio);
        }

        // Ask them for their pronouns.
        player.sendMessage(PREFIX + "Almost there! Lastly, what are your pronouns?");
        player.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', "&fType &a/pronouns <pronouns>&f, where &a<pronouns> &fare your pronouns. For example, /year they/them sets your pronouns to they/them."));
        player.sendMessage(PREFIX + "To skip this step, type " + ChatColor.GREEN + "/pronouns skip");
    }

    @CommandAlias("pronouns")
    @CommandPermission("masuite.bio")
    private void pronounsCommand(Player player, String pronouns) {
        if(!editingBios.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Type /bio to edit your biography.");
            return;
        }

        if(pronouns == null) {
            player.sendMessage(ChatColor.RED + "You forgot to include the pronouns. " + ChatColor.WHITE + "For example, /pronouns she/her sets your prononuns to she/her.");
            return;
        }

        pronouns = pronouns.trim();

        Bio bio = editingBios.get(player.getUniqueId());
        if(!pronouns.equalsIgnoreCase("skip")) {
            pronouns = pronouns.replaceAll(" ", "/");
            bio.setPronouns(pronouns);
        }

        new BukkitPluginChannel(plugin, player, "MaSuiteChat", "Bio", player.getUniqueId().toString(), orSkip(bio.getType()), orSkip(bio.getYear()), orSkip(bio.getSchool()), orSkip(bio.getPronouns())).send();
        removeEditing(player.getUniqueId());
        player.sendMessage(PREFIX + "All done! Your bio is visible when people hover over your name in chat.");
        player.sendMessage(PREFIX + "If you want to edit your bio again, just type" + ChatColor.LIGHT_PURPLE + "/bio" + ChatColor.WHITE + ".");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "--------------");
    }

    public void removeEditing(UUID uid) {
        editingBios.remove(uid);
    }

    private TextComponent _generateType(String type) {
        TextComponent typeCmp = new TextComponent(ChatColor.WHITE + "* " + ChatColor.GOLD + type);
        typeCmp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.WHITE + "Click here if you are a " + ChatColor.GOLD + type + ChatColor.WHITE + ".")}));
        typeCmp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/biotype " + type));
        return typeCmp;
    }

    private TextComponent _generateSchool(String school) {
        TextComponent typeCmp = new TextComponent(ChatColor.WHITE + "* " + ChatColor.BLUE + school);
        typeCmp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.WHITE + "Click here to select  " + ChatColor.BLUE + school + ChatColor.WHITE + ".")}));
        typeCmp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bioschool " + school));
        return typeCmp;
    }

    private String orSkip(String it) {
        if(it == null) return "skip";
        return it;
    }

}
