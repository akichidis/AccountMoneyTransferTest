package repository.db;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A simple implementation of an in-memory data store. It tries to give in a really simple and abstract
 * way a READ COMMITTED isolation level of transactions.
 *
 * There is not the concept of "tables". All the entities are stored on a common map and locks a hold on a "row" level.
 */
@Singleton
public class InMemoryDataStore implements DataStore {
    private ThreadLocal<DataStoreTransaction> transactionThreadLocal = new ThreadLocal<>();

    private Map<String, Object> entities;
    private Map<String, ReentrantReadWriteLock> entitiesLocks;

    public InMemoryDataStore() {
        entities = new HashMap<>();
        entitiesLocks = new ConcurrentHashMap<>();
    }

    /**
     * Method to store the entity, recongized by the corresponding key, on the data store
     *
     * @param key The key which identifies the entity
     * @param entity The actual entity to store
     */
    public void store(String key, Object entity) {
        ensureOperationIsPermitted();

        //every store operation should acquire a write lock
        acquireWriteLock(key);

        //retrieve the previous snapshot
        Object latestEntitySnapshot = entities.put(key, entity);

        //if it's in a transaction, we don't release - READ COMMITTED isolation level
        if (inTransaction()) {
            //keep previous snapshot - restore in case of failure
            transactionThreadLocal.get().addEntityPreviousSnapshot(key, latestEntitySnapshot);
        } else {
            //we are not in a transaction - return immediately
            releaseWriteLock(key);
        }
    }

    /**
     * Method to retrieve an entity, uniquely recongized by it key, from the datastore
     *
     * @param key The entity's key
     * @return The actual entity
     */
    public Object get(String key) {
        ensureOperationIsPermitted();

        acquireReadLock(key);

        Object entity = entities.get(key);

        releaseReadLock(key);

        return entity;
    }

    @Override
    public List<Object> getAll(Predicate<Object> predicate) {
        return entities.entrySet().stream().filter(entry -> {
            get(entry.getKey());

            return predicate.test(entry.getValue());
        }).map(Map.Entry::getValue).collect(Collectors.toList());
    }


    /**
     * To begin a transaction, this method has to be called. If a transaction is already
     * active, then it's just ignored
     */
    public void beginTransaction() {
        if (inTransaction()) {
            transactionThreadLocal.get().incrementTransactionCallsCounter();
            return;
        }
        transactionThreadLocal.set(new DataStoreTransaction());
    }

    /**
     * If everything has run smoothly then, this method has to be called. If
     * an exception is thrown, then rollback() should be called instread in order
     * not to leave in inconsistent state the data store.
     */
    public void commit() {
        //basically commit all the changes - release all locks
        if (inTransaction()) {
            DataStoreTransaction transaction = transactionThreadLocal.get();

            //if we have a balanced number of begin transaction / commit calls
            if (transaction.decrementTransactionCallsCounter() == 0) {

                releaseAllWriteLocks(transaction);

                transactionThreadLocal.remove();
            }
        }
    }

    /**
     * Rollbacks all the changes to the previous version of data and releases any locks
     * associated with them
     */
    public void rollback() {
        //restore previous data - release all locks
        if (inTransaction()) {
            DataStoreTransaction transaction = transactionThreadLocal.get();

            //if we have a balanced number of begin transaction / rollback calls
            if (transaction.decrementTransactionCallsCounter() == 0) {
                restoreAllEntities(transaction);
                releaseAllWriteLocks(transaction);

                transactionThreadLocal.remove();
            } else {
                transaction.setRollbackPending(true);
            }
        }
    }

    private void releaseAllWriteLocks(DataStoreTransaction transaction) {
        Map<String, Object> entitiesMap = transaction.getEntitiesStored();

        entitiesMap.forEach((key, entity) -> {
            releaseWriteLock(key);
        });
    }

    private void restoreAllEntities(DataStoreTransaction transaction) {
        Map<String, Object> entitiesMap = transaction.getEntitiesStored();

        entitiesMap.forEach((key, entity) -> {
            entitiesMap.put(key, entity);
        });
    }

    private void acquireReadLock(String key) {
        ReentrantReadWriteLock lock = entitiesLocks.computeIfAbsent(key, (s) -> new ReentrantReadWriteLock());

        //if lock is already held by current thread, then we don't bother retrieving
        //a read lock on it
        if (lock.isWriteLockedByCurrentThread()) {
            return;
        }

        try {
            boolean result = lock.readLock().tryLock(200, TimeUnit.MILLISECONDS);

            if (result) {
                return;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        throw new DataStoreException("Couldn't acquire read lock! This lock is held by another transaction");
    }

    private void releaseReadLock(String key) {
        ReentrantReadWriteLock lock = entitiesLocks.get(key);

        //if a write lock is held, then we don't release anything
        if (lock.isWriteLockedByCurrentThread()) {
            return;
        }

        lock.readLock().unlock();
    }

    private void releaseWriteLock(String key) {
        ReentrantReadWriteLock lock = entitiesLocks.get(key);

        lock.writeLock().unlock();
    }

    private void acquireWriteLock(String key) {
        ReentrantReadWriteLock lock = entitiesLocks.computeIfAbsent(key, (s) -> new ReentrantReadWriteLock());

        try {
            //No deadlock-detection mechanism has been implemented for simplicity reasons. Thus, all the possible deadlock
            //conditions are resolved by timeout mechanisms.
            boolean result = lock.writeLock().tryLock(100, TimeUnit.MILLISECONDS);

            if (result) {
                return;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        throw new DataStoreException("Couldn't acquire write lock! This lock is held by another transaction");
    }

    private boolean inTransaction() {
        DataStoreTransaction transaction = transactionThreadLocal.get();

        return Optional.ofNullable(transaction).isPresent();
    }

    private boolean isRollbackPending() {
        DataStoreTransaction transaction = transactionThreadLocal.get();

        return Optional.ofNullable(transaction).filter(DataStoreTransaction::isRollbackPending).isPresent();
    }

    private void ensureOperationIsPermitted() {
        if (inTransaction() && isRollbackPending()) {
            throw new DataStoreException("Rollback is pending. Operation not permitted!");
        }
    }
}
