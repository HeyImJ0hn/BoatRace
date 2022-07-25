package me.guitarxpress.boatrace.utils;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.guitarxpress.boatrace.Commands;

public class Utils {

	public static boolean playerInArea(Location start, Location end, Player player) {

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

	public static ItemMeta changeSpawnItemLore(ItemStack item, int spawn, Player p) {
		p.sendMessage(Commands.prefix() + "§eSet §6Player " + spawn + " §espawnpoint.");
		p.sendMessage(Commands.prefix() + "§eNow setting §6Player " + (spawn + 1) + " §espawnpoint.");

		ItemMeta meta = item.getItemMeta();
		List<String> lore = item.getItemMeta().getLore();
		lore.set(1, "§ePlayer " + (spawn + 1));
		meta.setLore(lore);
		return meta;
	}
	
	public static String getNameFromString(String string) {
		String s = string;
		if (s.length() > 2)
			s = s.substring(2, s.length()); // Remove "§b" from the line in order to get arena name
		return s;
	}

}
