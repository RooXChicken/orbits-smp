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

public class AstroOrbit extends BaseOrbit
{
    public NamespacedKey cooldown1Key;
    public NamespacedKey cooldown2Key;

    public int cooldown1Max = 200 * 20;
    public int cooldown2Max = 250 * 20;
    
    private Orbit plugin;

    public AstroOrbit(Orbit _plugin)
    {
        super(_plugin);
        plugin = _plugin;

        itemName = "§c§lPower Orbit";

        cooldown1Key = new NamespacedKey(plugin, "astro_cd1");
        cooldown2Key = new NamespacedKey(plugin, "astro_cd2");
    }

    // @Override
    // public void tick()
    // {
    //     for(Player player : Bukkit.getOnlinePlayers())
    //     {
    //         ItemStack item = player.getInventory().getItemInOffHand();
    //         if(checkItem(item))
    //         {
    //             player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 21, 0));
    //         }
    //     }
    // }

    @EventHandler
    private void useTrueInvisibility(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
            
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

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
