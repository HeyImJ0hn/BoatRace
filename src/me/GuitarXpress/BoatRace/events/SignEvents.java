package me.guitarxpress.boatrace.events;

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

import me.guitarxpress.boatrace.Arena;
import me.guitarxpress.boatrace.BoatRace;
import me.guitarxpress.boatrace.Commands;
import me.guitarxpress.boatrace.enums.Status;
import me.guitarxpress.boatrace.managers.GameManager;
import me.guitarxpress.boatrace.utils.Utils;

public class SignEvents implements Listener {

	private GameManager gm;

	public static List<Location> signsLoc;

	public SignEvents(BoatRace plugin) {
		signsLoc = new ArrayList<Location>();
		gm = plugin.getGameManager();
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		if (event.getPlayer().hasPermission("br.signs")) {
			if (event.getBlock().getType().toString().toLowerCase().contains("sign")) {
				if (!event.getLine(0).equals("[boatrace]") && !event.getLine(0).equals("[btr]"))
					return;

				if (event.getLine(1).isEmpty()) {
					if (event.getLine(2).equals("leave")) {
						event.setLine(0, "§7[§bBoatRace§7]");
						event.setLine(2, "§cLeave Queue");
						return;
					}
				}

				String aName = event.getLine(1);

				if (gm.exists(aName)) {
					World world = event.getBlock().getWorld();
					int x = event.getBlock().getX();
					int y = event.getBlock().getY();
					int z = event.getBlock().getZ();

					event.setLine(0, "§7[§bBoatRace§7]");
					event.setLine(1, "§6" + aName);
					event.setLine(2, "§e" + gm.getArena(aName).getPlayerCount() + "/4");

					Location loc = new Location(world, x, y, z);
					signsLoc.add(loc);
				} else {
					event.getPlayer().sendMessage("§7[§bBoatRace§7] §cInvalid Arena.");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
				}
			}
		}
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		if (event.getBlock().getState() instanceof Sign) {
			Sign sign = (Sign) event.getBlock().getState();
			if (signsLoc.contains(sign.getLocation())) {
				if (event.getPlayer().hasPermission("br.signs")) {
					signsLoc.remove(sign.getLocation());
				} else {
					event.getPlayer().sendMessage("§7[§bBoatRace§7] §cSorry! You can't break these signs.");
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

			if (!sign.getLine(0).equals("§7[§bBoatRace§7]"))
				return;

			if (sign.getLine(1).isEmpty()) {
				if (sign.getLine(2).equals("§cLeave Queue")) {
					Bukkit.dispatchCommand(event.getPlayer(), "btr leave");
					if (gm.isInArena(event.getPlayer()))
						updateSign(sign, gm, gm.getPlayerArena(event.getPlayer()).getName());
					return;
				}
			}
			
			String s = Utils.getNameFromString(sign.getLine(1));
			if (gm.exists(s)) {
				Arena arena = gm.getArena(s);
				if (arena.getStatus() == Status.JOINABLE || arena.getStatus() == Status.STARTING) {
					Bukkit.dispatchCommand(event.getPlayer(), "btr join " + s);
				} else if (arena.getStatus() == Status.ONGOING) {
					event.getPlayer().sendMessage(Commands.prefix() + "§cCan't join an ongoing race.");
//					gm.addSpectatorToArena(event.getPlayer(), arena); // TODO
//					event.getPlayer().sendMessage(Commands.prefix() + "§eTo leave use §6/btr spectate " + s + "§e.");
				} else if (arena.getStatus() == Status.SETTING_UP) {
					event.getPlayer().sendMessage(Commands.prefix() + "§cThis arena is being setup.");
				} else if (arena.getStatus() == Status.ENDED || arena.getStatus() == Status.CANCELLED) {
					event.getPlayer().sendMessage(Commands.prefix() + "§cThis arena is restarting.");
				}
				updateSign(sign, gm, s);
			}
		}
	}

	public static void updateSign(Sign sign, GameManager gm, String arena) {
		sign.setLine(0, "§7[§bBoatRace§7]");
		sign.setLine(1, "§6" + arena);
		sign.setLine(2, "§e" + gm.getArena(arena).getPlayerCount() + "/4");
		switch (gm.getArena(arena).getStatus()) {
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
		case CANCELLED:
			sign.setLine(3, "§cCANCELLED");
			break;
		case ENDED:
			sign.setLine(3, "§cENDED");
			break;
		case UNAVAILABLE:
			sign.setLine(3, "§cUNAVAILABLE");
			break;
		case STARTUP:
			sign.setLine(3, "§6STARTUP");
			break;
		default:
			sign.setLine(3, "§cCONTACT ADMIN");
			break;
		}
		sign.update();
	}
}
