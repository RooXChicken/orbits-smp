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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
import com.rooxchicken.orbit.Tasks.SolarOrbit_Rings;
import com.rooxchicken.orbit.Tasks.VoidOrbit_Freeze;

import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.IMaterial;

public class SolarOrbit extends BaseOrbit
{
    public NamespacedKey cooldown1Key;
    public NamespacedKey cooldown2Key;

    public int cooldown1Max = 200 * 20;
    public int cooldown2Max = 250 * 20;
    
    private Orbit plugin;

    public SolarOrbit(Orbit _plugin)
    {
        super(_plugin);
        plugin = _plugin;

        itemName = "§7§lAstro Orbit";

        cooldown1Key = new NamespacedKey(plugin, "void_cd1");
        cooldown2Key = new NamespacedKey(plugin, "void_cd2");
    }

    @Override
    public void tick()
    {

    }

    @EventHandler
    private void useCosmicThrow(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if(checkItem(item))// && checkCooldown(player, cooldown1Key, cooldown1Max))
        {
            Vector direction = player.getLocation().getDirection();
            direction.setY(Math.abs(direction.getY()));
            player.setVelocity(direction.multiply(2));
        }
    }

    @EventHandler
    private void spawnSaturnRings(PlayerSwapHandItemsEvent event)
    {
        Player player = event.getPlayer();
        ItemStack item = event.getMainHandItem();

        if(!player.isSneaking())
            return;

        //Bukkit.getLogger().info(item.getItemMeta().getDisplayName());

        if(checkItem(item))// && checkCooldown(player, cooldown1Key, cooldown1Max))
        {
            Orbit.tasks.add(new SolarOrbit_Rings(plugin, player));

            event.setCancelled(true);
        }
    }
}
