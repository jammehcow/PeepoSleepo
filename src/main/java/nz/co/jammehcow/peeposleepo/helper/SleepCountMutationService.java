package nz.co.jammehcow.peeposleepo.helper;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class SleepCountMutationService {
    // Should be a mutex
    private final Semaphore sleeperCountSemaphore = new Semaphore(1);
    private int sleeperCount = 0;

    public void modifySleepCount(Function<Integer, Integer> onAcquire) {
        boolean didAcquire = false;

        try {
            didAcquire = sleeperCountSemaphore.tryAcquire(1, 5000, TimeUnit.MILLISECONDS);

            if (!didAcquire)
                return;

            sleeperCount = onAcquire.apply(sleeperCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (didAcquire)
                sleeperCountSemaphore.release(1);
        }
    }
}
