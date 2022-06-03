package fr.eazyender.iwa;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandTavern implements CommandExecutor {

	public static Map<UUID, ItemStack> item_choose = new HashMap<UUID,ItemStack>();
	private static String prefix = "§r[§lTaverne§r] : ";	
	
	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg1, String[] args) {
		
		Player player = (Player) sender;
		
		if(!IwaFileConfig.getIwaFileConfig().getAuthorisedPlayers().contains(player.getUniqueId().toString()) && !player.isOp()) {
			
			player.sendMessage(prefix + "Vous ne pouvez pas utiliser cette commande.");
			
			return false;
		}
		
		if(args.length > 0) {
			
			if(args[0].equalsIgnoreCase("decay")) {
				if(args.length > 1) {
					try {
						
						Double decaytime = Double.parseDouble(args[1]);
						
						IwaFileConfig.getIwaFileConfig().setDecayTime(decaytime);
						
						player.sendMessage(prefix + "Le temps de décroissance a bien été modifié par " + decaytime + ".");
						
					} catch (Exception e) {
						player.sendMessage(prefix + "Le paramètre doit être un nombre");
					}
				}else {
					player.sendMessage(prefix + "Usage : /taverne decay (nombre)");
				}
			}
			else if(args[0].equalsIgnoreCase("effects")) {
				
				if(args.length > 1 && (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove"))) {
					
					if(args[1].equalsIgnoreCase("clear")) {
						
						IwaFileConfig.getIwaFileConfig().getEffects().clear();
						player.sendMessage(prefix + "Tous les effets ont été supprimés.");
						
					}else if(args[1].equalsIgnoreCase("remove")) {
						
						if(args.length > 2) {
							
							try {
								
								Double db = (Double)(Integer.parseInt(args[2])/100.0);
								
								if(IwaFileConfig.getIwaFileConfig().getEffects().containsKey(db)) {
									
									IwaFileConfig.getIwaFileConfig().getEffects().remove(db);
									player.sendMessage(prefix + "L'effet a été correctement supprimé.");
									
								}else {
									player.sendMessage(prefix + "Il n'y a aucun effet attribué à ce pourcentage.");
								}
								
								
							} catch (Exception e) {
								player.sendMessage(prefix + "Usage : /taverne effects remove (entier entre 0-100).");
								System.out.println(e);
							}
							
						}else {
							player.sendMessage(prefix + "Usage : /taverne effects remove (entier entre 0-100).");
						}
						
					}
					
					return false;
				}
				
				if(args.length > 2) {
					
					int percent = 0;
					try {
						percent = Integer.parseInt(args[1]);
					} catch (Exception e) {
						player.sendMessage(prefix + "/taverne (entier entre 0-100) (effet)");
						return false;
					}
					
					//TYPE|NAME|TIME|AMPLI
					
					String effect = args[2];
					
					if(IwaFileConfig.getIwaFileConfig().getEffects().containsKey((Double) (percent/100.0))) {
						IwaFileConfig.getIwaFileConfig().getEffects().replace((Double) (percent/100.0), effect);
					}else {
						IwaFileConfig.getIwaFileConfig().getEffects().put((Double) (percent/100.0), effect);
					}
					player.sendMessage(prefix + "L'effet a bien été attribué pour " + percent + "%.");
					
				}else {
					player.sendMessage(prefix + "Usage : /taverne effect (EFFET/clear/remove)");
				}
			}
			else if(args[0].equalsIgnoreCase("reset")) {
				
				if(args.length > 1 && Bukkit.getPlayer(args[1]) != null) {
					
					Player target = Bukkit.getPlayer(args[1]);
					if(AlcoholManager.alcohol.containsKey(target.getUniqueId())) {
						
						AlcoholManager.alcohol.remove(target.getUniqueId());
						player.sendMessage(prefix + "Vous avez clear l'alcool de : " + args[1]);
					}else {
						player.sendMessage(prefix + args[1] + " n'avait pas d'alcool.");
					}
					
				}else {
					AlcoholManager.alcohol.clear();
					player.sendMessage(prefix + "Vous avez clear l'alcool de tous le monde.");
				}
			
			}else if(args[0].equalsIgnoreCase("gorgee")) {
				
				if(args.length > 1 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("clear"))) {
					
					ItemStack item = player.getInventory().getItemInMainHand();
					if(item != null && item.hasItemMeta()) {
						NamespacedKey key = new NamespacedKey(IwaPl.instance, "l_drinks");
						ItemMeta meta = item.getItemMeta();
						List<String> lore = meta.getLore();
						if(lore == null) lore = new ArrayList<String>();
						if(args[1].equalsIgnoreCase("add")) {
							
							int gorgee = 0;
							
							if(!meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
								meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 0);
								lore.add("Gorgees restantes: " + 0);
							}
							
							if(args.length > 2) {
								try {
									gorgee = Integer.parseInt(args[2]);
								} catch (Exception e) {
									player.sendMessage(prefix + "Vous devez mettre un nombre.");
									meta.setLore(lore);
									item.setItemMeta(meta);
									return false;
								}
							}else {
								gorgee++;
							}
							
							meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 
							meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) + gorgee);
							
							lore.remove("Gorgees restantes: " + (meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) - gorgee));
							lore.add("Gorgees restantes: " + meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER));
							
							
						}else if(args[1].equalsIgnoreCase("clear")) {
							
							if(meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
								
								lore.remove("Gorgees restantes: " + meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER));
								meta.getPersistentDataContainer().remove(key);
								player.sendMessage(prefix + "L'item n'est plus buvable");
								
								
								
							}else {
								player.sendMessage(prefix + "L'item ne pouvait déja pas être bu.");
							}
							
						}
						meta.setLore(lore);
						item.setItemMeta(meta);
						
					}else {
						player.sendMessage(prefix + "Vous devez avoir un item en main.");
					}
					
				}else {
					player.sendMessage(prefix + "Usage : /taverne gorgee (add/clear)");
				}
				
			}
			else if(args[0].equalsIgnoreCase("check")) {
				
				if(args.length > 1 && Bukkit.getPlayer(args[1]) != null) {
					
					Player target = Bukkit.getPlayer(args[1]);
					if(AlcoholManager.alcohol.containsKey(target.getUniqueId())) {
						
						Double alc = AlcoholManager.alcohol.get(target.getUniqueId());
						player.sendMessage(prefix + args[1] + " a " + alc + "/100 d'alcool.");
						
					}else {
						player.sendMessage(prefix + args[1] + " a " + 0 + "/100 d'alcool.");
					}
					
				}else {
					player.sendMessage(prefix + "Usage : /taverne check (player)");
				}
				
			}
			
			else if(args[0].equalsIgnoreCase("permission")) {
				
				if(args.length > 2 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) && Bukkit.getPlayer(args[2]) != null) {
					
					Player target = Bukkit.getPlayer(args[2]);
					
					if(args[1].equalsIgnoreCase("add")) {
						if(IwaFileConfig.getIwaFileConfig().getAuthorisedPlayers().contains(target.getUniqueId().toString())) {
							
							player.sendMessage(prefix + "Cette personne a déjà les permissions, faites le avec remove pour l'enlever.");
							
							return false;
						}
						
						IwaFileConfig.getIwaFileConfig().getAuthorisedPlayers().add(target.getUniqueId().toString());
						player.sendMessage(prefix + "Vous avez rajouté les permissions à : " + target.getName() + ".");
						target.sendMessage(prefix + player.getName() + " vous a rajouté les permissions.");
					}else if(args[1].equalsIgnoreCase("remove")) {
						if(!IwaFileConfig.getIwaFileConfig().getAuthorisedPlayers().contains(target.getUniqueId().toString())) {
							
							player.sendMessage(prefix + "Cette personne n'a pas les permissions, rajoutez la avec add.");
							
							return false;
						}
						
						IwaFileConfig.getIwaFileConfig().getAuthorisedPlayers().remove(target.getUniqueId().toString());
						player.sendMessage(prefix + "Vous avez enlevé les permissions à : " + target.getName() + ".");
						target.sendMessage(prefix + player.getName() + " vous a enlevé les permissions.");
					}

				}else {
					player.sendMessage(prefix + "Usage : /taverne permission (add/remove) (player)");
				}
				
			}
			
			else if(args[0].equalsIgnoreCase("verrerie")) {
				
				player.sendMessage(prefix + "Disponible plus tard.");
				if(true)return false;
				
				if(args.length > 1 && args[1] == "cancel") {
					
					if(item_choose.containsKey(player.getUniqueId())) {
						item_choose.remove(player.getUniqueId());
						player.sendMessage(prefix + "L'opération a été annulée.");
					}else {
						player.sendMessage(prefix + "Vous n'avez pas d'opération en cours");
					}
					return false;
				}
				
				if(player.getItemInUse() != null) {
					ItemStack item = player.getItemInUse();
					UUID id = player.getUniqueId();
					
					if(args.length > 1 && args[1] == "remove") {
						
						if(IwaFileConfig.getIwaFileConfig().getLinkedItems().containsKey(item)) {
							IwaFileConfig.getIwaFileConfig().getLinkedItems().remove(item);
							player.sendMessage(prefix + "L'item n'a plus aucun lien.");
						}else {
							player.sendMessage(prefix + "L'item n'a aucun lien actif.");
						}
						
						return false;
					}
					
					if(item_choose.containsKey(id)) {
						
						ItemStack first_item = item_choose.get(id);
						
						if(item.equals(first_item)){
							
							player.sendMessage(prefix + "Vous ne pouvez pas lier un même item");
							
						}else {
							
							IwaFileConfig.getIwaFileConfig().getLinkedItems().put(first_item, item);
							item_choose.remove(player.getUniqueId());
							player.sendMessage(prefix + "Le lien a été crée.");
							
						}
						
					}else {
						
						if(IwaFileConfig.getIwaFileConfig().getLinkedItems().containsKey(item)) {
							player.sendMessage(prefix + "Cet item est déja lié à un autre, faire /taverne verrerie remove pour l'enlever");
							return false;
						}
						
						item_choose.put(id, item);
						player.sendMessage(prefix + "La prochaine utilisation de la commande enregistrera l'item en tant que 'déchet' de celui-ci. /taverne verrerie cancel , afin de mettre fin à l'opération");
						
					}
					
				}
			}else {
				player.sendMessage(prefix + "Usage : /taverne (permission/verrerie/gorgee/check/reset/effects/decay)");
			}
			
		}else {
			player.sendMessage(prefix + "Usage : /taverne (permission/verrerie/gorgee/check/reset/effects/decay)");
		}
		
		return false;
	}

}
