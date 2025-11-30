package net.copokbl.worldresetgame;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldSetManager implements Listener {
    private final List<WorldSet> worldSets = new ArrayList<>();

    public void addWorldSet(WorldSet worldSet) {
        worldSets.add(worldSet);
    }

    public List<WorldSet> getWorldSets() {
        return worldSets;
    }

    public @Nullable WorldSet getWorldSet(World world) {
        for (WorldSet ws : worldSets) {
            if (ws.getOverworld().equals(world) || ws.getNether().equals(world) || ws.getEnd().equals(world)) {
                return ws;
            }
        }
        return null;
    }

    public @Nullable WorldSet getWorldSet(Player player) {
        return getWorldSet(player.getWorld());
    }

    @EventHandler
    public void onDie(PlayerRespawnEvent e) {
        WorldSet ws = getWorldSet(Objects.requireNonNull(e.getPlayer().getLastDeathLocation()).getWorld());
        if (ws != null) {
            e.setRespawnLocation(ws.getOverworld().getSpawnLocation());
        }
    }

    @EventHandler
    public void onNether(PlayerPortalEvent e) {
        WorldSet ws = getWorldSet(e.getPlayer().getWorld());
        if (ws == null) {
            return;
        }

        Location target;
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (Objects.requireNonNull(e.getFrom().getWorld()).getEnvironment() == World.Environment.NETHER) {
                target = ws.getOverworld().getSpawnLocation();
            } else {
                target = ws.getNether().getSpawnLocation();
            }
        } else if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (Objects.requireNonNull(e.getFrom().getWorld()).getEnvironment() == World.Environment.NORMAL) {
                target = ws.getEnd().getSpawnLocation();
            } else {
                target = ws.getOverworld().getSpawnLocation();
            }
        } else {
            return;
        }


        // then set the component coords
        assert e.getTo() != null;
        target.setX(e.getTo().getX());
        target.setY(e.getTo().getY());
        target.setZ(e.getTo().getZ());
        e.setTo(target);
    }
}
