package com.rooxchicken.orbit.Tasks;

import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rooxchicken.orbit.Orbit;
import com.rooxchicken.orbit.Common.Sphere;

import net.minecraft.world.entity.animal.EntityBee.f;

public class SolarOrbit_Rings extends Task
{
    private Player player;
    private Location start;

    private double size = 1.5;
    private int count = 90;

    private int t = 0;
    
    private double[] cacheX;
    private double[] cacheZ;

    private HashMap<Player, Location> freezeMap;

    public SolarOrbit_Rings(Orbit _plugin, Player _player)
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

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
    }

    @Override
    public void run()
    {
        for(int i = 0; i < count; i++)
        {
            double xOffset = cacheX[i] * size;
            double zOffset = cacheZ[i] * size;
            player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(xOffset, 0.6, zOffset), 1, 0, 0, 0, new Particle.DustOptions(Color.GRAY, 1f));
            player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(xOffset*0.9, 0.6, zOffset*0.9), 1, 0, 0, 0, new Particle.DustOptions(Color.SILVER, 1f));
            player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(xOffset*0.8, 0.6, zOffset*0.8), 1, 0, 0, 0, new Particle.DustOptions(Color.ORANGE, 1f));
        }
    
        if(++t > 100)
            cancel = true;
    }
}
