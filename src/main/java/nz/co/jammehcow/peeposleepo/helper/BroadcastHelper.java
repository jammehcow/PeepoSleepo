package nz.co.jammehcow.peeposleepo.helper;

import org.bukkit.ChatColor;
import org.bukkit.Server;

/**
 * Helper methods for broadcasting messages to the server
 */
public class BroadcastHelper {
    /**
     * A prefix that should be shown before each broadcast message
     */
    private static final String BROADCAST_PREFIX = "&l&9[PeepoSleepo] &r";

    /**
     * Broadcast a message to the server with the plugin's prefix
     * @param message the message being broadcasted, can contain colours using ampersands (&)
     */
    public static void broadcastPrefixed(Server server, String message) {
        broadcastPrefixed(server, message, new Object[] {});
    }

    /**
     * Broadcast a message to the server with the plugin's prefix
     * @param message the String.format() compatible message being broadcasted, can contain colours using ampersands (&)
     * @param args the arguments to be passed to the formatter
     */
    public static void broadcastPrefixed(Server server, String message, Object... args) {
        String formattedMessage = BROADCAST_PREFIX + String.format(message, args);
        String translatedMessage = ChatColor.translateAlternateColorCodes('&', formattedMessage);

        server.broadcastMessage(translatedMessage);
    }
}
