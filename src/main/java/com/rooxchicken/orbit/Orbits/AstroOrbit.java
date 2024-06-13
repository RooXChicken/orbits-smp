package com.rooxchicken.orbit.Orbits;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.rooxchicken.orbit.Orbit;
import com.rooxchicken.orbit.Tasks.AstroOrbit_Evaporation;
import com.rooxchicken.orbit.Tasks.PowerOrbit_Cookout;

import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.IMaterial;

public class AstroOrbit extends BaseOrbit
{
    private Orbit plugin;
    private Player player;

    //public NamespacedKey cooldown1Key;
    //public NamespacedKey cooldown2Key;

    // public int cooldown1Max = 200 * 20;
    // public int cooldown2Max = 250 * 20;
    
    
    public AstroOrbit(Orbit _plugin, Player _player)
    {
        super(_plugin);
        plugin = _plugin;
        player = _player;

        itemName = "§7§lAstro Orbit";

        item = new ItemStack(Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);
        item.setItemMeta(meta);

        cooldown1Max = 200 * 20;
        cooldown2Max = 250 * 20;

        cooldown1Key = new NamespacedKey(plugin, "astro_cd1");
        cooldown2Key = new NamespacedKey(plugin, "astro_cd2");

        checkHasCooldown(player, cooldown1Key, cooldown2Key);
    }

    @EventHandler
    private void useTrueInvisibility(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(player != event.getPlayer())
            return;

        if(!checkOrbit(player, 1))
            return;
        
        ItemStack item = event.getItem();

        if(checkItem(item))// && checkCooldown(player, cooldown1Key, cooldown1Max))
        {
            Orbit.tasks.add(new AstroOrbit_Evaporation(plugin, player));
        }
    }
}
