package com.rooxchicken.orbit.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.rooxchicken.orbit.Orbit;

public class SetOrbit implements CommandExecutor
{
    private Orbit plugin;

    public SetOrbit(Orbit _plugin)
    {
        plugin = _plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender.isOp())
        {
            Player player = Bukkit.getPlayer(sender.getName());
            int orbit = Integer.parseInt(args[0]);
            plugin.setOrbit(player, orbit);
        }

        return true;
    }

}
