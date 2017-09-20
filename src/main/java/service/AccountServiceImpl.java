package service;

import dao.Account;
import dao.AccountMovementRecord;
import dao.AccountStatus;
import repository.AccountMovementRepository;
import repository.AccountRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

@Singleton
public class AccountServiceImpl implements AccountService {

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private AccountMovementRepository accountMovementRepository;

    @Override
    public Account getAccount(String accountId) {
        Account account = accountRepository.getAccount(accountId);

        ensureAccountExists(account);
        ensureAccountIsNotClosed(account);

        return account;
    }

    @Override
    public String createAccount(Account account) {
        return accountRepository.createAccount(account);
    }

    @Override
    public void addAccountMovementRecord(AccountMovementRecord accountMovementRecord) {
        accountMovementRepository.addAccountMovementRecord(accountMovementRecord);
    }

    @Override
    public List<AccountMovementRecord> getAccountMovements(String accountId) {
        Account account = getAccount(accountId);

        return accountMovementRepository.getAccountMovementRecords(accountId);
    }

    private void ensureAccountExists(Account account) {
        if (account == null) {
            throw new NotFoundException("Account not found");
        }
    }

    private void ensureAccountIsNotClosed(Account account) {
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new WebApplicationException("Account with id " + account.getId() + " is closed", Response.Status.CONFLICT);
        }
    }
}
