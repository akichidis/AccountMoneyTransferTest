package repository.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class DataStoreTransaction {;
    private Map<String, Object> entitiesStored = new HashMap<>();
    private AtomicInteger atomicInteger = new AtomicInteger(1);
    private boolean rollbackPending;

    void addEntityPreviousSnapshot(String key, Object entity) {
        this.entitiesStored.put(key, entity);
    }

    Map<String, Object> getEntitiesStored() {
        return entitiesStored;
    }

    void incrementTransactionCallsCounter() {
        atomicInteger.incrementAndGet();
    }

    int decrementTransactionCallsCounter() {
        return atomicInteger.decrementAndGet();
    }

    public boolean isRollbackPending() {
        return rollbackPending;
    }

    public void setRollbackPending(boolean rollbackPending) {
        this.rollbackPending = rollbackPending;
    }
}
