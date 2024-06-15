package com.rooxchicken.orbit.Tasks;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rooxchicken.orbit.Orbit;
import com.rooxchicken.orbit.Common.Sphere;

import net.minecraft.world.entity.animal.EntityBee.f;

public class VoidOrbit_Storm extends Task implements Listener
{
    private Player player;
    private Location start;

    private double size = 1.5;
    private int count = 90;

    private int t = 0;
    
    private double[] cacheX;
    private double[] cacheZ;

    private HashMap<Player, Location> freezeMap;

    public VoidOrbit_Storm(Orbit _plugin, Player _player)
    {
        super(_plugin);
        tickThreshold = 1;

        player = _player;
        freezeMap = new HashMap<Player, Location>();
        
        cacheX = new double[count];
        cacheZ = new double[count];

        for(int i = 0; i < count; i++)
        {
            double rad = Math.toRadians(i * (360.0/count));
            cacheX[i] = Math.sin(rad);
            cacheZ[i] = Math.cos(rad);
        }

        Bukkit.getPluginManager().registerEvents(this, _plugin);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_DEATH, 1, 1);
    }

    @Override
    public void run()
    {
        if(t == 8)
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, 1, 0.8f);
        if(t == 56)
        {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1, 1f);
            for(Object o : Orbit.getNearbyEntities(player.getLocation(), 6))
            {
                if(o instanceof Player && o != player)
                    ((Player)o).damage(10, player);
            }
        }

        double blockY = Orbit.getBlock(player, 40, 90).getLocation().getY() + 1.1;
        start = player.getEyeLocation().clone();
        start.setY(blockY);

        for(int i = 0; i < count; i++)
        {
            double xOffset = cacheX[i] * size;
            double zOffset = cacheZ[i] * size;
            player.getWorld().spawnParticle(Particle.REDSTONE, start.clone().add(xOffset, 0.3, zOffset), 8, 4, 0.2, 4, new Particle.DustOptions(Color.BLACK, 1f));
        }
    
        if(++t > 56)
            cancel = true;
    }

    @EventHandler
    public void trueDamage(EntityDamageByEntityEvent event)
    {
        if(!(event.getEntity() instanceof Player) || event.getDamager() != player)
            return;

        if(t == 56)
            event.setDamage(DamageModifier.ARMOR, 0);
    }
}
