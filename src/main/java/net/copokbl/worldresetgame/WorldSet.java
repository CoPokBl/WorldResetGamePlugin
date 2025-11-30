package net.copokbl.worldresetgame;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

public class WorldSet {
    private final World overworld;
    private final World nether;
    private final World end;

    public WorldSet(World overworld, World nether, World end) {
        this.overworld = overworld;
        this.nether = nether;
        this.end = end;
    }

    public WorldSet(String worldName, long seed) {
        WorldCreator wc = new WorldCreator(worldName);
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.NORMAL);
        wc.seed(seed);
        this.overworld = wc.createWorld();

        WorldCreator nc = new WorldCreator(worldName + "_nether");
        nc.environment(World.Environment.NETHER);
        nc.type(WorldType.NORMAL);
        nc.seed(seed);
        this.nether = nc.createWorld();

        WorldCreator ec = new WorldCreator(worldName + "_end");
        ec.environment(World.Environment.THE_END);
        ec.type(WorldType.NORMAL);
        ec.seed(seed);
        this.end = ec.createWorld();
    }

    public World getOverworld() {
        return overworld;
    }

    public World getNether() {
        return nether;
    }

    public World getEnd() {
        return end;
    }
}
