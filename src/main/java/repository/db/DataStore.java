package repository.db;

import javax.inject.Singleton;
import java.util.List;
import java.util.function.Predicate;

@Singleton
public interface DataStore {

    /**
     * Method to store the entity, recongized by the corresponding key, on the data store
     *
     * @param key The key which identifies the entity
     * @param entity The actual entity to store
     */
    void store(String key, Object entity);

    /**
     * Method to retrieve an entity, uniquely recongized by it key, from the datastore
     *
     * @param key The entity's key
     * @return The actual entity
     */
    Object get(String key);

    /**
     * Retrieve a list of entities according to the provided criteria
     *
     * @param predicate
     * @return
     */
    List<Object> getAll(Predicate<Object> predicate);

    /**
     * To begin a transaction, this method has to be called. If a transaction is already
     * active, then it's just ignored
     */
    void beginTransaction();

    /**
     * If everything has run smoothly then, this method has to be called. If
     * an exception is thrown, then rollback() should be called instread in order
     * not to leave in inconsistent state the data store.
     */
    void commit();

    /**
     * Rollbacks all the changes to the previous version of data and releases any locks
     * associated with them
     */
    void rollback();
}
