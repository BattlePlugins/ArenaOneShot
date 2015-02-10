package mc.alk.oitc;

import mc.alk.arena.BattleArena;
import mc.alk.arena.util.Log;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;
public class OneShot extends JavaPlugin{
	
	static OneShot plugin;
	@Override
	public void onEnable(){
		plugin = this;
		/// Registers this plugin with BattleArena
		/// this: our plugin
		/// "OneInTheChamber": The name of our competition
		/// "oic": the name of our command alias
		/// OITCArena.class: which arena should this competition use
		BattleArena.registerCompetition(this, "OneShot", "os", OSArena.class, new OITC_commandHandler());

		/// Load our config options
		loadConfig();

		Log.info("[" + getName()+ "] v" + getDescription().getVersion()+ " enabled!");
	}

	@Override
	public void onDisable(){
		Log.info("[" + getName()+ "] v" + getDescription().getVersion()+ " stopping!");
	}

	@Override
	public void reloadConfig(){
		super.reloadConfig();
		loadConfig();
	}

	public void loadConfig(){
		/// create our default config if it doesn't exist
		saveDefaultConfig();
		
		FileConfiguration config = getConfig();
		ConfigurationSection cs = config.getConfigurationSection("items");
		Set<String> keys = cs.getKeys(false);
		HashMap<Material, Integer> damages = new HashMap<Material,Integer>();
		for (String key : keys){
			Material m = Material.valueOf(key.toUpperCase());
			if (m == null)
				continue;
			int dmg = cs.getInt(key+".damage",-1);
			if (dmg != -1)
				damages.put(m, dmg);
		}
		OSArena.damages=damages;
        OSArena.velocity = config.getDouble("items.arrow.velocity", OSArena.velocity);
        OSArena.num = config.getInt("playerdeath.amount", OSArena.num);
        OSArena.item = config.getString("playerdeath.item", OSArena.item);
        OSArena.breakOnHit = config.getBoolean("items.arrow.breakOnHit", OSArena.breakOnHit);
		OSArena.instantShot = config.getBoolean("bow.instantShoot", OSArena.instantShot);
	}

}
