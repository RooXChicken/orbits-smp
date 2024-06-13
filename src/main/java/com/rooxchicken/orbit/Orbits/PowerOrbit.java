package com.rooxchicken.orbit.Orbits;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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

import com.rooxchicken.orbit.Orbit;
import com.rooxchicken.orbit.Tasks.PowerOrbit_Cookout;

public class PowerOrbit extends BaseOrbit
{
    private Orbit plugin;
    private Player player;

    public NamespacedKey cooldown1Key;
    public int cooldown1Max = 500 * 20;
    //public NamespacedKey cooldown2Key;

    public PowerOrbit(Orbit _plugin, Player _player)
    {
        super(_plugin);
        plugin = _plugin;
        player = _player;

        itemName = "§c§lPower Orbit";

        cooldown1Key = new NamespacedKey(plugin, "power_cd1");
        //cooldown2Key = new NamespacedKey(plugin, "power_cd2");
    }

    @Override
    public void tick()
    {
        ItemStack item = player.getInventory().getItemInOffHand();
        if(checkItem(item))
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 21, 0));
        }
    }

    @EventHandler
    private void useOrbit(PlayerSwapHandItemsEvent event)
    {
        if(event.getPlayer() != player)
            return;
        
        ItemStack item = event.getMainHandItem();

        if(!player.isSneaking())
            return;

        //Bukkit.getLogger().info(item.getItemMeta().getDisplayName());

        if(checkItem(item))// && checkCooldown(player, cooldown1Key, cooldown1Max))
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2));
            Orbit.tasks.add(new PowerOrbit_Cookout(plugin, player.getLocation(), player));

            event.setCancelled(true);
        }
    }
}
