package me.christiancompiles.homingarrow;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.Collection;

public final class HomingArrow extends JavaPlugin implements Listener{
    @Override
    public void onEnable() {
        // Plugin startup logic

        //getServer().getPluginManager().registerEvents(new XPBottleBreakListener());
        System.out.println("The plugin has started");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void EntityShootBowEvent(EntityShootBowEvent event){

        if (event.getEntityType() == EntityType.PLAYER) {

            Player player = (Player) event.getEntity();
            Location location = player.getEyeLocation();
            double distanceAway = Double.MAX_VALUE;
            Entity entityClosest = null;

            Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 80, 80, 80);

            for (Entity entity: nearbyEntities)
            {
                if (entity.getLocation().getY() < location.getY() - 3 || !(entity instanceof Mob || entity instanceof Player) || entity == player)
                    continue;

                double thisDistance = location.distanceSquared(entity.getLocation());

                if (thisDistance < distanceAway) {
                    entityClosest = entity;
                    distanceAway = thisDistance;
                }
            }
            if (entityClosest == null)
                return;

            if (event.getProjectile().getType() == EntityType.ARROW) {
                Arrow a = (Arrow) event.getProjectile();
                final Entity eClosest = entityClosest;

                new BukkitRunnable(){
                    Vector v;
                    @Override
                    public void run() {

                        if (eClosest.isDead() || !a.isValid() || a.isInBlock()) {
                            cancel();
                            return;
                        }

                        v = eClosest.getLocation().subtract(a.getLocation()).toVector();

                        v.normalize();

                        if (!eClosest.isOnGround()){
                            v.multiply(5);
                        }
                        a.setVelocity(v);
                    }
                }.runTaskTimer(this, 0, 2);
            }
        }
    }
}
