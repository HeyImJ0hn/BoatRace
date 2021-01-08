package me.GuitarXpress.BoatRace;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Signs implements Listener {

	Main plugin;

	static List<Location> signsLoc = new ArrayList<Location>();

	public Signs(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		if (event.getPlayer().hasPermission("br.admin")) {
			if (event.getBlock().getType() == Material.OAK_WALL_SIGN) {
				if (!event.getLine(0).equals("[boatrace]"))
					return;

				if (event.getLine(1).isEmpty()) {
					if (event.getLine(2).equals("leave")) {
						event.setLine(0, "§8[§bBoatRace§8]");
						event.setLine(2, "§cLeave Queue");
						return;
					}
				}
				if (ArenaManager.exists(event.getLine(1))) {
					String s = event.getLine(1);
					World world = event.getBlock().getWorld();
					int x = event.getBlock().getX();
					int y = event.getBlock().getY();
					int z = event.getBlock().getZ();
					Location loc = new Location(world, x, y, z);
					signsLoc.add(loc);
					int id = ArenaManager.aName.indexOf(s);
					startSignUpdates((Sign) event.getBlock().getState(), s, id);
				} else {
					event.getPlayer().sendMessage("§8[§bBoatRace§8]: §cInvalid Track.");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
				}
			}
		} else {
			event.getPlayer().sendMessage("§8[§bBoatRace§8]: §cYou don't have permission to create game signs.");
		}
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		if (event.getBlock().getState() instanceof Sign) {
			Sign sign = (Sign) event.getBlock().getState();
			if (signsLoc.contains(sign.getLocation())) {
				if (event.getPlayer().hasPermission("br.signs")) {
					signsLoc.remove(sign.getLocation());
					String s = sign.getLine(1);
					if (s.length() > 2) {
						s = s.substring(2, s.length()); // Remove "§6" from the line in order to get track name
					}
					int id = ArenaManager.aName.indexOf(s);
					Bukkit.getScheduler().cancelTask(id);
				} else {
					event.getPlayer().sendMessage("§8[§bBoatRace§8]: §cSorry! You can't break these signs.");
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() != Material.OAK_WALL_SIGN)
				return;

			Sign sign = (Sign) event.getClickedBlock().getState();

			if (!sign.getLine(0).equals("§8[§bBoatRace§8]"))
				return;

			if (sign.getLine(1).isEmpty()) {
				if (sign.getLine(2).equals("§cLeave Queue")) {
					Bukkit.dispatchCommand(event.getPlayer(), "boatrace leave");
					return;
				}
			}

			String s = sign.getLine(1);
			if (s.length() > 2) {
				s = s.substring(2, s.length()); // Remove "§6" from the line in order to get track name
			}

			if (ArenaManager.exists(s)) {
				if (ArenaManager.getArena(s).getStatus() == STATUS.JOINABLE
						|| ArenaManager.getArena(s).getStatus() == STATUS.STARTING) {
					Bukkit.dispatchCommand(event.getPlayer(), "boatrace join " + s);
				}
				int id = ArenaManager.aName.indexOf(s);
				startSignUpdates(sign, s, id);
			}
		}
	}

	public void startSignUpdates(Sign sign, String arena, int id) {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (!signsLoc.contains(sign.getLocation()))
					return;
				
				if (!ArenaManager.getArena(arena).getPlayers().isEmpty()) {
					sign.setLine(0, "§8[§bBoatRace§8]");
					sign.setLine(1, "§6" + arena);
					sign.setLine(2, "§e" + ArenaManager.getArena(arena).getPlayers().size() + "/4");
					switch (ArenaManager.getArena(arena).getStatus()) {
					case SETTING_UP:
						sign.setLine(3, "§6SETTING UP");
						break;
					case STARTING:
						sign.setLine(3, "§aSTARTING");
						break;
					case JOINABLE:
						sign.setLine(3, "§aJOINABLE");
						break;
					case ONGOING:
						sign.setLine(3, "§6ONGOING");
						break;
					case STARTUP:
						sign.setLine(3, "§6ONGOING");
						break;
					case CANCELLED:
						sign.setLine(3, "§cCANCELLED");
						break;
					case ENDED:
						sign.setLine(3, "§cENDED");
						break;
					case UNAVAILABLE:
						sign.setLine(3, "§cUNAVAILABLE");
						break;
					default:
						sign.setLine(3, "§cCONTACT ADMIN");
						break;
					}
					sign.update();
				} else {
					sign.setLine(0, "§8[§bBoatRace§8]");
					sign.setLine(1, "§6" + arena);
					sign.setLine(2, "§e0/4");
					switch (ArenaManager.getArena(arena).getStatus()) {
					case SETTING_UP:
						sign.setLine(3, "§6SETTING UP");
						break;
					case STARTING:
						sign.setLine(3, "§aSTARTING");
						break;
					case JOINABLE:
						sign.setLine(3, "§aJOINABLE");
						break;
					case ONGOING:
						sign.setLine(3, "§6ONGOING");
						break;
					case STARTUP:
						sign.setLine(3, "§6ONGOING");
						break;
					case CANCELLED:
						sign.setLine(3, "§cCANCELLED");
						break;
					case ENDED:
						sign.setLine(3, "§cENDED");
						break;
					case UNAVAILABLE:
						sign.setLine(3, "§cUNAVAILABLE");
						break;
					default:
						sign.setLine(3, "§cCONTACT ADMIN");
						break;
					}
					sign.update();
				}
			}
		}, 0, 5);
	}

}
