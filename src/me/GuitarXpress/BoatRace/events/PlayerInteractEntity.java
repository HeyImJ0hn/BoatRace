package me.guitarxpress.boatrace.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import me.guitarxpress.boatrace.BoatRace;
import me.guitarxpress.boatrace.managers.GameManager;

public class PlayerInteractEntity implements Listener {

	private GameManager gm;
	
	public PlayerInteractEntity(BoatRace plugin) {
		gm = plugin.getGameManager();
	}
	
	@EventHandler
	public void onInteraction(PlayerInteractEntityEvent event) {
		if (gm.isInArena(event.getPlayer()))
			event.setCancelled(true);
	}

}
