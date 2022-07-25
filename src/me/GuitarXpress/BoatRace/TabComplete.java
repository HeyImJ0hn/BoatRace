package me.guitarxpress.boatrace;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.guitarxpress.boatrace.enums.Status;
import me.guitarxpress.boatrace.managers.GameManager;

public class TabComplete implements TabCompleter {

	private GameManager gm;

	public TabComplete(BoatRace plugin) {
		gm = plugin.getGameManager();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("boatrace") || cmd.getName().equalsIgnoreCase("btr")) {

				if (gm.isInArena(player)) {
					list.add("leave");
					return list;
				}

				if (args.length == 1) {
					list.add("help");
					list.add("info");
					if (player.hasPermission("br.use")) {
						list.add("tracks");
						list.add("help");
						list.add("join");
						list.add("leave");
					}
					if (player.hasPermission("br.admin")) {
						list.add("setstatus");
						list.add("add");
						list.add("setlobby");
						list.add("remove");
						list.add("edit");
					}

				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("setstatus")
							|| args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit")) {
						if (player.hasPermission("br.use") || player.hasPermission("br.admin")) {
							for (String name : gm.getArenaNames()) {
								list.add(name);
							}
						}
					} else if (args[0].equalsIgnoreCase("add")) {
						if (player.hasPermission("br.admin"))
							list.add("name");
					}
				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("add")) {
						if (player.hasPermission("br.admin")) {
							list.add("laps");
						}
					} else if (args[0].equalsIgnoreCase("setstatus")) {
						if (player.hasPermission("br.admin")) {
							for (Status status : Status.values()) {
								list.add(status.toString());
							}
						}
					}
				}
			}
		}
		return list;
	}

}
