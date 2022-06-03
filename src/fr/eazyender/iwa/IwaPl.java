package fr.eazyender.iwa;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class IwaPl extends JavaPlugin {
	
	public static IwaPl instance;
	
	@Override
	public void onEnable() 
	{
		instance = this;
		
		
		IwaFileConfig file = new IwaFileConfig();
		
		getCommand("taverne").setExecutor(new CommandTavern());
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new AlcoholManager(), this);
	}

	@Override
	public void onDisable() 
	{
		IwaFileConfig.getIwaFileConfig().onDisable();
	}
	

}
