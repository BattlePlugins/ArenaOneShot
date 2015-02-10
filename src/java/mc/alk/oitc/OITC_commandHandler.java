package mc.alk.oitc;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.alk.arena.executors.CustomCommandExecutor;
import mc.alk.arena.executors.MCCommand;

public class OITC_commandHandler extends CustomCommandExecutor {
	@MCCommand(cmds = { "setItem" }, admin = true)
	public boolean setItem(CommandSender sender) {
		Player p = (Player) sender;
		OneShot.plugin.getConfig().set("playerdeath.item", p.getItemInHand().getType().name());
		OneShot.plugin.getConfig().set("playerdeath.amount", p.getItemInHand().getAmount());
		OneShot.plugin.saveConfig();
		sender.sendMessage(ChatColor.GREEN + "Item set to " + OneShot.plugin.getConfig().getString("playerdeath.item") + " and amount " + OneShot.plugin.getConfig().getInt("playerdeath.amount"));
		return true;
    }
}
