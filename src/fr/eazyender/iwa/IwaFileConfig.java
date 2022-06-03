package fr.eazyender.iwa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class IwaFileConfig {
	
public static IwaFileConfig instance;
	
	private static List<String> permission_players;
	private static Map<ItemStack, ItemStack> linked_items;
	private static Map<Double, String> effects;
	private static Double decay_time;
	private static Map<String, Double> coef_players;
	
	  private static File file;
	  private static FileConfiguration configFile;
	
	  public IwaFileConfig() {
		  instance = this;
		    registerFile();
		    load();
	  }
	  
	  public void onDisable() {
		  ConfigurationSection s = configFile.getConfigurationSection("IWA");
		  s.set("players", permission_players);
		  s.set("decay", decay_time);
		  s.set("effects_key", new ArrayList<Double>(effects.keySet()));
		  s.set("effects_values", new ArrayList<String>(effects.values()));
		  
		    saveFile();
		  }
	  
	  public static void create() {
		   
		  	permission_players = new ArrayList<String>();
		    linked_items = new HashMap<ItemStack, ItemStack>();
		    effects = new HashMap<Double, String>();
		    
		    decay_time = 2.0;
		    
		    ConfigurationSection s = configFile.createSection("IWA");  
		    s.set("players", permission_players);
		    s.set("effects_key", new ArrayList<Double>(effects.keySet()));
			s.set("effects_values", new ArrayList<String>(effects.values()));
		    
		    saveFile();
		  }
	  
	  
	 private void registerFile() {
		 file = new File(IwaPl.instance.getDataFolder(), "Config.yml");
		 configFile = YamlConfiguration.loadConfiguration(file);
		    saveFile();
		  }
	 
	 private static void saveFile() {
		    try {		    
		    	configFile.save(file);
		    } catch (IOException iOException) {}
		  }
	 
	 public void load() {
		    if (configFile.contains("IWA")) {
		      ConfigurationSection s = configFile.getConfigurationSection("IWA");
		      
		      permission_players = s.getStringList("players");
		      
		      List<Double> keys = s.getDoubleList("effects_key");
		      List<String> values = s.getStringList("effects_values");
		      
		      effects = new HashMap<Double, String>();
		      for (int i = 0; i < keys.size(); i++) {
				effects.put(keys.get(i), values.get(i));
			}
		      
		      decay_time = s.getDouble("decay");
			
		    } else {
		    	create();
		    }
		  }
	
	 
	 public List<String> getAuthorisedPlayers() {
			return permission_players;
		}
	 
	 public Map<ItemStack, ItemStack> getLinkedItems() {
		return linked_items;
	}
	 
	 public void setDecayTime(Double decay) {
		 decay_time = decay;
	 }
	 
	 public Double getDecayTime() {
		 return decay_time;
	 }

	public Map<Double, String> getEffects() {
		return effects;
	}
	
	public String getEffect(Double db) {
		if(getEffects().containsKey(db)) {
			return getEffects().get(db);
		}
		return null;
	}


	public static IwaFileConfig getIwaFileConfig() { return instance;  }

}
