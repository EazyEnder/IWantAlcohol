package fr.eazyender.iwa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class AlcoholManager implements Listener{
	
	public static Map<UUID, Double> alcohol = new HashMap<UUID, Double>();
	
	private static boolean loopRunning = false;
	
	public static void initAlcoholLoop() {
		
		if(!loopRunning) {
			
			loopRunning = true;
			
			new BukkitRunnable() {
				@Override
				public void run() {
					
					for (UUID uuid : alcohol.keySet()) {
						
						double alc = alcohol.get(uuid);
						
						if(alc > 0) alcohol.replace(uuid, alc-(1/IwaFileConfig.getIwaFileConfig().getDecayTime()));
						
						if(alcohol.get(uuid) <= 0) alcohol.remove(uuid);
						
						
						for (Double percent : IwaFileConfig.getIwaFileConfig().getEffects().keySet()) {
							
							if(alc-(1/IwaFileConfig.getIwaFileConfig().getDecayTime()) >= percent*100) {
								
								String effect = IwaFileConfig.getIwaFileConfig().getEffect(percent);
								
								String[] array = effect.split(",");
								
								if(array.length >= 4 && array[0].equalsIgnoreCase("POTION")) {
									
									PotionEffectType potion = PotionEffectType.getByName(array[1]);
									
									Player player = Bukkit.getPlayer(uuid);

									if(player != null && player.isOnline()) {

										player.addPotionEffect(new PotionEffect(potion, Integer.parseInt(array[2]),Integer.parseInt(array[3])), true);
										
									}
									
								}
								
							};
							
						}
						
						
						
					}
					
					if(alcohol.isEmpty()) {this.cancel();loopRunning = false; }
					
				} }.runTaskTimer(IwaPl.instance, 0, 20);
		}
		
	}
	
	@EventHandler()
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		//Memory usage 
		Player player = event.getPlayer();
		if(alcohol.containsKey(player.getUniqueId())) {
			
			new BukkitRunnable() {

				@Override
				public void run() {
					
					if(!player.isOnline())alcohol.remove(player.getUniqueId());
					
				}
				
			}.runTaskLater(IwaPl.instance, 20*60);
			
		}
	}
	
	@EventHandler()
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
			
			Player player = event.getPlayer();
			ItemStack item = event.getItem();
			if(item != null && item.hasItemMeta()) {
				
				NamespacedKey key = new NamespacedKey(IwaPl.instance, "l_drinks");
				ItemMeta meta = item.getItemMeta();
				if(meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
					
					int l_drinks = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
					
					if(l_drinks > 0) {
						
						player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1, 1);
						
						if(!alcohol.containsKey(player.getUniqueId())) {
							alcohol.put(player.getUniqueId(), 0.0);
							initAlcoholLoop();
						}
						
						Double alc = alcohol.get(player.getUniqueId());
						alcohol.replace(player.getUniqueId(), alc + 10);
						if(alc + 10 >= 100) alcohol.replace(player.getUniqueId(), 100.0);
						
						meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, l_drinks-1);
						if(l_drinks-1 <= 0) {
							
							item.setType(Material.GLASS_BOTTLE);							
						}
						
						List<String> lore = meta.getLore();
						if(lore == null) lore = new ArrayList<String>();
						
						lore.remove("Gorgees restantes: " + l_drinks);
						lore.add("Gorgees restantes: " + (l_drinks-1));
						
						meta.setLore(lore);
						item.setItemMeta(meta);
						
					}
					
				}
				
			}
			
		}
		
	}
	
	

}
