package com.rooxchicken.orbit.Tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
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
import com.rooxchicken.orbit.Orbits.AstroOrbit;

public class AstroOrbit_Dagger extends Task
{
    private Player player;
    private Location start;
    private ArrayList<ItemDisplay> daggers;
    private ItemDisplay currentDagger;

    private int t = 0;

    public AstroOrbit_Dagger(Orbit _plugin, Player _player)
    {
        super(_plugin);
        tickThreshold = 1;

        player = _player;
        start = player.getEyeLocation().clone();
        daggers = new ArrayList<ItemDisplay>();

        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        item.addEnchantment(Enchantment.DURABILITY, 1);

        Location spawn = start.clone();
        spawn.setYaw(spawn.getYaw() - 45);
        spawn.setPitch(start.getPitch() + 90);

        double rad = Math.toRadians(start.getYaw());
        double xOff = Math.cos(rad);
        double zOff = Math.sin(rad);

        for(int i = 0; i < 3; i++)
        {
            Location dag = spawn.clone();
            if(i == 0)
                dag.add(xOff, 0, zOff);
            if(i == 1)
                dag.add(-xOff, 0, -zOff);
            if(i == 2)
                dag.add(0, 1, 0);
            ItemDisplay dagger = (ItemDisplay)player.getWorld().spawnEntity(dag, EntityType.ITEM_DISPLAY);
            dagger.setItemStack(item);
            daggers.add(dagger);

            dagger.getWorld().spawnParticle(Particle.REDSTONE, dagger.getLocation(), 25, 0.1, 0.1, 0.1, new Particle.DustOptions(Color.GRAY, 1f));
        }

        currentDagger = daggers.get(0);
    }

    @Override
    public void run()
    {
        if(++t < 10)
            return;

        currentDagger.teleport(currentDagger.getLocation().add(player.getLocation().getDirection()));
        for(Object o : Orbit.getNearbyEntities(currentDagger.getLocation(), 1))
        {
            if(o instanceof LivingEntity && o != player)
            {
                ((LivingEntity)o).damage(18);
                currentDagger.getWorld().playSound(currentDagger.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                killDagger();
            }
        }

        if(!currentDagger.getLocation().getBlock().isPassable())
        {
            killDagger();
        }

        if(t > 200 || currentDagger == null)
        {
            cancel = true;
        }
    }

    @Override
    public void onCancel()
    {
        for(ItemDisplay item : daggers)
            item.remove();
    }

    private void killDagger()
    {
        currentDagger.getWorld().playSound(currentDagger.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        daggers.remove(currentDagger);
        currentDagger.remove();

        if(daggers.size() == 0)
        {
            cancel = true;
            return;
        }
        currentDagger = daggers.get(0);

        t = 0;
    }
}
