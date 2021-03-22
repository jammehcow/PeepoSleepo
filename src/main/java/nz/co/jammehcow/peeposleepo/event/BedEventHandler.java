package nz.co.jammehcow.peeposleepo.event;

import nz.co.jammehcow.peeposleepo.helper.BroadcastHelper;
import nz.co.jammehcow.peeposleepo.helper.SleepCountMutationService;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Optional;

public class BedEventHandler implements Listener {
    /**
     * The total amount of ticks in a day
     */
    private static final int DAY_TOTAL_TICKS = 24_000;
    /**
     * The minimum percentage (as a decimal) of players required to skip time
     */
    private static final double MIN_PLAYER_PERCENTAGE = 1d / 3;

    /**
     * A singleton instance of the SleepCountMutationService
     */
    private static final SleepCountMutationService mutationService = new SleepCountMutationService();

    /**
     * The index of the overworld World object in the service world list
     */
    private static Integer overworldIndex = null;

    /**
     * Recheck player sleeping count when players leave
     *
     * @param quitEvent the event fired by a leaving player
     */
    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent quitEvent) {
        Player player = quitEvent.getPlayer();

        mutationService.modifySleepCount((playerSleepCount) -> {
            if (getOverworldForServer(player.getServer()).getPlayers().size() <= 1)
                return 0;

            // Re-evaluate sleeping status
            if (isSleepingCountUnderThreshold(getTotalPlayersInOverworld(player.getServer()) - 1, playerSleepCount))
                return playerSleepCount;

            accelerateTimeToDay(player.getServer());
            BroadcastHelper.broadcastPrefixed(player.getServer(), "Thank god that guy left, now it's day time!");

            return 0;
        });
    }

    @EventHandler
    public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent bedLeaveEvent) {
        Player player = bedLeaveEvent.getPlayer();
        String sleepMessage = "&6%s &eis no longer peepoSleepo (%d/%d required)";

        mutationService.modifySleepCount((playerSleepCount) -> {
            BroadcastHelper.broadcastPrefixed(player.getServer(), sleepMessage,
                    player.getDisplayName(), playerSleepCount - 1,
                    getPlayersNeededForSkip(getTotalPlayersInOverworld(player.getServer())));

            return playerSleepCount - 1;
        });
    }

    @EventHandler
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent bedEnterEvent) {
        if (bedEnterEvent.isCancelled() || bedEnterEvent.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
            return;

        Player player = bedEnterEvent.getPlayer();
        String sleepMessage = "&6%s &eis peepoSleepo (%d/%d required)";

        // Acquire first semaphore
        mutationService.modifySleepCount((playerSleepCount) -> {
            // Add values to template message
            BroadcastHelper.broadcastPrefixed(player.getServer(), sleepMessage,
                    player.getDisplayName(), playerSleepCount + 1,
                    getPlayersNeededForSkip(getTotalPlayersInOverworld(player.getServer())));

            // Increment the current sleep count by one
            return playerSleepCount + 1;
        });

        mutationService.modifySleepCount((playerSleepCount) -> {
            // Return early if the current sleeping player count is under a third
            if (isSleepingCountUnderThreshold(getTotalPlayersInOverworld(player.getServer()), playerSleepCount))
                return playerSleepCount;

            // Push the world time forward
            accelerateTimeToDay(player.getServer());
            BroadcastHelper.broadcastPrefixed(player.getServer(), "&eWakey wakey, it's day time!");

            // Reset the player count to 0 as no players are sleeping
            return 0;
        });
    }


    private static void accelerateTimeToDay(Server server) {
        World overworldWorld = getOverworldForServer(server);

        // If a player sleeps in a thunderstorm, cancel the storm
        if (overworldWorld.isThundering())
            overworldWorld.setThundering(false);

        long timeUntilWake = DAY_TOTAL_TICKS - overworldWorld.getTime();
        long currentAbsoluteTime = overworldWorld.getFullTime();

        overworldWorld.setFullTime(currentAbsoluteTime + timeUntilWake);
    }

    private static int getTotalPlayersInOverworld(Server server) {
        World overWorld = getOverworldForServer(server);
        return overWorld.getPlayers().size();
    }

    /**
     * Get the current overworld World object from the server
     * @param server the server the world is loccated on
     * @return the overworld World object
     */
    private static World getOverworldForServer(Server server) {
        if (overworldIndex != null)
            return server.getWorlds().get(overworldIndex);

        List<World> worldList = server.getWorlds();
        Optional<World> world = worldList
                .stream()
                .filter(w -> w.getName().equalsIgnoreCase("overworld"))
                .findFirst();

        // Assert as this shouldn't be false. If it is you're using some custom shit; stop it.
        assert world.isPresent();

        overworldIndex = worldList.indexOf(world.get());
        return world.get();
    }

    /**
     * Calculate the minimum amount of players required to time skip
     * @param playersInOverworld the total number of players in the overworld
     * @return the minimum number of players required to time skip
     */
    private static int getPlayersNeededForSkip(int playersInOverworld) {
        return (int) Math.ceil(playersInOverworld * MIN_PLAYER_PERCENTAGE);
    }

    /**
     * Check if the current number of players sleeping is under the minimum amount required to time skip
     * @param playersOnline the total number of players online
     * @param playersInBed the total number of players currently in a bed
     * @return whether the number of sleeping players is under the skip threshold
     */
    private static boolean isSleepingCountUnderThreshold(int playersOnline, int playersInBed) {
        // Don't act on anything if no players are online
        return playersOnline == 0 || playersInBed < getPlayersNeededForSkip(playersOnline);
    }
}
