package me.guitarxpress.boatrace.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

import me.guitarxpress.boatrace.BoatRace;
import me.guitarxpress.boatrace.managers.GameManager;

public class VehicleExit implements Listener {
	
	private BoatRace plugin;
	private GameManager gm;
	
	public VehicleExit(BoatRace plugin) {
		this.plugin = plugin;
		gm = plugin.getGameManager();
	}
	
	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if (!(event.getExited() instanceof Player))
			return;
		
		Player p = (Player) event.getExited();
		if (gm.isInArena(p)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				event.getVehicle().addPassenger(p);
			}, 5);
		}
	}
}
