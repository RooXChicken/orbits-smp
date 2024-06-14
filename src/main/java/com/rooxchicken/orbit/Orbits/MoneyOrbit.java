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
import com.rooxchicken.orbit.Tasks.MoneyOrbit_Cheap;
import com.rooxchicken.orbit.Tasks.PowerOrbit_Cookout;

import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.IMaterial;

public class MoneyOrbit extends BaseOrbit
{
    private Orbit plugin;
    private Player player;
    
    public MoneyOrbit(Orbit _plugin, Player _player)
    {
        super(_plugin);
        plugin = _plugin;
        player = _player;

        itemName = "§a§lMoney Orbit";

        item = new ItemStack(Material.GREEN_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);
        item.setItemMeta(meta);

        cooldown1Max = 350 * 20;
        cooldown2Max = 500 * 20;

        cooldown1Key = new NamespacedKey(plugin, "money_cd1");
        cooldown2Key = new NamespacedKey(plugin, "money_cd2");

        checkHasCooldown(player, cooldown1Key, cooldown2Key);
    }

    @Override
    public void tick()
    {
        if(!checkOrbit(player, 4))
            return;
            
        ItemStack item = player.getInventory().getItemInOffHand();
        if(checkItem(item))
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 21, 0));
        }
    }

    @EventHandler
    private void useCheapVillagers(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(player != event.getPlayer())
            return;

        if(!checkOrbit(player, 4))
            return;
        
        ItemStack item = event.getItem();

        if(checkItem(item) && checkCooldown(player, cooldown1Key, cooldown1Max))
        {
            MoneyOrbit_Cheap cheap = new MoneyOrbit_Cheap(plugin, player);
            Bukkit.getPluginManager().registerEvents(cheap, plugin);
            Orbit.tasks.add(cheap);
        }
    }

    @EventHandler
    private void useCopy(PlayerSwapHandItemsEvent event)
    {
        if(event.getPlayer() != player)
            return;

        if(!checkOrbit(player, 4))
            return;
            
        ItemStack item = event.getMainHandItem();

        if(!player.isSneaking())
            return;

        //Bukkit.getLogger().info(item.getItemMeta().getDisplayName());

        if(checkItem(item) && checkCooldown(player, cooldown2Key, cooldown2Max))
        {
            activateAbility2();

            event.setCancelled(true);
        }
    }

    public void activateAbility2()
    {
        if(!checkKills(player))
            return;
            
        int steal = (int)(Math.random() * 4);
        int ability = (int)(Math.random() * 2);

        if(steal == 0)
            PowerOrbit.activateAbility1(player, plugin);

        if(steal == 1)
        {
            if(ability == 0)
                AstroOrbit.activateAbility1(player, plugin);
            if(ability == 1)
                AstroOrbit.activateAbility1(player, plugin); //make two when done
        }

        if(steal == 2)
        {
            if(ability == 0)
                VoidOrbit.activateAbility1(player, plugin);
            if(ability == 1)
                VoidOrbit.activateAbility2(player, plugin); //make two when done
        }

        if(steal == 3)
        {
            SolarOrbit.activateAbility2(player, plugin);
        }
        
    }
}
