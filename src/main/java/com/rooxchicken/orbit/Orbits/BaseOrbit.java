package com.rooxchicken.orbit.Orbits;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.rooxchicken.orbit.Orbit;

public abstract class BaseOrbit implements Listener
{
    private Orbit plugin;
    public String itemName;
    public ItemStack item;

    public NamespacedKey cooldown1Key;
    public NamespacedKey cooldown2Key;
    public int cooldown1Max;
    public int cooldown2Max;

    public BaseOrbit(Orbit _plugin)
    {
        plugin = _plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, _plugin);
    }

    public void tick() {}

    public boolean checkItem(ItemStack item)
    {
        return (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(itemName));
    }

    public boolean checkCooldown(Player player, NamespacedKey key, int cooldown)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(!data.has(key, PersistentDataType.INTEGER))
            data.set(key, PersistentDataType.INTEGER, 0);

        if(data.get(key, PersistentDataType.INTEGER) == 0)
        {
            data.set(key, PersistentDataType.INTEGER, cooldown);
            return true;
        }

        return false;
    }

    public boolean checkCooldownNoset(Player player, NamespacedKey key, int cooldown)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(!data.has(key, PersistentDataType.INTEGER))
            data.set(key, PersistentDataType.INTEGER, 0);

        return (data.get(key, PersistentDataType.INTEGER) == 0);
    }

    public boolean checkOrbit(Player player, int orbit)
    {
        return (player.getPersistentDataContainer().get(Orbit.orbitKey, PersistentDataType.INTEGER) == orbit);
    }

    public void checkHasCooldown(Player player, NamespacedKey cd1, NamespacedKey cd2)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(!data.has(cd1, PersistentDataType.INTEGER))
            data.set(cd1, PersistentDataType.INTEGER, 0);

        if(cd2 != null)
        if(!data.has(cd2, PersistentDataType.INTEGER))
            data.set(cd2, PersistentDataType.INTEGER, 0);
    }

    public boolean checkKills(Player player)
    {
        return (player.getPersistentDataContainer().get(Orbit.killsKey, PersistentDataType.INTEGER) >= 5);
    }
}
