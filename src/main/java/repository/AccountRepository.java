package repository;

import dao.Account;
import repository.db.DataStore;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountRepository implements Repository {

    @Inject
    private DataStore dataStore;

    public Account getAccount(String accountId) {
        return (Account) dataStore.get(accountId);
    }

    public String createAccount(Account account) {
        dataStore.store(account.getId(), account);

        return account.getId();
    }

    public void updateAccount(Account account) {
        dataStore.store(account.getId(), account);
    }

    @Override
    public DataStore getDataStore() {
        return dataStore;
    }
}
