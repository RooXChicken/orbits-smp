package com.rooxchicken.orbit.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.rooxchicken.orbit.Orbit;
import com.rooxchicken.orbit.Orbits.BaseOrbit;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class DisplayCooldown extends Task
{
    private Orbit plugin;

    public DisplayCooldown(Orbit _plugin)
    {
        super(_plugin);
        plugin = _plugin;
        tickThreshold = 1;
    }

    @Override
    public void run()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            String msg = "";
            BaseOrbit orbit = plugin.getOrbit(player);
            if(orbit == null)
                return;
            PersistentDataContainer data = player.getPersistentDataContainer();
            
            int cooldown1 = data.get(orbit.cooldown1Key, PersistentDataType.INTEGER) - 1;
            if(cooldown1 >= 0)
            data.set(orbit.cooldown1Key, PersistentDataType.INTEGER, cooldown1);
            if(cooldown1 < 1)
                msg += "READY";
            else
                msg += cooldown1/20 + "/" + orbit.cooldown1Max/20 + "s";

            int kills = data.get(Orbit.killsKey, PersistentDataType.INTEGER);
            if(orbit.cooldown2Max != -1 && kills >= 5)
            {
                int cooldown2 = data.get(orbit.cooldown2Key, PersistentDataType.INTEGER) - 1;
                if(cooldown2 >= 0)
                    data.set(orbit.cooldown2Key, PersistentDataType.INTEGER, cooldown2);
                if(cooldown2 < 1)
                    msg += " | READY";
                else
                    msg += " | " + cooldown2/20 + "/" + orbit.cooldown2Max/20 + "s";
            }

            if(orbit.cooldown2Max != -1 && kills < 5)
                msg += " | LOCKED";

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
        }
    }
}
