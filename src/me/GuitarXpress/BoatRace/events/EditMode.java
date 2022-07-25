package me.guitarxpress.boatrace.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.guitarxpress.boatrace.Arena;
import me.guitarxpress.boatrace.BoatRace;
import me.guitarxpress.boatrace.Commands;
import me.guitarxpress.boatrace.enums.Status;
import me.guitarxpress.boatrace.managers.GameManager;
import me.guitarxpress.boatrace.managers.ItemManager;
import me.guitarxpress.boatrace.utils.Utils;

public class EditMode implements Listener {

	private BoatRace plugin;
	private GameManager gm;

	public static Map<Player, ItemStack[]> oldInventory = new HashMap<Player, ItemStack[]>();

	public EditMode(BoatRace plugin) {
		this.plugin = plugin;
		this.gm = plugin.getGameManager();
	}

	public static void toggleEditMode(Player player, Arena arena) {
		ItemStack[] editMode = new ItemStack[ItemManager.editMode.length];

		for (int i = 0; i < ItemManager.editMode.length; i++) {
			if (ItemManager.editMode[i] != null) {
				ItemMeta meta = ItemManager.editMode[i].getItemMeta();
				List<String> lore = meta.getLore();
				lore.add(arena.getName());
				meta.setLore(lore);
				ItemStack item = ItemManager.editMode[i].clone();
				item.setItemMeta(meta);
				editMode[i] = item;
			}
		}

		if (!oldInventory.containsKey(player)) {
			oldInventory.put(player, player.getInventory().getContents());
			player.getInventory().setContents(editMode);
		} else {
			player.getInventory().setContents(oldInventory.get(player));
			oldInventory.remove(player);
		}

		arena.setStatus(Status.JOINABLE);
	}

	/////////// EVENTS ///////////

	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event) {
		if (event.getItem() == null)
			return;

		ItemStack item = event.getItem();
		Player p = event.getPlayer();

		if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
			if (item.getItemMeta().getLore().get(0).equals("§6Left Click §7to set corner 1.")
					&& item.getItemMeta().getLore().get(2).equals("§bBoatRace")) {
				event.setCancelled(true);
				Arena arena = gm.getArena(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1));
				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					arena.setCorner1(event.getClickedBlock().getLocation());
					p.sendMessage(Commands.prefix() + "§eSet arena corner 1");
				} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					arena.setCorner2(event.getClickedBlock().getLocation());
					p.sendMessage(Commands.prefix() + "§eSet arena corner 2");
				}
				plugin.saveArena(arena);
			} else if (item.getItemMeta().getLore().get(0)
					.equals("§7Stand on the spawn position and §6Right Click §7to set player spawnpoint.")
					&& item.getItemMeta().getLore().get(2).equals("§bBoatRace")) {
				event.setCancelled(true);
				Arena arena = gm.getArena(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1));
				if (event.getAction() == Action.RIGHT_CLICK_AIR) {
					if (item.getItemMeta().getLore().get(1).equals("§ePlayer 1")) {
						arena.setSpawns(new ArrayList<Location>());
						arena.addSpawn(p.getLocation());
						item.setItemMeta(Utils.changeSpawnItemLore(item, 1, p));
					} else if (item.getItemMeta().getLore().get(1).equals("§ePlayer 2")) {
						arena.addSpawn(p.getLocation());
						item.setItemMeta(Utils.changeSpawnItemLore(item, 2, p));
					} else if (item.getItemMeta().getLore().get(1).equals("§ePlayer 3")) {
						arena.addSpawn(p.getLocation());
						item.setItemMeta(Utils.changeSpawnItemLore(item, 3, p));
					} else if (item.getItemMeta().getLore().get(1).equals("§ePlayer 4")) {
						arena.addSpawn(p.getLocation());
						plugin.saveArena(arena);
						p.sendMessage(Commands.prefix() + "§eSet §6Player 4 §espawnpoint.");
						p.sendMessage(Commands.prefix() + "§aAll spawnpoints set.");
					}
				}
			}
		}

	}

}
