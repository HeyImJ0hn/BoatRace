package me.GuitarXpress.BoatRace;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnsManager implements Listener {
	
	Main plugin;

	private List<Location> locations;
	
	public SpawnsManager(Main plugin) {
		this.plugin = plugin;
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

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (item.getItemMeta().getLore().get(3).equals("§ePlayer 1")
					|| item.getItemMeta().getLore().get(3).equals("§ePlayer 2")
					|| item.getItemMeta().getLore().get(3).equals("§ePlayer 3")
					|| item.getItemMeta().getLore().get(3).equals("§ePlayer 4")) {
				event.setCancelled(true);
				return;
			}
		}

		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (item.getItemMeta().getLore().get(3).equals("§ePlayer 1")) {
				locations = new ArrayList<Location>();
				locations.add(p.getLocation());
				p.sendMessage(prefix() + "§eSet §6Player 1 §espawnpoint.");
				p.sendMessage(prefix() + "§eNow setting §6Player 2 §espawnpoint.");

				ItemMeta meta = item.getItemMeta();
				List<String> lore = item.getItemMeta().getLore();
				lore.set(3, "§ePlayer 2");
				meta.setLore(lore);
				item.setItemMeta(meta);
				return;
			}

			if (item.getItemMeta().getLore().get(3).equals("§ePlayer 2")) {
				locations.add(p.getLocation());
				p.sendMessage(prefix() + "§eSet §6Player 2 §espawnpoint.");
				p.sendMessage(prefix() + "§eNow setting §6Player 3 §espawnpoint.");

				ItemMeta meta = item.getItemMeta();
				List<String> lore = item.getItemMeta().getLore();
				lore.set(3, "§ePlayer 3");
				meta.setLore(lore);
				item.setItemMeta(meta);
				return;
			}

			if (item.getItemMeta().getLore().get(3).equals("§ePlayer 3")) {
				locations.add(p.getLocation());
				p.sendMessage(prefix() + "§eSet §6Player 3 §espawnpoint.");
				p.sendMessage(prefix() + "§eNow setting §6Player 4 §espawnpoint.");

				ItemMeta meta = item.getItemMeta();
				List<String> lore = item.getItemMeta().getLore();
				lore.set(3, "§ePlayer 4");
				meta.setLore(lore);
				item.setItemMeta(meta);
				return;
			}

			if (item.getItemMeta().getLore().get(3).equals("§ePlayer 4")) {
				locations.add(p.getLocation());
				String s = item.getItemMeta().getLore().get(2);
				s = s.substring(2, s.length());
				ArenaManager.getArena(s).setSpawns(locations);
				p.sendMessage(prefix() + "§eSet §6Player 4 §espawnpoint.");
				p.sendMessage(prefix() + "§aAll spawnpoints set.");
				p.getInventory().remove(item);
				if (ArenaManager.getArena(s).getCorner2() != null) {
					ArenaManager.getArena(s).setStatus(STATUS.JOINABLE);
				}
				return;
			}
		}

	}
	
	public String prefix() {
		return "§8[§bBoatRace§8]: ";
	}

}
