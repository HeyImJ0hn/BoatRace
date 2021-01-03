package me.GuitarXpress.BoatRace;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabComplete implements TabCompleter {

	List<String> arguments = new ArrayList<String>();
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
		if (arguments.isEmpty()) {
			Player player = (Player) sender;
			if (player.hasPermission("br.use")) {
				arguments.add("help");
				arguments.add("join");
				arguments.add("leave");
				arguments.add("tracks");
			}
			if (player.hasPermission("br.admin")) {
				arguments.add("add");
				arguments.add("remove");
				arguments.add("setlobby");
				arguments.add("setspawns");
				arguments.add("setbounds");
				arguments.add("setstate");
			}
		}
		
		List<String> result = new ArrayList<String>();
		if (args.length == 1) {
			for(String a : arguments) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
					result.add(a);
				}
			}
			
		}
		return result;
	}

}
