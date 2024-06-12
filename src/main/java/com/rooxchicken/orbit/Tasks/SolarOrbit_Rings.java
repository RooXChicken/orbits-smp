package com.rooxchicken.orbit.Tasks;

import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Location;
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
    private Sphere sphere;

    private double size = 0.4;
    private int count = 25;

    private int t = 0;

    private HashMap<Player, Location> freezeMap;

    public SolarOrbit_Rings(Orbit _plugin, Player _player)
    {
        super(_plugin);
        tickThreshold = 1;

        player = _player;
        freezeMap = new HashMap<Player, Location>();
        
        sphere = new Sphere(new Color[] {Color.PURPLE}, count);
    }

    @Override
    public void run()
    {
        for(Object o : Orbit.getNearbyEntities(start, 8))
        {
            if(o instanceof Player && o != player)
            {
                Player p = (Player)o;
                if(!freezeMap.containsKey(p))
                    freezeMap.put(p, p.getLocation().clone());
                
                p.teleport(freezeMap.get(p));
            }
        }
        
        sphere.run(start, 8, count, 1, 0.4);

        if(++t > 100)
            cancel = true;
    }
}
