package repository;

import repository.db.DataStore;

public interface Repository {

    DataStore getDataStore();
}
