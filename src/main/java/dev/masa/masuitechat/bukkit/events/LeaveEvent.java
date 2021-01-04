package dev.masa.masuitechat.bukkit.events;

import dev.masa.masuitechat.bukkit.MaSuiteChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEvent implements Listener {

    private MaSuiteChat plugin;

    public LeaveEvent(MaSuiteChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        plugin.bioCommand.removeEditing(e.getPlayer().getUniqueId());
        e.setQuitMessage(null);
    }
}
