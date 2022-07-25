package me.guitarxpress.boatrace.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.guitarxpress.boatrace.BoatRace;
import me.guitarxpress.boatrace.managers.GameManager;

public class PlayerQuit implements Listener {
	
	private GameManager gm;
	
	public PlayerQuit(BoatRace plugin) {
		gm = plugin.getGameManager();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (gm.isInArena(p)) {
			Bukkit.dispatchCommand(p, "boatrace leave");
		}
	}

}
