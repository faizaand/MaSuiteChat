package dev.masa.masuitechat.bungee;

import dev.masa.masuitechat.bungee.objects.Group;
import dev.masa.masuitechat.core.models.Bio;
import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitecore.bungee.chat.MDChat;
import dev.masa.masuitecore.core.configuration.BungeeConfiguration;
import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utilities {

    public static TextComponent newLine = new TextComponent(ComponentSerializer.parse("{text: \"\n\"}"));

    public static BaseComponent[] chatFormat(ProxiedPlayer p, String msg, String channel) {

        Formator formator = new Formator();
        BungeeConfiguration config = new BungeeConfiguration();

        String format = config.load("chat", "chat.yml").getString("formats." + channel);
        String server = config.load("chat", "chat.yml").getString("channels." + p.getServer().getInfo().getName().toLowerCase() + ".prefix");

        Group group = new Group().get(p.getUniqueId());
        format = formator.colorize(
                format.replace("%server%", server)
                        .replace("%prefix%", group.getPrefix())
                        .replace("%nickname%", p.getDisplayName())
                        .replace("%realname%", p.getName())
                        .replace("%suffix%", group.getSuffix()));
        if (p.hasPermission("masuitechat.chat.colors")) {
            format = formator.colorize(format.replace("%message%", msg));
        } else {
            format = format.replace("%message%", msg);
        }

        TextComponent message = MDChat.getMessageFromString(format);
        SimpleDateFormat customDate = new SimpleDateFormat(config.load("chat", "messages.yml").getString("timestamp-format"));
        customDate.setTimeZone(TimeZone.getTimeZone(config.load("chat", "messages.yml").getString("timestamp-timezone")));
        String dateFormat  = customDate.format(new Date());

        Bio bio = MaSuiteChat.bioService.getBio(p.getUniqueId());

        ComponentBuilder hoverText = new ComponentBuilder(formator.colorize(config.load("chat", "messages.yml")
                .getString("message-hover-actions")
                .replace("%timestamp%", dateFormat)));
        if(bio.hasData()) {
            hoverText.append(newLine);
            if(bio.getPronouns() != null) {
                hoverText.append(formator.colorize("&7Sent by &f" + p.getDisplayName() + " &r&7(" + bio.getPronouns() + ")."));
            } else {
                hoverText.append(formator.colorize("&7Sent by " + p.getDisplayName() + "&r&7."));
            }

            StringBuilder bioString = new StringBuilder();
            if(bio.getType() != null)
                bioString.append(formator.colorize("&3" + bio.getType()));
            if(bio.getYear() != null)
                bioString.append(formator.colorize(" &7(" + bio.getYear() + ")"));
            if(bio.getSchool() != null)
                bioString.append(formator.colorize(" &8• &r&f" + bio.getSchool()));

            String str = bioString.toString();
            if(!str.isEmpty()) {
                hoverText.append(newLine);
                hoverText.append(str);
            }
        }
        return new ComponentBuilder(message).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.create())).create();
    }
}
