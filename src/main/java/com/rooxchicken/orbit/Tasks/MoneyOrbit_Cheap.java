package com.rooxchicken.orbit.Tasks;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;
import com.rooxchicken.orbit.Orbit;
import com.rooxchicken.orbit.Common.Sphere;

public class MoneyOrbit_Cheap extends Task implements Listener
{
    private Player player;
    List<MerchantRecipe> villagerRecipe;

    private int t = 0;

    public MoneyOrbit_Cheap(Orbit _plugin, Player _player)
    {
        super(_plugin);
        tickThreshold = 1;

        player = _player;

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 1);
        player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(0, 1, 0), 75, 0.4, 0.4, 0.4, new Particle.DustOptions(Color.LIME, 1f));
    }

    @Override
    public void run()
    {
        if(++t > 600)
            cancel = true;
    }

    @EventHandler
    public void makeCheap(PlayerInteractEntityEvent event)
    {
        if(event.getPlayer() != player || !(event.getRightClicked() instanceof Villager))
            return;

        Villager villager = (Villager)event.getRightClicked();
        villagerRecipe = villager.getRecipes();

        for(MerchantRecipe recipe : villager.getRecipes())
        {
            //recipe.setPriceMultiplier(0);
            recipe.setSpecialPrice(-100000);
        }
    }

    @Override
    public void onCancel()
    {
        HandlerList.unregisterAll(this);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
        player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(0, 1, 0), 75, 0.4, 0.4, 0.4, new Particle.DustOptions(Color.GREEN, 1f));
    }
}
