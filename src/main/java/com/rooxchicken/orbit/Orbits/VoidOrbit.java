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
import com.rooxchicken.orbit.Tasks.VoidOrbit_Freeze;
import com.rooxchicken.orbit.Tasks.VoidOrbit_Storm;

import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.IMaterial;

public class VoidOrbit extends BaseOrbit
{
    private Orbit plugin;
    private Player player;

    //public NamespacedKey cooldown1Key;
    //public NamespacedKey cooldown2Key;

    // public int cooldown1Max;
    // public int cooldown2Max;

    public VoidOrbit(Orbit _plugin, Player _player)
    {
        super(_plugin);
        plugin = _plugin;
        player = _player;

        itemName = "§0§lVoid Orbit";

        item = new ItemStack(Material.BLACK_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);
        item.setItemMeta(meta);

        cooldown1Max = 300 * 20;
        cooldown2Max = 500 * 20;

        cooldown1Key = new NamespacedKey(plugin, "void_cd1");
        cooldown2Key = new NamespacedKey(plugin, "void_cd2");

        checkHasCooldown(player, cooldown1Key, cooldown2Key);
    }

    @Override
    public void tick()
    {

    }

    @EventHandler
    private void freezePlayers(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(player != event.getPlayer())
            return;

        if(!checkOrbit(player, 2))
            return;

        ItemStack item = event.getItem();

        if(checkItem(item) && checkCooldown(player, cooldown1Key, cooldown1Max))
        {
            activateAbility1(player, plugin);
        }
    }

    @EventHandler
    private void useVoidStorm(PlayerSwapHandItemsEvent event)
    {
        if(event.getPlayer() != player)
            return;

        if(!checkOrbit(player, 2))
            return;
            
        ItemStack item = event.getMainHandItem();

        if(!player.isSneaking())
            return;

        //Bukkit.getLogger().info(item.getItemMeta().getDisplayName());

        if(checkItem(item) && checkCooldown(player, cooldown2Key, cooldown2Max))
        {
            activateAbility2(player, plugin);

            event.setCancelled(true);
        }
    }

    public static void activateAbility1(Player _player, Orbit _plugin)
    {
        Orbit.tasks.add(new VoidOrbit_Freeze(_plugin, _player));
    }

    public static void activateAbility2(Player _player, Orbit _plugin)
    {
        Orbit.tasks.add(new VoidOrbit_Storm(_plugin, _player));
    }
}
