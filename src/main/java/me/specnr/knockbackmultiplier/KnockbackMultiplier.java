package me.specnr.knockbackmultiplier;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Timer;
import java.util.TimerTask;

public final class KnockbackMultiplier extends JavaPlugin implements Listener {

    public double multiplier = 1;
    public Timer timer = new Timer();

    class updateMultiplier extends TimerTask {
        public void run() {
            if (multiplier * getConfig().getDouble("Multiplier") > getConfig().getDouble("Limit")) {
                multiplier = getConfig().getDouble("Limit");
                timer.cancel();
                timer.purge();
            } else {
                multiplier *= getConfig().getDouble("Multiplier");
            }
            sendAll("Current Multiplier: " + multiplier);
        }
    }

    public void sendAll(String msg) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage("[§4§oKB§r] " + msg));
    }

    @EventHandler
    public void onDamageEvent(EntityDamageByEntityEvent e){
        Entity d = e.getDamager();
        Entity ent = e.getEntity();
        if (getConfig().getBoolean("PlayerOnly") && !(ent instanceof Player))
            return;
        ent.setVelocity(d.getLocation().getDirection().setY(0).normalize().multiply(multiplier));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("kb")) {
            timer.schedule(new updateMultiplier(), 0, (int) (getConfig().getDouble("Period") * 60000));
        }
        else if (command.getName().equals("kb-reset")) {
            timer.cancel();
            timer.purge();
            multiplier = 1;
        }
        return false;
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println("Knockback Multiplier Loaded");
    }

    @Override
    public void onDisable() {
        timer.cancel();
        timer.purge();
    }

}
