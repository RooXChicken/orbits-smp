package com.rooxchicken.orbit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.base.Predicate;
import com.rooxchicken.orbit.Commands.ResetCooldown;
import com.rooxchicken.orbit.Commands.SetOrbit;
import com.rooxchicken.orbit.Orbits.AstroOrbit;
import com.rooxchicken.orbit.Orbits.BaseOrbit;
import com.rooxchicken.orbit.Orbits.MoneyOrbit;
import com.rooxchicken.orbit.Orbits.PowerOrbit;
import com.rooxchicken.orbit.Orbits.SolarOrbit;
import com.rooxchicken.orbit.Orbits.VoidOrbit;
import com.rooxchicken.orbit.Tasks.CheckForOrbit;
import com.rooxchicken.orbit.Tasks.DisplayCooldown;
import com.rooxchicken.orbit.Tasks.MoneyOrbit_Cheap;
import com.rooxchicken.orbit.Tasks.Task;

import net.minecraft.world.entity.animal.EntityTropicalFish.Base;

public class Orbit extends JavaPlugin implements Listener
{
    public static NamespacedKey orbitKey;
    public static NamespacedKey killsKey;

    private ShapedRecipe rerollRecipe;

    public static ArrayList<Task> tasks;
    private List<String> blockedCommands = new ArrayList<>();

    private HashMap<Player, BaseOrbit> playerOrbitMap;

    @Override
    public void onEnable()
    {
        Bukkit.resetRecipes();
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);

        orbitKey = new NamespacedKey(this, "orbit");
        killsKey = new NamespacedKey(this, "kills");

        tasks = new ArrayList<Task>();
        tasks.add(new DisplayCooldown(this));
        tasks.add(new CheckForOrbit(this));
        
        playerOrbitMap = new HashMap<Player, BaseOrbit>();
        
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("setorbit").setExecutor(new SetOrbit(this));
        this.getCommand("resetcooldown").setExecutor(new ResetCooldown(this));

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            public void run()
            {
                for(BaseOrbit orbit : playerOrbitMap.values())
                    orbit.tick();
                
                ArrayList<Task> _tasks = new ArrayList<Task>();
                for(Task t : tasks)
                    _tasks.add(t);
                
                ArrayList<Task> toRemove = new ArrayList<Task>();

                for(Task t : _tasks)
                {
                    t.tick();

                    if(t.cancel)
                        toRemove.add(t);
                }

                for(Task t : toRemove)
                {
                    t.onCancel();
                    tasks.remove(t);
                }
            }
        }, 0, 1);

        for(Player player : Bukkit.getOnlinePlayers())
            addToList(player);

        {
            ItemStack item = new ItemStack(Material.WHITE_DYE);
    
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§f§lReroll Orbit");
            item.setItemMeta(meta);
    
            NamespacedKey key = new NamespacedKey(this, "rerollRecipe");
            rerollRecipe = new ShapedRecipe(key, item);
            rerollRecipe.shape("DxD", "xSx", "DxD");
    
            rerollRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
            rerollRecipe.setIngredient('x', Material.NETHERITE_SCRAP);
            rerollRecipe.setIngredient('S', Material.DIAMOND_SWORD);
    
            Bukkit.addRecipe(rerollRecipe);
            }

        getLogger().info("Orbiting since 1987 (made by roo)");
    }

    @EventHandler
    public void addPlayerOrbit(PlayerJoinEvent event)
    {
        addToList(event.getPlayer());
    }

    @EventHandler
    private void useRerollItem(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if(item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals("§f§lReroll Orbit"))
        {
            player.getInventory().remove(getOrbit(player).item);
            int oldOrbit = player.getPersistentDataContainer().get(orbitKey, PersistentDataType.INTEGER);
            int orbit = -1;
            while(orbit != -1 && orbit != oldOrbit)
            {
                orbit = (int)(Math.random()*5);
            }
            setOrbit(player, orbit);
            item.setAmount(item.getAmount() - 1);
            player.getInventory().addItem(getOrbit(player).item);

            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
            player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(0, 1, 0), 150, 0.4, 0.4, 0.4, new Particle.DustOptions(Color.WHITE, 1f));
        }
    }

    private void addToList(Player player)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(!data.has(orbitKey, PersistentDataType.INTEGER))
            data.set(orbitKey, PersistentDataType.INTEGER, (int)(Math.random()*5));

        if(!data.has(killsKey, PersistentDataType.INTEGER))
            data.set(killsKey, PersistentDataType.INTEGER, 0);
        
        if(!playerOrbitMap.containsKey(player))
        {
            playerOrbitMap.put(player, getOrbit(player, data.get(orbitKey, PersistentDataType.INTEGER)));
        }
    }

    public BaseOrbit getOrbit(Player player)
    {
        return playerOrbitMap.get(player);
    }

    private BaseOrbit getOrbit(Player player, int orbit)
    {
        switch(orbit)
        {
            case 0: return new PowerOrbit(this, player);
            case 1: return new AstroOrbit(this, player);
            case 2: return new VoidOrbit(this, player);
            case 3: return new SolarOrbit(this, player);
            case 4: return new MoneyOrbit(this, player);
        }

        return null;
    }

    public void setOrbit(Player player, int orbit)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        BaseOrbit baseOrbit = getOrbit(player);

        data.set(baseOrbit.cooldown1Key, PersistentDataType.INTEGER, 0);
        if(baseOrbit.cooldown2Max != -1)
            data.set(baseOrbit.cooldown2Key, PersistentDataType.INTEGER, 0);
        
        playerOrbitMap.remove(player);
        if(orbit != -1)
            data.set(orbitKey, PersistentDataType.INTEGER, orbit);
        else
            data.remove(orbitKey);

        addToList(player);
    }

    @EventHandler
    public void addToKills(PlayerDeathEvent event)
    {
        Entity killer = event.getEntity().getKiller();
        if(killer != null)
            killer.getPersistentDataContainer().set(killsKey, PersistentDataType.INTEGER, killer.getPersistentDataContainer().get(killsKey, PersistentDataType.INTEGER) + 1);

        event.getDrops().remove(getOrbit(event.getEntity()).item);
        //Bukkit.getLogger().info(killer.getName() + killer.getPersistentDataContainer().get(killsKey, PersistentDataType.INTEGER));
    }

    @EventHandler
    public void preventDropping(PlayerDropItemEvent event)
    {
        BaseOrbit orbit = getOrbit(event.getPlayer());
        if(event.getItemDrop().getItemStack().equals(orbit.item))
            event.setCancelled(true);
    }

    @EventHandler
	private void onPlayerTab(PlayerCommandSendEvent e)
    {
		e.getCommands().removeAll(blockedCommands);
	}

    public static Entity getTarget(Player player, int range)
    {
        Predicate<Entity> p = new Predicate<Entity>() {

            @Override
            public boolean apply(Entity input)
            {
                return(input != player);
            }
            
        };
        RayTraceResult ray = player.getWorld().rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, p);
        
        if(ray != null)
            return ray.getHitEntity();
        else
            return null;
    }

    public static Block getBlock(Player player, int range)
    {
        return getBlock(player, range, player.getLocation().getPitch());
    }

    public static Block getBlock(Player player, int range, float pitch)
    {
        Predicate<Entity> p = new Predicate<Entity>() {

            @Override
            public boolean apply(Entity input)
            {
                return(input != player);
            }
            
        };

        Location dir = player.getLocation().clone();
        dir.setPitch(pitch);

        RayTraceResult ray = player.getWorld().rayTrace(player.getEyeLocation(), dir.getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, p);
        
        if(ray != null)
            return ray.getHitBlock();
        else
            return null;
    }

    public static List<Block> getSphereBlocks(Location location, int radius)
    {
        List<Block> blocks = new ArrayList<>();

        int bx = location.getBlockX();
        int by = location.getBlockY();
        int bz = location.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++)
        {
            for (int y = by - radius; y <= by + radius; y++)
            {
                for (int z = bz - radius; z <= bz + radius; z++)
                {
                    double distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y));
                    if (distance < radius * radius && (distance < (radius - 1) * (radius - 1)))
                    {
                        Block block = new Location(location.getWorld(), x, y, z).getBlock();
                        if(block.getType() != Material.AIR && block.getType() != Material.BEDROCK)
                            blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }

    public static Object[] getNearbyEntities(Location where, int range)
    {
        return where.getWorld().getNearbyEntities(where, range, range, range).toArray();
    }

    public static double ClampD(double v, double min, double max)
    {
        if(v < min)
            return min;
        else if(v > max)
            return max;
        else
            return v;
    }
}
