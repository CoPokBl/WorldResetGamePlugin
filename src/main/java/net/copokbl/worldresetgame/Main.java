package net.copokbl.worldresetgame;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class Main extends JavaPlugin {
    private static Main instance;
    private @Nullable WorldResetter currentResetter;
    private final WorldSetManager worldSetManager = new WorldSetManager();

    public static Main getInstance() {
        return instance;
    }

    public @Nullable WorldResetter getCurrentResetter() {
        return currentResetter;
    }

    public WorldSetManager getWorldSetManager() {
        return worldSetManager;
    }

    public void setCurrentResetter(@Nullable WorldResetter resetter) {
        this.currentResetter = resetter;
    }

    @Override
    public void onEnable() {
        instance = this;

        registerCommand("worldreset", new WorldResetCommand());

        getServer().getPluginManager().registerEvents(getWorldSetManager(), this);

        getLogger().info("WorldResetGame plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldResetGame plugin disabled!");
    }

    private void registerCommand(String label, CommandExecutor command) {
        PluginCommand pluginCommand = this.getCommand(label);
        assert pluginCommand != null;
        pluginCommand.setExecutor(command);
    }
}
