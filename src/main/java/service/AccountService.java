package service;

import dao.Account;
import dao.AccountMovementRecord;

import java.util.List;

/**
 * AccountService is responsible for accessing and updating
 * the Account entities.
 */
public interface AccountService {

    Account getAccount(String accountId);

    String createAccount(Account account);

    void addAccountMovementRecord(AccountMovementRecord accountMovementRecord);

    List<AccountMovementRecord> getAccountMovements(String accountId);
}
