package edu.whimc.positiontracker.regionevents;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.Location;

public class RegionUtils {

    public static ApplicableRegionSet getRegions(Location location) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(
                BukkitAdapter.adapt(location));
    }

}
