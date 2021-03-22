package nz.co.jammehcow.peeposleepo;

import nz.co.jammehcow.peeposleepo.event.BedEventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginMain extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info(() -> {
            String version = this.getDescription().getVersion();
            String templateString = "Starting PeepoSleepo v%s";

            return String.format(templateString, version);
        });

        this.getServer().getPluginManager().registerEvents(new BedEventHandler(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info(() -> {
            String version = this.getDescription().getVersion();
            String templateString = "Stopping PeepoSleepo v%s";

            return String.format(templateString, version);
        });
    }
}
