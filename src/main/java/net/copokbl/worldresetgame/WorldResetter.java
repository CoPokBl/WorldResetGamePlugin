package net.copokbl.worldresetgame;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WorldResetter {
    private final long seed;
    private final World waitingWorld;
    private final int resetIntervalSeconds;
    private final String worldName;

    private int currentReset = -1;

    private World gameWorld;
    private Long nextResetTime = 0L;
    private boolean resetting = false;

    private List<Player> getPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public WorldResetter(World world, int resetIntervalSeconds) {
        this.seed = world.getSeed();
        this.resetIntervalSeconds = resetIntervalSeconds;
        this.worldName = world.getName();

        // Get a waiting world
        String waitingWorldName = "waiting_world_" + System.currentTimeMillis();
        WorldCreator wc = new WorldCreator(waitingWorldName);
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.FLAT);
        this.waitingWorld = wc.createWorld();

        assert this.waitingWorld != null;
        this.waitingWorld.setPVP(false);
        this.waitingWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);

        gameWorld = world;

        // listen every tick for timer
        new BukkitRunnable() {
            @Override
            public void run() {
                if (resetting) {
                    return;
                }

                if (System.currentTimeMillis() >= nextResetTime) {
                    reset();
                }

                // Actionbar countdown
                long timeLeft = (nextResetTime - System.currentTimeMillis()) / 1000;
                for (Player p : getPlayers()) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(Util.t("&eWorld resets in &c" + timeLeft + " &eseconds")));
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }

    public void reset() {
        currentReset++;
        resetting = true;

        // teleport all players to waiting
        for (Player p : getPlayers()) {
            p.teleport(waitingWorld.getSpawnLocation());
        }

        // wait half second for players to teleport
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            File folder = gameWorld.getWorldFolder();

            // unload game world
//            boolean success = Bukkit.getServer().unloadWorld(gameWorld, true);
//            if (!success) {
//                Bukkit.getLogger().severe("Failed to unload world " + gameWorld.getName() + " for reset!");
//                return;
//            }

            // delete game world folder
//            deleteFolder(folder);

//            File netherFolder = new File(folder.getParentFile(), folder.getName() + "_nether");
//            File endFolder = new File(folder.getParentFile(), folder.getName() + "_the_end");
//            if (netherFolder.exists()) {
//                deleteFolder(netherFolder);
//            }
//            if (endFolder.exists()) {
//                deleteFolder(endFolder);
//            }

            // recreate game world

            // make seed random
            WorldSet ws = new WorldSet(worldName + "_reset_" + currentReset, ThreadLocalRandom.current().nextLong());
//            WorldCreator wc = new WorldCreator(worldName);
//            wc.environment(World.Environment.NORMAL);
//            wc.seed(seed);
//            gameWorld = wc.createWorld();
            gameWorld = ws.getOverworld();
            Main.getInstance().getWorldSetManager().addWorldSet(ws);

            Arrays.stream(gameWorld.getLoadedChunks()).forEach(Chunk::unload);
            for (Player p : getPlayers()) {
                p.teleport(gameWorld.getSpawnLocation());
                p.setRespawnLocation(gameWorld.getSpawnLocation());
                p.sendMessage(Util.t("&aThe world has been reset! Current reset count: &e" + currentReset));
            }

            nextResetTime = System.currentTimeMillis() + resetIntervalSeconds * 1000L;
            resetting = false;
        }, 20L * 5);
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    if (!f.delete()) {
                        Bukkit.getLogger().severe("Failed to delete file " + f.getAbsolutePath() + " during world reset!");
                    }
                }
            }
        }
        if (!folder.delete()) {
            Bukkit.getLogger().severe("Failed to delete folder " + folder.getAbsolutePath() + " during world reset!");
        }
    }
}
