package com.rooxchicken.orbit.Tasks;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rooxchicken.orbit.Orbit;
import com.rooxchicken.orbit.Common.Sphere;

public class PowerOrbit_Cookout extends Task
{
    private Player player;
    private Location start;
    private Sphere sphere;

    private double size = 0.4;
    private int count = 25;

    private int t = 0;

    public PowerOrbit_Cookout(Orbit _plugin, Location _start, Player _player)
    {
        super(_plugin);
        tickThreshold = 1;

        start = _start.clone();
        player = _player;
        
        sphere = new Sphere(new Color[] {Color.RED}, count);
    }

    @Override
    public void run()
    {
        if(size < 10)
            size += 0.7;
        else
            cancel = true;

        for(Object o : Orbit.getNearbyEntities(start, (int)Math.ceil(size)))
        {
            if(o instanceof Player && o != player)
            {
                ((Player)o).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0));
                ((Player)o).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0));
            }
        }
        
        sphere.run(start, size, count, 1, 0.2);
    }
}
