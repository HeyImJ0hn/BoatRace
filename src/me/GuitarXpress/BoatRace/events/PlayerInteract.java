package me.guitarxpress.boatrace.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.guitarxpress.boatrace.BoatRace;
import me.guitarxpress.boatrace.managers.GameManager;

public class PlayerInteract implements Listener {

	private GameManager gm;

	public PlayerInteract(BoatRace plugin) {
		gm = plugin.getGameManager();
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		ItemStack item = (ItemStack) event.getItem();
		Player p = (Player) event.getPlayer();

		if (gm.isInArena(p))
			event.setCancelled(true);

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
	}
}
