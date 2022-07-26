package me.guitarxpress.boatrace.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.guitarxpress.boatrace.Arena;
import me.guitarxpress.boatrace.Commands;
import me.guitarxpress.boatrace.enums.Status;
import me.guitarxpress.boatrace.managers.GameManager;
import me.guitarxpress.boatrace.BoatRace;
import me.guitarxpress.boatrace.utils.Utils;

public class PlayerMove implements Listener {

	private BoatRace plugin;
	private GameManager gm;

	public PlayerMove(BoatRace plugin) {
		this.plugin = plugin;
		gm = plugin.getGameManager();
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();

		if (!gm.isInArena(p))
			return;

		Arena arena = gm.getPlayerArena(p);
		if (p.getWorld().equals(arena.getSpawns().get(0).getWorld())) {
			if (arena.getStatus() == Status.STARTUP)
				event.setCancelled(true);

			if (arena.getStatus() == Status.ONGOING) {

				// Checkpoint 1
				if (p.getLocation().clone().subtract(0, 2, 0).getBlock().getType() == gm.getCHECKPOINTONE()
						&& !gm.getChkpt1().get(p.getUniqueId())) {
					gm.getChkpt1().put(p.getUniqueId(), true);

					// Checkpoint 2
				} else if (p.getLocation().clone().subtract(0, 2, 0).getBlock().getType() == gm.getCHECKPOINTTWO()
						&& gm.getChkpt1().get(p.getUniqueId()) && !gm.getChkpt2().get(p.getUniqueId())) {
					gm.getChkpt2().put(p.getUniqueId(), true);

					// Finish Line
				} else if (p.getLocation().clone().subtract(0, 2, 0).getBlock().getType() == gm.getFINISHLINE()) {

					// If player went the wrong way
					if (gm.getChkpt1().get(p.getUniqueId()) && !gm.getChkpt2().get(p.getUniqueId())) {
						p.sendMessage(Commands.prefix() + "§cYou're going the wrong way!");
						gm.getChkpt1().put(p.getUniqueId(), false);

						// If player successfully completed a lap
					} else if (gm.getChkpt1().get(p.getUniqueId()) && gm.getChkpt2().get(p.getUniqueId())) {
						int lap = gm.getPlayerLap().get(p.getUniqueId());
						gm.getPlayerLap().put(p.getUniqueId(), ++lap);
						gm.getChkpt1().put(p.getUniqueId(), false);
						gm.getChkpt2().put(p.getUniqueId(), false);

						for (UUID uuid : arena.getPlayers())
							gm.createScoreboard(Bukkit.getPlayer(uuid));

						if (gm.getPlayerLap().get(p.getUniqueId()) == arena.getLaps()) {
							p.sendTitle("Final Lap!", null, 2, 20, 2);
						} else {
							p.sendTitle("Lap " + gm.getPlayerLap().get(p.getUniqueId()), null, 2, 20, 2);
						}

						if (gm.getPlayerLap().get(p.getUniqueId()) > arena.getLaps()) {
							arena.addScore(p);
							for (UUID uuid : arena.getPlayers()) {
								gm.createScoreboard(p);
								Bukkit.getPlayer(uuid)
										.sendMessage(Commands.prefix() + p.getDisplayName() + " §efinished!");
							}

							p.getVehicle().remove();
							p.setGameMode(GameMode.SPECTATOR);
							p.sendTitle("Finished!", null, 2, 40, 2);
							p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
							if (!gm.endCountdown.containsKey(arena))
								endTimer(arena.getName());
							if (arena.getScoreboard().size() == arena.getPlayers().size()) {
								gm.endGame(arena.getName());
							}
						}
					}
				}
			}
		}
		if (p.getGameMode() == GameMode.SPECTATOR) {
			if (!Utils.playerInArea(arena.getCorner1(), arena.getCorner2(), p)) {
				p.teleport(arena.getSpawns().get(0));
//					p.teleport(event.getFrom()); // Very buggy
			}
		}
	}

	public void endTimer(String name) {
		Arena arena = gm.getArena(name);
		gm.endCountdown.put(arena, true);
		for (UUID uuid : arena.getPlayers())
			Bukkit.getPlayer(uuid).sendMessage(
					Commands.prefix() + "§eA Player has finished the race. You have §660 seconds §eto finish.");
		int task = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			if (arena.getStatus() == Status.ONGOING)
				gm.endGame(name);
		}, 60 * 20);
		gm.endCountdownTask.put(arena, task);
	}
}
