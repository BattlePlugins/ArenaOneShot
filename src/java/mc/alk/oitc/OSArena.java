package mc.alk.oitc;

import mc.alk.arena.BattleArena;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.events.ArenaEventHandler;
import mc.alk.arena.objects.messaging.MatchMessageHandler;
import mc.alk.arena.util.DmgDeathUtil;
import mc.alk.arena.util.InventoryUtil;
import mc.alk.arena.util.Log;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class OSArena extends Arena{
	static double velocity = 3;
	static Map<Material,Integer> damages = new HashMap<Material,Integer>();
	static boolean instantShot = false;
    public static boolean breakOnHit = false;
    public static int num = 1;
    public static String item = "ARROW";
    MatchMessageHandler mmh;
    HashMap<ArenaPlayer,Integer> killstreak = new HashMap<ArenaPlayer,Integer>();
    static class EnchantAdapter{
		Integer power, knockback;
		public EnchantAdapter(Integer power, Integer knockback){
			this.power = power;
			this.knockback = knockback;
		}
	}

//	Map<Integer, EnchantAdapter> arrowIds = new ConcurrentHashMap<Integer, EnchantAdapter>();
	Set<Integer> arrowIds = Collections.synchronizedSet(new HashSet<Integer>());

	@ArenaEventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		Integer dmg = 0;
		switch (event.getDamager().getType()){
		case ARROW:
			dmg = damages.get(Material.ARROW);
//			EnchantAdapter ea = event.getDamager().getEntityId();
			break;
		case PLAYER:
			ArenaPlayer ap = DmgDeathUtil.getPlayerCause(event);
			if (ap == null)
				return;
			ItemStack is = ap.getInventory().getItemInHand();
			if (is == null){
				dmg = damages.get(Material.AIR);
			} else {
				if(is.getType()!=Material.ARROW){
					dmg = damages.get(is.getType());
				}
			}
			break;
		default:
			return;
		}
		if (dmg != null)
			event.setDamage(dmg);
	}
    @Override
    public void onOpen(){
    	mmh = match.getMessageHandler();
    }
	@ArenaEventHandler(entityMethod="getEntity")
	public void onEntityShootBowEvent(EntityShootBowEvent event){
		Entity proj = event.getProjectile();
		if (proj == null || proj.getType() != EntityType.ARROW)
			return;

        shotArrow((Arrow) proj, null, true);
	}

	@ArenaEventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event){
		death(event.getEntity());
		ArenaPlayer killer = DmgDeathUtil.getPlayerCause(event);
		killstreak.put(BattleArena.toArenaPlayer(event.getEntity()), 0);
		if (killer != null){
		InventoryUtil.addItemToInventory(killer.getPlayer(), new ItemStack(Material.valueOf(item.toUpperCase()),num));
		if(killstreak.get(killer)==null) {
			killstreak.put(killer, 1);
		} else {
		killstreak.put(killer, killstreak.get(killer)+1);
		}
		switch(killstreak.get(killer)){
		case 2: match.sendMessage(mmh.getMessage("OneShot.double_kill").replace("%p", killer.getName()).replace('&', '§').replace("%k", killstreak.get(killer)+"")); 
		break;
		case 3: match.sendMessage(mmh.getMessage("OneShot.triple_kill").replace("%p", killer.getName()).replace('&', '§').replace("%k", killstreak.get(killer)+"")); 
		break;
		case 4: match.sendMessage(mmh.getMessage("OneShot.quadra_kill").replace("%p", killer.getName()).replace('&', '§').replace("%k", killstreak.get(killer)+""));
		break;
		case 5: match.sendMessage(mmh.getMessage("OneShot.penta_kill").replace("%p", killer.getName()).replace('&', '§').replace("%k", killstreak.get(killer)+"")); 
		break;
		}
		if(killstreak.get(killer)>5) match.sendMessage(mmh.getMessage("OneShot.higher_kill").replace("%p", killer.getName()).replace("%k", killstreak.get(killer)+"").replace('&', '§'));
		}
	}

    @ArenaEventHandler(needsPlayer=false)
	public void onProjectileHitEvent(ProjectileHitEvent event){
        Log.debug("### checking " + event.getEntity().getEntityId());
		if (arrowIds.remove(event.getEntity().getEntityId())){
			Entity e = event.getEntity();
//			event.getEntity().t
            Log.debug("### removing " + event.getEntity().getEntityId());
			Location l = e.getLocation().clone();
			l.setY(l.getY()-256); /// into the void with you
			e.teleport(l);
			e.remove();
		}
	}
    private void shotArrow(Arrow arrow, Player p, boolean shotFromBow){
        Log.debug("onEntityShootBowEvent  " + arrow.getEntityId());
        arrow.setVelocity(arrow.getVelocity().multiply(velocity));
        arrowIds.add(arrow.getEntityId());
        if (!shotFromBow){ /// They are using the classic instashot arrow from a click
            boolean inf = false;
            ItemStack is = p.getItemInHand();
            Map<Enchantment,Integer> encs = is.getEnchantments();
            if (encs != null){
                if (encs.containsKey(Enchantment.ARROW_FIRE)){
                    arrow.setFireTicks(100);}
                inf = encs.containsKey(Enchantment.ARROW_INFINITE);
            }
            if (!inf){
                InventoryUtil.removeItems(p.getInventory(), new ItemStack(Material.ARROW,1));
            }
        }
    }
	@ArenaEventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
        Log.debug(" onPlayerInteract FRIIGN  " + event);
		if (!instantShot ||
				!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) ||
				event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() != Material.BOW ||
				!event.getPlayer().getInventory().contains(Material.ARROW)){
			return;}
		Player p = event.getPlayer();
		Arrow arrow = p.launchProjectile(Arrow.class);
        shotArrow(arrow, p, false);
		event.setCancelled(true);
	}
	public void death(Player p){
		Random rand = new Random();
		for(int i=0;i<21;i++){
			ItemStack is = new ItemStack(Material.REDSTONE);
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(""+rand.nextInt());
			is.setItemMeta(meta);
			final Item ent = p.getWorld().dropItem(p.getLocation(), is);
			ent.setPickupDelay(Integer.MAX_VALUE);
            Vector vec = new Vector(rand.nextDouble()/2-0.25,rand.nextDouble()/1.5,rand.nextDouble()/2-0.25);
			ent.setVelocity(vec);
			Bukkit.getScheduler().scheduleSyncDelayedTask(OneShot.plugin, new Runnable(){
				public void run() {
                    ent.remove();
				}
			}, 40L);
		}
	}
}
