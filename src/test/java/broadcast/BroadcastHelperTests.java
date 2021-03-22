package broadcast;

import helper.StubbedTestServer;
import nz.co.jammehcow.peeposleepo.helper.BroadcastHelper;
import org.bukkit.Server;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BroadcastHelperTests {
    private static final String TEST_BROADCAST_MESSAGE = "Test message";
    private static final String BROADCAST_PREFIX = "PeepoSleepo";

    /**
     * Verify that a message passed to BroadcastHelper.broadcastPrefixed() is prefixed with the plugin broadcast prefix
     */
    @Test
    public void messageDoesContainBroadcastPrefix() {
        Server mockServer = mock(StubbedTestServer.class);

        BroadcastHelper.broadcastPrefixed(mockServer, TEST_BROADCAST_MESSAGE);

        verify(mockServer).broadcastMessage(contains(BROADCAST_PREFIX));
    }

    /**
     * Verify that a message passed to BroadcastHelper.broadcastPrefixed() is passed to the server broadcast message
     */
    @Test
    public void messageDoesContainBroadcastMessage() {
        Server mockServer = mock(StubbedTestServer.class);

        BroadcastHelper.broadcastPrefixed(mockServer, TEST_BROADCAST_MESSAGE);

        verify(mockServer).broadcastMessage(contains(TEST_BROADCAST_MESSAGE));
    }

    /**
     * Verify that a message passed to BroadcastHelper.broadcastPrefixed() has a formatting reset char before the message
     */
    @Test
    public void messageDoesContainResetFormatCode() {
        Server mockServer = mock(StubbedTestServer.class);

        BroadcastHelper.broadcastPrefixed(mockServer, TEST_BROADCAST_MESSAGE);

        verify(mockServer).broadcastMessage(contains("\u00A7r" + TEST_BROADCAST_MESSAGE));
    }

    /**
     * Verify that a message passed to BroadcastHelper.broadcastPrefixed() translates colour codes from &{x}
     */
    @Test
    public void messageDoesFormatExternalColourCodes() {
        Server mockServer = mock(StubbedTestServer.class);

        BroadcastHelper.broadcastPrefixed(mockServer, "&7" + TEST_BROADCAST_MESSAGE);

        verify(mockServer).broadcastMessage(contains("\u00A77" + TEST_BROADCAST_MESSAGE));
    }
}
