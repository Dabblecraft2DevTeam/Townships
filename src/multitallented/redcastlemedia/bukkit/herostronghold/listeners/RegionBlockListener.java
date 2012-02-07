package multitallented.redcastlemedia.bukkit.herostronghold.listeners;

import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegion;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegionType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Multitallented
 */
public class RegionBlockListener extends BlockListener {
    private final RegionManager regionManager;
    private final HeroStronghold plugin;
    public RegionBlockListener(HeroStronghold plugin) {
        this.plugin = plugin;
        this.regionManager = plugin.getRegionManager();
    }
    
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        Location loc = event.getBlock().getLocation();
        Location currentLoc = null;
        boolean delete = false;
        for (SuperRegion sr : regionManager.getContainingSuperRegions(loc)) {
            SuperRegionType currentRegionType = regionManager.getSuperRegionType(sr.getType());
            Player player = event.getPlayer();
            if ((player == null || (!sr.hasOwner(player.getName()) && !sr.hasMember(player.getName())))
                    && currentRegionType.hasEffect("denyblockbreak") && regionManager.hasAllRequiredRegions(sr) &&
                    sr.getPower() > 0 && sr.getBalance() > 0) {
                event.setCancelled(true);
                if (player != null) {
                    player.sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                }
                return;
            }
            if ((player == null || (!sr.hasOwner(player.getName()) && !sr.hasMember(player.getName())))
                    && currentRegionType.hasEffect("denyblockbreaknoreagent")) {
                event.setCancelled(true);
                if (player != null) {
                    player.sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                }
                return;
            }
        }
        
        double x1 = loc.getX();
        double y1 = loc.getY();
        double z1 = loc.getZ();
        for (Region r : regionManager.getSortedRegions()) {
            try {
                currentLoc = r.getLocation();
                if (currentLoc.getBlock().equals(loc.getBlock())) {
                    Region currentRegion = regionManager.getRegion(currentLoc);
                    RegionType currentRegionType = regionManager.getRegionType(currentRegion.getType());
                    Player player = event.getPlayer();
                    Effect effect = new Effect(plugin);
                    if ((player == null || (!currentRegion.isOwner(player.getName()) && !currentRegion.isMember(player.getName())))
                            && effect.regionHasEffect(currentRegionType.getEffects(), "denyblockbreak") != 0 && effect.hasReagents(currentLoc)) {
                        event.setCancelled(true);
                        if (player != null)
                            player.sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                        return;
                    }
                    if ((player == null || (!currentRegion.isOwner(player.getName()) && !currentRegion.isMember(player.getName())))
                            && effect.regionHasEffect(currentRegionType.getEffects(), "denyblockbreaknoreagent") != 0) {
                        event.setCancelled(true);
                        if (player != null)
                            player.sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                        return;
                    }
                    regionManager.destroyRegion(currentLoc);
                    delete=true;
                    break;
                }

                int radius = regionManager.getRegionType(r.getType()).getRadius();
                Location l = r.getLocation();
                if (l.getX() + radius < loc.getX()) {
                    return;
                }
                if (l.getX() - radius < x1 && l.getY() + radius > y1 && l.getY() - radius < y1 && 
                        l.getZ() + radius > z1 && l.getZ() - radius < z1 && l.getWorld().equals(loc.getWorld())) {
                    Region currentRegion = regionManager.getRegion(currentLoc);
                    RegionType currentRegionType = regionManager.getRegionType(currentRegion.getType());
                    Player player = event.getPlayer();
                    Effect effect = new Effect(plugin);
                    if ((player == null || (!currentRegion.isOwner(player.getName()) && !currentRegion.isMember(player.getName())))
                            && effect.regionHasEffect(currentRegionType.getEffects(), "denyblockbreak") != 0 && effect.hasReagents(currentLoc)) {
                        event.setCancelled(true);
                        if (player != null)
                            player.sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                        return;
                    }
                    if ((player == null || (!currentRegion.isOwner(player.getName()) && !currentRegion.isMember(player.getName())))
                            && effect.regionHasEffect(currentRegionType.getEffects(), "denyblockbreaknoreagent") != 0) {
                        event.setCancelled(true);
                        if (player != null)
                            player.sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                        return;
                    }
                    int amountRequired = 0;
                    int i = 0;
                    for (ItemStack currentStack : currentRegionType.getRequirements()) {
                        if (currentStack.getTypeId() == event.getBlock().getTypeId()) {
                            amountRequired = new Integer(currentStack.getAmount());
                            break;
                        }
                    }
                    if (amountRequired == 0)
                        return;
                    int radius1 = (int) Math.sqrt(regionManager.getRegionType(r.getType()).getBuildRadius()); 

                    for (int x= (int) (currentLoc.getX()-radius1); x<radius1 + currentLoc.getX(); x++) {
                        for (int y = currentLoc.getY()- radius1 > 1 ? (int) (currentLoc.getY() - radius1) : 1; y< radius1 + currentLoc.getY() && y < 128; y++) {
                            for (int z = (int) (currentLoc.getZ() - radius1); z<radius1 + currentLoc.getZ(); z++) {
                                Block tempBlock = currentLoc.getWorld().getBlockAt(x, y, z);
                                if (tempBlock.getTypeId() == event.getBlock().getTypeId()) {
                                    if (i >= amountRequired) {
                                        return;
                                    } else {
                                        i++;
                                    }
                                }
                            }
                        }
                    }
                    regionManager.destroyRegion(currentLoc);
                    delete = true;
                    break;
                }
            } catch (NullPointerException npe) {
                plugin.warning("Region " + r.getID() + " corrupted.");
            }
        }
        if (delete && currentLoc != null) {
            regionManager.removeRegion(currentLoc);
        }
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if ((event.isCancelled() || !regionManager.shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 0, "denyblockbuild", true)) &&
                  (event.isCancelled() || !regionManager.shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 0, "denyblockbuildnoreagent", false))) {
            return;
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
    }
    
    @Override
        public void onBlockDamage(BlockDamageEvent event) {
            if (event.isCancelled() || !event.getBlock().getType().equals(Material.CAKE_BLOCK))
                return;
            if (regionManager.shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 0, "denyblockbreak", true)) {
                event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                event.setCancelled(true);
                return;
            }
            if (regionManager.shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 0, "denyblockbreaknoreagent", false)) {
                event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                event.setCancelled(true);
                return;
            }
        }
        
        @Override
        public void onBlockFromTo(BlockFromToEvent event) {
            if ((event.isCancelled() || !regionManager.shouldTakeAction(event.getToBlock().getLocation(), null, 0, "denyliquid", true)) && 
                    (event.isCancelled() || !regionManager.shouldTakeAction(event.getToBlock().getLocation(), null, 0, "denyliquidnoreagent", false))) {
                return;
            }
            
            Block blockFrom = event.getBlock();

            // Check the fluid block (from) whether it is air.
           if (blockFrom.getTypeId() == 0 || blockFrom.getTypeId() == 8 || blockFrom.getTypeId() == 9) {
                event.setCancelled(true);
                return;
            }
            if (blockFrom.getTypeId() == 10 || blockFrom.getTypeId() == 11) {
                event.setCancelled(true);
                return;
            }
        }
        
        @Override
        public void onBlockIgnite(BlockIgniteEvent event) {
            if (event.isCancelled()) {
                return;
            }

            IgniteCause cause = event.getCause();
            if (((cause == IgniteCause.LIGHTNING || cause == IgniteCause.LAVA || cause == IgniteCause.SPREAD) &&
                    regionManager.shouldTakeAction(event.getBlock().getLocation(), null, 0, "denyfire", true)) ||
                    (cause == IgniteCause.FLINT_AND_STEEL && regionManager.shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 1, "denyfire", true))) {
                event.setCancelled(true);
                return;
            }

            if (((cause == IgniteCause.LIGHTNING || cause == IgniteCause.LAVA || cause == IgniteCause.SPREAD) &&
                    regionManager.shouldTakeAction(event.getBlock().getLocation(), null, 0, "denyfirenoreagent", false)) ||
                    (cause == IgniteCause.FLINT_AND_STEEL && regionManager.shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 1, "denyfirenoreagent", false))) {
                event.setCancelled(true);
                return;
            }

        }
        
        @Override
        public void onBlockBurn(BlockBurnEvent event) {
            if ((event.isCancelled() || !regionManager.shouldTakeAction(event.getBlock().getLocation(), null, 0, "denyfire", true)) &&
                    (event.isCancelled() || !regionManager.shouldTakeAction(event.getBlock().getLocation(), null, 0, "denyfirenoreagent", false))) {
                return;
            }
            event.setCancelled(true);
        }
        
        @Override
        public void onSignChange(SignChangeEvent event) {
            if ((event.isCancelled() || !regionManager.shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 0, "denyblockbreak", true)) &&
                    (event.isCancelled() || !regionManager.shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 0, "denyblockbreaknoreagent", false))) {
                return;
            }
            event.setCancelled(true);
            if (event.getPlayer() != null)
                event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
        }
        
        @Override
        public void onBlockPistonExtend(BlockPistonExtendEvent event) {
            if (event.isCancelled()) {
                return;
            }
            
            for (Block b : event.getBlocks()) {
                if ((regionManager.shouldTakeAction(b.getLocation(), null, 0, "denyblockbreak", true)) && 
                        (regionManager.shouldTakeAction(b.getLocation(), null, 0, "denyblockbreaknoreagent", false))) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        @Override
        public void onBlockPistonRetract(BlockPistonRetractEvent event) {
            if ((event.isCancelled() || !event.isSticky() || !regionManager.shouldTakeAction(event.getBlock().getLocation(), null, 0, "denyblockbreak", true)) &&
                    (event.isCancelled() || !event.isSticky() || !regionManager.shouldTakeAction(event.getBlock().getLocation(), null, 0, "denyblockbreaknoreagent", false))) {
                return;
            }
            
            event.setCancelled(true);
        }
}
