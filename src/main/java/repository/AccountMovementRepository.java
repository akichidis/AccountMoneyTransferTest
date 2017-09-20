package repository;

import dao.AccountMovementRecord;
import repository.db.DataStore;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AccountMovementRepository implements Repository {

    @Inject
    private DataStore dataStore;

    public void addAccountMovementRecord(AccountMovementRecord accountMovementRecord) {
        dataStore.store(accountMovementRecord.getId(), accountMovementRecord);
    }

    public AccountMovementRecord getAccountMovementRecord(String accountMovementId) {
        return (AccountMovementRecord) dataStore.get(accountMovementId);
    }

    public List<AccountMovementRecord> getAccountMovementRecords(String accountId) {
        Predicate<Object> selectAccountMovements = entity -> {
            if (entity instanceof AccountMovementRecord) {
                AccountMovementRecord record = (AccountMovementRecord) entity;

                return record.getAccountId().equals(accountId);
            }
            return false;
        };

        return dataStore.getAll(selectAccountMovements).stream().map(entity -> (AccountMovementRecord)entity).collect(Collectors.toList());
    }

    @Override
    public DataStore getDataStore() {
        return dataStore;
    }
}
