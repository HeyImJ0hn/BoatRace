package me.GuitarXpress.BoatRace;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Events implements Listener {

	Main plugin;
	GameManager gm;

	public Events(Main plugin) {
		this.plugin = plugin;
		gm = new GameManager(plugin);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (Commands.pArena.get(p.getUniqueId()) != null) {
			Bukkit.dispatchCommand(p, "boatrace leave");
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		String arenaName = Commands.pArena.get(p.getUniqueId());
		Arena arena = ArenaManager.getArena(arenaName);
		if (Commands.pArena.get(p.getUniqueId()) != null) {
			if (p.getWorld().equals(arena.getSpawns().get(0).getWorld())) {
				if (p.getLocation().clone().subtract(0, 2, 0).getBlock().getType() == GameManager.CHECKPOINTONE
						&& !GameManager.chkpt1.get(p.getUniqueId())) {
					GameManager.chkpt1.put(p.getUniqueId(), true);
					return;
				}
				if (p.getLocation().clone().subtract(0, 2, 0).getBlock().getType() == GameManager.CHECKPOINTTWO
						&& GameManager.chkpt1.get(p.getUniqueId()) && !GameManager.chkpt2.get(p.getUniqueId())) {
					GameManager.chkpt2.put(p.getUniqueId(), true);
					return;
				}
				if (p.getLocation().clone().subtract(0, 2, 0).getBlock().getType() == GameManager.FINISHLINE) {
					if (GameManager.chkpt1.get(p.getUniqueId()) && !GameManager.chkpt2.get(p.getUniqueId())) {
						p.sendMessage(prefix() + "§cYou're going the wrong way!");
						GameManager.chkpt1.put(p.getUniqueId(), false);
					}
					if (GameManager.chkpt1.get(p.getUniqueId()) && GameManager.chkpt2.get(p.getUniqueId())) {
						int lap = GameManager.playerLap.get(p.getUniqueId());
						GameManager.playerLap.put(p.getUniqueId(), lap + 1);
						GameManager.chkpt1.put(p.getUniqueId(), false);
						GameManager.chkpt2.put(p.getUniqueId(), false);
						if (GameManager.playerLap.get(p.getUniqueId()) == arena.getLaps()) {
							p.sendTitle("Final Lap!", null, gm.fadeIn, gm.stay, gm.fadeOut);
						} else {
							p.sendTitle("Lap " + GameManager.playerLap.get(p.getUniqueId()), null, gm.fadeIn, gm.stay,
									gm.fadeOut);
						}
						if (GameManager.playerLap.get(p.getUniqueId()) > arena.getLaps()) {
							arena.addScore(p);
							p.getVehicle().remove();
							p.setGameMode(GameMode.SPECTATOR);
							p.sendTitle("Finished!", null, gm.fadeIn, 40, gm.fadeOut);
							p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
							if (arena.getScoreboard().size() == arena.getPlayers().size()) {
								gm.endGame(arenaName);
							}
							return;
						}
					}
					return;
				}
			}
			if (p.getGameMode() == GameMode.SPECTATOR) {
				if (!playerInArea(arena.getCorner1(), arena.getCorner2(), p)) {
//					p.teleport(arena.getSpawns().get(0));
					p.teleport(event.getFrom());
					return;
				}
			}
		}
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		ItemStack item = (ItemStack) event.getItem();
		Player p = (Player) event.getPlayer();

		if (event.getItem() == null)
			return;

		if (!item.hasItemMeta())
			return;

		if (!item.getItemMeta().hasLore())
			return;

		if (!item.getItemMeta().getLore().contains("§9BoatRace"))
			return;

		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (item.getItemMeta().getLore().get(0).equals("§7Right click on each corner to set boundaries.")) {
				event.setCancelled(true);
				return;
			}
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (item.getItemMeta().getLore().get(3).equals("§eCorner 1")) {
				Location loc = event.getClickedBlock().getLocation();
				String s = item.getItemMeta().getLore().get(2);
				s = s.substring(2, s.length());
				Arena arena = ArenaManager.getArena(s);
				arena.setCorner1(loc);
				p.sendMessage(prefix() + "§eSet first corner.");
				p.sendMessage(prefix() + "§eNow setting second corner.");

				ItemMeta meta = item.getItemMeta();
				List<String> lore = item.getItemMeta().getLore();
				lore.set(3, "§eCorner 2");
				meta.setLore(lore);
				item.setItemMeta(meta);
				return;
			}
			if (item.getItemMeta().getLore().get(3).equals("§eCorner 2")) {
				Location loc = event.getClickedBlock().getLocation();
				String s = item.getItemMeta().getLore().get(2);
				s = s.substring(2, s.length());
				Arena arena = ArenaManager.getArena(s);
				arena.setCorner2(loc);
				p.sendMessage(prefix() + "§eSet second corner.");
				p.sendMessage(prefix() + "§eFinished setting track boundaries.");
				p.getInventory().remove(event.getItem());
				if (arena.getSpawns() != null) {
					arena.setStatus(STATUS.JOINABLE);
				}
				return;
			}
		}
	}

	public String prefix() {
		return "§8[§bBoatRace§8]: ";
	}

	public boolean playerInArea(Location start, Location end, Player player) {

		int topBlockX = (start.getBlockX() < end.getBlockX() ? end.getBlockX() : start.getBlockX());
		int bottomBlockX = (start.getBlockX() > end.getBlockX() ? end.getBlockX() : start.getBlockX());

		int topBlockY = (start.getBlockY() < end.getBlockY() ? end.getBlockY() : start.getBlockY());
		int bottomBlockY = (start.getBlockY() > end.getBlockY() ? end.getBlockY() : start.getBlockY());

		int topBlockZ = (start.getBlockZ() < end.getBlockZ() ? end.getBlockZ() : start.getBlockZ());
		int bottomBlockZ = (start.getBlockZ() > end.getBlockZ() ? end.getBlockZ() : start.getBlockZ());

		double x = player.getLocation().getX();
		double y = player.getLocation().getY();
		double z = player.getLocation().getZ();

		if (x >= bottomBlockX && x <= topBlockX && y >= bottomBlockY && y <= topBlockY && z >= bottomBlockZ
				&& z <= topBlockZ) {
			return true;
		}
		return false;
	}

}
