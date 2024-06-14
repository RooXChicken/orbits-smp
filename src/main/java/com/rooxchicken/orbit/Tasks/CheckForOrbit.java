package com.rooxchicken.orbit.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.rooxchicken.orbit.Orbit;
import com.rooxchicken.orbit.Orbits.BaseOrbit;

public class CheckForOrbit extends Task
{
    Orbit plugin;
    public CheckForOrbit(Orbit _plugin)
    {
        super(_plugin);
        plugin = _plugin;
        tickThreshold = 40;
    }

    @Override
    public void run()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            BaseOrbit orbit = plugin.getOrbit(player);
            if(!player.getInventory().contains(orbit.item))
                if(!player.getInventory().getItemInOffHand().equals(orbit.item) && !player.getItemOnCursor().equals(orbit.item))
                    player.getInventory().addItem(orbit.item);
        }
    }
}
