package com.rooxchicken.orbit.Tasks;

import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rooxchicken.orbit.Orbit;
import com.rooxchicken.orbit.Common.Sphere;

public class VoidOrbit_Freeze extends Task
{
    private Player player;
    private Sphere sphere;

    private double size = 0.4;
    private int count = 25;

    private int t = 0;

    private HashMap<Player, Location> freezeMap;

    public VoidOrbit_Freeze(Orbit _plugin, Player _player)
    {
        super(_plugin);
        tickThreshold = 1;

        player = _player;
        freezeMap = new HashMap<Player, Location>();
        
        sphere = new Sphere(new Color[] {Color.PURPLE}, count);
        sphere.run(player.getLocation(), 8, count, 1, 0.4);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);

    }

    @Override
    public void run()
    {
        for(Object o : Orbit.getNearbyEntities(player.getLocation(), 8))
        {
            if(o instanceof Player && o != player)
            {
                Player p = (Player)o;
                if(!freezeMap.containsKey(p))
                    freezeMap.put(p, p.getLocation().clone());
                
                p.teleport(freezeMap.get(p));
                p.getWorld().spawnParticle(Particle.REDSTONE, p.getLocation().clone().add(0, 1, 0), 15, 0.4, 0.4, 0.4, new Particle.DustOptions(Color.BLACK, 1));
            }
        }

        if(++t > 100)
            cancel = true;
    }

    @Override
    public void onCancel()
    {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
    }
}
