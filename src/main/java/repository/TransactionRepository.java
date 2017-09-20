package repository;

import dao.Transaction;
import repository.db.DataStore;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransactionRepository implements Repository {

    @Inject
    private DataStore dataStore;

    public void addTransaction(Transaction transaction) {
        dataStore.store(transaction.getTransactionId(), transaction);
    }

    public Transaction getTransaction(String transactionId) {
        return (Transaction) dataStore.get(transactionId);
    }

    @Override
    public DataStore getDataStore() {
        return dataStore;
    }
}
