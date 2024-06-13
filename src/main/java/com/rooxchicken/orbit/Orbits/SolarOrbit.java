package com.rooxchicken.orbit.Orbits;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    private Orbit plugin;
    private Player player;

    //public NamespacedKey cooldown1Key;
    //public NamespacedKey cooldown2Key;

    // public int cooldown1Max;
    // public int cooldown2Max;

    private int state = 0;

    public SolarOrbit(Orbit _plugin, Player _player)
    {
        super(_plugin);
        plugin = _plugin;
        player = _player;

        itemName = "§e§lSolar Orbit";

        item = new ItemStack(Material.ORANGE_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);
        item.setItemMeta(meta);

        cooldown1Max = 300*20;
        cooldown2Max = 350*20;

        cooldown1Key = new NamespacedKey(plugin, "solar_cd1");
        cooldown2Key = new NamespacedKey(plugin, "solar_cd2");

        checkHasCooldown(player, cooldown1Key, cooldown2Key);
    }

    @Override
    public void tick()
    {
        if(state != 0 && player.isOnGround())
            state = 0;

        //Bukkit.getLogger().info("" + player.getVelocity().getY());
    }

    @EventHandler
    private void useCosmicThrow(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(player != event.getPlayer())
            return;

        if(!checkOrbit(player, 3))
            return;

        ItemStack item = event.getItem();

        if(checkItem(item))
        {
            switch(state)
            {
                case 0:
                    if(checkCooldown(player, cooldown1Key, cooldown1Max))
                    {
                        Vector direction = player.getLocation().getDirection();
                        direction.setY(Math.abs(direction.getY()));
                        player.setVelocity(direction.multiply(2));
                        state = 1;
                    }
                    else
                    {
                        state = 1;
                        useCosmicThrow(event);
                    }
                break;

                case 1:
                    if(checkCooldown(player, cooldown1Key, cooldown1Max))
                    {
                        Fireball fireball = (Fireball)player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREBALL);
                        double yaw = Math.toRadians(player.getLocation().getYaw() + 90);
                        //fireball.setVelocity(new Vector(Math.cos(yaw), Math.sin(player.getLocation().getPitch()), Math.sin(yaw)));
                        fireball.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());
                        fireball.teleport(fireball.getLocation().clone().add(new Vector(Math.cos(yaw), Math.sin(Math.toRadians(player.getLocation().getPitch()))*-1, Math.sin(yaw)).multiply(2)));
                        fireball.setYield(3);
                    }
                    state = 2;
                break;
            }
        }
    }

    @EventHandler
    private void spawnSaturnRings(PlayerSwapHandItemsEvent event)
    {
        if(player != event.getPlayer())
            return;

        if(!checkOrbit(player, 3))
            return;

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

    @EventHandler
    public void cancelFall(EntityDamageEvent event)
    {
        if(event.getCause() != DamageCause.FALL || event.getEntityType() != EntityType.PLAYER)
            return;

        if(state != 0)
        {
            event.setCancelled(true);
        }
    }
}
