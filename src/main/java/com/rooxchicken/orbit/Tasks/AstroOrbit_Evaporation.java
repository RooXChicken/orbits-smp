package com.rooxchicken.orbit.Tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.rooxchicken.orbit.Orbit;
import com.rooxchicken.orbit.Common.Sphere;

public class AstroOrbit_Evaporation extends Task
{
    private Player player;
    private ProtocolManager protocolManager;
    private PacketContainer packet;

    private PacketAdapter packetAdapter;

    private int t = 0;

    public AstroOrbit_Evaporation(Orbit _plugin, Player _player)
    {
        super(_plugin);
        tickThreshold = 1;

        player = _player;
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 15*20, 0));

        protocolManager = ProtocolLibrary.getProtocolManager();

        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, player.getEntityId());
        
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemStack(Material.AIR)));
        list.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, new ItemStack(Material.AIR)));
        list.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, new ItemStack(Material.AIR)));
        list.add(new Pair<>(EnumWrappers.ItemSlot.FEET, new ItemStack(Material.AIR)));
        list.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, new ItemStack(Material.AIR)));
        list.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, new ItemStack(Material.AIR)));
        packet.getSlotStackPairLists().write(0, list);

        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(p != player)
                protocolManager.sendServerPacket(p, packet);
        }

        packetAdapter = new PacketAdapter(_plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT)
        {
            @Override
            public void onPacketSending(PacketEvent event)
            {
                if(event.getPacket().getIntegers().read(0) == player.getEntityId())
                    event.setCancelled(true);
            }
        };

        protocolManager.addPacketListener(packetAdapter);

        player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().clone().add(0, 1, 0), 50, 0.4, 0.4, 0.4, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 2);
    }

    @Override
    public void run()
    {
        // for(Player p : Bukkit.getOnlinePlayers())
        // {
        //     protocolManager.sendServerPacket(p, packet);
        // }

        if(++t > 15*20)
        {
            cancel = true;
        }
    }

    @Override
    public void onCancel()
    {
        protocolManager.removePacketListener(packetAdapter);

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, player.getInventory().getHelmet()));
        list.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, player.getInventory().getChestplate()));
        list.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, player.getInventory().getLeggings()));
        list.add(new Pair<>(EnumWrappers.ItemSlot.FEET, player.getInventory().getBoots()));
        list.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, player.getInventory().getItemInMainHand()));
        list.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, player.getInventory().getItemInOffHand()));
        packet.getSlotStackPairLists().write(0, list);

        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(p != player)
                protocolManager.sendServerPacket(p, packet);
        }
    }
}
