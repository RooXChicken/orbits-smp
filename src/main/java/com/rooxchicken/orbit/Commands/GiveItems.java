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

public class GiveItems implements CommandExecutor
{
    private Orbit plugin;

    public GiveItems(Orbit _plugin)
    {
        plugin = _plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender.isOp())
        {
            Player player = Bukkit.getPlayer(sender.getName());
            Bukkit.dispatchCommand(sender, "give @s red_dye{display:{Name:'{\"text\":\"Power Orbit\",\"color\":\"red\",\"bold\":true}'}} 1");
            Bukkit.dispatchCommand(sender, "give @s gray_dye{display:{Name:'{\"text\":\"Astro Orbit\",\"color\":\"gray\",\"bold\":true}'}} 1");
        }

        return true;
    }

}
